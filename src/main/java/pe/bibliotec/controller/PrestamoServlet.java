package pe.bibliotec.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.bibliotec.model.Multa;
import pe.bibliotec.model.Prestamo;
import pe.bibliotec.model.Rol;
import pe.bibliotec.model.Usuario;
import pe.bibliotec.service.PrestamoService;
import pe.bibliotec.service.ReglaNegocioException;

/** Panel del bibliotecario: registrar prestamos, registrar devoluciones y cobrar multas. */
@WebServlet("/prestamos")
public class PrestamoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final PrestamoService servicio = new PrestamoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!esPersonal(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        pasarMensajes(req);
        req.setAttribute("prestamos", servicio.prestamosActivos());
        req.setAttribute("multas", servicio.multasPendientes());
        req.getRequestDispatcher("/WEB-INF/vistas/prestamos.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!esPersonal(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        HttpSession ses = req.getSession();
        String accion = req.getParameter("accion");
        try {
            if ("registrar".equals(accion)) {
                Prestamo p = servicio.registrar(req.getParameter("codigoUsuario"),
                        req.getParameter("codigoBarras"));
                ses.setAttribute("flashMensaje",
                        "Prestamo registrado. Fecha de vencimiento: " + p.getFechaVencimiento() + ".");
            } else if ("devolver".equals(accion)) {
                Optional<Multa> multa = servicio.devolver(entero(req.getParameter("idPrestamo")));
                BigDecimal monto = multa.map(Multa::getMonto).orElse(null);
                ses.setAttribute("flashMensaje", monto == null
                        ? "Devolucion registrada."
                        : "Devolucion registrada con retraso. Multa generada: S/ " + monto
                          + ". El usuario queda suspendido hasta pagarla.");
            } else if ("pagarMulta".equals(accion)) {
                servicio.pagarMulta(entero(req.getParameter("idMulta")));
                ses.setAttribute("flashMensaje", "Multa pagada.");
            } else {
                ses.setAttribute("flashError", "Accion no reconocida.");
            }
        } catch (ReglaNegocioException e) {
            ses.setAttribute("flashError", e.getMessage());
        } catch (NumberFormatException e) {
            ses.setAttribute("flashError", "Dato invalido.");
        }
        resp.sendRedirect(req.getContextPath() + "/prestamos");
    }

    private static boolean esPersonal(HttpServletRequest req) {
        HttpSession ses = req.getSession(false);
        Usuario u = ses == null ? null : (Usuario) ses.getAttribute("usuario");
        return u != null && (u.getRol() == Rol.BIBLIOTECARIO || u.getRol() == Rol.ADMIN);
    }

    private static Integer entero(String texto) {
        return Integer.valueOf(texto == null ? "" : texto.trim());
    }

    /** Muestra una sola vez los mensajes guardados en la sesion tras una redireccion. */
    private static void pasarMensajes(HttpServletRequest req) {
        HttpSession ses = req.getSession();
        for (String clave : new String[] {"flashMensaje", "flashError"}) {
            Object valor = ses.getAttribute(clave);
            if (valor != null) {
                req.setAttribute(clave.substring(5).toLowerCase(), valor);
                ses.removeAttribute(clave);
            }
        }
    }
}
