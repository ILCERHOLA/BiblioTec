package pe.bibliotec.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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
@Table(name = "multa")
public class Multa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_multa")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_prestamo")
    private Prestamo prestamo;

    @Column(name = "dias_retraso", nullable = false)
    private Integer diasRetraso;

    @Column(name = "monto", nullable = false, precision = 8, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDate fechaGeneracion;

    @Convert(converter = EstadoMultaConverter.class)
    @Column(name = "estado", nullable = false)
    private EstadoMulta estado = EstadoMulta.PENDIENTE;

    public Integer getId() { return id; }
    public Prestamo getPrestamo() { return prestamo; }
    public void setPrestamo(Prestamo prestamo) { this.prestamo = prestamo; }
    public Integer getDiasRetraso() { return diasRetraso; }
    public void setDiasRetraso(Integer diasRetraso) { this.diasRetraso = diasRetraso; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public LocalDate getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDate fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    public EstadoMulta getEstado() { return estado; }
    public void setEstado(EstadoMulta estado) { this.estado = estado; }
}
