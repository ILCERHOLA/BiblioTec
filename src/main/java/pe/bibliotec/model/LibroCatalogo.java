package pe.bibliotec.model;

/** Fila del catálogo: lo que se muestra en pantalla, ya calculado. */
public class LibroCatalogo {

    private final String titulo;
    private final String autores;
    private final String categoria;
    private final long ejemplares;
    private final long disponibles;

    public LibroCatalogo(String titulo, String autores, String categoria,
                         long ejemplares, long disponibles) {
        this.titulo = titulo;
        this.autores = autores;
        this.categoria = categoria;
        this.ejemplares = ejemplares;
        this.disponibles = disponibles;
    }

    public String getTitulo() { return titulo; }
    public String getAutores() { return autores; }
    public String getCategoria() { return categoria; }
    public long getEjemplares() { return ejemplares; }
    public long getDisponibles() { return disponibles; }
}