package co.com.equipo.certificacion.automationexercise.tasks;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;
import static co.com.equipo.certificacion.automationexercise.userinterfaces.CatalogPage.*;

public final class BrowseCatalog {
    private BrowseCatalog() {}

    public static Performable searchFor(String query) {
        return Task.where("{0} searches the catalog for " + query,
                Enter.theValue(query).into(SEARCH_QUERY), on(SEARCH));
    }

    public static Performable view(String product) {
        return Task.where("{0} opens the details for " + product, on(VIEW_PRODUCT.of(product)));
    }

    public static Performable openSubcategory(String category, String subcategory) {
        return Task.where("{0} opens " + subcategory + " under " + category,
                on(CATEGORY.of(category)),
                on(SUBCATEGORY.of(category, subcategory)));
    }
}
