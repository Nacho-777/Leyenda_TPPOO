package exceptions;

public class OvrInvalidoException extends Exception {
    public OvrInvalidoException(String mensaje) {
        super(mensaje);
    }

    public OvrInvalidoException() {
        super("El OVR debe estar entre 1 y 99.");
    }
}
