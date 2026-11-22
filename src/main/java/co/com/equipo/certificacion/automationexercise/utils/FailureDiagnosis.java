package co.com.equipo.certificacion.automationexercise.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Clasifica una excepcion de prueba en una causa probable legible en espanol.
 */
public final class FailureDiagnosis {
    private static final int MAX_CAUSE_DEPTH = 10;
    private static final int MAX_DETAIL_LENGTH = 200;
    private static final Pattern BUSINESS_COMPLAINT_PREFIX = Pattern.compile("^AC\\d+(?:/AC\\d+)*:\\s+.+");

    private FailureDiagnosis() {}

    /**
     * Resultado estructurado del diagnostico de un fallo.
     */
    public record Diagnosis(String category, String probableCause, String whatToCheck, String detail) {}

    /**
     * Analiza la cadena de causas de una excepcion y devuelve el diagnostico segun prioridad fija.
     */
    public static Diagnosis of(Throwable error) {
        return of(error, null);
    }

    /**
     * Analiza la cadena de causas junto con texto visible de la pagina para distinguir saturacion del sitio.
     */
    public static Diagnosis of(Throwable error, String pageContext) {
        if (error == null) {
            return new Diagnosis(
                    "SIN_ERROR",
                    "No se registro una excepcion para este fallo",
                    "Revisar el reporte de Serenity para mas contexto",
                    "");
        }

        List<Throwable> chain = collectCauseChain(error);
        String detail = extractDetail(chain);

        if (isSiteOverloadedText(pageContext)) {
            return new Diagnosis(
                    "SITIO_NO_DISPONIBLE",
                    "El sitio publico respondio con sobrecarga temporal (heavy load / queue full)",
                    "Reintentar la ejecucion; es una saturacion temporal de automationexercise.com, no un defecto de la prueba",
                    detail);
        }

        for (Throwable cause : chain) {
            if (isShopExpectationError(cause) || isErrorTallyBusinessRuleAssertion(cause)) {
                String message = firstNonBlankLine(cause.getMessage());
                if (message.endsWith(" -")) {
                    message = message.substring(0, message.length() - 2).trim();
                }
                String probableCause = message.isEmpty()
                        ? "El sitio respondio, pero el resultado no cumple el criterio de aceptacion"
                        : "El sitio respondio, pero el resultado no cumple el criterio de aceptacion: " + message;
                return new Diagnosis(
                        "REGLA_DE_NEGOCIO",
                        probableCause,
                        "Comparar esperado y obtenido en el reporte (captura del paso)",
                        detail);
            }
        }

        for (Throwable cause : chain) {
            if (cause instanceof AssertionError && !isElementUnavailableAssertion(cause)) {
                return new Diagnosis(
                        "VALOR_INESPERADO",
                        "La pantalla mostro un valor distinto al esperado",
                        "Esperado/obtenido del mensaje de Hamcrest; cambio el sitio o el dato de prueba?",
                        detail);
            }
        }

        for (Throwable cause : chain) {
            if (cause.getClass().getSimpleName().contains("ElementClickIntercepted")) {
                return new Diagnosis(
                        "ELEMENTO_TAPADO",
                        "Otro elemento tapaba el control (anuncio, modal o banner de cookies)",
                        "Captura del paso; reglas de bloqueo de anuncios en serenity.conf",
                        detail);
            }
        }

        for (Throwable cause : chain) {
            if (isElementUnavailableType(cause.getClass().getSimpleName())
                    || isElementUnavailableAssertion(cause)) {
                return new Diagnosis(
                        "ELEMENTO_NO_DISPONIBLE",
                        "El elemento no aparecio o no se pudo usar dentro del tiempo de espera",
                        "El paso anterior llego a la pagina correcta? Cambio el localizador? El sitio esta lento?",
                        detail);
            }
        }

        for (Throwable cause : chain) {
            if (cause.getClass().getSimpleName().contains("StaleElementReference")) {
                return new Diagnosis(
                        "PAGINA_RECARGADA",
                        "La pagina se recargo mientras se leia el elemento",
                        "Esperar el estado final antes de leer",
                        detail);
            }
        }

        for (Throwable cause : chain) {
            if (cause.getClass().getSimpleName().contains("SessionNotCreated")) {
                return new Diagnosis(
                        "NAVEGADOR",
                        "Chrome y ChromeDriver no son compatibles o Chrome no esta instalado",
                        "Version de Chrome; Selenium Manager",
                        detail);
            }
        }

        for (Throwable cause : chain) {
            if (isNetworkError(cause.getClass().getSimpleName(), cause.getMessage())) {
                return new Diagnosis(
                        "RED",
                        "Sin internet o el sitio esta caido",
                        "Abrir el sitio manualmente",
                        detail);
            }
        }

        Throwable rootCause = chain.get(chain.size() - 1);
        return new Diagnosis(
                "NO_CLASIFICADO",
                "Error no clasificado: " + rootCause.getClass().getSimpleName(),
                "Traza completa en el reporte de Serenity",
                detail);
    }

    private static List<Throwable> collectCauseChain(Throwable error) {
        List<Throwable> chain = new ArrayList<>();
        Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        Throwable current = error;
        while (current != null && chain.size() < MAX_CAUSE_DEPTH && visited.add(current)) {
            chain.add(current);
            current = current.getCause();
        }
        return chain;
    }

    private static boolean isShopExpectationError(Throwable throwable) {
        for (Class<?> type = throwable.getClass(); type != null; type = type.getSuperclass()) {
            if ("ShopExpectationError".equals(type.getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * En Serenity 4.1.3, ErrorTally.throwSummaryExceptionFrom reemplaza la subclase
     * personalizada de AssertionError por un AssertionError plano al propagar el
     * fallo hacia Cucumber/JUnit, conservando el mensaje de orComplainWith.
     * Reconocer el prefijo AC##: mantiene la categoria REGLA_DE_NEGOCIO en consola
     * sin acoplar esta utilidad al API interno de Serenity.
     */
    private static boolean isErrorTallyBusinessRuleAssertion(Throwable throwable) {
        if (!(throwable instanceof AssertionError)) {
            return false;
        }
        String firstLine = firstNonBlankLine(throwable.getMessage());
        return BUSINESS_COMPLAINT_PREFIX.matcher(firstLine).matches();
    }

    private static boolean isElementUnavailableType(String simpleName) {
        return simpleName.contains("Timeout")
                || simpleName.contains("NoSuchElement")
                || simpleName.contains("ElementShouldBe")
                || simpleName.contains("ElementNotVisible")
                || simpleName.contains("ElementNotInteractable");
    }

    /**
     * Reconoce los AssertionError que Serenity 4.1.3 emite cuando falla WaitUntil.the(target, matcher)
     * mediante EventualConsequence y WebElementQuestion, para clasificarlos como ELEMENTO_NO_DISPONIBLE
     * en lugar de VALOR_INESPERADO.
     */
    private static boolean isElementUnavailableAssertion(Throwable throwable) {
        if (!(throwable instanceof AssertionError)) {
            return false;
        }
        String message = throwable.getMessage();
        if (message == null || message.isBlank()) {
            return false;
        }
        String lower = message.toLowerCase(Locale.ROOT);
        return lower.contains("no matching element found by")
                || lower.contains("an element that is clickable")
                || lower.contains("an element that is visible")
                || lower.contains("an element that is enabled")
                || lower.contains("an element that is present")
                || lower.contains("element should be")
                || lower.contains("expected condition failed: waiting for");
    }

    private static boolean isSiteOverloadedText(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        String lower = text.toLowerCase(Locale.ROOT);
        return lower.contains("under heavy load") || lower.contains("queue full");
    }

    private static boolean isNetworkError(String simpleName, String message) {
        if (simpleName.contains("UnknownHost")) {
            return true;
        }
        if (message == null) {
            return false;
        }
        return message.contains("ERR_NAME_NOT_RESOLVED")
                || message.contains("ERR_INTERNET_DISCONNECTED")
                || message.contains("ERR_CONNECTION")
                || message.contains("UnknownHost");
    }

    private static String extractDetail(List<Throwable> chain) {
        for (int index = chain.size() - 1; index >= 0; index--) {
            String summary = summarizeMessage(chain.get(index).getMessage());
            if (!summary.isEmpty()) {
                return truncate(summary, MAX_DETAIL_LENGTH);
            }
        }
        return truncate(chain.get(chain.size() - 1).getClass().getSimpleName(), MAX_DETAIL_LENGTH);
    }

    private static String summarizeMessage(String message) {
        if (message == null || message.isBlank()) {
            return "";
        }
        List<String> nonBlankLines = new ArrayList<>();
        for (String line : message.split("\\R")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                nonBlankLines.add(trimmed);
            }
        }
        if (nonBlankLines.isEmpty()) {
            return "";
        }
        for (int i = 0; i < nonBlankLines.size(); i++) {
            String line = nonBlankLines.get(i);
            if (line.startsWith("Expected:")) {
                if (i + 1 < nonBlankLines.size() && nonBlankLines.get(i + 1).startsWith("but:")) {
                    return line + " | " + nonBlankLines.get(i + 1);
                }
                return line;
            }
        }
        return nonBlankLines.get(0);
    }

    private static String firstNonBlankLine(String message) {
        if (message == null || message.isBlank()) {
            return "";
        }
        String[] lines = message.split("\\R");
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                return trimmed;
            }
        }
        return "";
    }

    private static String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}

