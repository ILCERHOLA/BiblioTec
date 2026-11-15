package pe.bibliotec.model;

public enum EstadoUsuario {
    ACTIVO("activo"),
    SUSPENDIDO("suspendido");

    private final String valor;

    EstadoUsuario(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoUsuario desdeValor(String valor) {
        for (EstadoUsuario e : values()) {
            if (e.valor.equals(valor)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Estado de usuario desconocido: " + valor);
    }
}
