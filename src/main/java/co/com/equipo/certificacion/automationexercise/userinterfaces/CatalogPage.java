package co.com.equipo.certificacion.automationexercise.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;

public final class CatalogPage {
    public static final Target SEARCH_QUERY = Target.the("product search").locatedBy("#search_product");
    public static final Target SEARCH = Target.the("search button").locatedBy("#submit_search");
    public static final Target RESULTS_TITLE = Target.the("search results heading")
            .locatedBy("//h2[contains(@class,'title') and normalize-space()='Searched Products']");
    public static final Target RESULT_NAMES = Target.the("names of displayed products")
            .locatedBy(".features_items .productinfo p");
    public static final Target CATEGORY = Target.the("{0} category")
            .locatedBy(".left-sidebar a[href='#{0}']");
    public static final Target SUBCATEGORY = Target.the("{1} subcategory under {0}")
            .locatedBy("//div[@id=\"{0}\"]//a[normalize-space()=\"{1}\"]");
    public static final Target CATALOG_TITLE = Target.the("catalog heading").locatedBy(".features_items h2.title");
    public static final Target VIEW_PRODUCT = Target.the("details link for {0}")
            .locatedBy("//div[contains(@class,'product-image-wrapper')][.//div[contains(@class,'productinfo')]/p[normalize-space()=\"{0}\"]]//a[starts-with(@href,'/product_details/')]");
    public static final Target PRODUCT_NAME = Target.the("product name").locatedBy(".product-information h2");
    public static final Target PRICE = Target.the("product price").locatedBy(".product-information span span");
    public static final Target AVAILABILITY = Target.the("availability")
            .locatedBy("//div[contains(@class,'product-information')]/p[b[normalize-space()='Availability:']]");
    public static final Target QUANTITY = Target.the("product quantity").locatedBy("#quantity");
    public static final Target ADD_TO_CART = Target.the("Add to cart").locatedBy(".product-information button.cart");
    public static final Target VIEW_CART = Target.the("View Cart after adding a product")
            .locatedBy("#cartModal a[href='/view_cart']");

    private CatalogPage() {}
}
