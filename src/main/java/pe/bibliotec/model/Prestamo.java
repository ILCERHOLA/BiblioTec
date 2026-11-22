package pe.bibliotec.model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

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
@Table(name = "prestamo")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestamo")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ejemplar")
    private Ejemplar ejemplar;

    @Column(name = "id_reserva")
    private Integer idReserva;

    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    @Column(name = "renovaciones", nullable = false)
    private Integer renovaciones = 0;

    @Convert(converter = EstadoPrestamoConverter.class)
    @Column(name = "estado", nullable = false)
    private EstadoPrestamo estado = EstadoPrestamo.VIGENTE;

    public Integer getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Ejemplar getEjemplar() { return ejemplar; }
    public void setEjemplar(Ejemplar ejemplar) { this.ejemplar = ejemplar; }
    public Integer getIdReserva() { return idReserva; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
    public Integer getRenovaciones() { return renovaciones; }
    public void setRenovaciones(Integer renovaciones) { this.renovaciones = renovaciones; }
    public EstadoPrestamo getEstado() { return estado; }
    public void setEstado(EstadoPrestamo estado) { this.estado = estado; }

    private static LocalDate hoy() {
        return LocalDate.now(ZoneId.of("America/Lima"));
    }

    /** Sin devolver y con la fecha de vencimiento ya pasada. */
    public boolean isVencido() {
        return fechaDevolucion == null && hoy().isAfter(fechaVencimiento);
    }

    /** Dias de retraso hasta hoy (0 si no esta vencido o ya se devolvio). */
    public long getDiasRetraso() {
        return isVencido() ? ChronoUnit.DAYS.between(fechaVencimiento, hoy()) : 0;
    }
}
