package co.com.equipo.certificacion.automationexercise.exceptions;

/**
 * Falla de negocio cuando direcciones, productos o totales en checkout no coinciden.
 */
public class CheckoutDataMismatch extends ShopExpectationError {
    public CheckoutDataMismatch(String message) {
        super(message);
    }

    public CheckoutDataMismatch(String message, Throwable cause) {
        super(message, cause);
    }
}

