package ch.eifr.lesdarons.pizzaorders.webservice.skeleton;

import java.math.BigDecimal;
import java.util.Set;

public interface Pizza {
    String getName();
    BigDecimal getPriceSmall();
    BigDecimal getPriceBig();
    Set<Ingredient> getIngredients();

    public enum Size {
        SMALL, BIG
    }
}
