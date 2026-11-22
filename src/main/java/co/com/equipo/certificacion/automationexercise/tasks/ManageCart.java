package co.com.equipo.certificacion.automationexercise.tasks;

import co.com.equipo.certificacion.automationexercise.interactions.TypeWhenReady;
import co.com.equipo.certificacion.automationexercise.userinterfaces.CartPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.CatalogPage;
import co.com.equipo.certificacion.automationexercise.userinterfaces.Shop;
import co.com.equipo.certificacion.automationexercise.utils.WaitTimeouts;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;

public final class ManageCart {
    private ManageCart() {}

    public static Performable add(String product, int quantity) {
        return Task.where("{0} adds " + quantity + " unit(s) of " + product + " to the cart",
                on(Shop.PRODUCTS),
                BrowseCatalog.view(product),
                TypeWhenReady.theValue(Integer.toString(quantity)).into(CatalogPage.QUANTITY),
                addFromDetails());
    }

    public static Performable addFromDetails() {
        return Task.where("{0} adds the product from its details and opens the cart",
                on(CatalogPage.ADD_TO_CART),
                on(CatalogPage.VIEW_CART));
    }

    public static Performable remove(String product) {
        return Task.where("{0} removes " + product + " from the cart",
                on(CartPage.REMOVE.of(product)),
                WaitUntil.the(CartPage.ROW_FOR.of(product), isNotVisible()).forNoMoreThan(WaitTimeouts.cartRemovalSeconds()).seconds());
    }
}
