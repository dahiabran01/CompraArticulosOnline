package co.com.equipo.certificacion.automationexercise.stepdefinitions;

import co.com.equipo.certificacion.automationexercise.models.Shopper;
import co.com.equipo.certificacion.automationexercise.questions.DisplayedText;
import co.com.equipo.certificacion.automationexercise.tasks.CompleteCheckout;
import co.com.equipo.certificacion.automationexercise.userinterfaces.AccountPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.CartPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.CheckoutPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.Shop;
import co.com.equipo.certificacion.automationexercise.utils.WaitTimeouts;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopAssertions.verifyText;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopperSession.actor;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopperSession.registeredAccount;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;

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
        verifyRegisteredAddress(CheckoutPage.DELIVERY_ADDRESS);
    }

    @Then("checkout shows the shopper's registered delivery and billing addresses")
    public void verifyDeliveryAndBillingAddresses() {
        verifyDeliveryAddress();
        verifyRegisteredAddress(CheckoutPage.BILLING_ADDRESS);
    }

    private void verifyRegisteredAddress(Target address) {
        Shopper shopper = registeredAccount();
        actor().attemptsTo(WaitUntil.the(address, isVisible()).forNoMoreThan(WaitTimeouts.seconds()).seconds());
        actor().should(seeThat(DisplayedText.of(address), allOf(
                containsString(shopper.displayName()), containsString(shopper.street()),
                containsString(shopper.city()), containsString(shopper.state()),
                containsString(shopper.postcode()), containsString(shopper.country()),
                containsString(shopper.mobile()))));
    }

    @Then("the order summary lists only {string} with a quantity of {int} and a subtotal of {string}")
    public void verifySingleOrderProduct(String product, int quantity, String subtotal) {
        actor().should(seeThat(DisplayedText.ofAll(CartPage.PRODUCT_NAMES), contains(product)));
        verifyOrderLine(product, quantity, subtotal);
    }

    @Then("the order summary lists only {string} and {string}")
    public void verifyOrderProducts(String firstProduct, String secondProduct) {
        actor().should(seeThat(DisplayedText.ofAll(CartPage.PRODUCT_NAMES),
                containsInAnyOrder(firstProduct, secondProduct)));
    }

    @Then("the order summary shows {string} with a quantity of {int} and a subtotal of {string}")
    public void verifyOrderLine(String product, int quantity, String subtotal) {
        verifyText(CartPage.QUANTITY.of(product), Integer.toString(quantity));
        verifyText(CartPage.SUBTOTAL.of(product), subtotal);
    }

    @Then("the order total is {string}")
    public void verifyOrderTotal(String total) {
        verifyText(CheckoutPage.ORDER_TOTAL, total);
    }

    @When("the shopper submits the order with test payment details")
    public void payForOrder() {
        actor().attemptsTo(CompleteCheckout.payWithTestCard(registeredAccount().displayName()));
    }

    @Then("the shop confirms that the order was placed")
    public void verifyOrderConfirmation() {
        verifyText(CheckoutPage.ORDER_PLACED, "Order Placed!");
        verifyText(CheckoutPage.CONFIRMATION, "Congratulations! Your order has been confirmed!");
    }

    @Then("checkout prompts the shopper to register or sign in")
    public void verifyGuestCheckoutPrompt() {
        verifyText(CartPage.REGISTER_OR_LOGIN, "Register / Login");
    }

    @When("the shopper chooses to continue on the cart")
    public void continueOnCart() {
        actor().attemptsTo(on(CartPage.CONTINUE_ON_CART), WaitUntil.the(CartPage.REGISTER_OR_LOGIN, isNotVisible()).forNoMoreThan(WaitTimeouts.seconds()).seconds());
    }

    @Then("the checkout prompt closes and the cart remains available")
    public void verifyReturnToCart() {
        actor().should(seeThat(the(CartPage.REGISTER_OR_LOGIN), isNotVisible()),
                seeThat(the(CartPage.PROCEED), isVisible()));
    }

    @When("the shopper follows the registration or sign-in link")
    public void goToSignupFromCheckout() {
        actor().attemptsTo(on(CartPage.REGISTER_OR_LOGIN));
    }

    @Then("the sign-up form displays the heading {string}")
    public void verifySignupHeading(String heading) {
        verifyText(AccountPage.SIGNUP_TITLE, heading);
    }
}
