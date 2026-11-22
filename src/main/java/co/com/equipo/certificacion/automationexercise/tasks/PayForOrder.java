package co.com.equipo.certificacion.automationexercise.tasks;

import co.com.equipo.certificacion.automationexercise.interactions.TypeWhenReady;
import co.com.equipo.certificacion.automationexercise.models.PaymentCard;
import co.com.equipo.certificacion.automationexercise.userinterfaces.CheckoutPage;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;

/**
 * Navega al formulario de pago y envia los datos de la tarjeta de prueba.
 */
public class PayForOrder implements Task {
    private final PaymentCard card;
    private final boolean includeCardNumber;
    private final String actionDescription;

    public PayForOrder(PaymentCard card, boolean includeCardNumber, String actionDescription) {
        this.card = card;
        this.includeCardNumber = includeCardNumber;
        this.actionDescription = actionDescription;
    }

    public static PayForOrder with(PaymentCard card) {
        return Tasks.instrumented(
                PayForOrder.class,
                card,
                true,
                "submits the order with test card details");
    }

    public static PayForOrder withoutCardNumber(PaymentCard card) {
        return Tasks.instrumented(
                PayForOrder.class,
                card,
                false,
                "submits the order without a card number");
    }

    @Override
    @Step("{0} #actionDescription")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                on(CheckoutPage.PLACE_ORDER),
                TypeWhenReady.theValue(card.holder()).into(CheckoutPage.CARDHOLDER));
        if (includeCardNumber) {
            actor.attemptsTo(TypeWhenReady.theSecret(card.number()).into(CheckoutPage.CARD_NUMBER));
        }
        actor.attemptsTo(
                TypeWhenReady.theSecret(card.cvc()).into(CheckoutPage.CVC),
                TypeWhenReady.theSecret(card.expiryMonth()).into(CheckoutPage.EXPIRY_MONTH),
                TypeWhenReady.theSecret(card.expiryYear()).into(CheckoutPage.EXPIRY_YEAR),
                on(CheckoutPage.PAY));
    }
}
