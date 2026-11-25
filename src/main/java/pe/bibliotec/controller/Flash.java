package pe.bibliotec.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import pe.bibliotec.model.Rol;
import pe.bibliotec.model.Usuario;

/** Utilidades comunes de los servlets: mensajes de una sola vez (patron PRG) y roles. */
final class Flash {

    private Flash() {
    }

    /** Pasa a la peticion los mensajes guardados en la sesion y los borra. */
    static void pasar(HttpServletRequest req) {
        HttpSession ses = req.getSession();
        for (String clave : new String[] {"flashMensaje", "flashError"}) {
            Object valor = ses.getAttribute(clave);
            if (valor != null) {
                req.setAttribute(clave.substring(5).toLowerCase(), valor);
                ses.removeAttribute(clave);
            }
        }
    }

    static void mensaje(HttpServletRequest req, String texto) {
        req.getSession().setAttribute("flashMensaje", texto);
    }

    static void error(HttpServletRequest req, String texto) {
        req.getSession().setAttribute("flashError", texto);
    }

    static Usuario usuario(HttpServletRequest req) {
        HttpSession ses = req.getSession(false);
        return ses == null ? null : (Usuario) ses.getAttribute("usuario");
    }

    static boolean esPersonal(Usuario u) {
        return u != null && (u.getRol() == Rol.BIBLIOTECARIO || u.getRol() == Rol.ADMIN);
    }
}
