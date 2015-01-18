package ch.eifr.lesdarons.pizzaorders.webservice.orm.entities;

import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Order;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Pizza;

import javax.persistence.*;

@Entity
@Table(name = "assoc_pizza_order")
public class PizzaToOrderAssocEntity {
    private PizzaToOrderAssocId assocId;

    /*
    @MapsId("name")
    @ManyToOne(targetEntity = PizzaEntity.class)
    Pizza pizza;

    @MapsId("id")
    @ManyToOne(targetEntity = OrderEntity.class)
    Order order;
    */

    private int quantity;
    private Pizza.Size pizzaSize;

    public PizzaToOrderAssocEntity() {
    }

    @EmbeddedId
    public PizzaToOrderAssocId getAssocId() {
        return assocId;
    }

    public void setAssocId(PizzaToOrderAssocId assocId) {
        this.assocId = assocId;
    }

    @Column
    public int getQuantity() {
        return quantity;
    }

    @Column(name = "pizza_size")
    public Pizza.Size getPizzaSize() {
        return pizzaSize;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPizzaSize(Pizza.Size pizzaSize) {
        this.pizzaSize = pizzaSize;
    }
}
