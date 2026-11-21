package co.com.equipo.certificacion.automationexercise.exceptions;

/**
 * Excepcion base de negocio cuando el sitio responde pero incumple un criterio de aceptacion.
 */
public class ShopExpectationError extends AssertionError {
    public ShopExpectationError(String message) {
        super(message);
    }

    public ShopExpectationError(String message, Throwable cause) {
        super(message, cause);
    }
}

