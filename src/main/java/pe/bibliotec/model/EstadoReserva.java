package pe.bibliotec.model;

public enum EstadoReserva {
    ACTIVA("activa"),
    ATENDIDA("atendida"),
    EXPIRADA("expirada"),
    CANCELADA("cancelada");

    private final String valor;

    EstadoReserva(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoReserva desdeValor(String valor) {
        for (EstadoReserva e : values()) {
            if (e.valor.equals(valor)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Estado de reserva desconocido: " + valor);
    }
}
