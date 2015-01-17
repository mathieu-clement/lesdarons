package ch.eifr.lesdarons.pizzaorders.webservice.orm.entities;

import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Ingredient;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Pizza;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "ingredients")
public class IngredientEntity implements Ingredient {
    private String name;
    //private Set<Pizza> pizzas;

    public IngredientEntity() {
    }

    @Override
    @Id
    @Basic(optional = false)
    @Column(name = "name", unique = true, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
    @ManyToMany(fetch = FetchType.LAZY,
                targetEntity = PizzaEntity.class,
                mappedBy = "ingredients")
    public Set<Pizza> getPizzas() {

        return pizzas;
    }

    public void setPizzas(Set<Pizza> pizzas) {
        this.pizzas = pizzas;
    }
    */
}
