package ch.eifr.lesdarons.pizzaorders.webservice.entities;

import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Ingredient;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Pizza;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "pizzas")
public class PizzaEntity implements Pizza {

    private String name;
    private BigDecimal priceBig;
    private BigDecimal priceSmall;
    //private Set<Ingredient> ingredients;

    public PizzaEntity() {
        // constructor for Hibernate, can be private or package-local.
    }

    @Override
    @Id
    @Basic(optional = false)
    @Column(name = "name", unique = true, nullable = false)
    public String getName() {
        return null;
    }

    @Override
    @Column(name = "price_small", precision = 8, scale = 2)
    public BigDecimal getPriceSmall() {
        return null;
    }

    @Override
    @Column(name = "price_big", precision = 8, scale = 2)
    public BigDecimal getPriceBig() {
        return null;
    }

    /*
    @Override
    public Set<Ingredient> getIngredients() {
        return null;
    }
    */

    public void setName(String name) {
        this.name = name;
    }

    public void setPriceBig(BigDecimal priceBig) {
        this.priceBig = priceBig;
    }

    public void setPriceSmall(BigDecimal priceSmall) {
        this.priceSmall = priceSmall;
    }

    /*
    public void setIngredients(Set<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
    */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PizzaEntity that = (PizzaEntity) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "PizzaEntity{" +
                "name='" + name + '\'' +
                ", priceBig=" + priceBig +
                ", priceSmall=" + priceSmall +
                '}';
    }
}
