package pe.bibliotec.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pe.bibliotec.dao.LibroDAO;
import pe.bibliotec.model.Autor;
import pe.bibliotec.model.Libro;
import pe.bibliotec.model.LibroCatalogo;

public class CatalogoService {

    private final LibroDAO libroDAO = new LibroDAO();

    public List<LibroCatalogo> listar(String texto) {
        List<LibroCatalogo> filas = new ArrayList<>();
        for (Libro l : libroDAO.buscar(texto)) {
            String autores = l.getAutores().stream()
                    .map(Autor::getNombreCompleto)
                    .sorted()
                    .collect(Collectors.joining(", "));
            filas.add(new LibroCatalogo(
                    l.getTitulo(),
                    autores,
                    l.getCategoria().getNombre(),
                    libroDAO.contarEjemplares(l.getId()),
                    libroDAO.contarDisponibles(l.getId())));
        }
        return filas;
    }
}
