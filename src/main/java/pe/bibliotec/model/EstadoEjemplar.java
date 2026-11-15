package pe.bibliotec.model;

public enum EstadoEjemplar {
    DISPONIBLE("disponible"),
    PRESTADO("prestado"),
    RESERVADO("reservado"),
    BAJA("baja");

    private final String valor;

    EstadoEjemplar(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoEjemplar desdeValor(String valor) {
        for (EstadoEjemplar e : values()) {
            if (e.valor.equals(valor)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Estado de ejemplar desconocido: " + valor);
    }
}