package co.com.equipo.certificacion.automationexercise.tasks;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;
import static co.com.equipo.certificacion.automationexercise.userinterfaces.AccountPage.*;

public final class SignIn {
    private SignIn() {}

    public static Performable with(String email, String password) {
        return Task.where("{0} signs in to the shop",
                Enter.theValue(email).into(LOGIN_EMAIL),
                Enter.theValue(password).into(LOGIN_PASSWORD),
                on(LOGIN_BUTTON));
    }
}
