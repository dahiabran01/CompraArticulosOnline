package co.com.equipo.certificacion.automationexercise.stepdefinitions;

import co.com.equipo.certificacion.automationexercise.exceptions.CartContentMismatch;
import co.com.equipo.certificacion.automationexercise.exceptions.CatalogContentMismatch;
import co.com.equipo.certificacion.automationexercise.questions.TheCart;
import co.com.equipo.certificacion.automationexercise.questions.TheProduct;
import co.com.equipo.certificacion.automationexercise.tasks.ManageCart;
import co.com.equipo.certificacion.automationexercise.tasks.StartShopping;
import co.com.equipo.certificacion.automationexercise.userinterfaces.CartPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.Shop;
import co.com.equipo.certificacion.automationexercise.utils.Money;
import co.com.equipo.certificacion.automationexercise.utils.WaitTimeouts;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopAssertions.verifyText;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopperSession.actor;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CartSteps {
    @Given("the shopper has an empty cart")
    public void openEmptyCart() {
        actor().wasAbleTo(StartShopping.at(Shop.CART),
                WaitUntil.the(CartPage.EMPTY, isVisible()).forNoMoreThan(WaitTimeouts.seconds()).seconds());
    }

    @Given("the shopper has {string} with a quantity of {int} in the cart")
    public void prepareCart(String product, int quantity) {
        openEmptyCart();
        addToCart(product, quantity);
        actor().wasAbleTo(WaitUntil.the(CartPage.ROW_FOR.of(product), isVisible()).forNoMoreThan(WaitTimeouts.seconds()).seconds());
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
                seeThat(TheProduct.selectedQuantity(), equalTo(minimum))
                        .orComplainWith(CatalogContentMismatch.class,
                                "AC22: la cantidad inicial del producto no coincide con el minimo esperado"),
                seeThat(TheProduct.minimumQuantity(), equalTo(minimum))
                        .orComplainWith(CatalogContentMismatch.class,
                                "AC22: el atributo minimo del campo cantidad no coincide con lo esperado"));
    }

    @Then("checkout is unavailable from the empty cart")
    public void verifyCheckoutUnavailable() {
        actor().should(seeThat(TheCart.isCheckoutAvailable(), is(false))
                .orComplainWith(CartContentMismatch.class,
                        "AC24: el acceso al checkout sigue disponible con el carrito vacio"));
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
        actor().should(seeThat(TheCart.quantityOf(product), equalTo(quantity))
                .orComplainWith(CartContentMismatch.class,
                        "AC08/AC10/AC18/AC22/AC23/AC25/AC30: la cantidad del producto en el carrito no coincide con la esperada"));
    }

    @Then("the cart shows a unit price of {string} and a subtotal of {string} for {string}")
    public void verifyLinePrices(String unitPrice, String subtotal, String product) {
        actor().should(
                seeThat(TheCart.unitPriceOf(product), equalTo(Money.rupees(unitPrice)))
                        .orComplainWith(CartContentMismatch.class,
                                "AC08/AC18/AC22/AC25/AC30: el precio unitario en el carrito no coincide con el esperado"),
                seeThat(TheCart.subtotalOf(product), equalTo(Money.rupees(subtotal)))
                        .orComplainWith(CartContentMismatch.class,
                                "AC08/AC18/AC22/AC25/AC30: el subtotal en el carrito no coincide con el esperado"));
    }

    @Then("the cart displays {string}")
    public void verifyCartNotice(String message) {
        verifyText(CartPage.EMPTY, message, CartContentMismatch.class,
                "AC09/AC24: no se mostro el aviso esperado de carrito vacio");
    }

    @Then("the cart shows only {string} with a quantity of {int}")
    public void verifyOnlyCartItem(String product, int quantity) {
        actor().should(seeThat(TheCart.productNames(), contains(product))
                .orComplainWith(CartContentMismatch.class,
                        "AC10/AC18/AC22/AC23/AC25/AC30: el carrito no contiene unicamente el producto esperado"));
        verifyCartQuantity(product, quantity);
    }
}
