package pe.bibliotec.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import jakarta.persistence.EntityManager;
import pe.bibliotec.dao.PrestamoDAO;
import pe.bibliotec.dao.ReservaDAO;
import pe.bibliotec.model.Ejemplar;
import pe.bibliotec.model.EstadoEjemplar;
import pe.bibliotec.model.EstadoMulta;
import pe.bibliotec.model.EstadoPrestamo;
import pe.bibliotec.model.EstadoReserva;
import pe.bibliotec.model.EstadoUsuario;
import pe.bibliotec.model.Multa;
import pe.bibliotec.model.Prestamo;
import pe.bibliotec.model.Reserva;
import pe.bibliotec.model.Usuario;
import pe.bibliotec.model.VerificacionPrestamo;
import pe.bibliotec.util.JpaUtil;

/** Casos de uso de prestamos: registrar, devolver, renovar y cobrar multas. */
public class PrestamoService {

    private static final ZoneId ZONA = ZoneId.of("America/Lima");

    private final PrestamoDAO dao = new PrestamoDAO();
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final ReservaService reservas = new ReservaService();

    public static LocalDate hoy() {
        return LocalDate.now(ZONA);
    }

    /** RF-04. Aplica RN-01, RN-02, RN-03 y RN-07 dentro de una transaccion. */
    public Prestamo registrar(String codigoUsuario, String codigoBarras) {
        if (vacio(codigoUsuario) || vacio(codigoBarras)) {
            throw new ReglaNegocioException("Ingrese el codigo del usuario y el codigo de barras del ejemplar.");
        }
        return enTransaccion(em -> {
            Usuario usuario = dao.usuarioPorCodigo(em, codigoUsuario.trim())
                    .orElseThrow(() -> new ReglaNegocioException(
                            "No existe un usuario con el codigo " + codigoUsuario.trim() + "."));
            Ejemplar ejemplar = dao.ejemplarPorCodigoBloqueado(em, codigoBarras.trim())
                    .orElseThrow(() -> new ReglaNegocioException(
                            "No existe un ejemplar con el codigo de barras " + codigoBarras.trim() + "."));

            // RN-09: las reservas vencidas liberan su ejemplar antes de decidir.
            reservas.expirarVencidas(em);

            // RN-07: solo se presta un ejemplar disponible, o uno reservado por el mismo usuario.
            Reserva reserva = null;
            if (ejemplar.getEstado() == EstadoEjemplar.RESERVADO) {
                reserva = reservaDAO.activaDeEjemplar(em, ejemplar.getId())
                        .filter(r -> r.getUsuario().getId().equals(usuario.getId()))
                        .orElseThrow(() -> new ReglaNegocioException(
                                "El ejemplar esta reservado por otro usuario."));
            } else if (ejemplar.getEstado() != EstadoEjemplar.DISPONIBLE) {
                throw new ReglaNegocioException(
                        "El ejemplar no esta disponible (estado: " + ejemplar.getEstado().getValor() + ").");
            }

            // RN-01 y RN-02.
            Optional<String> motivo = ReglasPrestamo.motivoRechazo(
                    usuario.getRol(),
                    usuario.getEstado(),
                    dao.tieneMultaPendiente(em, usuario.getId()),
                    (int) dao.contarPrestamosActivos(em, usuario.getId()));
            if (motivo.isPresent()) {
                throw new ReglaNegocioException(motivo.get());
            }

            LocalDate hoy = hoy();
            Prestamo prestamo = new Prestamo();
            prestamo.setUsuario(usuario);
            prestamo.setEjemplar(ejemplar);
            prestamo.setFechaPrestamo(hoy);
            prestamo.setFechaVencimiento(ReglasPrestamo.fechaVencimiento(hoy, usuario.getRol())); // RN-03
            prestamo.setRenovaciones(0);
            prestamo.setEstado(EstadoPrestamo.VIGENTE);
            ejemplar.setEstado(EstadoEjemplar.PRESTADO);
            if (reserva != null) {
                reserva.setEstado(EstadoReserva.ATENDIDA);
                prestamo.setIdReserva(reserva.getId());
            }
            em.persist(prestamo);
            return prestamo;
        });
    }

    /**
     * RF-05. Registra la devolucion, libera el ejemplar y, si hay retraso, genera la multa (RN-05)
     * y suspende al usuario (RN-06). Devuelve la multa generada, si la hubo.
     */
    public Optional<Multa> devolver(Integer idPrestamo) {
        return enTransaccion(em -> {
            Prestamo prestamo = em.find(Prestamo.class, idPrestamo, JpaUtil.bloqueo());
            if (prestamo == null) {
                throw new ReglaNegocioException("El prestamo no existe.");
            }
            if (prestamo.getFechaDevolucion() != null) {
                throw new ReglaNegocioException("Este prestamo ya fue devuelto.");
            }

            LocalDate hoy = hoy();
            prestamo.setFechaDevolucion(hoy);
            prestamo.setEstado(EstadoPrestamo.DEVUELTO);
            prestamo.getEjemplar().setEstado(EstadoEjemplar.DISPONIBLE);

            BigDecimal monto = ReglasPrestamo.calcularMulta(prestamo.getFechaVencimiento(), hoy);
            if (monto.signum() <= 0) {
                return Optional.<Multa>empty();
            }

            // Dias de retraso reales; si el prestamo ya tenia multa pendiente, se actualiza (RN-05).
            int dias = (int) java.time.temporal.ChronoUnit.DAYS
                    .between(prestamo.getFechaVencimiento(), hoy);
            Optional<Multa> existente = dao.multaDePrestamo(em, prestamo.getId());
            Multa multa;
            if (existente.isPresent()) {
                multa = existente.get();
                if (multa.getEstado() == EstadoMulta.PENDIENTE) {
                    multa.setDiasRetraso(dias);
                    multa.setMonto(monto);
                }
            } else {
                multa = new Multa();
                multa.setPrestamo(prestamo);
                multa.setDiasRetraso(dias);
                multa.setMonto(monto);
                multa.setFechaGeneracion(hoy);
                multa.setEstado(EstadoMulta.PENDIENTE);
                em.persist(multa);
            }
            prestamo.getUsuario().setEstado(EstadoUsuario.SUSPENDIDO); // RN-06
            return Optional.of(multa);
        });
    }

    /** RF-07. Aplica RN-04: una renovacion y no si esta vencido. Solo el dueno del prestamo puede renovar. */
    public void renovar(Integer idPrestamo, Integer idUsuario) {
        enTransaccion(em -> {
            Prestamo prestamo = em.find(Prestamo.class, idPrestamo, JpaUtil.bloqueo());
            if (prestamo == null || !prestamo.getUsuario().getId().equals(idUsuario)) {
                throw new ReglaNegocioException("El prestamo no existe o no te pertenece.");
            }
            if (prestamo.getFechaDevolucion() != null) {
                throw new ReglaNegocioException("Este prestamo ya fue devuelto.");
            }
            if (dao.tieneMultaPendiente(em, idUsuario)) {
                throw new ReglaNegocioException("Tienes una multa pendiente; regularizala antes de renovar.");
            }
            boolean yaRenovado = prestamo.getRenovaciones() != null && prestamo.getRenovaciones() >= 1;
            if (!ReglasPrestamo.puedeRenovar(yaRenovado, hoy(), prestamo.getFechaVencimiento())) {
                throw new ReglaNegocioException(
                        "No se puede renovar: el prestamo ya fue renovado una vez o esta vencido.");
            }
            int dias = ReglasPrestamo.diasPrestamo(prestamo.getUsuario().getRol());
            prestamo.setFechaVencimiento(prestamo.getFechaVencimiento().plusDays(dias));
            prestamo.setRenovaciones(1);
            return null;
        });
    }

    /** RF-09. Marca la multa como pagada y, si no quedan otras, reactiva al usuario (RN-06). */
    public void pagarMulta(Integer idMulta) {
        enTransaccion(em -> {
            Multa multa = em.find(Multa.class, idMulta, JpaUtil.bloqueo());
            if (multa == null) {
                throw new ReglaNegocioException("La multa no existe.");
            }
            if (multa.getEstado() == EstadoMulta.PAGADA) {
                throw new ReglaNegocioException("Esta multa ya fue pagada.");
            }
            multa.setEstado(EstadoMulta.PAGADA);
            em.flush();
            Usuario usuario = multa.getPrestamo().getUsuario();
            if (!dao.tieneMultaPendiente(em, usuario.getId())
                    && usuario.getEstado() == EstadoUsuario.SUSPENDIDO) {
                usuario.setEstado(EstadoUsuario.ACTIVO);
            }
            return null;
        });
    }

    /**
     * Paso previo del panel del bibliotecario: comprueba las reglas de negocio sin registrar nada
     * y devuelve el resumen que se muestra antes de confirmar.
     */
    public VerificacionPrestamo verificar(String codigoUsuario, String codigoBarras) {
        if (vacio(codigoUsuario) || vacio(codigoBarras)) {
            throw new ReglaNegocioException("Ingrese el codigo del usuario y el codigo de barras del ejemplar.");
        }
        return lectura(em -> {
            Usuario usuario = dao.usuarioPorCodigo(em, codigoUsuario.trim())
                    .orElseThrow(() -> new ReglaNegocioException(
                            "No existe un usuario con el codigo " + codigoUsuario.trim() + "."));
            Ejemplar ejemplar = em.createQuery(
                    "select e from Ejemplar e join fetch e.libro where e.codigoBarras = :c", Ejemplar.class)
                    .setParameter("c", codigoBarras.trim())
                    .getResultStream()
                    .findFirst()
                    .orElseThrow(() -> new ReglaNegocioException(
                            "No existe un ejemplar con el codigo de barras " + codigoBarras.trim() + "."));

            int vigentes = (int) dao.contarPrestamosActivos(em, usuario.getId());
            boolean multa = dao.tieneMultaPendiente(em, usuario.getId());
            Optional<String> motivo = ReglasPrestamo.motivoRechazo(
                    usuario.getRol(), usuario.getEstado(), multa, vigentes);

            String problema = motivo.orElse(null);
            if (problema == null && ejemplar.getEstado() == EstadoEjemplar.RESERVADO) {
                boolean propia = reservaDAO.activaDeEjemplar(em, ejemplar.getId())
                        .filter(r -> r.getUsuario().getId().equals(usuario.getId()))
                        .isPresent();
                if (!propia) {
                    problema = "El ejemplar esta reservado por otro usuario.";
                }
            } else if (problema == null && ejemplar.getEstado() != EstadoEjemplar.DISPONIBLE) {
                problema = "El ejemplar no esta disponible (estado: " + ejemplar.getEstado().getValor() + ").";
            }
            return new VerificacionPrestamo(
                    usuario.getCodigo(), ejemplar.getCodigoBarras(), usuario.getNombreCompleto(),
                    usuario.getRolEtiqueta(), vigentes, ReglasPrestamo.limitePrestamos(usuario.getRol()),
                    !multa, ejemplar.getLibro().getTitulo(), ReglasPrestamo.diasPrestamo(usuario.getRol()),
                    problema == null, problema);
        });
    }

    /**
     * Solicitud de prestamo desde el detalle del libro: usa el ejemplar que el usuario tenia reservado
     * o, si no hay, el primero disponible, y aplica las mismas reglas que el mostrador.
     */
    public Prestamo solicitar(Integer idUsuario, Integer idLibro) {
        String[] datos = lectura(em -> {
            Usuario usuario = dao.usuarioPorId(em, idUsuario)
                    .orElseThrow(() -> new ReglaNegocioException("Usuario no encontrado."));
            String barras = reservaDAO.activasDeUsuario(em, idUsuario).stream()
                    .filter(r -> r.getEjemplar().getLibro().getId().equals(idLibro))
                    .map(r -> r.getEjemplar().getCodigoBarras())
                    .findFirst()
                    .orElseGet(() -> em.createQuery(
                            "select e.codigoBarras from Ejemplar e "
                          + "where e.libro.id = :l and e.estado = :d order by e.id", String.class)
                            .setParameter("l", idLibro)
                            .setParameter("d", EstadoEjemplar.DISPONIBLE)
                            .setMaxResults(1)
                            .getResultStream()
                            .findFirst()
                            .orElse(null));
            return new String[] {usuario.getCodigo(), barras};
        });
        if (datos[1] == null) {
            throw new ReglaNegocioException("No hay ejemplares disponibles de este libro.");
        }
        return registrar(datos[0], datos[1]);
    }

    /** Prestamos sin devolver del usuario (los devueltos no se muestran en Mis prestamos). */
    public List<Prestamo> prestamosSinDevolverDeUsuario(Integer idUsuario) {
        return lectura(em -> dao.deUsuario(em, idUsuario).stream()
                .filter(p -> p.getFechaDevolucion() == null)
                .toList());
    }

    // ---- Consultas para las vistas ----

    public List<Prestamo> prestamosActivos() {
        return lectura(dao::activos);
    }

    public List<Prestamo> prestamosDeUsuario(Integer idUsuario) {
        return lectura(em -> dao.deUsuario(em, idUsuario));
    }

    public List<Multa> multasPendientes() {
        return lectura(dao::multasPendientes);
    }

    public List<Multa> multasPendientesDeUsuario(Integer idUsuario) {
        return lectura(em -> dao.multasPendientesDeUsuario(em, idUsuario));
    }

    /** Estado actual del usuario en la base de datos (la sesion puede estar desactualizada). */
    public Optional<Usuario> usuarioActual(Integer idUsuario) {
        return lectura(em -> dao.usuarioPorId(em, idUsuario));
    }

    // ---- Utilidades ----

    private static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private <T> T enTransaccion(Function<EntityManager, T> trabajo) {
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            em.getTransaction().begin();
            T resultado = trabajo.apply(em);
            em.getTransaction().commit();
            return resultado;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    private <T> T lectura(Function<EntityManager, T> consulta) {
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            return consulta.apply(em);
        } finally {
            em.close();
        }
    }
}
