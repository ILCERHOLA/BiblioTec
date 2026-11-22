package pe.bibliotec.model;

public enum EstadoPrestamo {
    VIGENTE("vigente"),
    DEVUELTO("devuelto"),
    VENCIDO("vencido");

    private final String valor;

    EstadoPrestamo(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoPrestamo desdeValor(String valor) {
        for (EstadoPrestamo e : values()) {
            if (e.valor.equals(valor)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Estado de prestamo desconocido: " + valor);
    }
}
