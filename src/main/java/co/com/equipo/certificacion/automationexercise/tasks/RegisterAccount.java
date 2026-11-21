package co.com.equipo.certificacion.automationexercise.tasks;

import co.com.equipo.certificacion.automationexercise.models.Shopper;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.SelectFromOptions;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;
import static co.com.equipo.certificacion.automationexercise.userinterfaces.AccountPage.*;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public final class RegisterAccount {
    private RegisterAccount() {}

    public static Performable startFor(Shopper shopper) {
        return Task.where("{0} submits the shopper's name and email",
                Enter.theValue(shopper.displayName()).into(SIGNUP_NAME),
                Enter.theValue(shopper.email()).into(SIGNUP_EMAIL),
                on(SIGNUP_BUTTON));
    }

    public static Performable forShopper(Shopper shopper) {
        return Task.where("{0} creates a new shopper account",
                startFor(shopper),
                WaitUntil.the(PASSWORD, isVisible()).forNoMoreThan(10).seconds(),
                Enter.theValue(shopper.password()).into(PASSWORD),
                Enter.theValue(shopper.firstName()).into(FIRST_NAME),
                Enter.theValue(shopper.lastName()).into(LAST_NAME),
                Enter.theValue(shopper.street()).into(STREET),
                SelectFromOptions.byVisibleText(shopper.country()).from(COUNTRY),
                Enter.theValue(shopper.state()).into(STATE),
                Enter.theValue(shopper.city()).into(CITY),
                Enter.theValue(shopper.postcode()).into(POSTCODE),
                Enter.theValue(shopper.mobile()).into(MOBILE),
                on(CREATE));
    }

    public static Performable continueShopping() {
        return Task.where("{0} continues from the account confirmation", on(CONTINUE));
    }
}
