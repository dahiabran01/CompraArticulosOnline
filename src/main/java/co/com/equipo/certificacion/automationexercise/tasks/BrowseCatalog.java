package co.com.equipo.certificacion.automationexercise.tasks;

import co.com.equipo.certificacion.automationexercise.interactions.TypeWhenReady;
import co.com.equipo.certificacion.automationexercise.userinterfaces.CatalogPage;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;

import static co.com.equipo.certificacion.automationexercise.interactions.ClickWhenReady.on;

public final class BrowseCatalog {
    private BrowseCatalog() {}

    public static Performable searchFor(String query) {
        return Task.where("{0} searches the catalog for " + query,
                TypeWhenReady.theValue(query).into(CatalogPage.SEARCH_QUERY),
                on(CatalogPage.SEARCH));
    }

    public static Performable view(String product) {
        return Task.where("{0} opens the details for " + product, on(CatalogPage.VIEW_PRODUCT.of(product)));
    }

    public static Performable openSubcategory(String category, String subcategory) {
        return Task.where("{0} opens " + subcategory + " under " + category,
                on(CatalogPage.CATEGORY.of(category)),
                on(CatalogPage.SUBCATEGORY.of(category, subcategory)));
    }
}
