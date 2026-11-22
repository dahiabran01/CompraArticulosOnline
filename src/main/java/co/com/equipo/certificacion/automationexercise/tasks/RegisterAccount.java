package co.com.equipo.certificacion.automationexercise.tasks;

import co.com.equipo.certificacion.automationexercise.interactions.SelectWhenReady;
import co.com.equipo.certificacion.automationexercise.interactions.TypeWhenReady;
import co.com.equipo.certificacion.automationexercise.models.Shopper;
import co.com.equipo.certificacion.automationexercise.userinterfaces.AccountPage;
import co.com.equipo.certificacion.automationexercise.utils.WaitTimeouts;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/**
 * Registra una nueva cuenta de comprador completando el formulario inicial y el perfil completo.
 */
public class RegisterAccount implements Task {
    private final Shopper shopper;
    private final boolean includePassword;
    private final String actionDescription;

    public RegisterAccount(Shopper shopper, boolean includePassword, String actionDescription) {
        this.shopper = shopper;
        this.includePassword = includePassword;
        this.actionDescription = actionDescription;
    }

    public static RegisterAccount forShopper(Shopper shopper) {
        return Tasks.instrumented(
                RegisterAccount.class,
                shopper,
                true,
                "creates a new shopper account");
    }

    public static RegisterAccount withoutPasswordFor(Shopper shopper) {
        return Tasks.instrumented(
                RegisterAccount.class,
                shopper,
                false,
                "submits registration details without a password");
    }

    public static Performable startFor(Shopper shopper) {
        return Task.where("{0} submits the shopper's name and email",
                TypeWhenReady.theValue(shopper.displayName()).into(AccountPage.SIGNUP_NAME),
                TypeWhenReady.theValue(shopper.email()).into(AccountPage.SIGNUP_EMAIL),
                on(AccountPage.SIGNUP_BUTTON));
    }

    public static Performable continueShopping() {
        return Task.where("{0} continues from the account confirmation", on(AccountPage.CONTINUE));
    }

    @Override
    @Step("{0} #actionDescription")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                startFor(shopper),
                WaitUntil.the(AccountPage.PASSWORD, isVisible()).forNoMoreThan(WaitTimeouts.seconds()).seconds());
        if (includePassword) {
            actor.attemptsTo(TypeWhenReady.theSecret(shopper.password()).into(AccountPage.PASSWORD));
        }
        actor.attemptsTo(
                TypeWhenReady.theValue(shopper.firstName()).into(AccountPage.FIRST_NAME),
                TypeWhenReady.theValue(shopper.lastName()).into(AccountPage.LAST_NAME),
                TypeWhenReady.theValue(shopper.street()).into(AccountPage.STREET),
                SelectWhenReady.option(shopper.country()).from(AccountPage.COUNTRY),
                TypeWhenReady.theValue(shopper.state()).into(AccountPage.STATE),
                TypeWhenReady.theValue(shopper.city()).into(AccountPage.CITY),
                TypeWhenReady.theValue(shopper.postcode()).into(AccountPage.POSTCODE),
                TypeWhenReady.theValue(shopper.mobile()).into(AccountPage.MOBILE),
                on(AccountPage.CREATE));
    }
}
