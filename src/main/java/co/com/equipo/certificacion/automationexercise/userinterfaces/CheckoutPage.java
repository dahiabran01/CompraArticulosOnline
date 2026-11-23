package co.com.equipo.certificacion.automationexercise.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;

public final class CheckoutPage {
    public static final Target DELIVERY_ADDRESS = Target.the("delivery address").locatedBy("#address_delivery");
    public static final Target BILLING_ADDRESS = Target.the("billing address").locatedBy("#address_invoice");
    public static final Target ORDER_TOTAL = Target.the("order total")
            .locatedBy("//td[h4[normalize-space()='Total Amount']]/following-sibling::td//p");
    public static final Target PLACE_ORDER = Target.the("Place Order").locatedBy("css:a[href='/payment']");
    public static final Target CARDHOLDER = paymentField("name-on-card");
    public static final Target CARD_NUMBER = paymentField("card-number");
    public static final Target MISSING_CARD_NUMBER = Target.the("required empty card number")
            .locatedBy("css:[data-qa='card-number']:required:invalid");
    public static final Target CVC = paymentField("cvc");
    public static final Target EXPIRY_MONTH = paymentField("expiry-month");
    public static final Target EXPIRY_YEAR = paymentField("expiry-year");
    public static final Target PAY = paymentField("pay-button");
    public static final Target ORDER_PLACED = paymentField("order-placed");
    public static final Target CONFIRMATION = Target.the("order confirmation message")
            .locatedBy("//h2[@data-qa='order-placed']/following-sibling::p[1]");

    private CheckoutPage() {}

    private static Target paymentField(String key) {
        return Target.the(key).locatedBy("[data-qa='" + key + "']");
    }
}
