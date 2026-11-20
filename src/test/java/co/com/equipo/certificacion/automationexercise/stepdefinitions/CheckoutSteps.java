package co.com.equipo.certificacion.automationexercise.stepdefinitions;

import co.com.equipo.certificacion.automationexercise.models.Shopper;
import co.com.equipo.certificacion.automationexercise.questions.DisplayedText;
import co.com.equipo.certificacion.automationexercise.tasks.CompleteCheckout;
import co.com.equipo.certificacion.automationexercise.userinterfaces.AccountPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.CartPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.CheckoutPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.targets.Target;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopAssertions.verifyText;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopperSession.actor;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopperSession.registeredAccount;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;

public class CheckoutSteps {
    @When("the shopper proceeds to checkout")
    public void proceedToCheckout() {
        actor().attemptsTo(CompleteCheckout.proceed());
    }

    @Then("checkout shows the shopper's registered delivery address")
    public void verifyDeliveryAddress() {
        verifyRegisteredAddress(CheckoutPage.DELIVERY_ADDRESS);
    }

    @Then("checkout shows the shopper's registered billing address")
    public void verifyBillingAddress() {
        verifyRegisteredAddress(CheckoutPage.BILLING_ADDRESS);
    }

    private void verifyRegisteredAddress(Target address) {
        Shopper shopper = registeredAccount();
        actor().should(seeThat(DisplayedText.of(address), allOf(
                containsString(shopper.displayName()), containsString(shopper.street()),
                containsString(shopper.city()), containsString(shopper.state()),
                containsString(shopper.postcode()), containsString(shopper.country()),
                containsString(shopper.mobile()))));
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

    @Then("the order page displays the heading {string}")
    public void verifyOrderHeading(String heading) {
        verifyText(CheckoutPage.ORDER_PLACED, heading);
    }

    @Then("the order page displays the confirmation {string}")
    public void verifyOrderConfirmation(String confirmation) {
        verifyText(CheckoutPage.CONFIRMATION, confirmation);
    }

    @Then("checkout prompts the shopper to register or sign in")
    public void verifyGuestCheckoutPrompt() {
        verifyText(CartPage.REGISTER_OR_LOGIN, "Register / Login");
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
