package co.com.equipo.certificacion.automationexercise.utils;

/**
 * Punto unico de salida por consola en ASCII para el reporte de progreso en vivo.
 */
public final class ConsoleOutput {
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String GRAY = "\u001B[90m";

    private ConsoleOutput() {}

    /**
     * Imprime una linea en stdout y vacia el buffer de inmediato.
     */
    public static void line(String text) {
        System.out.println(text == null ? "" : text);
        System.out.flush();
    }

    /**
     * Lee una propiedad booleana de sistema con valor por defecto.
     */
    public static boolean enabled(String property, boolean defaultValue) {
        String raw = System.getProperty(property);
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(raw.trim());
    }

    /**
     * Aplica un codigo ANSI solo si -Dprogress.colors=true esta activo.
     */
    public static String colorize(String text, String ansiCode) {
        String safeText = text == null ? "" : text;
        if (!enabled("progress.colors", false) || ansiCode == null || ansiCode.isEmpty()) {
            return safeText;
        }
        return ansiCode + safeText + RESET;
    }
}

