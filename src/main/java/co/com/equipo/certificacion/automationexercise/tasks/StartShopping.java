package co.com.equipo.certificacion.automationexercise.tasks;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.targets.Target;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;

public final class StartShopping {
    private StartShopping() {}

    public static Performable at(Target destination) {
        return Task.where("{0} opens Automation Exercise and goes to " + destination.getName(),
                Open.browserOn().thePageNamed("pages.home"),
                on(destination));
    }
}
