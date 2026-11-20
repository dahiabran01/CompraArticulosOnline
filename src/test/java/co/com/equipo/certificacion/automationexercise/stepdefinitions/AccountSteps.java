package co.com.equipo.certificacion.automationexercise.stepdefinitions;

import co.com.equipo.certificacion.automationexercise.models.Shopper;
import co.com.equipo.certificacion.automationexercise.tasks.DeleteTestAccount;
import co.com.equipo.certificacion.automationexercise.tasks.RegisterAccount;
import co.com.equipo.certificacion.automationexercise.tasks.SignIn;
import co.com.equipo.certificacion.automationexercise.tasks.StartShopping;
import co.com.equipo.certificacion.automationexercise.userinterfaces.AccountPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.Shop;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopAssertions.verifyText;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopperSession.actor;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopperSession.forgetRegisteredAccount;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopperSession.registeredAccount;
import static co.com.equipo.certificacion.automationexercise.stepdefinitions.ShopperSession.rememberAccount;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;

public class AccountSteps {
    private Shopper deletedAccount;

    @Given("the shopper is on the sign-up page")
    public void openSignup() {
        actor().wasAbleTo(StartShopping.at(Shop.ACCOUNT));
    }

    @When("the shopper registers a new account")
    public void register() {
        Shopper shopper = Shopper.newAccount();
        actor().attemptsTo(RegisterAccount.forShopper(shopper));
        actor().attemptsTo(WaitUntil.the(AccountPage.CREATED, isVisible()));
        rememberAccount(shopper);
    }

    @Then("the shop displays {string}")
    public void verifyRegistration(String heading) {
        verifyText(AccountPage.CREATED, heading);
    }

    @When("the shopper continues from the account confirmation")
    public void continueAfterRegistration() {
        actor().attemptsTo(RegisterAccount.continueShopping());
    }

    @Then("the header shows the registered shopper's name")
    public void verifyShopperName() {
        verifyText(Shop.SHOPPER_NAME, registeredAccount().displayName());
    }

    @Given("the shopper has just registered and is signed in")
    public void createSignedInAccount() {
        openSignup();
        register();
        continueAfterRegistration();
        verifyShopperName();
    }

    @Given("the shopper has registered an account and signed out")
    public void createAccountAndSignOut() {
        createSignedInAccount();
        signOut();
    }

    @When("the shopper signs out")
    public void signOut() {
        actor().attemptsTo(on(Shop.LOGOUT));
    }

    @Then("the sign-in page is displayed and the shopper is signed out")
    public void verifySignedOut() {
        verifyText(AccountPage.LOGIN_TITLE, "Login to your account");
        actor().should(seeThat(the(Shop.ACCOUNT), isVisible()),
                seeThat(the(Shop.SHOPPER_NAME), isNotVisible()));
    }

    @When("the shopper deletes the account")
    public void deleteAccount() {
        actor().attemptsTo(DeleteTestAccount.belongingTo(registeredAccount()));
        deletedAccount = forgetRegisteredAccount();
    }

    @Then("the shop confirms that the account was deleted")
    public void verifyAccountDeletion() {
        verifyText(AccountPage.DELETED, "Account Deleted!");
    }

    @When("the shopper continues and tries to sign in with the deleted account")
    public void signInAfterDeletion() {
        actor().attemptsTo(on(AccountPage.CONTINUE), on(Shop.ACCOUNT),
                SignIn.with(deletedAccount.email(), deletedAccount.password()));
    }

    @When("the shopper opens the sign-in page")
    public void openSignIn() {
        actor().attemptsTo(on(Shop.ACCOUNT));
    }

    @When("the shopper starts sign-up with the registered email")
    public void attemptDuplicateSignup() {
        actor().attemptsTo(RegisterAccount.startFor(registeredAccount()));
    }

    @When("the shopper signs in with the registered credentials")
    public void signIn() {
        Shopper shopper = registeredAccount();
        actor().attemptsTo(SignIn.with(shopper.email(), shopper.password()));
    }

    @When("the shopper signs in with an incorrect password")
    public void signInWithWrongPassword() {
        actor().attemptsTo(SignIn.with(registeredAccount().email(), "Different-Demo-Password!"));
    }

    @Then("the sign-up form displays the error {string}")
    public void verifySignupError(String message) {
        verifyText(AccountPage.SIGNUP_ERROR, message);
    }

    @Then("the sign-in form displays {string}")
    public void verifySignInMessage(String message) {
        verifyText(AccountPage.LOGIN_ERROR, message);
    }
}
