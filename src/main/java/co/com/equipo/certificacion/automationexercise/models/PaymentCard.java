package co.com.equipo.certificacion.automationexercise.models;

import java.time.Year;

/**
 * Datos de tarjeta ficticia para simular el pago en checkout sin exponer valores en logs.
 */
public record PaymentCard(String holder, String number, String cvc, String expiryMonth, String expiryYear) {
    private static final String TEST_NUMBER = "4111111111111111";
    private static final String TEST_CVC = "123";
    private static final String TEST_EXPIRY_MONTH = "12";

    /**
     * Construye los datos de tarjeta de prueba para el titular indicado.
     */
    public static PaymentCard testCardFor(String holder) {
        String expiryYear = Year.now().plusYears(2).toString();
        return new PaymentCard(holder, TEST_NUMBER, TEST_CVC, TEST_EXPIRY_MONTH, expiryYear);
    }

    @Override
    public String toString() {
        return "PaymentCard[holder=" + holder + ", number=****, cvc=***, expiry=**/**]";
    }
}

