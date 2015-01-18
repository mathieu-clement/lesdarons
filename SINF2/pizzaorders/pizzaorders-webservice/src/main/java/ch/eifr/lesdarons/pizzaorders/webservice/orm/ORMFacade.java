package ch.eifr.lesdarons.pizzaorders.webservice.orm;

import ch.eifr.lesdarons.pizzaorders.webservice.orm.entities.IngredientEntity;
import ch.eifr.lesdarons.pizzaorders.webservice.orm.entities.OrderEntity;
import ch.eifr.lesdarons.pizzaorders.webservice.orm.entities.PizzaEntity;
import ch.eifr.lesdarons.pizzaorders.webservice.orm.entities.PizzaToOrderAssocEntity;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Ingredient;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Order;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Pizza;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ORMFacade {
    private static Logger logger = LoggerFactory.getLogger("pizzaorders.webservice.ormfacade");

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
        Set<Pizza> pizzas = new HashSet<>(session.createCriteria(PizzaEntity.class).list());
        return pizzas;
    }

    // Returns null if not found
    public static Pizza findPizza(Session session, String pizzaName) {
        //return (Pizza) session.createCriteria(PizzaEntity.class).add(Restrictions.eq("name", pizzaName)).uniqueResult();
        return (Pizza) session.load(PizzaEntity.class, pizzaName);
    }

    public static Set<Ingredient> getAllIngredients(Session session) {
        Set<Ingredient> ingredients = new HashSet<>(session.createCriteria(IngredientEntity.class).list());
        return ingredients;
    }

    // Returns null if not found
    public static Ingredient findIngredient(Session session, String ingredientName) {
        //return (Ingredient) session.createCriteria(IngredientEntity.class).add(Restrictions.eq("name", ingredientName)).uniqueResult();
        return (Ingredient) session.load(IngredientEntity.class, ingredientName);
    }

    public static List<Order> getOrdersByDateDesc(Session session) {
        List<Order> orders = session.createQuery("from OrderEntity order by dateTime desc ").list();
        for (Order order : orders) {
            OrderEntity orderEntity = (OrderEntity) order;
            List list = getAssociatedPizzas(session, order.getId()).list();
            Set<PizzaToOrderAssocEntity> associatedPizzas = new LinkedHashSet<>();
            for (Object o : list) {
                associatedPizzas.add((PizzaToOrderAssocEntity) o);
            }
            orderEntity.setPizzaAssocs(associatedPizzas);
        }
        return orders;
    }

    // Returns null if not found
    public static Order findOrder(Session session, long orderId) {
        OrderEntity order = (OrderEntity) session.load(OrderEntity.class, orderId);
        List list = getAssociatedPizzas(session, order.getId()).list();
        Set<PizzaToOrderAssocEntity> associatedPizzas = new LinkedHashSet<>();
        for (Object o : list) {
            associatedPizzas.add((PizzaToOrderAssocEntity) o);
        }
        order.setPizzaAssocs(associatedPizzas);
        Order ret = order;
        return ret;
    }

    private static Query getAssociatedPizzas(Session session, long orderId) {
        return session
                .createQuery("from PizzaToOrderAssocEntity where assocId.order.id = :orderId")
                .setParameter("orderId", orderId);
    }
}
