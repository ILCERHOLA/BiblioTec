package pe.bibliotec.model;

/** Resultado de comprobar las reglas de negocio antes de confirmar un prestamo (panel del bibliotecario). */
public class VerificacionPrestamo {

    private final String codigoUsuario;
    private final String codigoBarras;
    private final String nombreUsuario;
    private final String rol;
    private final int prestamosVigentes;
    private final int limite;
    private final boolean sinMultas;
    private final String tituloLibro;
    private final int plazoDias;
    private final boolean cumple;
    private final String motivo;

    public VerificacionPrestamo(String codigoUsuario, String codigoBarras, String nombreUsuario, String rol,
                                int prestamosVigentes, int limite, boolean sinMultas, String tituloLibro,
                                int plazoDias, boolean cumple, String motivo) {
        this.codigoUsuario = codigoUsuario;
        this.codigoBarras = codigoBarras;
        this.nombreUsuario = nombreUsuario;
        this.rol = rol;
        this.prestamosVigentes = prestamosVigentes;
        this.limite = limite;
        this.sinMultas = sinMultas;
        this.tituloLibro = tituloLibro;
        this.plazoDias = plazoDias;
        this.cumple = cumple;
        this.motivo = motivo;
    }

    public String getCodigoUsuario() { return codigoUsuario; }
    public String getCodigoBarras() { return codigoBarras; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getRol() { return rol; }
    public int getPrestamosVigentes() { return prestamosVigentes; }
    public int getLimite() { return limite; }
    public boolean isSinMultas() { return sinMultas; }
    public String getTituloLibro() { return tituloLibro; }
    public int getPlazoDias() { return plazoDias; }
    public boolean isCumple() { return cumple; }
    public String getMotivo() { return motivo; }
}
