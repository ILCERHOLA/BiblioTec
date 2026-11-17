package pe.bibliotec.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.bibliotec.service.CatalogoService;

@WebServlet("/catalogo")
public class CatalogoServlet extends HttpServlet {

    private final CatalogoService servicio = new CatalogoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String q = req.getParameter("q");
        req.setAttribute("q", q == null ? "" : q);
        req.setAttribute("libros", servicio.listar(q));
        req.getRequestDispatcher("/WEB-INF/vistas/catalogo.jsp").forward(req, resp);
    }
}
