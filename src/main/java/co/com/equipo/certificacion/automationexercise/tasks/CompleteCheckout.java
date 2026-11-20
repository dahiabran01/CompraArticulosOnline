package co.com.equipo.certificacion.automationexercise.tasks;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;

import java.time.Year;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;
import static co.com.equipo.certificacion.automationexercise.userinterfaces.CartPage.PROCEED;
import static co.com.equipo.certificacion.automationexercise.userinterfaces.CheckoutPage.*;

public final class CompleteCheckout {
    private CompleteCheckout() {}

    public static Performable proceed() {
        return Task.where("{0} proceeds to checkout", on(PROCEED));
    }

    public static Performable payWithTestCard(String cardholder) {
        return Task.where("{0} submits the order with demo card details",
                on(PLACE_ORDER),
                Enter.theValue(cardholder).into(CARDHOLDER),
                Enter.theValue("4111111111111111").into(CARD_NUMBER),
                Enter.theValue("123").into(CVC),
                Enter.theValue("12").into(EXPIRY_MONTH),
                Enter.theValue(Year.now().plusYears(2).toString()).into(EXPIRY_YEAR),
                on(PAY));
    }
}
