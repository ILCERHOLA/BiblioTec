package pe.bibliotec.service;

/** Se lanza cuando una operacion incumple una regla de negocio; el mensaje es apto para mostrar al usuario. */
public class ReglaNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
