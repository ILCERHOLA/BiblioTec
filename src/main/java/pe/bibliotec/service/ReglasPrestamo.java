package pe.bibliotec.service;

import pe.bibliotec.model.EstadoUsuario;
import pe.bibliotec.model.Rol;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Reglas de negocio de prestamos, multas y renovaciones (Evidencia 1).
 * Clase sin acceso a base de datos: se puede probar con JUnit.
 *
 * RN-01 solo usuarios activos y sin multas pendientes pueden pedir prestado.
 * RN-02 maximo de prestamos simultaneos: 3 estudiantes, 5 docentes.
 * RN-03 plazo de prestamo: 7 dias estudiantes, 15 docentes.
 * RN-04 una sola renovacion, y no si el prestamo esta vencido.
 * RN-05 multa de S/ 1.00 por dia de retraso, con tope de S/ 60.00.
 */
public final class ReglasPrestamo {

    public static final BigDecimal MULTA_POR_DIA = new BigDecimal("1.00");
    public static final BigDecimal MULTA_MAXIMA = new BigDecimal("60.00");

    private ReglasPrestamo() {
    }

    /** RN-02. Los bibliotecarios no piden libros prestados. */
    public static int limitePrestamos(Rol rol) {
        return switch (rol) {
            case ESTUDIANTE -> 3;
            case DOCENTE -> 5;
            default -> 0;
        };
    }

    /** RN-03. */
    public static int diasPrestamo(Rol rol) {
        return switch (rol) {
            case ESTUDIANTE -> 7;
            case DOCENTE -> 15;
            default -> 0;
        };
    }

    public static LocalDate fechaVencimiento(LocalDate inicio, Rol rol) {
        return inicio.plusDays(diasPrestamo(rol));
    }

    /**
     * RN-01 y RN-02. Devuelve el motivo del rechazo, o vacio si el prestamo es valido.
     */
    public static Optional<String> motivoRechazo(Rol rol, EstadoUsuario estado,
                                                 boolean tieneMultaPendiente, int prestamosActivos) {
        if (estado != EstadoUsuario.ACTIVO) {
            return Optional.of("El usuario no esta activo.");
        }
        if (tieneMultaPendiente) {
            return Optional.of("El usuario tiene una multa pendiente.");
        }
        int limite = limitePrestamos(rol);
        if (limite == 0) {
            return Optional.of("Este rol no puede solicitar prestamos.");
        }
        if (prestamosActivos >= limite) {
            return Optional.of("Alcanzo el maximo de " + limite + " prestamos simultaneos.");
        }
        return Optional.empty();
    }

    /** RN-05. Dias de retraso x S/ 1.00, con tope de S/ 60.00. Sin retraso no hay multa. */
    public static BigDecimal calcularMulta(LocalDate vencimiento, LocalDate devolucion) {
        long diasRetraso = ChronoUnit.DAYS.between(vencimiento, devolucion);
        if (diasRetraso <= 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        BigDecimal monto = MULTA_POR_DIA.multiply(BigDecimal.valueOf(diasRetraso));
        return monto.min(MULTA_MAXIMA).setScale(2);
    }

    /** RN-04. Una renovacion como maximo y solo si el prestamo no esta vencido. */
    public static boolean puedeRenovar(boolean yaRenovado, LocalDate hoy, LocalDate vencimiento) {
        return !yaRenovado && !hoy.isAfter(vencimiento);
    }
}
