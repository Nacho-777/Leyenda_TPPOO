package exceptions;

public class DorsalInvalidoException extends Exception {
    public DorsalInvalidoException(String mensaje) {
        super(mensaje);
    }

    public DorsalInvalidoException() {
        super("El dorsal debe estar entre 1 y 99.");
    }
}
