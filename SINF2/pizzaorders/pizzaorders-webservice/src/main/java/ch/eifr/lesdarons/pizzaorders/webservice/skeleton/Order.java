package ch.eifr.lesdarons.pizzaorders.webservice.skeleton;

import ch.eifr.lesdarons.pizzaorders.webservice.orm.entities.PizzaToOrderAssocEntity;

import java.util.Date;
import java.util.Set;

public interface Order {
    long getId();
    Date getDateTime();
    Address getDeliveryAddress();
    Set<PizzaToOrderAssocEntity> getPizzas();
}
