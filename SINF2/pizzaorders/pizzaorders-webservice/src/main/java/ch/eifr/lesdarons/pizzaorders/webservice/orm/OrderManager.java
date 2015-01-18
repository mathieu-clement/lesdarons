package ch.eifr.lesdarons.pizzaorders.webservice.orm;

import ch.eifr.lesdarons.pizzaorders.webservice.orm.entities.PizzaToOrderAssocEntity;
import ch.eifr.lesdarons.pizzaorders.webservice.orm.entities.PizzaToOrderAssocId;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Order;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Pizza;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Map;
import java.util.TreeMap;

// A manager for long-running orders, staying available between requests.
// This class is not responsbible for closing orders or rolling them back.
// Failure to do so will result in memory leaks due to strong references kept by the class.
public class OrderManager {
    private static final OrderManager INSTANCE = new OrderManager();
    private Map<Long, OrderEntry> orders = new TreeMap<>();

    private OrderManager() {

    }

    public static OrderManager getInstance() {
        return INSTANCE;
    }

    // One call to this method must equal one call to cancel or commit.
    public OrderEntry beginOrder(Order order) {

        Session session = HibernateUtil.makeSession();
        Transaction transaction = session.getTransaction();
        transaction.begin();
        OrderEntry orderEntry;
        synchronized (this) {
            orderEntry = new OrderEntry(session, transaction, order);
            session.save(order);
            session.flush();
            assert order.getId() != -1;
            orders.put(order.getId(), orderEntry);
        }
        return orderEntry;

    }

    // Returns null if not found
    public OrderEntry get(long orderId) {
        return orders.get(orderId);
    }

    public void cancel(long orderId) {
        OrderEntry orderEntry = orders.get(orderId);
        orderEntry.getTransaction().rollback();
        orderEntry.getSession().close();

        orders.remove(orderId);
    }

    public void persistPermanently(long orderId) {
        OrderEntry orderEntry = orders.get(orderId);
        orderEntry.getTransaction().commit();
        orderEntry.getSession().close();

        orders.remove(orderId);
    }

    public void addPizza(long orderId, Pizza pizza, Pizza.Size pizzaSize, int quantity) {
        OrderEntry orderEntry = orders.get(orderId);
        Order order = orderEntry.getOrder();

        PizzaToOrderAssocEntity assocEntity = new PizzaToOrderAssocEntity();
        assocEntity.setPizzaSize(pizzaSize);
        assocEntity.setQuantity(quantity);

        PizzaToOrderAssocId assocId = new PizzaToOrderAssocId();
        assocId.setPizza(pizza);
        assocId.setOrder(order);
        assocEntity.setAssocId(assocId);

        orderEntry.getSession().save(assocEntity);
    }

    public static class OrderEntry {
        private Session session;
        private Transaction transaction;
        private Order order;

        private OrderEntry(Session session, Transaction transaction, Order order) {
            this.session = session;
            this.transaction = transaction;
            this.order = order;
        }

        public Session getSession() {
            return session;
        }

        public Transaction getTransaction() {
            return transaction;
        }

        public Order getOrder() {
            return order;
        }
    }


}
