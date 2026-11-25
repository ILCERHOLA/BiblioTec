package pe.bibliotec.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import pe.bibliotec.model.Ejemplar;
import pe.bibliotec.model.EstadoEjemplar;
import pe.bibliotec.model.EstadoReserva;
import pe.bibliotec.model.Reserva;
import pe.bibliotec.util.JpaUtil;

/** Consultas de reservas. Reciben el EntityManager para agruparse en una transaccion. */
public class ReservaDAO {

    /** Reservas activas cuyo plazo de recojo ya paso (RN-09). */
    public List<Reserva> activasVencidas(EntityManager em, LocalDateTime ahora) {
        return em.createQuery(
                "select r from Reserva r join fetch r.ejemplar "
              + "where r.estado = :estado and r.fechaLimiteRecojo < :ahora", Reserva.class)
                .setParameter("estado", EstadoReserva.ACTIVA)
                .setParameter("ahora", ahora)
                .setLockMode(JpaUtil.bloqueo())
                .getResultList();
    }

    /** Reserva activa de un ejemplar concreto, si la hay. */
    public Optional<Reserva> activaDeEjemplar(EntityManager em, Integer idEjemplar) {
        return em.createQuery(
                "select r from Reserva r join fetch r.usuario "
              + "where r.ejemplar.id = :id and r.estado = :estado", Reserva.class)
                .setParameter("id", idEjemplar)
                .setParameter("estado", EstadoReserva.ACTIVA)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public boolean tieneActivaDelLibro(EntityManager em, Integer idUsuario, Integer idLibro) {
        Long total = em.createQuery(
                "select count(r) from Reserva r "
              + "where r.usuario.id = :u and r.ejemplar.libro.id = :l and r.estado = :estado", Long.class)
                .setParameter("u", idUsuario)
                .setParameter("l", idLibro)
                .setParameter("estado", EstadoReserva.ACTIVA)
                .getSingleResult();
        return total > 0;
    }

    public long contarActivas(EntityManager em, Integer idUsuario) {
        return em.createQuery(
                "select count(r) from Reserva r where r.usuario.id = :u and r.estado = :estado", Long.class)
                .setParameter("u", idUsuario)
                .setParameter("estado", EstadoReserva.ACTIVA)
                .getSingleResult();
    }

    /** Primer ejemplar disponible del libro, bloqueado hasta el fin de la transaccion. */
    public Optional<Ejemplar> primerDisponibleBloqueado(EntityManager em, Integer idLibro) {
        return em.createQuery(
                "select e from Ejemplar e where e.libro.id = :l and e.estado = :estado order by e.id", Ejemplar.class)
                .setParameter("l", idLibro)
                .setParameter("estado", EstadoEjemplar.DISPONIBLE)
                .setLockMode(JpaUtil.bloqueo())
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    /** Reservas activas del usuario con ejemplar y libro cargados. */
    public List<Reserva> activasDeUsuario(EntityManager em, Integer idUsuario) {
        return em.createQuery(
                "select r from Reserva r join fetch r.usuario "
              + "join fetch r.ejemplar e join fetch e.libro "
              + "where r.usuario.id = :u and r.estado = :estado order by r.fechaLimiteRecojo", Reserva.class)
                .setParameter("u", idUsuario)
                .setParameter("estado", EstadoReserva.ACTIVA)
                .getResultList();
    }
}
