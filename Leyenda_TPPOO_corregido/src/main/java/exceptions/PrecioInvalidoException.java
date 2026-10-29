package exceptions;

public class PrecioInvalidoException extends Exception {
    public PrecioInvalidoException(String mensaje) {
        super(mensaje);
    }

    public PrecioInvalidoException() {
        super("El precio no puede ser negativo.");
    }
}
