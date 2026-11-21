package co.com.equipo.certificacion.automationexercise.interactions;

import co.com.equipo.certificacion.automationexercise.utils.Narrator;
import co.com.equipo.certificacion.automationexercise.utils.WaitTimeouts;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;

/**
 * Espera a que un elemento sea clicable y realiza un unico reintento centrado si otro elemento lo tapa.
 */
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
        actor.attemptsTo(WaitUntil.the(target, isClickable()).forNoMoreThan(WaitTimeouts.seconds()).seconds());
        try {
            target.resolveFor(actor).click();
        } catch (ElementClickInterceptedException intercepted) {
            scrollIntoCenter(actor);
            target.resolveFor(actor).click();
        }
    }

    private void scrollIntoCenter(Actor actor) {
        JavascriptExecutor executor = (JavascriptExecutor) BrowseTheWeb.as(actor).getDriver();
        executor.executeScript("arguments[0].scrollIntoView({block:'center'});", target.resolveFor(actor).getElement());
    }
}
