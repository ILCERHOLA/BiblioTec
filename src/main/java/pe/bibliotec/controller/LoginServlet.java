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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final AuthService auth = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession ses = req.getSession(false);
        if (ses != null && ses.getAttribute("usuario") != null) {
            resp.sendRedirect(req.getContextPath() + "/catalogo");
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

        Optional<Usuario> usuario = auth.autenticar(codigo, clave);
        if (usuario.isEmpty()) {
            req.setAttribute("error", "Código o contraseña incorrectos.");
            req.setAttribute("codigo", codigo);
            req.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(req, resp);
            return;
        }

        // Sesión nueva al iniciar sesión (evita reutilizar una sesión anterior)
        HttpSession vieja = req.getSession(false);
        if (vieja != null) {
            vieja.invalidate();
        }
        HttpSession ses = req.getSession(true);
        ses.setAttribute("usuario", usuario.get());
        resp.sendRedirect(req.getContextPath() + "/catalogo");
    }
}