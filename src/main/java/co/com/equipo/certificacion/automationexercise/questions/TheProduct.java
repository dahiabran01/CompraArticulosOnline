package co.com.equipo.certificacion.automationexercise.questions;

import co.com.equipo.certificacion.automationexercise.models.ProductDetails;
import co.com.equipo.certificacion.automationexercise.userinterfaces.CatalogPage;
import co.com.equipo.certificacion.automationexercise.utils.WaitTimeouts;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.targets.Target;

import java.time.Duration;

/**
 * Preguntas de negocio sobre la ficha de detalle de un producto.
 */
public final class TheProduct {
    private TheProduct() {}

    public static Question<ProductDetails> details() {
        return Question.about("product details").answeredBy(actor -> {
            String name = readText(actor, CatalogPage.PRODUCT_NAME);
            String price = readText(actor, CatalogPage.PRICE);
            String rawAvailability = readText(actor, CatalogPage.AVAILABILITY);
            String availability = rawAvailability.replaceFirst("(?i)^Availability:\\s*", "").trim();
            return new ProductDetails(name, price, availability);
        });
    }

    public static Question<String> selectedQuantity() {
        return Question.about("selected product quantity")
                .answeredBy(actor -> CatalogPage.QUANTITY
                        .waitingForNoMoreThan(Duration.ofSeconds(WaitTimeouts.seconds()))
                        .resolveFor(actor)
                        .waitUntilVisible()
                        .getValue());
    }

    public static Question<String> minimumQuantity() {
        return Question.about("minimum product quantity")
                .answeredBy(actor -> CatalogPage.QUANTITY
                        .waitingForNoMoreThan(Duration.ofSeconds(WaitTimeouts.seconds()))
                        .resolveFor(actor)
                        .waitUntilVisible()
                        .getDomAttribute("min"));
    }

    private static String readText(Actor actor, Target target) {
        return target.waitingForNoMoreThan(Duration.ofSeconds(WaitTimeouts.seconds()))
                .resolveFor(actor)
                .waitUntilVisible()
                .getText()
                .replaceAll("[\\s\\u00a0]+", " ")
                .trim();
    }
}

