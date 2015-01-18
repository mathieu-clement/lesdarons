package ch.eifr.lesdarons.pizzaorders.webservice.ws_resources;

import ch.eifr.lesdarons.pizzaorders.webservice.orm.boilerplate.GsonForHibernate;
import ch.eifr.lesdarons.pizzaorders.webservice.orm.HibernateUtil;
import ch.eifr.lesdarons.pizzaorders.webservice.orm.ORMFacade;
import ch.eifr.lesdarons.pizzaorders.webservice.orm.entities.PizzaEntity;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Order;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Collection;

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

        if(order != null) {
            String output = GsonForHibernate.getGsonInstance().toJson(order);
            response = Response.ok().entity(output).build();
        } else {
            response = Response.status(Response.Status.NOT_FOUND).entity("Order not found").build();
        }
        session.close();
        return response;
    }

    @POST
    @Path("add")
    public Response addOrder(
            @FormParam("name") String name,
            @FormParam("price_small") BigDecimal priceSmall,
            @FormParam("price_big") BigDecimal priceBig
    ) {
        if (name == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Name cannot be null").build();
        }
        if (priceSmall == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Price small cannot be null").build();
        }
        if (priceBig == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Price big cannot be null").build();
        }

        PizzaEntity pizzaEntity = new PizzaEntity();
        pizzaEntity.setName(name);
        pizzaEntity.setPriceBig(priceBig);
        pizzaEntity.setPriceSmall(priceSmall);

        logger.info("Adding new pizza " + pizzaEntity);
        try {
            ORMFacade.save(pizzaEntity);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
            //return Response.status(Response.Status.CONFLICT).entity("Pizza already exists.").build();
        }
        return Response.ok().build();
    }
}
