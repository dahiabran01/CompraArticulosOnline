package co.com.equipo.certificacion.automationexercise.interactions;

import co.com.equipo.certificacion.automationexercise.utils.Narrator;
import co.com.equipo.certificacion.automationexercise.utils.WaitTimeouts;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isEnabled;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/**
 * Escribe texto (normal o secreto enmascarado) en un campo cuando esta visible y habilitado.
 */
public class TypeWhenReady implements Interaction {
    private final String value;
    private final Target target;
    private final boolean secret;
    private final String displayValue;

    public TypeWhenReady(String value, Target target, boolean secret) {
        this.value = value;
        this.target = target;
        this.secret = secret;
        this.displayValue = secret ? "****" : value;
    }

    public static Builder theValue(String value) {
        return new Builder(value, false);
    }

    public static Builder theSecret(String value) {
        return new Builder(value, true);
    }

    @Override
    @Step("{0} types '#displayValue' into #target")
    public <T extends Actor> void performAs(T actor) {
        String targetName = target == null ? "campo" : target.getName();
        Narrator.says(actor, "escribe \"" + displayValue + "\" en \"" + targetName + "\"");
        long timeoutSeconds = WaitTimeouts.seconds();
        actor.attemptsTo(
                WaitUntil.the(target, isVisible()).forNoMoreThan(timeoutSeconds).seconds(),
                WaitUntil.the(target, isEnabled()).forNoMoreThan(timeoutSeconds).seconds());
        if (secret) {
            target.resolveFor(actor).type(value);
        } else {
            actor.attemptsTo(Enter.theValue(value).into(target));
        }
    }

    /**
     * Constructor fluido para especificar el campo destino.
     */
    public static final class Builder {
        private final String value;
        private final boolean secret;

        private Builder(String value, boolean secret) {
            this.value = value;
            this.secret = secret;
        }

        public TypeWhenReady into(Target target) {
            return Tasks.instrumented(TypeWhenReady.class, value, target, secret);
        }
    }
}

