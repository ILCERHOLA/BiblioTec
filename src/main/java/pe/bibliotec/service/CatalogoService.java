package pe.bibliotec.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pe.bibliotec.dao.LibroDAO;
import pe.bibliotec.model.Autor;
import pe.bibliotec.model.Categoria;
import pe.bibliotec.model.Libro;
import pe.bibliotec.model.LibroCatalogo;
import pe.bibliotec.model.LibroDetalle;

public class CatalogoService {

    private final LibroDAO libroDAO = new LibroDAO();
    private final ReservaService reservas = new ReservaService();

    /** Busca por titulo, autor o ISBN y filtra por categoria (null = todas). */
    public List<LibroCatalogo> listar(String texto, Integer idCategoria) {
        reservas.expirarVencidas(); // RN-09: libera ejemplares de reservas vencidas
        List<LibroCatalogo> filas = new ArrayList<>();
        for (Libro l : libroDAO.buscar(texto, idCategoria)) {
            filas.add(new LibroCatalogo(
                    l.getId(),
                    l.getTitulo(),
                    autoresCorto(l),
                    l.getCategoria().getNombre(),
                    libroDAO.contarEjemplares(l.getId()),
                    libroDAO.contarDisponibles(l.getId())));
        }
        return filas;
    }

    public List<Categoria> categorias() {
        return libroDAO.categorias();
    }

    public Optional<LibroDetalle> detalle(Integer idLibro) {
        reservas.expirarVencidas();
        return libroDAO.porId(idLibro).map(l -> new LibroDetalle(
                l.getId(),
                l.getTitulo(),
                autores(l),
                l.getCategoria().getNombre(),
                l.getEditorial(),
                l.getAnioPublicacion(),
                l.getIsbn(),
                libroDAO.ubicacion(l.getId()),
                libroDAO.contarEjemplares(l.getId()),
                libroDAO.contarDisponibles(l.getId())));
    }

    private static String autores(Libro l) {
        return l.getAutores().stream()
                .map(Autor::getNombreCompleto)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    /** Para las tarjetas: el primer autor y "y otros" si hay mas. */
    private static String autoresCorto(Libro l) {
        List<String> nombres = l.getAutores().stream()
                .map(Autor::getNombreCompleto)
                .sorted()
                .toList();
        if (nombres.isEmpty()) {
            return "";
        }
        return nombres.size() > 1 ? nombres.get(0) + " y otros" : nombres.get(0);
    }
}
