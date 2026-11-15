package pe.bibliotec.model;

public enum Rol {
    ESTUDIANTE("estudiante"),
    DOCENTE("docente"),
    BIBLIOTECARIO("bibliotecario"),
    ADMIN("admin");

    private final String valor;

    Rol(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static Rol desdeValor(String valor) {
        for (Rol r : values()) {
            if (r.valor.equals(valor)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Rol desconocido: " + valor);
    }
}
