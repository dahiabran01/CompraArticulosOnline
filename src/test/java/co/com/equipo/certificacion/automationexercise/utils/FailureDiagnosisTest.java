package co.com.equipo.certificacion.automationexercise.utils;

import co.com.equipo.certificacion.automationexercise.exceptions.CheckoutDataMismatch;
import co.com.equipo.certificacion.automationexercise.exceptions.ShopExpectationError;
import co.com.equipo.certificacion.automationexercise.utils.FailureDiagnosis.Diagnosis;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

public class FailureDiagnosisTest {
    private static final class ElementClickInterceptedException extends RuntimeException {
        ElementClickInterceptedException(String message) {
            super(message);
        }
    }

    private static final class TimeoutException extends RuntimeException {
        TimeoutException(String message) {
            super(message);
        }
    }

    private static final class NoSuchElementException extends RuntimeException {
        NoSuchElementException(String message) {
            super(message);
        }
    }

    private static final class StaleElementReferenceException extends RuntimeException {
        StaleElementReferenceException(String message) {
            super(message);
        }
    }

    private static final class SessionNotCreatedException extends RuntimeException {
        SessionNotCreatedException(String message) {
            super(message);
        }
    }

    @Test
    public void returnsNoErrorDiagnosisWhenThrowableIsNull() {
        // Arrange
        Throwable error = null;

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(error);

        // Assert
        assertThat(diagnosis.category(), is(equalTo("SIN_ERROR")));
        assertThat(diagnosis.detail(), is(equalTo("")));
    }

    @Test
    public void classifiesShopExpectationErrorBeforeGenericAssertionError() {
        // Arrange
        ShopExpectationError error = new CheckoutDataMismatch(
                "AC11: total esperado Rs. 1000 pero fue Rs. 500",
                new AssertionError("mismatch"));

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(error);

        // Assert
        assertThat(diagnosis.category(), is(equalTo("REGLA_DE_NEGOCIO")));
        assertThat(diagnosis.probableCause(), containsString("AC11: total esperado Rs. 1000"));
    }

    @Test
    public void classifiesAssertionErrorAsUnexpectedValue() {
        // Arrange
        Throwable error = new AssertionError("\nExpected: \"Blue Topx\"\n     but: was \"Blue Top\"");

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(error);

        // Assert
        assertThat(diagnosis.category(), is(equalTo("VALOR_INESPERADO")));
        assertThat(diagnosis.detail(), is(equalTo("Expected: \"Blue Topx\" | but: was \"Blue Top\"")));
    }

    @Test
    public void classifiesInterceptedClickAsCoveredElement() {
        // Arrange
        Throwable error = new ElementClickInterceptedException("Element click intercepted by ad iframe");

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(error);

        // Assert
        assertThat(diagnosis.category(), is(equalTo("ELEMENTO_TAPADO")));
    }

    @Test
    public void classifiesTimeoutExceptionAsUnavailableElement() {
        // Arrange
        Throwable error = new TimeoutException("Expected condition failed: waiting for Proceed To Checkout");

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(error);

        // Assert
        assertThat(diagnosis.category(), is(equalTo("ELEMENTO_NO_DISPONIBLE")));
    }

    @Test
    public void classifiesSerenityWaitUntilAssertionAsUnavailableElement() {
        // Arrange
        Throwable error = new AssertionError(
                "\nExpected: an element that is clickable\n     but: no matching element found by [search button located with: #no-existe] was not clicked");

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(error);

        // Assert
        assertThat(diagnosis.category(), is(equalTo("ELEMENTO_NO_DISPONIBLE")));
    }

    @Test
    public void classifiesStaleElementAsReloadedPage() {
        // Arrange
        Throwable error = new StaleElementReferenceException("stale element reference: element is not attached");

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(error);

        // Assert
        assertThat(diagnosis.category(), is(equalTo("PAGINA_RECARGADA")));
    }

    @Test
    public void classifiesSessionNotCreatedAsBrowserIssue() {
        // Arrange
        Throwable error = new SessionNotCreatedException("Could not start a new session with Chrome");

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(error);

        // Assert
        assertThat(diagnosis.category(), is(equalTo("NAVEGADOR")));
    }

    @Test
    public void classifiesConnectionFailureMessageAsNetworkIssue() {
        // Arrange
        Throwable error = new RuntimeException("unknown error: net::ERR_NAME_NOT_RESOLVED");

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(error);

        // Assert
        assertThat(diagnosis.category(), is(equalTo("RED")));
    }

    @Test
    public void classifiesUnknownThrowableAsUnclassified() {
        // Arrange
        Throwable error = new IllegalStateException("estado inesperado en la prueba");

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(error);

        // Assert
        assertThat(diagnosis.category(), is(equalTo("NO_CLASIFICADO")));
        assertThat(diagnosis.probableCause(), containsString("IllegalStateException"));
    }

    @Test
    public void classifiesMatchingCauseWrappedInRuntimeException() {
        // Arrange
        Throwable cause = new NoSuchElementException("Unable to locate element: #submit_search");
        Throwable wrapper = new RuntimeException("Fallo ejecutando tarea", cause);

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(wrapper);

        // Assert
        assertThat(diagnosis.category(), is(equalTo("ELEMENTO_NO_DISPONIBLE")));
        assertThat(diagnosis.detail(), containsString("Unable to locate element"));
    }

    @Test
    public void stopsSafelyOnCyclicCauseChain() {
        // Arrange
        RuntimeException first = new RuntimeException("primer error");
        RuntimeException second = new RuntimeException("segundo error", first);
        first.initCause(second);

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(first);

        // Assert
        assertThat(diagnosis.category(), is(equalTo("NO_CLASIFICADO")));
    }

    @Test
    public void truncatesLongFirstLineDetailToTwoHundredCharacters() {
        // Arrange
        String longMessage = "X".repeat(250) + "\nSegunda linea";
        Throwable error = new IllegalArgumentException(longMessage);

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(error);

        // Assert
        assertThat(diagnosis.detail().length(), is(lessThanOrEqualTo(200)));
        assertThat(diagnosis.detail().endsWith("..."), is(true));
    }

    @Test
    public void includesButLineInDetailForHamcrestWaitUntilFailure() {
        // Arrange
        Throwable error = new AssertionError(
                "\nExpected: an element that is visible\n     but: no matching element found by [cart row for Blue Top located with: #x]");

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(error);

        // Assert
        assertThat(diagnosis.category(), is(equalTo("ELEMENTO_NO_DISPONIBLE")));
        assertThat(diagnosis.detail(), containsString("Expected: an element that is visible"));
        assertThat(diagnosis.detail(), containsString("no matching element found by"));
        assertThat(diagnosis.detail().length(), is(lessThanOrEqualTo(200)));
    }

    @Test
    public void classifiesRealShopExpectationSubclassAndTrimsTrailingComplaintDash() {
        // Arrange
        Throwable cause = new AssertionError("\nExpected: \"Rs. 9999\"\n     but: was \"Rs. 1000\"");
        Throwable error = new CheckoutDataMismatch(
                "AC11: el total del pedido no coincide - " + cause.getMessage(),
                cause);

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(error);

        // Assert
        assertThat(diagnosis.category(), is(equalTo("REGLA_DE_NEGOCIO")));
        assertThat(diagnosis.probableCause(), is(equalTo(
                "El sitio respondio, pero el resultado no cumple el criterio de aceptacion: AC11: el total del pedido no coincide")));
    }

    @Test
    public void classifiesErrorTallyBusinessComplaintAssertionAsBusinessRule() {
        // Arrange
        Throwable error = new AssertionError(
                "AC11/AC14/AC19/AC26: el total del pedido no coincide - \n"
                        + "Expected: a string equal to \"Rs. 9999\" ignoring case\n"
                        + "     but: was \"Rs. 1000\"");

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(error);

        // Assert
        assertThat(diagnosis.category(), is(equalTo("REGLA_DE_NEGOCIO")));
        assertThat(diagnosis.probableCause(), is(equalTo(
                "El sitio respondio, pero el resultado no cumple el criterio de aceptacion: AC11/AC14/AC19/AC26: el total del pedido no coincide")));
        assertThat(diagnosis.detail(), is(equalTo(
                "Expected: a string equal to \"Rs. 9999\" ignoring case | but: was \"Rs. 1000\"")));
    }

    @Test
    public void classifiesSiteOverloadPageContextAsUnavailableSiteBeforeBusinessRule() {
        // Arrange
        Throwable error = new CheckoutDataMismatch(
                "AC27: el resumen del pedido no lista exactamente los productos agregados",
                new AssertionError("empty"));

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(error, "This website is under heavy load (queue full)");

        // Assert
        assertThat(diagnosis.category(), is(equalTo("SITIO_NO_DISPONIBLE")));
        assertThat(diagnosis.probableCause(), containsString("heavy load"));
    }

    @Test
    public void keepsBusinessRuleDiagnosisWhenPageContextIsNormal() {
        // Arrange
        Throwable error = new CheckoutDataMismatch(
                "AC27: el total del pedido no coincide",
                new AssertionError("mismatch"));

        // Act
        Diagnosis diagnosis = FailureDiagnosis.of(error, "Automation Exercise - Checkout");

        // Assert
        assertThat(diagnosis.category(), is(equalTo("REGLA_DE_NEGOCIO")));
        assertThat(diagnosis.probableCause(), containsString("AC27: el total del pedido no coincide"));
    }
}

