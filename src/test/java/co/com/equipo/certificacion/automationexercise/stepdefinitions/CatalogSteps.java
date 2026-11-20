package co.com.equipo.certificacion.automationexercise.stepdefinitions;

import co.com.equipo.certificacion.automationexercise.questions.DisplayedText;
import co.com.equipo.certificacion.automationexercise.tasks.BrowseCatalog;
import co.com.equipo.certificacion.automationexercise.tasks.StartShopping;
import co.com.equipo.certificacion.automationexercise.userinterfaces.CatalogPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.Shop;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopAssertions.verifyText;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopperSession.actor;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

public class CatalogSteps {
    @Given("the shopper is viewing the product catalog")
    public void openCatalog() {
        actor().wasAbleTo(StartShopping.at(Shop.PRODUCTS));
    }

    @When("the shopper searches for {string}")
    public void search(String query) {
        actor().attemptsTo(BrowseCatalog.searchFor(query), WaitUntil.the(CatalogPage.RESULTS_TITLE, isVisible()));
    }

    @Then("the search results include {string}")
    public void verifyProductFound(String product) {
        actor().should(seeThat(DisplayedText.ofAll(CatalogPage.RESULT_NAMES), hasItem(product)));
    }

    @Then("the search results contain no products")
    public void verifyNoMatches() {
        actor().should(seeThat(DisplayedText.ofAll(CatalogPage.RESULT_NAMES), empty()));
    }

    @When("the shopper opens the details for {string}")
    public void viewProduct(String product) {
        actor().attemptsTo(BrowseCatalog.view(product));
    }

    @Then("the product details show {string} at {string} as {string}")
    public void verifyProductDetails(String product, String price, String availability) {
        verifyText(CatalogPage.PRODUCT_NAME, product);
        verifyText(CatalogPage.PRICE, price);
        verifyText(CatalogPage.AVAILABILITY, "Availability: " + availability);
    }

    @When("the shopper opens the {string} subcategory under {string}")
    public void openSubcategory(String subcategory, String category) {
        actor().attemptsTo(BrowseCatalog.openSubcategory(category, subcategory));
    }

    @Then("the catalog displays the heading {string}")
    public void verifyCatalogHeading(String heading) {
        verifyText(CatalogPage.CATALOG_TITLE, heading);
    }

    @Then("the category results include {string} and exclude {string}")
    public void verifyCategoryResults(String included, String excluded) {
        actor().should(seeThat(DisplayedText.ofAll(CatalogPage.RESULT_NAMES),
                allOf(hasItem(included), not(hasItem(excluded)))));
    }
}
