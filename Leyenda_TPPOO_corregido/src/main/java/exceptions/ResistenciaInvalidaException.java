package exceptions;

public class ResistenciaInvalidaException extends Exception {
    public ResistenciaInvalidaException(String mensaje) {
        super(mensaje);
    }

    public ResistenciaInvalidaException() {
        super("La resistencia debe estar entre 1 y 99.");
    }
}
