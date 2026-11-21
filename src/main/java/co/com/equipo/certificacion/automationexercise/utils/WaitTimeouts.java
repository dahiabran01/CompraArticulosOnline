package co.com.equipo.certificacion.automationexercise.utils;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.environment.SystemEnvironmentVariables;

/**
 * Centraliza el limite de espera explicita leyendo webdriver.wait.for.timeout de serenity.conf.
 */
public final class WaitTimeouts {
    private static final String WAIT_FOR_TIMEOUT_PROPERTY = "webdriver.wait.for.timeout";
    private static final long DEFAULT_TIMEOUT_MILLIS = 10_000L;

    private WaitTimeouts() {}

    /**
     * Devuelve el tiempo maximo de espera configurado redondeado hacia arriba en segundos.
     */
    public static long seconds() {
        long millis = configuredTimeoutMillis();
        return Math.max(1L, (millis + 999L) / 1_000L);
    }

    private static long configuredTimeoutMillis() {
        String raw = EnvironmentSpecificConfiguration
                .from(SystemEnvironmentVariables.currentEnvironmentVariables())
                .getOptionalProperty(WAIT_FOR_TIMEOUT_PROPERTY)
                .orElse("");
        String trimmed = raw.trim();
        if (trimmed.matches("\\d{1,12}")) {
            long parsed = Long.parseLong(trimmed);
            if (parsed > 0L) {
                return parsed;
            }
        }
        return DEFAULT_TIMEOUT_MILLIS;
    }
}

