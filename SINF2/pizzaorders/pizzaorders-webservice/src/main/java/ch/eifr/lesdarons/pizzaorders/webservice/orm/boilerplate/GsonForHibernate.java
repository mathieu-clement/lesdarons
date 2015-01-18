package ch.eifr.lesdarons.pizzaorders.webservice.orm.boilerplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonForHibernate {
    private static final Gson GSON_INSTANCE;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        GSON_INSTANCE = gsonBuilder.create();
    }

    public static Gson getGsonInstance() {
        return GSON_INSTANCE;
    }
}
