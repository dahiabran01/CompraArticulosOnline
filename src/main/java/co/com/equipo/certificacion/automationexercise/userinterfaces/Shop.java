package co.com.equipo.certificacion.automationexercise.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;

public final class Shop {
    public static final Target ACCOUNT = Target.the("Signup / Login").locatedBy("header a[href='/login']");
    public static final Target PRODUCTS = Target.the("Products").locatedBy("header a[href='/products']");
    public static final Target CART = Target.the("Cart").locatedBy("header a[href='/view_cart']");
    public static final Target LOGOUT = Target.the("Logout").locatedBy("header a[href='/logout']");
    public static final Target DELETE_ACCOUNT = Target.the("Delete Account").locatedBy("header a[href='/delete_account']");
    public static final Target SHOPPER_NAME = Target.the("signed-in shopper's name").locatedBy("header .shop-menu a b");

    private Shop() {}
}
