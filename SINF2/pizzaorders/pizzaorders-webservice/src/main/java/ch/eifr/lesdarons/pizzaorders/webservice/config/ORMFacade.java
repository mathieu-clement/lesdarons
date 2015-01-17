package ch.eifr.lesdarons.pizzaorders.webservice.config;

import ch.eifr.lesdarons.pizzaorders.webservice.boilerplate.HibernateUtil;
import ch.eifr.lesdarons.pizzaorders.webservice.entities.IngredientEntity;
import ch.eifr.lesdarons.pizzaorders.webservice.entities.PizzaEntity;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Ingredient;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Pizza;
import org.hibernate.Session;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class ORMFacade {
    public static void save(Collection<Object> collection) {
        Session session = HibernateUtil.makeSession();
        try {
            session.beginTransaction();
            for (Object o : collection) {
                session.save(o);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }

    public static void save(final Object o) {
        save(new Vector<Object>() {{
            add(o);
        }});
    }

    public static Collection<Pizza> getPizzas(Session session) {
        session.beginTransaction();
        Set<Pizza> pizzas = new HashSet<>(session.createCriteria(PizzaEntity.class).list());
        session.getTransaction().commit();
        return pizzas;
    }

    // Returns null if not found
    public static Pizza findPizza(Session session, String pizzaName) {
        //return (Pizza) session.createCriteria(PizzaEntity.class).add(Restrictions.eq("name", pizzaName)).uniqueResult();
        return (Pizza) session.load(PizzaEntity.class, pizzaName);
    }

    // Returns null if not found
    public static Ingredient findIngredient(Session session, String ingredientName) {
        //return (Ingredient) session.createCriteria(IngredientEntity.class).add(Restrictions.eq("name", ingredientName)).uniqueResult();
        return (Ingredient) session.load(IngredientEntity.class, ingredientName);
    }

    public static Set<Ingredient> getAllIngredients(Session session) {
        session.beginTransaction();
        Set<Ingredient> ingredients = new HashSet<>(session.createCriteria(IngredientEntity.class).list());
        session.getTransaction().commit();
        return ingredients;
    }
}
