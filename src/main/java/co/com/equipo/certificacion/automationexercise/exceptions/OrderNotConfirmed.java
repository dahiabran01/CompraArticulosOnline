package co.com.equipo.certificacion.automationexercise.exceptions;

/**
 * Falla de negocio cuando la tienda no confirma el pedido tras intentar el pago.
 */
public class OrderNotConfirmed extends ShopExpectationError {
    public OrderNotConfirmed(String message) {
        super(message);
    }

    public OrderNotConfirmed(String message, Throwable cause) {
        super(message, cause);
    }
}

