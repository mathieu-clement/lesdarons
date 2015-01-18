package ch.eifr.lesdarons.pizzaorders.webservice.orm.entities;

import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Order;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Pizza;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class PizzaToOrderAssocId implements Serializable {
    private Order order;
    private Pizza pizza;

    //@Id
    @ManyToOne(targetEntity = OrderEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    public Order getOrder() {
        return order;
    }

    //@Id
    @ManyToOne(targetEntity = PizzaEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "pizza_name")
    public Pizza getPizza() {
        return pizza;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PizzaToOrderAssocId that = (PizzaToOrderAssocId) o;

        if (!order.equals(that.order)) return false;
        if (!pizza.equals(that.pizza)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = order.hashCode();
        result = 31 * result + pizza.hashCode();
        return result;
    }
}
