package pe.bibliotec.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "libro")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_libro")
    private Integer id;

    @Column(name = "isbn", nullable = false, length = 17)
    private String isbn;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "anio_publicacion")
    private Integer anioPublicacion;

    @Column(name = "editorial", length = 80)
    private String editorial;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @ManyToMany
    @JoinTable(name = "libro_autor",
               joinColumns = @JoinColumn(name = "id_libro"),
               inverseJoinColumns = @JoinColumn(name = "id_autor"))
    private Set<Autor> autores = new LinkedHashSet<>();

    @OneToMany(mappedBy = "libro")
    private List<Ejemplar> ejemplares = new ArrayList<>();

    public Integer getId() { return id; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Integer getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(Integer anioPublicacion) { this.anioPublicacion = anioPublicacion; }
    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public Set<Autor> getAutores() { return autores; }
    public List<Ejemplar> getEjemplares() { return ejemplares; }
}