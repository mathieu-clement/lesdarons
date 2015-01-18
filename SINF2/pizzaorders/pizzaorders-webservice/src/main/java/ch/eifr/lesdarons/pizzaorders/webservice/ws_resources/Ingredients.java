package ch.eifr.lesdarons.pizzaorders.webservice.ws_resources;

import ch.eifr.lesdarons.pizzaorders.webservice.orm.boilerplate.GsonForHibernate;
import ch.eifr.lesdarons.pizzaorders.webservice.orm.HibernateUtil;
import ch.eifr.lesdarons.pizzaorders.webservice.orm.ORMFacade;
import ch.eifr.lesdarons.pizzaorders.webservice.orm.entities.IngredientEntity;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Ingredient;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Pizza;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/ingredients")
public class Ingredients {
    private Logger logger = LoggerFactory.getLogger("pizzaorders.webservice.ingredients");

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllIngredients() {
        Session session = HibernateUtil.makeSession();
        Collection<Ingredient> allIngredients = ORMFacade.getAllIngredients(session);

        String output = GsonForHibernate.getGsonInstance().toJson(allIngredients);
        session.close();

        return Response.ok().entity(output).build();
    }

    @POST
    @Path("forPizza/{pizzaName}/{ingredientName}")
    public Response addIngredientToPizza(@PathParam("pizzaName") String pizzaName,
                                         @PathParam("ingredientName") String ingredientName) {
        logger.info("Adding ingredient '" + ingredientName + "' to pizza '" + pizzaName + "'");
        Session session = HibernateUtil.makeSession();
        Pizza pizza = ORMFacade.findPizza(session, pizzaName);
        Response response;

        if (pizza != null) {
            Ingredient ingredient = ORMFacade.findIngredient(session, ingredientName);
            if (ingredient != null) {
                pizza.getIngredients().add(ingredient);
                session.beginTransaction();
                session.update(pizza);
                session.getTransaction().commit();
                response = Response.ok().build();
            } else {
                response = Response.status(Response.Status.NOT_FOUND).entity("Ingredient was not found").build();
            }
        } else {
            response = Response.status(Response.Status.NOT_FOUND).entity("Pizza was not found").build();
        }

        session.close();

        return response;
    }

    @PUT
    @Path("add")
    public Response addIngredient(@FormParam("name") String name) {
        if (name == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Name cannot be null").build();
        }
        logger.info("Adding new ingredient with name " + name);
        IngredientEntity ingredientEntity = new IngredientEntity();
        ingredientEntity.setName(name);
        try {
            ORMFacade.save(ingredientEntity);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.CONFLICT).entity(
                    "Error processing request. Maybe the ingredient already exists.").build();
        }
        return Response.ok().build();
    }
}
