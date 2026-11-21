package co.com.equipo.certificacion.automationexercise.questions;

import co.com.equipo.certificacion.automationexercise.models.CartItem;
import co.com.equipo.certificacion.automationexercise.userinterfaces.CartPage;
import co.com.equipo.certificacion.automationexercise.utils.Money;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Preguntas de negocio sobre las filas y controles del carrito o resumen de pedido.
 */
public final class TheCart {
    private TheCart() {}

    public static Question<List<CartItem>> items() {
        return Question.about("items in the cart").answeredBy(TheCart::readItems);
    }

    public static Question<List<String>> productNames() {
        return Question.about("product names in the cart")
                .answeredBy(actor -> readItems(actor).stream().map(CartItem::name).toList());
    }

    public static Question<Integer> quantityOf(String product) {
        return Question.about("quantity of " + product + " in the cart")
                .answeredBy(actor -> findItem(actor, product).map(CartItem::quantity).orElse(0));
    }

    public static Question<Integer> unitPriceOf(String product) {
        return Question.about("unit price of " + product + " in the cart")
                .answeredBy(actor -> findItem(actor, product).map(CartItem::unitPrice).orElse(0));
    }

    public static Question<Integer> subtotalOf(String product) {
        return Question.about("subtotal of " + product + " in the cart")
                .answeredBy(actor -> findItem(actor, product).map(CartItem::subtotal).orElse(0));
    }

    public static Question<Boolean> isCheckoutAvailable() {
        return Question.about("checkout availability")
                .answeredBy(actor -> CartPage.PROCEED.isVisibleFor(actor));
    }

    public static Question<Boolean> isGuestCheckoutPromptVisible() {
        return Question.about("guest checkout prompt visibility")
                .answeredBy(actor -> CartPage.REGISTER_OR_LOGIN.isVisibleFor(actor));
    }

    private static List<CartItem> readItems(Actor actor) {
        List<WebElementFacade> rows = CartPage.ROWS.resolveAllFor(actor);
        List<CartItem> items = new ArrayList<>();
        for (WebElementFacade row : rows) {
            String name = normalize(row.findBy(By.cssSelector(".cart_description h4 a")).getText());
            int unitPrice = Money.rupees(row.findBy(By.cssSelector(".cart_price p")).getText());
            int quantity = Integer.parseInt(normalize(row.findBy(By.cssSelector(".cart_quantity")).getText()));
            int subtotal = Money.rupees(row.findBy(By.cssSelector(".cart_total_price")).getText());
            items.add(new CartItem(name, unitPrice, quantity, subtotal));
        }
        return items;
    }

    private static Optional<CartItem> findItem(Actor actor, String product) {
        return readItems(actor).stream()
                .filter(item -> item.name().equalsIgnoreCase(product))
                .findFirst();
    }

    private static String normalize(String text) {
        return text == null ? "" : text.replaceAll("[\\s\\u00a0]+", " ").trim();
    }
}

