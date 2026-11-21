package co.com.equipo.certificacion.automationexercise.tasks;

import co.com.equipo.certificacion.automationexercise.userinterfaces.CartPage;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;

public final class CompleteCheckout {
    private CompleteCheckout() {}

    public static Performable proceed() {
        return Task.where("{0} proceeds to checkout", on(CartPage.PROCEED));
    }
}
