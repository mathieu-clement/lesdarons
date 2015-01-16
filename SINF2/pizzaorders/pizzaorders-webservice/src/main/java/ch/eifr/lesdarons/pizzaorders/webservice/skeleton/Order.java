package ch.eifr.lesdarons.pizzaorders.webservice.skeleton;

import org.joda.time.DateTime;

public interface Order {
    long getId();
    DateTime getDateTime();
    Address getDeliveryAddress();
}
