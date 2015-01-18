package ch.eifr.lesdarons.pizzaorders.webservice.skeleton;

import java.math.BigDecimal;
import java.util.Set;

public interface Pizza {
    String getName();
    BigDecimal getPriceSmall();
    BigDecimal getPriceBig();
    Set<Ingredient> getIngredients();

    public enum Size {
        SMALL, BIG;

        public static Size fromString(String str) { // for Jersey
            if("small".equalsIgnoreCase(str)) {
                return SMALL;
            } else if ("big".equalsIgnoreCase(str)) {
                return BIG;
            } else {
                throw new IllegalArgumentException(str + " is not a known size");
            }
        }
    }
}
