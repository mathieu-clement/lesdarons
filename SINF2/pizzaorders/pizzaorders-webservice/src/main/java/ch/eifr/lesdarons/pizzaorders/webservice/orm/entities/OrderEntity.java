package ch.eifr.lesdarons.pizzaorders.webservice.orm.entities;

import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Address;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Order;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "orders")
public class OrderEntity implements Order {
    private long id;
    private Date dateTime;
    private Address deliveryAddress;

    public OrderEntity() {
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    @Override
    @Column
    public Date getDateTime() {
        return dateTime;
    }

    @Override
    @ManyToOne(targetEntity = AddressEntity.class,
            cascade = CascadeType.REMOVE,
            fetch = FetchType.EAGER)
    public Address getDeliveryAddress() {
        return deliveryAddress;
    }
}
