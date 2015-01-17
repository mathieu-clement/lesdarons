package ch.eifr.lesdarons.pizzaorders.webservice.skeleton;

import java.math.BigDecimal;

public interface Pizza {
    String getName();
    BigDecimal getPriceSmall();
    BigDecimal getPriceBig();
    //Collection<Ingredient> getIngredients();
}
