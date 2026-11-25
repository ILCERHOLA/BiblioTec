package pe.bibliotec.controller;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.bibliotec.model.Usuario;
import pe.bibliotec.service.AuthService;

/** P1: inicio de sesion. Segun el rol redirige al catalogo (A) o al panel del bibliotecario (B). */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final AuthService auth = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Usuario enSesion = Flash.usuario(req);
        if (enSesion != null) {
            resp.sendRedirect(req.getContextPath() + destino(enSesion));
            return;
        }
        req.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String codigo = req.getParameter("codigo");
        String clave = req.getParameter("clave");
        String perfil = req.getParameter("perfil");

        Optional<Usuario> usuario = auth.autenticar(codigo, clave);
        if (usuario.isEmpty()) {
            rechazar(req, resp, codigo, "Código o contraseña incorrectos.");
            return;
        }
        // Cada boton de la pantalla solo admite el tipo de usuario que dice.
        boolean personal = Flash.esPersonal(usuario.get());
        if ("bibliotecario".equals(perfil) && !personal) {
            rechazar(req, resp, codigo, "Esta cuenta no es de bibliotecario. Usa «Ingresar como estudiante».");
            return;
        }
        if ("estudiante".equals(perfil) && personal) {
            rechazar(req, resp, codigo, "Esta cuenta es de bibliotecario. Usa «Ingresar como bibliotecario».");
            return;
        }

        // Sesión nueva al iniciar sesión (evita reutilizar una sesión anterior)
        HttpSession vieja = req.getSession(false);
        if (vieja != null) {
            vieja.invalidate();
        }
        HttpSession ses = req.getSession(true);
        ses.setAttribute("usuario", usuario.get());
        resp.sendRedirect(req.getContextPath() + destino(usuario.get()));
    }

    private static String destino(Usuario u) {
        return Flash.esPersonal(u) ? "/prestamos" : "/catalogo";
    }

    private static void rechazar(HttpServletRequest req, HttpServletResponse resp, String codigo, String mensaje)
            throws ServletException, IOException {
        req.setAttribute("error", mensaje);
        req.setAttribute("codigo", codigo);
        req.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(req, resp);
    }
}
