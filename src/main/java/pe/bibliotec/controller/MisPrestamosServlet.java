package pe.bibliotec.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.bibliotec.model.Usuario;
import pe.bibliotec.service.PrestamoService;
import pe.bibliotec.service.ReglaNegocioException;

/** Prestamos del usuario en sesion (RF-06) y renovacion (RF-07). */
@WebServlet("/mis-prestamos")
public class MisPrestamosServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final PrestamoService servicio = new PrestamoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession ses = req.getSession();
        Usuario enSesion = (Usuario) ses.getAttribute("usuario");

        // La sesion puede tener el estado viejo (por ejemplo, tras pagar una multa): se actualiza.
        Usuario actual = servicio.usuarioActual(enSesion.getId()).orElse(enSesion);
        ses.setAttribute("usuario", actual);

        for (String clave : new String[] {"flashMensaje", "flashError"}) {
            Object valor = ses.getAttribute(clave);
            if (valor != null) {
                req.setAttribute(clave.substring(5).toLowerCase(), valor);
                ses.removeAttribute(clave);
            }
        }
        req.setAttribute("prestamos", servicio.prestamosDeUsuario(actual.getId()));
        req.setAttribute("multas", servicio.multasPendientesDeUsuario(actual.getId()));
        req.getRequestDispatcher("/WEB-INF/vistas/mis-prestamos.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession ses = req.getSession();
        Usuario usuario = (Usuario) ses.getAttribute("usuario");
        try {
            Integer idPrestamo = Integer.valueOf(req.getParameter("idPrestamo").trim());
            servicio.renovar(idPrestamo, usuario.getId());
            ses.setAttribute("flashMensaje", "Prestamo renovado.");
        } catch (ReglaNegocioException e) {
            ses.setAttribute("flashError", e.getMessage());
        } catch (NumberFormatException | NullPointerException e) {
            ses.setAttribute("flashError", "Dato invalido.");
        }
        resp.sendRedirect(req.getContextPath() + "/mis-prestamos");
    }
}
