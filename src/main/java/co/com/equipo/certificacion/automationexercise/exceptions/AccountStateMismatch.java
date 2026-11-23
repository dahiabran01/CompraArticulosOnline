package co.com.equipo.certificacion.automationexercise.exceptions;

/**
 * Falla de negocio cuando el estado de autenticacion o registro de cuenta no es el esperado.
 */
public class AccountStateMismatch extends ShopExpectationError {
    public AccountStateMismatch(String message) {
        super(message);
    }

    public AccountStateMismatch(String message, Throwable cause) {
        super(message, cause);
    }
}

