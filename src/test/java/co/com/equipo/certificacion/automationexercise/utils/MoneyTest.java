package co.com.equipo.certificacion.automationexercise.utils;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class MoneyTest {
    @Test
    public void parsesStandardRupeesFormat() {
        // Arrange
        String text = "Rs. 1000";

        // Act
        int amount = Money.rupees(text);

        // Assert
        assertThat(amount, is(equalTo(1000)));
    }

    @Test
    public void parsesRupeesWithExtraAndNonBreakingSpaces() {
        // Arrange
        String text = "  Rs.\u00a0 500  ";

        // Act
        int amount = Money.rupees(text);

        // Assert
        assertThat(amount, is(equalTo(500)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsTextWithoutDigits() {
        // Arrange
        String text = "Gratis";

        // Act
        Money.rupees(text);

        // Assert: expected IllegalArgumentException
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullText() {
        // Arrange
        String text = null;

        // Act
        Money.rupees(text);

        // Assert: expected IllegalArgumentException
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsBlankText() {
        // Arrange
        String text = "   ";

        // Act
        Money.rupees(text);

        // Assert: expected IllegalArgumentException
    }
}

