package co.com.equipo.certificacion.automationexercise.runners.automationexercise;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "classpath:features/automationexercise",
        glue = "co.com.equipo.certificacion.automationexercise.stepdefinitions",
        plugin = {"co.com.equipo.certificacion.automationexercise.utils.ExecutionProgressReporter"},
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class AutomationExerciseRunner {}
