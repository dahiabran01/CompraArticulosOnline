package co.com.equipo.certificacion.automationexercise.stepdefinitions;

import co.com.equipo.certificacion.automationexercise.models.Shopper;
import co.com.equipo.certificacion.automationexercise.tasks.DeleteTestAccount;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

public class ShopperSession {
    private static final String REGISTERED_ACCOUNT = "registered demo account";

    @Before
    public void prepareShopper() {
        OnStage.setTheStage(new OnlineCast());
        OnStage.theActorCalled("Alex");
    }

    @After
    public void closeShopperSession(Scenario scenario) {
        Shopper shopper = registeredAccount();
        try {
            if (shopper != null) {
                actor().attemptsTo(DeleteTestAccount.belongingTo(shopper));
            }
        } catch (RuntimeException | AssertionError cleanupError) {
            scenario.log("Could not delete the demo account " + shopper.email() + ": " + cleanupError.getMessage());
            if (!scenario.isFailed()) {
                throw cleanupError;
            }
        } finally {
            OnStage.drawTheCurtain();
        }
    }

    static Actor actor() {
        return OnStage.theActorInTheSpotlight();
    }

    static Shopper registeredAccount() {
        return actor().recall(REGISTERED_ACCOUNT);
    }

    static void rememberAccount(Shopper shopper) {
        actor().remember(REGISTERED_ACCOUNT, shopper);
    }

    static Shopper forgetRegisteredAccount() {
        return actor().forget(REGISTERED_ACCOUNT);
    }
}
