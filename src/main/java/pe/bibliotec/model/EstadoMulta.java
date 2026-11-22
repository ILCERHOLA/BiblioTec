package pe.bibliotec.model;

public enum EstadoMulta {
    PENDIENTE("pendiente"),
    PAGADA("pagada");

    private final String valor;

    EstadoMulta(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoMulta desdeValor(String valor) {
        for (EstadoMulta e : values()) {
            if (e.valor.equals(valor)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Estado de multa desconocido: " + valor);
    }
}
