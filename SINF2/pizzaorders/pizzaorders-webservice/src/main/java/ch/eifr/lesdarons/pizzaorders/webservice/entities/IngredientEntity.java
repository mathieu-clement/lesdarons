package ch.eifr.lesdarons.pizzaorders.webservice.entities;

import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Ingredient;

import javax.persistence.*;

@Entity
@Table(name = "ingredients")
public class IngredientEntity implements Ingredient {
    private String name;

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
}
