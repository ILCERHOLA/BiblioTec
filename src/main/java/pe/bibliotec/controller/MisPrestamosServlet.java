package pe.bibliotec.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.bibliotec.model.Multa;
import pe.bibliotec.model.Usuario;
import pe.bibliotec.service.PrestamoService;
import pe.bibliotec.service.ReglaNegocioException;
import pe.bibliotec.service.ReglasPrestamo;
import pe.bibliotec.service.ReservaService;

/** P4: prestamos del usuario, renovacion, multas pendientes y reservas activas. */
@WebServlet("/mis-prestamos")
public class MisPrestamosServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final PrestamoService servicio = new PrestamoService();
    private final ReservaService reservas = new ReservaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession ses = req.getSession();
        Usuario enSesion = (Usuario) ses.getAttribute("usuario");

        // La sesión puede tener el estado viejo (por ejemplo, tras pagar una multa): se actualiza.
        Usuario actual = servicio.usuarioActual(enSesion.getId()).orElse(enSesion);
        ses.setAttribute("usuario", actual);
        Flash.pasar(req);

        reservas.expirarVencidas();
        List<Multa> multas = servicio.multasPendientesDeUsuario(actual.getId());
        int dias = 0;
        BigDecimal total = BigDecimal.ZERO;
        for (Multa m : multas) {
            dias += m.getDiasRetraso();
            total = total.add(m.getMonto());
        }
        req.setAttribute("limite", ReglasPrestamo.limitePrestamos(actual.getRol()));
        req.setAttribute("prestamos", servicio.prestamosSinDevolverDeUsuario(actual.getId()));
        req.setAttribute("reservas", reservas.activasDeUsuario(actual.getId()));
        req.setAttribute("multas", multas);
        req.setAttribute("multasDias", dias);
        req.setAttribute("multasTotal", total);
        req.getRequestDispatcher("/WEB-INF/vistas/mis-prestamos.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Usuario usuario = Flash.usuario(req);
        String accion = req.getParameter("accion");
        try {
            Integer id = Integer.valueOf(req.getParameter("id").trim());
            if ("cancelarReserva".equals(accion)) {
                reservas.cancelar(id, usuario.getId());
                Flash.mensaje(req, "Reserva cancelada.");
            } else {
                servicio.renovar(id, usuario.getId());
                Flash.mensaje(req, "Préstamo renovado.");
            }
        } catch (ReglaNegocioException e) {
            Flash.error(req, e.getMessage());
        } catch (NumberFormatException | NullPointerException e) {
            Flash.error(req, "Dato inválido.");
        }
        resp.sendRedirect(req.getContextPath() + "/mis-prestamos");
    }
}
