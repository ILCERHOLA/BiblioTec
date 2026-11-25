package pe.bibliotec.dao;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import pe.bibliotec.model.Categoria;
import pe.bibliotec.model.EstadoEjemplar;
import pe.bibliotec.model.Libro;
import pe.bibliotec.util.JpaUtil;

public class LibroDAO {

    /** Busca solo por texto (usado por la clase de prueba ProbarCatalogo). */
    public List<Libro> buscar(String texto) {
        return buscar(texto, null);
    }

    /**
     * Catalogo: busca por titulo, autor o ISBN (vacio = todos) y, opcionalmente,
     * filtra por categoria. Carga categoria y autores para la vista.
     */
    public List<Libro> buscar(String texto, Integer idCategoria) {
        String patron = "%" + (texto == null ? "" : texto.trim().toLowerCase()) + "%";
        String jpql = "select distinct l from Libro l "
                + "join fetch l.categoria c "
                + "left join fetch l.autores "
                + "where (lower(l.titulo) like :patron "
                + "   or lower(l.isbn) like :patron "
                + "   or exists (select 1 from l.autores a where lower(a.nombreCompleto) like :patron)) "
                + (idCategoria == null ? "" : "and c.id = :idCategoria ")
                + "order by l.id";
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            TypedQuery<Libro> consulta = em.createQuery(jpql, Libro.class)
                .setParameter("patron", patron);
            if (idCategoria != null) {
                consulta.setParameter("idCategoria", idCategoria);
            }
            return consulta.getResultList();
        } finally {
            em.close();
        }
    }

    /** Libro con categoria y autores cargados. */
    public Optional<Libro> porId(Integer idLibro) {
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            return em.createQuery(
                    "select distinct l from Libro l "
                  + "join fetch l.categoria "
                  + "left join fetch l.autores "
                  + "where l.id = :id", Libro.class)
                .setParameter("id", idLibro)
                .getResultStream()
                .findFirst();
        } finally {
            em.close();
        }
    }

    public List<Categoria> categorias() {
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            return em.createQuery("select c from Categoria c order by c.nombre", Categoria.class)
                .getResultList();
        } finally {
            em.close();
        }
    }

    /** Ubicacion del primer ejemplar del libro (todos comparten estante). */
    public String ubicacion(Integer idLibro) {
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            return em.createQuery(
                    "select e.ubicacion from Ejemplar e where e.libro.id = :id order by e.id", String.class)
                .setParameter("id", idLibro)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse("");
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
