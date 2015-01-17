package ch.eifr.lesdarons.pizzaorders.webservice.skeleton;

import java.util.Date;

public interface Order {
    long getId();
    Date getDateTime();
    Address getDeliveryAddress();
}
