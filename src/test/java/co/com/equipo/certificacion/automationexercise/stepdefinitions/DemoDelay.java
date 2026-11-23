package co.com.equipo.certificacion.automationexercise.stepdefinitions;

import co.com.equipo.certificacion.automationexercise.utils.ConsoleOutput;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Scenario;

public class DemoDelay {
    @AfterStep
    public void pauseForPresentation(Scenario scenario) throws InterruptedException {
        long delay = Long.getLong("demo.delay", 0L);
        if (delay > 0 && !scenario.isFailed()) {
            ConsoleOutput.line("      (pausa demo " + delay + " ms)");
            Thread.sleep(delay);
        }
    }
}
