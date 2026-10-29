package exceptions;

public class EdadInvalidaException extends Exception {
    public EdadInvalidaException(String mensaje) {
        super(mensaje);
    }

    public EdadInvalidaException() {
        super("La edad debe estar entre 16 y 40 años.");
    }
}
