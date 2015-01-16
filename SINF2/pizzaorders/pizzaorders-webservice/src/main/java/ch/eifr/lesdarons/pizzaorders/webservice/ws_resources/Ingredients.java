package ch.eifr.lesdarons.pizzaorders.webservice.ws_resources;

import ch.eifr.lesdarons.pizzaorders.webservice.config.SessionFacade;
import ch.eifr.lesdarons.pizzaorders.webservice.entities.IngredientEntity;
import ch.eifr.lesdarons.pizzaorders.webservice.skeleton.Ingredient;
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
        Collection<Ingredient> allIngredients = SessionFacade.getAllIngredients();
        StringBuilder sb = new StringBuilder("[");
        for (Ingredient ingredient : allIngredients) {
            sb.append("{\"name\":\"");
            sb.append(ingredient.getName());
            sb.append("\"},");
        }
        if (!allIngredients.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(']');
        return Response.ok().entity(sb.toString()).build();
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
