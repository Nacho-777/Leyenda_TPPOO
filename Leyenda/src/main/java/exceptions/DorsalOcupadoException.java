package exceptions;

public class DorsalOcupadoException extends Exception {
    public DorsalOcupadoException(String mensaje) {
        super(mensaje);
    }

    public DorsalOcupadoException() {
        super("El dorsal debe estar entre 1 y 99 y no estar ocupado.");
    }
}
