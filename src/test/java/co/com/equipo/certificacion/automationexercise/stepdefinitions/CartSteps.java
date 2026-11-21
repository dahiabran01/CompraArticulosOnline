package co.com.equipo.certificacion.automationexercise.stepdefinitions;

import co.com.equipo.certificacion.automationexercise.questions.DisplayedText;
import co.com.equipo.certificacion.automationexercise.tasks.ManageCart;
import co.com.equipo.certificacion.automationexercise.tasks.StartShopping;
import co.com.equipo.certificacion.automationexercise.userinterfaces.CartPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.CatalogPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.Shop;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopAssertions.verifyText;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopperSession.actor;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

public class CartSteps {
    @Given("the shopper has an empty cart")
    public void openEmptyCart() {
        actor().wasAbleTo(StartShopping.at(Shop.CART), WaitUntil.the(CartPage.EMPTY, isVisible()));
    }

    @Given("the shopper has {string} with a quantity of {int} in the cart")
    public void prepareCart(String product, int quantity) {
        openEmptyCart();
        addToCart(product, quantity);
        actor().wasAbleTo(WaitUntil.the(CartPage.ROW_FOR.of(product), isVisible()));
    }

    @When("the shopper adds {string} with a quantity of {int} and opens the cart")
    public void addToCart(String product, int quantity) {
        actor().attemptsTo(ManageCart.add(product, quantity));
    }

    @When("the shopper adds the displayed product without changing its quantity")
    public void addDefaultQuantity() {
        actor().attemptsTo(ManageCart.addFromDetails());
    }

    @When("the shopper follows the empty cart link to the catalog")
    public void returnToCatalog() {
        actor().attemptsTo(on(CartPage.CONTINUE_SHOPPING));
    }

    @Then("the product quantity starts at its minimum of {string}")
    public void verifyDefaultQuantity(String minimum) {
        actor().should(
                seeThat("the default quantity", shopper -> CatalogPage.QUANTITY.resolveFor(shopper).getValue(),
                        equalTo(minimum)),
                seeThat("the minimum quantity", shopper -> CatalogPage.QUANTITY.resolveFor(shopper).getDomAttribute("min"),
                        equalTo(minimum)));
    }

    @Then("checkout is unavailable from the empty cart")
    public void verifyCheckoutUnavailable() {
        actor().should(seeThat(the(CartPage.PROCEED), isNotVisible()));
    }

    @When("the shopper opens the cart")
    public void openCart() {
        actor().attemptsTo(on(Shop.CART));
    }

    @When("the shopper removes {string} from the cart")
    public void removeFromCart(String product) {
        actor().attemptsTo(ManageCart.remove(product));
    }

    @Then("the cart shows {string} with a quantity of {int}")
    public void verifyCartQuantity(String product, int quantity) {
        verifyText(CartPage.QUANTITY.of(product), Integer.toString(quantity));
    }

    @Then("the cart shows a unit price of {string} and a subtotal of {string} for {string}")
    public void verifyLinePrices(String unitPrice, String subtotal, String product) {
        verifyText(CartPage.UNIT_PRICE.of(product), unitPrice);
        verifyText(CartPage.SUBTOTAL.of(product), subtotal);
    }

    @Then("the cart displays {string}")
    public void verifyCartNotice(String message) {
        verifyText(CartPage.EMPTY, message);
    }

    @Then("the cart shows only {string} with a quantity of {int}")
    public void verifyOnlyCartItem(String product, int quantity) {
        actor().should(seeThat(DisplayedText.ofAll(CartPage.PRODUCT_NAMES), contains(product)));
        verifyCartQuantity(product, quantity);
    }
}
