package ch.eifr.lesdarons.pizzaorders.webservice.entities;

import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Ingredient;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Pizza;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;

@Entity
@Table(name = "pizzas")
public class PizzaEntity implements Pizza {

    private String name;
    private BigDecimal priceBig;
    private BigDecimal priceSmall;
    private Collection<Ingredient> ingredients;

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

    @Override
    public Collection<Ingredient> getIngredients() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriceBig(BigDecimal priceBig) {
        this.priceBig = priceBig;
    }

    public void setPriceSmall(BigDecimal priceSmall) {
        this.priceSmall = priceSmall;
    }

    public void setIngredients(Collection<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
