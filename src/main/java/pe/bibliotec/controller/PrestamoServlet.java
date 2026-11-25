package pe.bibliotec.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.bibliotec.model.Multa;
import pe.bibliotec.model.Prestamo;
import pe.bibliotec.model.VerificacionPrestamo;
import pe.bibliotec.service.PrestamoService;
import pe.bibliotec.service.ReglaNegocioException;

/**
 * P5: panel del bibliotecario. Registrar préstamo (con verificación previa de las reglas),
 * registrar devolución y cobrar multas.
 */
@WebServlet("/prestamos")
public class PrestamoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final PrestamoService servicio = new PrestamoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!Flash.esPersonal(Flash.usuario(req))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        Flash.pasar(req);
        String vista = req.getParameter("vista");
        if (!"devolucion".equals(vista) && !"multas".equals(vista)) {
            vista = "prestamo";
        }
        req.setAttribute("vista", vista);
        if ("devolucion".equals(vista)) {
            req.setAttribute("prestamos", servicio.prestamosActivos());
        } else if ("multas".equals(vista)) {
            req.setAttribute("multas", servicio.multasPendientes());
        }
        req.getRequestDispatcher("/WEB-INF/vistas/prestamos.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!Flash.esPersonal(Flash.usuario(req))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");
        String vista = "prestamo";
        try {
            if ("verificar".equals(accion)) {
                // Paso 1: comprueba las reglas y muestra el resumen sin registrar nada.
                String codigoUsuario = req.getParameter("codigoUsuario");
                String codigoBarras = req.getParameter("codigoBarras");
                req.setAttribute("codigoUsuario", codigoUsuario);
                req.setAttribute("codigoBarras", codigoBarras);
                try {
                    VerificacionPrestamo v = servicio.verificar(codigoUsuario, codigoBarras);
                    req.setAttribute("verificacion", v);
                } catch (ReglaNegocioException e) {
                    req.setAttribute("error", e.getMessage());
                }
                req.setAttribute("vista", "prestamo");
                req.getRequestDispatcher("/WEB-INF/vistas/prestamos.jsp").forward(req, resp);
                return;
            } else if ("registrar".equals(accion)) {
                Prestamo p = servicio.registrar(req.getParameter("codigoUsuario"),
                        req.getParameter("codigoBarras"));
                Flash.mensaje(req, "Préstamo registrado. Vence el " + p.getFechaVencimientoTexto() + ".");
            } else if ("devolver".equals(accion)) {
                vista = "devolucion";
                Optional<Multa> multa = servicio.devolver(entero(req.getParameter("idPrestamo")));
                BigDecimal monto = multa.map(Multa::getMonto).orElse(null);
                Flash.mensaje(req, monto == null
                        ? "Devolución registrada."
                        : "Devolución registrada con retraso. Multa generada: S/ " + monto
                          + ". El usuario queda suspendido hasta pagarla.");
            } else if ("pagarMulta".equals(accion)) {
                vista = "multas";
                servicio.pagarMulta(entero(req.getParameter("idMulta")));
                Flash.mensaje(req, "Multa pagada.");
            } else {
                Flash.error(req, "Acción no reconocida.");
            }
        } catch (ReglaNegocioException e) {
            Flash.error(req, e.getMessage());
        } catch (NumberFormatException e) {
            Flash.error(req, "Dato inválido.");
        }
        resp.sendRedirect(req.getContextPath() + "/prestamos?vista=" + vista);
    }

    private static Integer entero(String texto) {
        return Integer.valueOf(texto == null ? "" : texto.trim());
    }
}
