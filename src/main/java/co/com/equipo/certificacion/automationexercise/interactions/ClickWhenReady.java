package co.com.equipo.certificacion.automationexercise.interactions;

import co.com.equipo.certificacion.automationexercise.utils.Narrator;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;

public class ClickWhenReady implements Interaction {
    private final Target target;

    public ClickWhenReady(Target target) {
        this.target = target;
    }

    public static ClickWhenReady on(Target target) {
        return Tasks.instrumented(ClickWhenReady.class, target);
    }

    @Override
    @Step("{0} clicks #target when it is ready")
    public <T extends Actor> void performAs(T actor) {
        String targetName = target == null ? "elemento" : target.getName();
        Narrator.says(actor, "hace clic en \"" + targetName + "\"");
        actor.attemptsTo(WaitUntil.the(target, isClickable()), Click.on(target));
    }
}
