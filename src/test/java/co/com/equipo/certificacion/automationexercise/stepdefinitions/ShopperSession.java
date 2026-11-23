package co.com.equipo.certificacion.automationexercise.stepdefinitions;

import co.com.equipo.certificacion.automationexercise.models.Shopper;
import co.com.equipo.certificacion.automationexercise.questions.TheSite;
import co.com.equipo.certificacion.automationexercise.tasks.DeleteTestAccount;
import co.com.equipo.certificacion.automationexercise.utils.FailureDiagnosis;
import co.com.equipo.certificacion.automationexercise.utils.FailureDiagnosis.Diagnosis;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import net.serenitybdd.core.Serenity;
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
        if (scenario.isFailed()) {
            recordSiteOverloadDiagnosis(scenario);
        }
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

    /**
     * Deja en el reporte de Serenity el diagnostico en espanol cuando el escenario fallo porque la tienda
     * mostro su pagina de sobrecarga; sin esto, el reporte solo muestra el error tecnico del paso.
     */
    private static void recordSiteOverloadDiagnosis(Scenario scenario) {
        try {
            String notice = actor().asksFor(TheSite.overloadNotice());
            if (notice.isEmpty()) {
                return;
            }
            Diagnosis diagnosis = FailureDiagnosis.siteUnavailable(notice);
            String explanation = "Categoria: " + diagnosis.category() + System.lineSeparator()
                    + "Causa probable: " + diagnosis.probableCause() + System.lineSeparator()
                    + "Que revisar: " + diagnosis.whatToCheck() + System.lineSeparator()
                    + "Pagina mostrada: " + diagnosis.detail();
            Serenity.recordReportData().withTitle("Diagnostico del fallo").andContents(explanation);
            scenario.log(explanation);
        } catch (RuntimeException diagnosisError) {
            scenario.log("No se pudo registrar el diagnostico de sobrecarga: " + diagnosisError.getMessage());
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
