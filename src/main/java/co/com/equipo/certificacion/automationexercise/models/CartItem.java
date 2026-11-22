package co.com.equipo.certificacion.automationexercise.models;

/**
 * Representa una fila de producto en la tabla del carrito o del resumen de pedido.
 */
public record CartItem(String name, int unitPrice, int quantity, int subtotal) {
    public boolean hasConsistentSubtotal() {
        return unitPrice > 0 && quantity > 0 && subtotal == unitPrice * quantity;
    }
}

