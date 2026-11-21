package co.com.equipo.certificacion.automationexercise.questions;

import co.com.equipo.certificacion.automationexercise.userinterfaces.CheckoutPage;
import co.com.equipo.certificacion.automationexercise.utils.WaitTimeouts;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.targets.Target;

import java.time.Duration;

/**
 * Preguntas de negocio sobre las direcciones y el total mostrados en checkout.
 */
public final class TheOrder {
    private TheOrder() {}

    public static Question<String> total() {
        return normalizedText("order total", CheckoutPage.ORDER_TOTAL);
    }

    public static Question<String> deliveryAddress() {
        return normalizedText("delivery address", CheckoutPage.DELIVERY_ADDRESS);
    }

    public static Question<String> billingAddress() {
        return normalizedText("billing address", CheckoutPage.BILLING_ADDRESS);
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

