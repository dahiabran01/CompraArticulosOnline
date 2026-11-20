package co.com.equipo.certificacion.automationexercise.tasks;

import co.com.equipo.certificacion.automationexercise.models.Shopper;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.waits.WaitUntil;
import net.serenitybdd.annotations.Step;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;
import static co.com.equipo.certificacion.automationexercise.userinterfaces.AccountPage.DELETED;
import static co.com.equipo.certificacion.automationexercise.userinterfaces.Shop.ACCOUNT;
import static co.com.equipo.certificacion.automationexercise.userinterfaces.Shop.DELETE_ACCOUNT;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class DeleteTestAccount implements Task {
    private final Shopper shopper;

    public DeleteTestAccount(Shopper shopper) {
        this.shopper = shopper;
    }

    public static DeleteTestAccount belongingTo(Shopper shopper) {
        return Tasks.instrumented(DeleteTestAccount.class, shopper);
    }

    @Override
    @Step("{0} deletes the account created for this scenario")
    public <T extends Actor> void performAs(T actor) {
        if (ACCOUNT.resolveFor(actor).isCurrentlyVisible()) {
            actor.attemptsTo(on(ACCOUNT), SignIn.with(shopper.email(), shopper.password()));
        }
        actor.attemptsTo(on(DELETE_ACCOUNT), WaitUntil.the(DELETED, isVisible()));
    }
}
