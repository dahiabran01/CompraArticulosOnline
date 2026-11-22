package co.com.equipo.certificacion.automationexercise.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Locale;

/**
 * Consultas sobre el estado general del sitio mostrado en el navegador.
 */
public final class TheSite {
    private TheSite() {}

    public static Question<String> overloadNotice() {
        return Question.about("website overload notice").answeredBy(actor -> {
            WebDriver driver = activeDriverOf(actor);
            if (driver == null) {
                return "";
            }
            String title = normalize(driver.getTitle());
            if (containsOverloadText(title)) {
                return title;
            }
            List<WebElement> headings = driver.findElements(By.tagName("h2"));
            for (WebElement heading : headings) {
                String text = normalize(heading.getText());
                if (containsOverloadText(text)) {
                    return text;
                }
            }
            return "";
        });
    }

    public static Question<Boolean> isOverloaded() {
        return Question.about("whether the website displays an overload notice")
                .answeredBy(actor -> !overloadNotice().answeredBy(actor).isEmpty());
    }

    /**
     * Tras un fallo de paso, Serenity suspende WebDriverFacade (webdriverCallsAreSuspended)
     * haciendo que getTitle() y findElements() devuelvan vacio. Desenvolver getProxiedDriver()
     * solo cuando isInstantiated() es true permite leer el DOM real sin abrir Chrome en dry-run.
     */
    private static WebDriver activeDriverOf(Actor actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        if (driver instanceof WebDriverFacade facade) {
            return facade.isInstantiated() ? facade.getProxiedDriver() : null;
        }
        return driver;
    }

    private static boolean containsOverloadText(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        return lower.contains("under heavy load") || lower.contains("queue full");
    }

    private static String normalize(String text) {
        return text == null ? "" : text.replaceAll("[\\s\\u00a0]+", " ").trim();
    }
}
