package ch.eifr.lesdarons.pizzaorders.webservice.ws_resources;

import ch.eifr.lesdarons.pizzaorders.webservice.orm.HibernateUtil;
import ch.eifr.lesdarons.pizzaorders.webservice.orm.ORMFacade;
import ch.eifr.lesdarons.pizzaorders.webservice.orm.OrderManager;
import ch.eifr.lesdarons.pizzaorders.webservice.orm.boilerplate.GsonForHibernate;
import ch.eifr.lesdarons.pizzaorders.webservice.orm.entities.AddressEntity;
import ch.eifr.lesdarons.pizzaorders.webservice.orm.entities.OrderEntity;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Order;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Pizza;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Date;

@Path("/orders")
public class Orders {
    private Logger logger = LoggerFactory.getLogger("pizzaorders.webservice.orders");

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrders() {
        Session session = HibernateUtil.makeSession();
        Collection<Order> orders = ORMFacade.getOrdersByDateDesc(session);
        String output = GsonForHibernate.getGsonInstance().toJson(orders);
        session.close();
        return Response.ok().entity(output).build();
    }

    @GET
    @Path("{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrder(@PathParam("orderId") long orderId) {
        Session session = HibernateUtil.makeSession();
        Order order = ORMFacade.findOrder(session, orderId);
        Response response;

        if (order != null) {
            String output = GsonForHibernate.getGsonInstance().toJson(order);
            response = Response.ok().entity(output).build();
        } else {
            response = Response.status(Response.Status.NOT_FOUND).entity("Order not found").build();
        }
        session.close();
        return response;
    }

    // This methods returns the ID of the order
    // that can be used to add items to the order later
    @POST // Use POST if resource URL is not known in advance
    // https://jcalcote.wordpress.com/2009/08/06/restful-transactions/
    @Path("begin")
    public Response beginOrder(
            @FormParam("name") String name,
            @FormParam("firstName") String firstName,
            @FormParam("lastName") String lastName,
            @FormParam("streetName") String streetName,
            @FormParam("houseNumber") String houseNumber,
            @FormParam("postalCode") String postalCode,
            @FormParam("city") String city,
            @FormParam("country") String country
    ) {
        if (name == null || firstName == null || lastName == null || streetName == null ||
                houseNumber == null || postalCode == null || postalCode == null ||
                city == null || country == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("One or more fields is missing from the request.").build();
        }

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setDateTime(new Date());
        AddressEntity deliveryAddress = new AddressEntity();
        deliveryAddress.setFirstName(firstName);
        deliveryAddress.setLastName(lastName);
        deliveryAddress.setStreetName(streetName);
        deliveryAddress.setHouseNumber(houseNumber);
        deliveryAddress.setPostalCode(postalCode);
        deliveryAddress.setCity(city);
        deliveryAddress.setCountry(country);
        orderEntity.setDeliveryAddress(deliveryAddress);

        logger.info("Creating (but not saving yet) a new order " + orderEntity);
        OrderManager.getInstance().beginOrder(orderEntity);

        return Response.status(Response.Status.CREATED)
                .header("Location", "" + orderEntity.getId()) // This is REST!
                .entity("{\"id\": " + orderEntity.getId() + "}") // And this is for people who don't understand REST.
                .build();
    }

    @DELETE
    @Path("{orderId}")
    public Response cancelOrder(@PathParam("orderId") long orderId) {
        logger.info("Cancelling order no. " + orderId);
        OrderManager.getInstance().cancel(orderId);
        return Response.ok().build();
    }

    @POST
    @Path("{orderId}/confirm")
    public Response confirmOrder(@PathParam("orderId") long orderId) {
        logger.info("Confirming order no. " + orderId);
        OrderManager.getInstance().persistPermanently(orderId);
        return Response.ok().build();
    }

    @POST
    @Path("{orderId}/{pizzaQty}/{pizzaSize}/{pizzaName}")
    public Response addPizzaToOrder(
            @PathParam("orderId") long orderId,
            @PathParam("pizzaQty") int pizzaQty,
            @PathParam("pizzaSize") Pizza.Size pizzaSize,
            @PathParam("pizzaName") String pizzaName

    ) {
        // No need to check for null because the URL wouldn't match with nulls.

        logger.info("Adding pizza " + pizzaName + " to order no. " + orderId);
        Session session = HibernateUtil.makeSession();
        Pizza pizza = ORMFacade.findPizza(session, pizzaName);
        session.close();
        OrderManager.getInstance().addPizza(orderId, pizza, pizzaSize, pizzaQty);
        return Response.ok().build();
    }
}
