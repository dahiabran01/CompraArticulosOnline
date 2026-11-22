package co.com.equipo.certificacion.automationexercise.models;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CartItemTest {
    @Test
    public void confirmsConsistentSubtotalMatchesUnitPriceTimesQuantity() {
        // Arrange
        CartItem item = new CartItem("Blue Top", 500, 10, 5000);

        // Act
        boolean consistent = item.hasConsistentSubtotal();

        // Assert
        assertThat(consistent, is(true));
    }

    @Test
    public void rejectsSubtotalThatDoesNotMatchUnitPriceTimesQuantity() {
        // Arrange
        CartItem item = new CartItem("Blue Top", 500, 3, 1400);

        // Act
        boolean consistent = item.hasConsistentSubtotal();

        // Assert
        assertThat(consistent, is(false));
    }

    @Test
    public void rejectsNonPositiveQuantityOrUnitPrice() {
        // Arrange
        CartItem zeroQuantity = new CartItem("Blue Top", 500, 0, 0);
        CartItem zeroPrice = new CartItem("Blue Top", 0, 2, 0);

        // Act
        boolean validQuantity = zeroQuantity.hasConsistentSubtotal();
        boolean validPrice = zeroPrice.hasConsistentSubtotal();

        // Assert
        assertThat(validQuantity, is(false));
        assertThat(validPrice, is(false));
    }
}
