package co.com.equipo.certificacion.automationexercise.runners.automationexercise;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "classpath:features/automationexercise/checkout.feature",
        glue = "co.com.equipo.certificacion.automationexercise.stepdefinitions",
        tags = "@e2e",
        plugin = "pretty"
)
public class PurchaseRunner {}
