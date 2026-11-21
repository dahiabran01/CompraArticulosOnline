package co.com.equipo.certificacion.automationexercise.tasks;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;
import static co.com.equipo.certificacion.automationexercise.userinterfaces.CartPage.REMOVE;
import static co.com.equipo.certificacion.automationexercise.userinterfaces.CartPage.ROW_FOR;
import static co.com.equipo.certificacion.automationexercise.userinterfaces.CatalogPage.*;
import static co.com.equipo.certificacion.automationexercise.userinterfaces.Shop.PRODUCTS;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;

public final class ManageCart {
    private ManageCart() {}

    public static Performable add(String product, int quantity) {
        return Task.where("{0} adds " + quantity + " unit(s) of " + product + " to the cart",
                on(PRODUCTS),
                BrowseCatalog.view(product),
                Enter.theValue(Integer.toString(quantity)).into(QUANTITY),
                addFromDetails());
    }

    public static Performable addFromDetails() {
        return Task.where("{0} adds the product from its details and opens the cart",
                on(ADD_TO_CART), on(VIEW_CART));
    }

    public static Performable remove(String product) {
        return Task.where("{0} removes " + product + " from the cart",
                on(REMOVE.of(product)), WaitUntil.the(ROW_FOR.of(product), isNotVisible()));
    }
}
