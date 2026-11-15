package pe.bibliotec.util;

import java.util.stream.Collectors;

import pe.bibliotec.dao.LibroDAO;
import pe.bibliotec.model.Autor;
import pe.bibliotec.model.Libro;

public class ProbarCatalogo {
    public static void main(String[] args) {
        LibroDAO dao = new LibroDAO();
        try {
            for (Libro l : dao.buscar("")) {
                String autores = l.getAutores().stream()
                        .map(Autor::getNombreCompleto)
                        .collect(Collectors.joining(", "));
                System.out.println(">>> " + l.getTitulo()
                        + " | " + l.getCategoria().getNombre()
                        + " | " + autores
                        + " | ejemplares: " + dao.contarEjemplares(l.getId())
                        + ", disponibles: " + dao.contarDisponibles(l.getId()));
            }
        } finally {
            JpaUtil.cerrar();
        }
    }
}
