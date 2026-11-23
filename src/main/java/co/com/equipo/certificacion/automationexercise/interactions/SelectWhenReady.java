package co.com.equipo.certificacion.automationexercise.interactions;

import co.com.equipo.certificacion.automationexercise.utils.Narrator;
import co.com.equipo.certificacion.automationexercise.utils.WaitTimeouts;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.SelectFromOptions;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/**
 * Selecciona una opcion visible en un listado desplegable cuando esta disponible.
 */
public class SelectWhenReady implements Interaction {
    private final String option;
    private final Target target;

    public SelectWhenReady(String option, Target target) {
        this.option = option;
        this.target = target;
    }

    public static Builder option(String option) {
        return new Builder(option);
    }

    @Override
    @Step("{0} selects '#option' from #target")
    public <T extends Actor> void performAs(T actor) {
        String targetName = target == null ? "lista" : target.getName();
        Narrator.says(actor, "elige \"" + option + "\" en \"" + targetName + "\"");
        actor.attemptsTo(
                WaitUntil.the(target, isVisible()).forNoMoreThan(WaitTimeouts.seconds()).seconds(),
                SelectFromOptions.byVisibleText(option).from(target));
    }

    /**
     * Constructor fluido para especificar la lista desplegable destino.
     */
    public static final class Builder {
        private final String option;

        private Builder(String option) {
            this.option = option;
        }

        public SelectWhenReady from(Target target) {
            return Tasks.instrumented(SelectWhenReady.class, option, target);
        }
    }
}

