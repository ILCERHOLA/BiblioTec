package pe.bibliotec.dao;

import java.util.List;

import jakarta.persistence.EntityManager;
import pe.bibliotec.model.EstadoEjemplar;
import pe.bibliotec.model.Libro;
import pe.bibliotec.util.JpaUtil;

public class LibroDAO {

    /** Catálogo: busca por título (vacío = todos), con categoría y autores cargados. */
    public List<Libro> buscar(String texto) {
        String patron = "%" + (texto == null ? "" : texto.trim().toLowerCase()) + "%";
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            return em.createQuery(
                    "select distinct l from Libro l "
                  + "join fetch l.categoria "
                  + "left join fetch l.autores "
                  + "where lower(l.titulo) like :patron "
                  + "order by l.titulo", Libro.class)
                .setParameter("patron", patron)
                .getResultList();
        } finally {
            em.close();
        }
    }

    public long contarEjemplares(Integer idLibro) {
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            return em.createQuery(
                    "select count(e) from Ejemplar e where e.libro.id = :id", Long.class)
                .setParameter("id", idLibro)
                .getSingleResult();
        } finally {
            em.close();
        }
    }

    public long contarDisponibles(Integer idLibro) {
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            return em.createQuery(
                    "select count(e) from Ejemplar e "
                  + "where e.libro.id = :id and e.estado = :estado", Long.class)
                .setParameter("id", idLibro)
                .setParameter("estado", EstadoEjemplar.DISPONIBLE)
                .getSingleResult();
        } finally {
            em.close();
        }
    }
}
