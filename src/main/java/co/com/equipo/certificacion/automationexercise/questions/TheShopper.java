package co.com.equipo.certificacion.automationexercise.questions;

import co.com.equipo.certificacion.automationexercise.userinterfaces.AccountPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.Shop;
import co.com.equipo.certificacion.automationexercise.utils.WaitTimeouts;
import net.serenitybdd.screenplay.Question;

import java.time.Duration;

/**
 * Preguntas de negocio sobre el estado de sesion e identidad del comprador.
 */
public final class TheShopper {
    private TheShopper() {}

    public static Question<String> signedInName() {
        return Question.about("signed-in shopper name").answeredBy(actor -> Shop.SHOPPER_NAME
                .waitingForNoMoreThan(Duration.ofSeconds(WaitTimeouts.seconds()))
                .resolveFor(actor)
                .waitUntilVisible()
                .getText()
                .replaceAll("[\\s\\u00a0]+", " ")
                .trim());
    }

    public static Question<Boolean> isSignedIn() {
        return Question.about("whether the shopper is signed in")
                .answeredBy(actor -> Shop.SHOPPER_NAME.isVisibleFor(actor));
    }

    public static Question<Boolean> isSignInLinkVisible() {
        return Question.about("whether the sign-in link is visible")
                .answeredBy(actor -> Shop.ACCOUNT.isVisibleFor(actor));
    }

    public static Question<Boolean> isMissingSignInPassword() {
        return Question.about("whether the sign-in password is invalid as required")
                .answeredBy(actor -> AccountPage.MISSING_LOGIN_PASSWORD.isVisibleFor(actor));
    }

    public static Question<Boolean> isMissingRegistrationPassword() {
        return Question.about("whether the registration password is invalid as required")
                .answeredBy(actor -> AccountPage.MISSING_REGISTRATION_PASSWORD.isVisibleFor(actor));
    }

    public static Question<Boolean> isRegistrationFormVisible() {
        return Question.about("whether the registration form is visible")
                .answeredBy(actor -> AccountPage.CREATE.isVisibleFor(actor));
    }

    public static Question<Boolean> isAccountCreatedVisible() {
        return Question.about("whether the account confirmation is visible")
                .answeredBy(actor -> AccountPage.CREATED.isVisibleFor(actor));
    }
}

