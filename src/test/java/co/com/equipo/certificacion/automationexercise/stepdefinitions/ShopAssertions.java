package co.com.equipo.certificacion.automationexercise.stepdefinitions;

import co.com.equipo.certificacion.automationexercise.exceptions.ShopExpectationError;
import co.com.equipo.certificacion.automationexercise.questions.DisplayedText;
import co.com.equipo.certificacion.automationexercise.utils.WaitTimeouts;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopperSession.actor;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.hamcrest.Matchers.equalToIgnoringCase;

final class ShopAssertions {
    private ShopAssertions() {}

    static void verifyText(
            Target target,
            String expected,
            Class<? extends ShopExpectationError> complaintType,
            String message) {
        actor().attemptsTo(WaitUntil.the(target, isVisible()).forNoMoreThan(WaitTimeouts.seconds()).seconds());
        actor().should(seeThat(DisplayedText.of(target), equalToIgnoringCase(expected))
                .orComplainWith(complaintType, message));
    }
}
