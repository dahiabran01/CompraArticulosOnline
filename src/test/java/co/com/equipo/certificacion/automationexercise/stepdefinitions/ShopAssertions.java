package co.com.equipo.certificacion.automationexercise.stepdefinitions;

import co.com.equipo.certificacion.automationexercise.questions.DisplayedText;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopperSession.actor;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.hamcrest.Matchers.equalToIgnoringCase;

final class ShopAssertions {
    private ShopAssertions() {}

    static void verifyText(Target target, String expected) {
        actor().attemptsTo(WaitUntil.the(target, isVisible()).forNoMoreThan(10).seconds());
        actor().should(seeThat(DisplayedText.of(target), equalToIgnoringCase(expected)));
    }
}
