package ch.eifr.lesdarons.pizzaorders.webservice.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.servlet.ServletException;

public class MyServletContainer extends ServletContainer {

    private static MyServletContainer INSTANCE;

    private SessionFactory sessionFactory;

    public MyServletContainer() {
        super();
        registerInstance();
    }

    public MyServletContainer(ResourceConfig resourceConfig) {
        super(resourceConfig);
        registerInstance();
    }

    private void registerInstance() {
        if(INSTANCE == null) {
            INSTANCE = this;
        } else {
            throw new Error("register instance called twice.");
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @Override
    public void destroy() {
        super.destroy();
        if(sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public static MyServletContainer getInstance() {
        return INSTANCE;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
