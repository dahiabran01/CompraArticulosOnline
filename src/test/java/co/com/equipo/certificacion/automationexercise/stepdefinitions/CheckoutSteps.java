package co.com.equipo.certificacion.automationexercise.stepdefinitions;

import co.com.equipo.certificacion.automationexercise.exceptions.AccountStateMismatch;
import co.com.equipo.certificacion.automationexercise.exceptions.CheckoutDataMismatch;
import co.com.equipo.certificacion.automationexercise.exceptions.OrderNotConfirmed;
import co.com.equipo.certificacion.automationexercise.models.PaymentCard;
import co.com.equipo.certificacion.automationexercise.models.Shopper;
import co.com.equipo.certificacion.automationexercise.questions.TheCart;
import co.com.equipo.certificacion.automationexercise.questions.TheOrder;
import co.com.equipo.certificacion.automationexercise.questions.TheOrderConfirmation;
import co.com.equipo.certificacion.automationexercise.tasks.CompleteCheckout;
import co.com.equipo.certificacion.automationexercise.tasks.PayForOrder;
import co.com.equipo.certificacion.automationexercise.userinterfaces.AccountPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.CartPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.Shop;
import co.com.equipo.certificacion.automationexercise.utils.Money;
import co.com.equipo.certificacion.automationexercise.utils.WaitTimeouts;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopAssertions.verifyText;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopperSession.actor;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopperSession.registeredAccount;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;

public class CheckoutSteps {
    @When("the shopper proceeds to checkout")
    public void proceedToCheckout() {
        actor().attemptsTo(CompleteCheckout.proceed());
    }

    @When("the shopper resumes checkout from the cart")
    public void resumeCheckout() {
        actor().attemptsTo(on(Shop.CART), CompleteCheckout.proceed());
    }

    @Then("checkout shows the shopper's registered delivery address")
    public void verifyDeliveryAddress() {
        verifyRegisteredAddress(TheOrder.deliveryAddress(),
                "AC11/AC19/AC26: la direccion de entrega en checkout no coincide con la registrada");
    }

    @Then("checkout shows the shopper's registered delivery and billing addresses")
    public void verifyDeliveryAndBillingAddresses() {
        verifyDeliveryAddress();
        verifyRegisteredAddress(TheOrder.billingAddress(),
                "AC19: la direccion de facturacion en checkout no coincide con la registrada");
    }

    private void verifyRegisteredAddress(Question<String> addressQuestion, String message) {
        Shopper shopper = registeredAccount();
        actor().should(seeThat(addressQuestion, allOf(
                        containsString(shopper.displayName()), containsString(shopper.street()),
                        containsString(shopper.city()), containsString(shopper.state()),
                        containsString(shopper.postcode()), containsString(shopper.country()),
                        containsString(shopper.mobile())))
                .orComplainWith(CheckoutDataMismatch.class, message));
    }

    @Then("the order summary lists only {string} with a quantity of {int} and a subtotal of {string}")
    public void verifySingleOrderProduct(String product, int quantity, String subtotal) {
        actor().should(seeThat(TheCart.productNames(), contains(product))
                .orComplainWith(CheckoutDataMismatch.class,
                        "AC11/AC19/AC26: el resumen del pedido no lista unicamente el producto esperado"));
        verifyOrderLine(product, quantity, subtotal);
    }

    @Then("the order summary lists only {string} and {string}")
    public void verifyOrderProducts(String firstProduct, String secondProduct) {
        actor().should(seeThat(TheCart.productNames(), containsInAnyOrder(firstProduct, secondProduct))
                .orComplainWith(CheckoutDataMismatch.class,
                        "AC14: el resumen del pedido no lista exactamente los productos agregados"));
    }

    @Then("the order summary shows {string} with a quantity of {int} and a subtotal of {string}")
    public void verifyOrderLine(String product, int quantity, String subtotal) {
        actor().should(
                seeThat(TheCart.quantityOf(product), equalTo(quantity))
                        .orComplainWith(CheckoutDataMismatch.class,
                                "AC11/AC14/AC19/AC26: la cantidad en el resumen del pedido no coincide con la esperada"),
                seeThat(TheCart.subtotalOf(product), equalTo(Money.rupees(subtotal)))
                        .orComplainWith(CheckoutDataMismatch.class,
                                "AC11/AC14/AC19/AC26: el subtotal en el resumen del pedido no coincide con el esperado"));
    }

    @Then("the order total is {string}")
    public void verifyOrderTotal(String total) {
        actor().should(seeThat(TheOrder.total(), equalToIgnoringCase(total))
                .orComplainWith(CheckoutDataMismatch.class,
                        "AC11/AC14/AC19/AC26: el total del pedido no coincide con lo agregado al carrito"));
    }

    @When("the shopper submits the order with test payment details")
    public void payForOrder() {
        actor().attemptsTo(PayForOrder.with(PaymentCard.testCardFor(registeredAccount().displayName())));
    }

    @Then("the shop confirms that the order was placed")
    public void verifyOrderConfirmation() {
        actor().should(
                seeThat(TheOrderConfirmation.title(), equalToIgnoringCase("Order Placed!"))
                        .orComplainWith(OrderNotConfirmed.class,
                                "AC12/AC26: no se mostro el titulo de confirmacion del pedido"),
                seeThat(TheOrderConfirmation.message(),
                        equalToIgnoringCase("Congratulations! Your order has been confirmed!"))
                        .orComplainWith(OrderNotConfirmed.class,
                                "AC12/AC26: no se mostro el mensaje de confirmacion del pedido"));
    }

    @Then("checkout prompts the shopper to register or sign in")
    public void verifyGuestCheckoutPrompt() {
        verifyText(CartPage.REGISTER_OR_LOGIN, "Register / Login", CheckoutDataMismatch.class,
                "AC13/AC25: no se mostro el aviso para registrarse o iniciar sesion en checkout");
    }

    @When("the shopper chooses to continue on the cart")
    public void continueOnCart() {
        actor().attemptsTo(on(CartPage.CONTINUE_ON_CART),
                WaitUntil.the(CartPage.REGISTER_OR_LOGIN, isNotVisible()).forNoMoreThan(WaitTimeouts.seconds()).seconds());
    }

    @Then("the checkout prompt closes and the cart remains available")
    public void verifyReturnToCart() {
        actor().should(
                seeThat(TheCart.isGuestCheckoutPromptVisible(), is(false))
                        .orComplainWith(CheckoutDataMismatch.class,
                                "AC25: el modal de inicio de sesion no se cerro al continuar en el carrito"),
                seeThat(TheCart.isCheckoutAvailable(), is(true))
                        .orComplainWith(CheckoutDataMismatch.class,
                                "AC25: el acceso al checkout no siguio disponible en el carrito"));
    }

    @When("the shopper follows the registration or sign-in link")
    public void goToSignupFromCheckout() {
        actor().attemptsTo(on(CartPage.REGISTER_OR_LOGIN));
    }

    @Then("the sign-up form displays the heading {string}")
    public void verifySignupHeading(String heading) {
        verifyText(AccountPage.SIGNUP_TITLE, heading, AccountStateMismatch.class,
                "AC13: no se mostro el encabezado del formulario de registro");
    }
}
