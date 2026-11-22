package pe.bibliotec.service;

import org.junit.jupiter.api.Test;
import pe.bibliotec.model.EstadoUsuario;
import pe.bibliotec.model.Rol;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReglasPrestamoTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 20);

    // RN-02
    @Test
    void limiteDePrestamosSegunRol() {
        assertEquals(3, ReglasPrestamo.limitePrestamos(Rol.ESTUDIANTE));
        assertEquals(5, ReglasPrestamo.limitePrestamos(Rol.DOCENTE));
    }

    // RN-03
    @Test
    void plazoDePrestamoSegunRol() {
        assertEquals(HOY.plusDays(7), ReglasPrestamo.fechaVencimiento(HOY, Rol.ESTUDIANTE));
        assertEquals(HOY.plusDays(15), ReglasPrestamo.fechaVencimiento(HOY, Rol.DOCENTE));
    }

    // RN-01
    @Test
    void usuarioActivoSinMultasPuedePedirPrestado() {
        assertTrue(ReglasPrestamo.motivoRechazo(Rol.ESTUDIANTE, EstadoUsuario.ACTIVO, false, 0).isEmpty());
    }

    @Test
    void usuarioSuspendidoNoPuedePedirPrestado() {
        assertTrue(ReglasPrestamo.motivoRechazo(Rol.DOCENTE, EstadoUsuario.SUSPENDIDO, false, 0).isPresent());
    }

    @Test
    void usuarioConMultaPendienteNoPuedePedirPrestado() {
        assertTrue(ReglasPrestamo.motivoRechazo(Rol.ESTUDIANTE, EstadoUsuario.ACTIVO, true, 0).isPresent());
    }

    // RN-02
    @Test
    void estudianteNoPuedeSuperarTresPrestamos() {
        assertTrue(ReglasPrestamo.motivoRechazo(Rol.ESTUDIANTE, EstadoUsuario.ACTIVO, false, 2).isEmpty());
        assertTrue(ReglasPrestamo.motivoRechazo(Rol.ESTUDIANTE, EstadoUsuario.ACTIVO, false, 3).isPresent());
    }

    @Test
    void docenteNoPuedeSuperarCincoPrestamos() {
        assertTrue(ReglasPrestamo.motivoRechazo(Rol.DOCENTE, EstadoUsuario.ACTIVO, false, 4).isEmpty());
        assertTrue(ReglasPrestamo.motivoRechazo(Rol.DOCENTE, EstadoUsuario.ACTIVO, false, 5).isPresent());
    }

    // RN-05
    @Test
    void sinRetrasoNoHayMulta() {
        assertEquals(new BigDecimal("0.00"), ReglasPrestamo.calcularMulta(HOY, HOY));
        assertEquals(new BigDecimal("0.00"), ReglasPrestamo.calcularMulta(HOY, HOY.minusDays(2)));
    }

    @Test
    void multaDeUnSolPorDiaDeRetraso() {
        assertEquals(new BigDecimal("1.00"), ReglasPrestamo.calcularMulta(HOY, HOY.plusDays(1)));
        assertEquals(new BigDecimal("4.00"), ReglasPrestamo.calcularMulta(HOY, HOY.plusDays(4)));
    }

    @Test
    void multaTieneTopeDeSesentaSoles() {
        assertEquals(new BigDecimal("60.00"), ReglasPrestamo.calcularMulta(HOY, HOY.plusDays(60)));
        assertEquals(new BigDecimal("60.00"), ReglasPrestamo.calcularMulta(HOY, HOY.plusDays(200)));
    }

    // RN-04
    @Test
    void sePuedeRenovarUnaVezSiNoEstaVencido() {
        assertTrue(ReglasPrestamo.puedeRenovar(false, HOY, HOY.plusDays(2)));
        assertTrue(ReglasPrestamo.puedeRenovar(false, HOY, HOY));
    }

    @Test
    void noSePuedeRenovarDosVecesNiVencido() {
        assertFalse(ReglasPrestamo.puedeRenovar(true, HOY, HOY.plusDays(2)));
        assertFalse(ReglasPrestamo.puedeRenovar(false, HOY.plusDays(1), HOY));
    }
}
