package ch.eifr.lesdarons.pizzaorders.webservice.ws_resources;

import ch.eifr.lesdarons.pizzaorders.webservice.boilerplate.HibernateProxyTypeAdapter;
import ch.eifr.lesdarons.pizzaorders.webservice.boilerplate.HibernateUtil;
import ch.eifr.lesdarons.pizzaorders.webservice.config.SessionFacade;
import ch.eifr.lesdarons.pizzaorders.webservice.entities.IngredientEntity;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Ingredient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/ingredients")
public class Ingredients {
    private Logger logger = LoggerFactory.getLogger("/pizzaorders/webservice/ingredients");

    @GET
    @Produces("text/json")
    public Response getAllIngredients() {
        Session session = HibernateUtil.makeSession();
        Collection<Ingredient> allIngredients = SessionFacade.getAllIngredients(session);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        Gson gson = gsonBuilder.create();
        String output = gson.toJson(allIngredients);
        session.close();

        return Response.ok().entity(output).build();
    }

    @POST
    @Path("add")
    public Response addIngredient(@FormParam("name") String name) {
        if (name == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Name cannot be null").build();
        }
        logger.info("Adding new ingredient with name " + name);
        IngredientEntity ingredientEntity = new IngredientEntity();
        ingredientEntity.setName(name);
        try {
            SessionFacade.save(ingredientEntity);
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity("Ingredient already exists.").build();
        }
        return Response.ok().build();
    }
}
