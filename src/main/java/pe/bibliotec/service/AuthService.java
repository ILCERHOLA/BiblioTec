package pe.bibliotec.service;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

import pe.bibliotec.dao.UsuarioDAO;
import pe.bibliotec.model.Usuario;

public class AuthService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Devuelve el usuario si el código y la clave son correctos (RN-11: BCrypt).
     * Si el código no existe o la clave es incorrecta, devuelve vacío en ambos casos,
     * para no revelar cuál de los dos falló.
     */
    public Optional<Usuario> autenticar(String codigo, String clave) {
        if (codigo == null || codigo.isBlank() || clave == null || clave.isEmpty()) {
            return Optional.empty();
        }
        return usuarioDAO.buscarPorCodigo(codigo.trim())
                .filter(u -> BCrypt.checkpw(clave, u.getClaveHash()));
    }
}