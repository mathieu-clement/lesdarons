package ch.eifr.lesdarons.pizzaorders.webservice.config;

import ch.eifr.lesdarons.pizzaorders.webservice.boilerplate.HibernateUtil;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Ingredient;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Pizza;
import org.hibernate.Session;

import java.util.*;

public class SessionFacade {
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

    public static Collection<Pizza> getPizzas() {
        Session session = HibernateUtil.makeSession();
        Collection<Pizza> pizzas = new LinkedList<>();
        Iterator it = session.createQuery("from ch.eifr.lesdarons.pizzaorders.webservice.entities.PizzaEntity").iterate();
        while (it.hasNext()) {
            pizzas.add((Pizza) it.next());
        }
        session.close();
        return pizzas;
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
