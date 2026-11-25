package pe.bibliotec.model;

/** Datos del libro para la pantalla de detalle. */
public class LibroDetalle {

    private final Integer id;
    private final String titulo;
    private final String autores;
    private final String categoria;
    private final String editorial;
    private final Integer anio;
    private final String isbn;
    private final String ubicacion;
    private final long ejemplares;
    private final long disponibles;

    public LibroDetalle(Integer id, String titulo, String autores, String categoria, String editorial,
                        Integer anio, String isbn, String ubicacion, long ejemplares, long disponibles) {
        this.id = id;
        this.titulo = titulo;
        this.autores = autores;
        this.categoria = categoria;
        this.editorial = editorial;
        this.anio = anio;
        this.isbn = isbn;
        this.ubicacion = ubicacion;
        this.ejemplares = ejemplares;
        this.disponibles = disponibles;
    }

    public Integer getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutores() { return autores; }
    public String getCategoria() { return categoria; }
    public String getEditorial() { return editorial; }
    public Integer getAnio() { return anio; }
    public String getIsbn() { return isbn; }
    public String getUbicacion() { return ubicacion; }
    public long getEjemplares() { return ejemplares; }
    public long getDisponibles() { return disponibles; }
    public String getClaseCubierta() { return LibroCatalogo.claseCubierta(id); }
}
