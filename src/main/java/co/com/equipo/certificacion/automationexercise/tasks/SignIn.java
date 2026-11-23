package co.com.equipo.certificacion.automationexercise.tasks;

import co.com.equipo.certificacion.automationexercise.interactions.TypeWhenReady;
import co.com.equipo.certificacion.automationexercise.userinterfaces.AccountPage;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;

public final class SignIn {
    private SignIn() {}

    public static Performable withoutPassword(String email) {
        return Task.where("{0} attempts to sign in without a password",
                TypeWhenReady.theValue(email).into(AccountPage.LOGIN_EMAIL),
                on(AccountPage.LOGIN_BUTTON));
    }

    public static Performable with(String email, String password) {
        return Task.where("{0} signs in to the shop",
                TypeWhenReady.theValue(email).into(AccountPage.LOGIN_EMAIL),
                TypeWhenReady.theSecret(password).into(AccountPage.LOGIN_PASSWORD),
                on(AccountPage.LOGIN_BUTTON));
    }
}
