package co.com.equipo.certificacion.automationexercise.stepdefinitions;

import co.com.equipo.certificacion.automationexercise.exceptions.AccountStateMismatch;
import co.com.equipo.certificacion.automationexercise.models.Shopper;
import co.com.equipo.certificacion.automationexercise.questions.TheShopper;
import co.com.equipo.certificacion.automationexercise.tasks.DeleteTestAccount;
import co.com.equipo.certificacion.automationexercise.tasks.RegisterAccount;
import co.com.equipo.certificacion.automationexercise.tasks.SignIn;
import co.com.equipo.certificacion.automationexercise.tasks.StartShopping;
import co.com.equipo.certificacion.automationexercise.userinterfaces.AccountPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.CartPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.Shop;
import co.com.equipo.certificacion.automationexercise.utils.WaitTimeouts;
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
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;

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
        actor().attemptsTo(WaitUntil.the(AccountPage.CREATED, isVisible()).forNoMoreThan(WaitTimeouts.seconds()).seconds());
        rememberAccount(shopper);
    }

    @Then("the shop displays {string}")
    public void verifyRegistration(String heading) {
        verifyText(AccountPage.CREATED, heading, AccountStateMismatch.class,
                "AC01: no se mostro el mensaje esperado tras crear la cuenta");
    }

    @When("the shopper continues from the account confirmation")
    public void continueAfterRegistration() {
        actor().attemptsTo(RegisterAccount.continueShopping());
    }

    @Then("the header shows the registered shopper's name")
    public void verifyShopperName() {
        actor().should(seeThat(TheShopper.signedInName(), equalToIgnoringCase(registeredAccount().displayName()))
                .orComplainWith(AccountStateMismatch.class,
                        "AC01/AC03/AC18: el encabezado no muestra el nombre del comprador autenticado"));
    }

    @Given("the shopper has just registered and is signed in")
    public void createSignedInAccount() {
        openSignup();
        completeRegistration();
    }

    @Given("the shopper is signed in with a new account")
    public void registerFromStorefront() {
        openSignIn();
        completeRegistration();
    }

    @Given("the shopper has an account and is browsing as a guest")
    public void prepareReturningGuest() {
        registerFromStorefront();
        signOut();
    }

    @When("the shopper registers from the checkout prompt")
    public void registerFromCheckout() {
        actor().attemptsTo(on(CartPage.REGISTER_OR_LOGIN));
        completeRegistration();
    }

    @When("the shopper signs in from the checkout prompt")
    public void signInFromCheckout() {
        actor().attemptsTo(on(CartPage.REGISTER_OR_LOGIN));
        signIn();
        verifyShopperName();
    }

    private void completeRegistration() {
        register();
        verifyRegistration("Account Created!");
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
        verifyText(AccountPage.LOGIN_TITLE, "Login to your account", AccountStateMismatch.class,
                "AC04/AC15/AC20: no se mostro el formulario de inicio de sesion");
        actor().should(
                seeThat(TheShopper.isSignInLinkVisible(), is(true))
                        .orComplainWith(AccountStateMismatch.class,
                                "AC04/AC15/AC20: no se mostro el enlace para iniciar sesion"),
                seeThat(TheShopper.isSignedIn(), is(false))
                        .orComplainWith(AccountStateMismatch.class,
                                "AC04/AC15/AC20: el comprador sigue autenticado en el encabezado"));
    }

    @When("the shopper deletes the account")
    public void deleteAccount() {
        actor().attemptsTo(DeleteTestAccount.belongingTo(registeredAccount()));
        deletedAccount = forgetRegisteredAccount();
    }

    @Then("the shop confirms that the account was deleted")
    public void verifyAccountDeletion() {
        verifyText(AccountPage.DELETED, "Account Deleted!", AccountStateMismatch.class,
                "AC16: la tienda no confirmo la eliminacion de la cuenta");
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
        verifyText(AccountPage.SIGNUP_ERROR, message, AccountStateMismatch.class,
                "AC02: no se mostro el error esperado para correo ya registrado");
    }

    @When("the shopper submits the sign-in form with an email and no password")
    public void signInWithoutPassword() {
        actor().attemptsTo(SignIn.withoutPassword(Shopper.newAccount().email()));
    }

    @Then("the password is required and the shopper remains signed out")
    public void verifyMissingPassword() {
        verifySignedOut();
        actor().should(seeThat(TheShopper.isMissingSignInPassword(), is(true))
                .orComplainWith(AccountStateMismatch.class,
                        "AC20: el campo de contrasena no se marco como obligatorio e invalido"));
    }

    @Then("the sign-in form displays {string}")
    public void verifySignInMessage(String message) {
        verifyText(AccountPage.LOGIN_ERROR, message, AccountStateMismatch.class,
                "AC04/AC16: no se mostro el mensaje esperado de credenciales invalidas");
    }
}
