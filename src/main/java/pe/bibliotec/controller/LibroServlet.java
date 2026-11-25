package pe.bibliotec.controller;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.bibliotec.model.LibroDetalle;
import pe.bibliotec.model.Prestamo;
import pe.bibliotec.model.Reserva;
import pe.bibliotec.model.Usuario;
import pe.bibliotec.service.CatalogoService;
import pe.bibliotec.service.PrestamoService;
import pe.bibliotec.service.ReglaNegocioException;
import pe.bibliotec.service.ReglasPrestamo;
import pe.bibliotec.service.ReservaService;

/** P3: detalle del libro. Permite solicitar el prestamo (A) o reservar por 48 horas (B). */
@WebServlet("/libro")
public class LibroServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final CatalogoService catalogo = new CatalogoService();
    private final PrestamoService prestamos = new PrestamoService();
    private final ReservaService reservas = new ReservaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer id = CatalogoServlet.enteroONulo(req.getParameter("id"));
        Optional<LibroDetalle> libro = id == null ? Optional.empty() : catalogo.detalle(id);
        if (libro.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Usuario usuario = Flash.usuario(req);
        Flash.pasar(req);
        req.setAttribute("libro", libro.get());
        req.setAttribute("plazoDias", ReglasPrestamo.diasPrestamo(usuario.getRol()));
        req.getRequestDispatcher("/WEB-INF/vistas/libro.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Usuario usuario = Flash.usuario(req);
        Integer id = CatalogoServlet.enteroONulo(req.getParameter("id"));
        String accion = req.getParameter("accion");
        if (id == null || Flash.esPersonal(usuario)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            if ("prestamo".equals(accion)) {
                Prestamo p = prestamos.solicitar(usuario.getId(), id);
                Flash.mensaje(req, "Préstamo registrado. Vence el " + p.getFechaVencimientoTexto() + ".");
            } else if ("reserva".equals(accion)) {
                Reserva r = reservas.reservar(usuario.getId(), id);
                Flash.mensaje(req, "Reserva confirmada. Recoge el ejemplar hasta el " + r.getLimiteTexto()
                        + "; después la reserva expira.");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/mis-prestamos");
        } catch (ReglaNegocioException e) {
            Flash.error(req, e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/libro?id=" + id);
        }
    }
}
