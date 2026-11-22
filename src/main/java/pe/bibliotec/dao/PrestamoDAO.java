package pe.bibliotec.dao;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import pe.bibliotec.model.Ejemplar;
import pe.bibliotec.model.EstadoMulta;
import pe.bibliotec.model.Multa;
import pe.bibliotec.model.Prestamo;
import pe.bibliotec.model.Usuario;

/**
 * Consultas de prestamos y multas. Cada metodo recibe el EntityManager
 * para que PrestamoService pueda agruparlos en una sola transaccion.
 */
public class PrestamoDAO {

    public Optional<Usuario> usuarioPorCodigo(EntityManager em, String codigo) {
        return em.createQuery("select u from Usuario u where u.codigo = :codigo", Usuario.class)
                .setParameter("codigo", codigo)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public Optional<Usuario> usuarioPorId(EntityManager em, Integer id) {
        return Optional.ofNullable(em.find(Usuario.class, id));
    }

    /** Bloquea la fila del ejemplar hasta el fin de la transaccion (RN-07). */
    public Optional<Ejemplar> ejemplarPorCodigoBloqueado(EntityManager em, String codigoBarras) {
        return em.createQuery("select e from Ejemplar e where e.codigoBarras = :codigo", Ejemplar.class)
                .setParameter("codigo", codigoBarras)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    /** Prestamos sin devolver (vigentes o vencidos) del usuario. */
    public long contarPrestamosActivos(EntityManager em, Integer idUsuario) {
        return em.createQuery(
                "select count(p) from Prestamo p "
              + "where p.usuario.id = :id and p.fechaDevolucion is null", Long.class)
                .setParameter("id", idUsuario)
                .getSingleResult();
    }

    public boolean tieneMultaPendiente(EntityManager em, Integer idUsuario) {
        Long total = em.createQuery(
                "select count(m) from Multa m "
              + "where m.prestamo.usuario.id = :id and m.estado = :estado", Long.class)
                .setParameter("id", idUsuario)
                .setParameter("estado", EstadoMulta.PENDIENTE)
                .getSingleResult();
        return total > 0;
    }

    public Optional<Multa> multaDePrestamo(EntityManager em, Integer idPrestamo) {
        return em.createQuery("select m from Multa m where m.prestamo.id = :id", Multa.class)
                .setParameter("id", idPrestamo)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    /** Prestamos sin devolver, con usuario, ejemplar y libro cargados para la vista. */
    public List<Prestamo> activos(EntityManager em) {
        return em.createQuery(
                "select p from Prestamo p "
              + "join fetch p.usuario "
              + "join fetch p.ejemplar e join fetch e.libro "
              + "where p.fechaDevolucion is null "
              + "order by p.fechaVencimiento, p.id", Prestamo.class)
                .getResultList();
    }

    /** Historial completo del usuario, del mas reciente al mas antiguo. */
    public List<Prestamo> deUsuario(EntityManager em, Integer idUsuario) {
        return em.createQuery(
                "select p from Prestamo p "
              + "join fetch p.usuario "
              + "join fetch p.ejemplar e join fetch e.libro "
              + "where p.usuario.id = :id "
              + "order by p.fechaPrestamo desc, p.id desc", Prestamo.class)
                .setParameter("id", idUsuario)
                .getResultList();
    }

    public List<Multa> multasPendientes(EntityManager em) {
        return em.createQuery(
                "select m from Multa m "
              + "join fetch m.prestamo p join fetch p.usuario "
              + "join fetch p.ejemplar e join fetch e.libro "
              + "where m.estado = :estado order by m.id", Multa.class)
                .setParameter("estado", EstadoMulta.PENDIENTE)
                .getResultList();
    }

    public List<Multa> multasPendientesDeUsuario(EntityManager em, Integer idUsuario) {
        return em.createQuery(
                "select m from Multa m "
              + "join fetch m.prestamo p join fetch p.usuario "
              + "join fetch p.ejemplar e join fetch e.libro "
              + "where m.estado = :estado and p.usuario.id = :id order by m.id", Multa.class)
                .setParameter("estado", EstadoMulta.PENDIENTE)
                .setParameter("id", idUsuario)
                .getResultList();
    }
}
