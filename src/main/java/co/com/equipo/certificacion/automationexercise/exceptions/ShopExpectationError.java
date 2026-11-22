package co.com.equipo.certificacion.automationexercise.exceptions;

/**
 * Excepcion base de negocio cuando el sitio responde pero incumple un criterio de aceptacion.
 *
 * <p>Todo mensaje pasado a {@code orComplainWith} debe iniciar con el prefijo del criterio
 * ({@code AC##:} o {@code AC##/AC##:}), ya que Serenity 4.1.3 propaga via {@code ErrorTally}
 * un {@code AssertionError} plano hacia Cucumber y {@code FailureDiagnosis} reconoce ese
 * prefijo para clasificar el fallo en consola como {@code REGLA_DE_NEGOCIO}.
 */
public class ShopExpectationError extends AssertionError {
    public ShopExpectationError(String message) {
        super(message);
    }

    public ShopExpectationError(String message, Throwable cause) {
        super(message, cause);
    }
}

