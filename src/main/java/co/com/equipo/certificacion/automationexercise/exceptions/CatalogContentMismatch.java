package co.com.equipo.certificacion.automationexercise.exceptions;

/**
 * Falla de negocio cuando los resultados de busqueda o ficha de catalogo no coinciden.
 */
public class CatalogContentMismatch extends ShopExpectationError {
    public CatalogContentMismatch(String message) {
        super(message);
    }

    public CatalogContentMismatch(String message, Throwable cause) {
        super(message, cause);
    }
}

