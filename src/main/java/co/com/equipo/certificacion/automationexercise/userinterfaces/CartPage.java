package co.com.equipo.certificacion.automationexercise.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;

public final class CartPage {
    private static final String ROW = "//table[@id='cart_info_table']//tr[td[@class='cart_description']//a[normalize-space()=\"{0}\"]]";
    public static final Target EMPTY = Target.the("empty cart notice").locatedBy("#empty_cart b");
    public static final Target PRODUCT_NAMES = Target.the("cart products").locatedBy("#cart_info_table .cart_description h4 a");
    public static final Target QUANTITY = Target.the("cart quantity for {0}")
            .locatedBy(ROW + "//td[@class='cart_quantity']");
    public static final Target UNIT_PRICE = Target.the("unit price for {0}")
            .locatedBy(ROW + "//td[@class='cart_price']/p");
    public static final Target SUBTOTAL = Target.the("subtotal for {0}")
            .locatedBy(ROW + "//p[contains(@class,'cart_total_price')]");
    public static final Target REMOVE = Target.the("remove {0}").locatedBy(ROW + "//a[contains(@class,'cart_quantity_delete')]");
    public static final Target ROW_FOR = Target.the("cart row for {0}").locatedBy(ROW);
    public static final Target PROCEED = Target.the("Proceed To Checkout").locatedBy(".check_out");
    public static final Target CONTINUE_SHOPPING = Target.the("product catalog link in the empty cart")
            .locatedBy("#empty_cart a[href='/products']");
    public static final Target CONTINUE_ON_CART = Target.the("Continue On Cart in the checkout prompt")
            .locatedBy("#checkoutModal button[data-dismiss='modal']");
    public static final Target REGISTER_OR_LOGIN = Target.the("Register / Login in the checkout prompt")
            .locatedBy("#checkoutModal a[href='/login']");

    private CartPage() {}
}
