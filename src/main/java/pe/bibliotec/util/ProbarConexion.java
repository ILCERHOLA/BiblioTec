package pe.bibliotec.util;

import jakarta.persistence.EntityManager;

public class ProbarConexion {
    public static void main(String[] args) {
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            Object libros = em.createNativeQuery("SELECT COUNT(*) FROM libro")
                              .getSingleResult();
            System.out.println(">>> Conexión OK. Libros en la BD: " + libros);
        } finally {
            em.close();
            JpaUtil.cerrar();
        }
    }
}