package co.com.equipo.certificacion.automationexercise.questions;

import co.com.equipo.certificacion.automationexercise.userinterfaces.CheckoutPage;
import co.com.equipo.certificacion.automationexercise.utils.WaitTimeouts;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.targets.Target;

import java.time.Duration;

/**
 * Preguntas de negocio sobre la confirmacion del pedido despues del pago.
 */
public final class TheOrderConfirmation {
    private TheOrderConfirmation() {}

    public static Question<String> title() {
        return normalizedText("order confirmation title", CheckoutPage.ORDER_PLACED);
    }

    public static Question<String> message() {
        return normalizedText("order confirmation message", CheckoutPage.CONFIRMATION);
    }

    private static Question<String> normalizedText(String label, Target target) {
        return Question.about(label).answeredBy(actor -> target
                .waitingForNoMoreThan(Duration.ofSeconds(WaitTimeouts.seconds()))
                .resolveFor(actor)
                .waitUntilVisible()
                .getText()
                .replaceAll("[\\s\\u00a0]+", " ")
                .trim());
    }
}

