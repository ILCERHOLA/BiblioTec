package pe.bibliotec.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ejemplar")
public class Ejemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ejemplar")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_libro")
    private Libro libro;

    @Column(name = "codigo_barras", nullable = false, length = 20)
    private String codigoBarras;

    @Column(name = "ubicacion", length = 40)
    private String ubicacion;

    @Convert(converter = EstadoEjemplarConverter.class)
    @Column(name = "estado", nullable = false)
    private EstadoEjemplar estado = EstadoEjemplar.DISPONIBLE;

    public Integer getId() { return id; }
    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public EstadoEjemplar getEstado() { return estado; }
    public void setEstado(EstadoEjemplar estado) { this.estado = estado; }
}