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

    public PayForOrder(PaymentCard card) {
        this.card = card;
    }

    public static PayForOrder with(PaymentCard card) {
        return Tasks.instrumented(PayForOrder.class, card);
    }

    @Override
    @Step("{0} submits the order with test card details")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                on(CheckoutPage.PLACE_ORDER),
                TypeWhenReady.theValue(card.holder()).into(CheckoutPage.CARDHOLDER),
                TypeWhenReady.theSecret(card.number()).into(CheckoutPage.CARD_NUMBER),
                TypeWhenReady.theSecret(card.cvc()).into(CheckoutPage.CVC),
                TypeWhenReady.theSecret(card.expiryMonth()).into(CheckoutPage.EXPIRY_MONTH),
                TypeWhenReady.theSecret(card.expiryYear()).into(CheckoutPage.EXPIRY_YEAR),
                on(CheckoutPage.PAY));
    }
}
