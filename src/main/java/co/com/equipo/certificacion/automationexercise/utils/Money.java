package co.com.equipo.certificacion.automationexercise.utils;

/**
 * Utilidad para interpretar montos en rupias mostrados en la tienda.
 */
public final class Money {
    private Money() {}

    /**
     * Convierte un texto con monto (por ejemplo "Rs. 1000") en su valor entero.
     */
    public static int rupees(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("El texto monetario no puede ser nulo ni vacio");
        }
        String normalized = text.replace('\u00a0', ' ').trim();
        String digits = normalized.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron digitos en el monto: " + normalized);
        }
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException overflow) {
            throw new IllegalArgumentException("El monto excede el rango entero permitido: " + normalized, overflow);
        }
    }
}

