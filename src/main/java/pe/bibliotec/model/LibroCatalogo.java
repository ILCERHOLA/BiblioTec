package pe.bibliotec.model;

/** Fila del catalogo: lo que se muestra en pantalla, ya calculado. */
public class LibroCatalogo {

    private final Integer id;
    private final String titulo;
    private final String autores;
    private final String categoria;
    private final long ejemplares;
    private final long disponibles;

    public LibroCatalogo(Integer id, String titulo, String autores, String categoria,
                         long ejemplares, long disponibles) {
        this.id = id;
        this.titulo = titulo;
        this.autores = autores;
        this.categoria = categoria;
        this.ejemplares = ejemplares;
        this.disponibles = disponibles;
    }

    /** Clase CSS del color de portada (c1 a c6), segun el id del libro. */
    public static String claseCubierta(Integer idLibro) {
        int n = idLibro == null ? 0 : Math.floorMod(idLibro - 1, 6);
        return "c" + (n + 1);
    }

    public Integer getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutores() { return autores; }
    public String getCategoria() { return categoria; }
    public long getEjemplares() { return ejemplares; }
    public long getDisponibles() { return disponibles; }
    public String getClaseCubierta() { return claseCubierta(id); }
}
