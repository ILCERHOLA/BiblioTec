package pe.bibliotec.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import pe.bibliotec.dao.PrestamoDAO;
import pe.bibliotec.dao.ReservaDAO;
import pe.bibliotec.model.Ejemplar;
import pe.bibliotec.model.EstadoEjemplar;
import pe.bibliotec.model.EstadoReserva;
import pe.bibliotec.model.Reserva;
import pe.bibliotec.model.Usuario;
import pe.bibliotec.util.JpaUtil;

/** Casos de uso de reservas: reservar (RF-08), cancelar y expirar (RN-08, RN-09). */
public class ReservaService {

    /** RN-08: la reserva aparta el ejemplar durante 48 horas. */
    public static final int HORAS_RESERVA = 48;
    private static final ZoneId ZONA = ZoneId.of("America/Lima");

    private final ReservaDAO dao = new ReservaDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    public static LocalDateTime ahora() {
        return LocalDateTime.now(ZONA);
    }

    /** Aparta un ejemplar disponible del libro para el usuario durante 48 horas. */
    public Reserva reservar(Integer idUsuario, Integer idLibro) {
        return enTransaccion(em -> {
            expirarVencidas(em);
            Usuario usuario = prestamoDAO.usuarioPorId(em, idUsuario)
                    .orElseThrow(() -> new ReglaNegocioException("Usuario no encontrado."));
            if (!usuario.isActivo()) {
                throw new ReglaNegocioException("Tu cuenta esta suspendida; no puedes reservar.");
            }
            if (prestamoDAO.tieneMultaPendiente(em, idUsuario)) {
                throw new ReglaNegocioException("Tienes una multa pendiente; regularizala antes de reservar.");
            }
            if (dao.tieneActivaDelLibro(em, idUsuario, idLibro)) {
                throw new ReglaNegocioException("Ya tienes una reserva activa de este libro.");
            }
            Ejemplar ejemplar = dao.primerDisponibleBloqueado(em, idLibro)
                    .orElseThrow(() -> new ReglaNegocioException("No hay ejemplares disponibles para reservar."));

            LocalDateTime ahora = ahora();
            Reserva reserva = new Reserva();
            reserva.setUsuario(usuario);
            reserva.setEjemplar(ejemplar);
            reserva.setFechaReserva(ahora);
            reserva.setFechaLimiteRecojo(ahora.plusHours(HORAS_RESERVA));
            reserva.setEstado(EstadoReserva.ACTIVA);
            ejemplar.setEstado(EstadoEjemplar.RESERVADO);
            em.persist(reserva);
            return reserva;
        });
    }

    public void cancelar(Integer idReserva, Integer idUsuario) {
        enTransaccion(em -> {
            Reserva reserva = em.find(Reserva.class, idReserva, LockModeType.PESSIMISTIC_WRITE);
            if (reserva == null || !reserva.getUsuario().getId().equals(idUsuario)) {
                throw new ReglaNegocioException("La reserva no existe o no te pertenece.");
            }
            if (reserva.getEstado() != EstadoReserva.ACTIVA) {
                throw new ReglaNegocioException("Esta reserva ya no esta activa.");
            }
            reserva.setEstado(EstadoReserva.CANCELADA);
            reserva.getEjemplar().setEstado(EstadoEjemplar.DISPONIBLE);
            return null;
        });
    }

    /**
     * RN-09: marca como expiradas las reservas cuyo plazo de recojo pasó y libera sus ejemplares.
     * Se ejecuta al consultar el catalogo, reservar o prestar (sin tarea programada).
     */
    public void expirarVencidas() {
        enTransaccion(em -> {
            expirarVencidas(em);
            return null;
        });
    }

    void expirarVencidas(EntityManager em) {
        for (Reserva r : dao.activasVencidas(em, ahora())) {
            r.setEstado(EstadoReserva.EXPIRADA);
            if (r.getEjemplar().getEstado() == EstadoEjemplar.RESERVADO) {
                r.getEjemplar().setEstado(EstadoEjemplar.DISPONIBLE);
            }
        }
    }

    public List<Reserva> activasDeUsuario(Integer idUsuario) {
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            return dao.activasDeUsuario(em, idUsuario);
        } finally {
            em.close();
        }
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
}
