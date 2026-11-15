package pe.bibliotec.dao;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import pe.bibliotec.model.Usuario;
import pe.bibliotec.util.JpaUtil;

public class UsuarioDAO {

    public Optional<Usuario> buscarPorCodigo(String codigo) {
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            return em.createQuery(
                    "select u from Usuario u where u.codigo = :codigo", Usuario.class)
                .setParameter("codigo", codigo)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
        } finally {
            em.close();
        }
    }
}