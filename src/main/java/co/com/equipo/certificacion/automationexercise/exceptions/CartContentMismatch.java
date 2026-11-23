package co.com.equipo.certificacion.automationexercise.exceptions;

/**
 * Falla de negocio cuando el contenido, cantidades o disponibilidad del carrito no coinciden.
 */
public class CartContentMismatch extends ShopExpectationError {
    public CartContentMismatch(String message) {
        super(message);
    }

    public CartContentMismatch(String message, Throwable cause) {
        super(message, cause);
    }
}

