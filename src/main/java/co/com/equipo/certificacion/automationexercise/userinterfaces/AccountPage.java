package co.com.equipo.certificacion.automationexercise.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;

public final class AccountPage {
    public static final Target SIGNUP_TITLE = Target.the("sign-up heading").locatedBy(".signup-form h2");
    public static final Target LOGIN_TITLE = Target.the("sign-in heading").locatedBy(".login-form h2");
    public static final Target SIGNUP_NAME = field("signup-name");
    public static final Target SIGNUP_EMAIL = field("signup-email");
    public static final Target SIGNUP_BUTTON = field("signup-button");
    public static final Target SIGNUP_ERROR = Target.the("sign-up error").locatedBy(".signup-form form p");
    public static final Target LOGIN_EMAIL = field("login-email");
    public static final Target LOGIN_PASSWORD = field("login-password");
    public static final Target MISSING_LOGIN_PASSWORD = Target.the("required empty sign-in password")
            .locatedBy("css:[data-qa='login-password']:required:invalid");
    public static final Target LOGIN_BUTTON = field("login-button");
    public static final Target LOGIN_ERROR = Target.the("sign-in error").locatedBy(".login-form form p");
    public static final Target PASSWORD = field("password");
    public static final Target FIRST_NAME = field("first_name");
    public static final Target LAST_NAME = field("last_name");
    public static final Target STREET = field("address");
    public static final Target COUNTRY = field("country");
    public static final Target STATE = field("state");
    public static final Target CITY = field("city");
    public static final Target POSTCODE = field("zipcode");
    public static final Target MOBILE = field("mobile_number");
    public static final Target CREATE = field("create-account");
    public static final Target CREATED = field("account-created");
    public static final Target DELETED = field("account-deleted");
    public static final Target CONTINUE = field("continue-button");

    private AccountPage() {}

    private static Target field(String key) {
        return Target.the(key).locatedBy("[data-qa='" + key + "']");
    }
}
