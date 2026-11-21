package co.com.equipo.certificacion.automationexercise.models;

import java.util.UUID;

public record Shopper(String firstName, String lastName, String email, String password,
                      String street, String city, String state, String postcode,
                      String country, String mobile) {
    public static Shopper newAccount() {
        return new Shopper("Alex", "Rivera", "ae-qa-" + UUID.randomUUID() + "@example.com",
                "Demo-Clothes-2026!", "25 Oak Street", "Austin", "Texas", "78701",
                "United States", "5125550100");
    }

    public String displayName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "Shopper[name=" + displayName() + ", email=" + email + ", password=****]";
    }
}
