package co.com.equipo.certificacion.automationexercise.stepdefinitions;

import co.com.equipo.certificacion.automationexercise.exceptions.CatalogContentMismatch;
import co.com.equipo.certificacion.automationexercise.models.ProductDetails;
import co.com.equipo.certificacion.automationexercise.questions.DisplayedText;
import co.com.equipo.certificacion.automationexercise.questions.TheProduct;
import co.com.equipo.certificacion.automationexercise.tasks.BrowseCatalog;
import co.com.equipo.certificacion.automationexercise.tasks.StartShopping;
import co.com.equipo.certificacion.automationexercise.userinterfaces.CatalogPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.Shop;
import co.com.equipo.certificacion.automationexercise.utils.WaitTimeouts;
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

public class CatalogSteps {
    @Given("the shopper is viewing the product catalog")
    public void openCatalog() {
        actor().wasAbleTo(StartShopping.at(Shop.PRODUCTS));
    }

    @When("the shopper searches for {string}")
    public void search(String query) {
        actor().attemptsTo(BrowseCatalog.searchFor(query),
                WaitUntil.the(CatalogPage.RESULTS_TITLE, isVisible()).forNoMoreThan(WaitTimeouts.seconds()).seconds());
    }

    @Then("the search results include {string}")
    public void verifyProductFound(String product) {
        actor().should(seeThat(DisplayedText.ofAll(CatalogPage.RESULT_NAMES), hasItem(product))
                .orComplainWith(CatalogContentMismatch.class,
                        "AC05/AC21: los resultados de busqueda no incluyen el producto esperado"));
    }

    @Then("the search results contain no products")
    public void verifyNoMatches() {
        actor().should(seeThat(DisplayedText.ofAll(CatalogPage.RESULT_NAMES), empty())
                .orComplainWith(CatalogContentMismatch.class,
                        "AC06: la busqueda sin coincidencias mostro productos inesperados"));
    }

    @When("the shopper opens the details for {string}")
    public void viewProduct(String product) {
        actor().attemptsTo(BrowseCatalog.view(product));
    }

    @Then("the product details show {string} at {string} as {string}")
    public void verifyProductDetails(String product, String price, String availability) {
        actor().should(seeThat(TheProduct.details(), equalTo(new ProductDetails(product, price, availability)))
                .orComplainWith(CatalogContentMismatch.class,
                        "AC07/AC21: la ficha del producto no muestra el nombre, precio o disponibilidad esperados"));
    }

    @When("the shopper opens the {string} subcategory under {string}")
    public void openSubcategory(String subcategory, String category) {
        actor().attemptsTo(BrowseCatalog.openSubcategory(category, subcategory));
    }

    @Then("the catalog displays the heading {string}")
    public void verifyCatalogHeading(String heading) {
        verifyText(CatalogPage.CATALOG_TITLE, heading, CatalogContentMismatch.class,
                "AC17/AC23: el encabezado del catalogo no coincide con el esperado");
    }

    @Then("the category results include {string} and exclude {string}")
    public void verifyCategoryResults(String included, String excluded) {
        actor().should(seeThat(DisplayedText.ofAll(CatalogPage.RESULT_NAMES),
                        allOf(hasItem(included), not(hasItem(excluded))))
                .orComplainWith(CatalogContentMismatch.class,
                        "AC17: la categoria filtrada no incluye o excluye los productos esperados"));
    }
}
