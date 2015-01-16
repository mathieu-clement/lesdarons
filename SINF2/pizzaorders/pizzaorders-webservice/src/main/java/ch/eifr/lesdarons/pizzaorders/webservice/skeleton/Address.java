package ch.eifr.lesdarons.pizzaorders.webservice.skeleton;

public interface Address {
    // Everything not null, except when mentioned otherwise.

    String getFirstName();
    String getLastName();
    String getStreetName();
    String getHouseNumber(); // can include letters, can be null
    String getPostalCode();  // can include letters, can be null
    String getCity();
    String getCountry();
}
