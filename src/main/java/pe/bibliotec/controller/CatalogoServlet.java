package pe.bibliotec.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.bibliotec.service.CatalogoService;

/** P2: catalogo con busqueda por titulo, autor o ISBN y filtro por categoria. */
@WebServlet("/catalogo")
public class CatalogoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final CatalogoService servicio = new CatalogoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String q = req.getParameter("q");
        Integer categoria = enteroONulo(req.getParameter("categoria"));
        Flash.pasar(req);
        req.setAttribute("q", q == null ? "" : q);
        req.setAttribute("categoriaSel", categoria);
        req.setAttribute("categorias", servicio.categorias());
        req.setAttribute("libros", servicio.listar(q, categoria));
        req.getRequestDispatcher("/WEB-INF/vistas/catalogo.jsp").forward(req, resp);
    }

    static Integer enteroONulo(String texto) {
        try {
            return texto == null || texto.isBlank() ? null : Integer.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
