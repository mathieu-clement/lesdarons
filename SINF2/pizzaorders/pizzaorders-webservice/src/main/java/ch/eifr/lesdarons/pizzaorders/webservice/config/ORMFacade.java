package ch.eifr.lesdarons.pizzaorders.webservice.config;

import ch.eifr.lesdarons.pizzaorders.webservice.boilerplate.HibernateUtil;
import ch.eifr.lesdarons.pizzaorders.webservice.entities.IngredientEntity;
import ch.eifr.lesdarons.pizzaorders.webservice.entities.PizzaEntity;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Ingredient;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Pizza;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.*;

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
        Collection<Pizza> pizzas = new LinkedList<>();
        Iterator it = session.createQuery("from ch.eifr.lesdarons.pizzaorders.webservice.entities.PizzaEntity").iterate();
        while (it.hasNext()) {
            pizzas.add((Pizza) it.next());
        }
        return pizzas;
    }

    // Returns null if not found
    public static Pizza findPizza(Session session, String pizzaName) {
        return (Pizza) session.createCriteria(PizzaEntity.class).add(Restrictions.eq("name", pizzaName)).uniqueResult();
    }

    // Returns null if not found
    public static Ingredient findIngredient(Session session, String ingredientName) {
        return (Ingredient) session.createCriteria(IngredientEntity.class).add(Restrictions.eq("name", ingredientName)).uniqueResult();
    }

    public static Collection<Ingredient> getAllIngredients(Session session) {
        session.beginTransaction();
        Collection<Ingredient> ingredients = new LinkedList<>();
        Iterator it = session.createQuery("from ch.eifr.lesdarons.pizzaorders.webservice.entities.IngredientEntity").iterate();
        while (it.hasNext()) {
            ingredients.add((Ingredient) it.next());
        }
        session.getTransaction().commit();
        return ingredients;
    }

    public static Collection<Ingredient> getIngredients(Pizza pizza) {
        Session session = HibernateUtil.makeSession();
        Collection<Ingredient> ingredients = new HashSet<>();
        //session.create
        // TODO Implement
        return null;
    }
}
