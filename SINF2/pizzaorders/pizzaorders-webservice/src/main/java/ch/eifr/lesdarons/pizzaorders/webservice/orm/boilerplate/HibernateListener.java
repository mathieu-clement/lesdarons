package ch.eifr.lesdarons.pizzaorders.webservice.orm.boilerplate;

import ch.eifr.lesdarons.pizzaorders.webservice.orm.HibernateUtil;
import ch.eifr.lesdarons.pizzaorders.webservice.ws_resources.Ingredients;
import ch.eifr.lesdarons.pizzaorders.webservice.ws_resources.Pizzas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class HibernateListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger("pizzaorders.webservice.HibernateListener");

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        HibernateUtil.getSessionFactory();  // call static initializer

        try {
            populateDatabase(sce.getServletContext());
            logger.info("Database populated successfully!");
        } catch (Exception e) {
            logger.error("Could not populate DB", e);
        }
    }

    private void populateDatabase(ServletContext servletContext) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        Ingredients ingredientsResource = new Ingredients();
        Pizzas pizzasResource = new Pizzas();

        InputStream is = servletContext.getResourceAsStream("/WEB-INF/pizzas.xml");
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        doc.normalize();

        Set<String> addedPizzaNames = new HashSet<>();
        Set<String> addedIngredientNames = new HashSet<>();

        XPathExpression ingredientXPath = XPathFactory.newInstance().newXPath().compile("//ingredient");
        NodeList ingredientNodeList = (NodeList) ingredientXPath.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < ingredientNodeList.getLength(); i++) {
            Node node = ingredientNodeList.item(i);
            if(node.getNodeType() != Node.ELEMENT_NODE) continue;

            Element element = (Element) node;
            String ingredientName = element.getTextContent();

            logger.info("Ingredient name: " + ingredientName);

            if (!addedIngredientNames.contains(ingredientName)) {
                ingredientsResource.addIngredient(ingredientName);
                addedIngredientNames.add(ingredientName);
            }

            // Look for the pizza (grandparent)
            Node pizzaNode = node.getParentNode().getParentNode();
            NodeList pizzaChildNodes = pizzaNode.getChildNodes();
            String pizzaName = null;
            String pizzaSmallPrice = null;
            String pizzaBigPrice = null;

            for (int j = 0; j < pizzaChildNodes.getLength(); j++) {
                Node pizzaChildNode = pizzaChildNodes.item(j);
                if ("name".equals(pizzaChildNode.getNodeName())) {
                    pizzaName = pizzaChildNode.getTextContent();
                } else if ("price".equals(pizzaChildNode.getNodeName())) {
                    NamedNodeMap pizzaChildNodeAttributes = pizzaChildNode.getAttributes();
                    pizzaSmallPrice = pizzaChildNodeAttributes.getNamedItem("small").getTextContent();
                    pizzaBigPrice = pizzaChildNodeAttributes.getNamedItem("big").getTextContent();
                }
            } // end for pizza child nodes

            logger.info("Pizza name: " + pizzaName);

            if (!addedPizzaNames.contains(pizzaName)) {
                // Add pizza to database
                pizzasResource.addPizza(pizzaName, new BigDecimal(pizzaSmallPrice), new BigDecimal(pizzaBigPrice));
                addedPizzaNames.add(pizzaName);
            }

            // Associate ingredient with pizza
            ingredientsResource.addIngredientToPizza(pizzaName, ingredientName);

        } // end for ingredient nodes
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        HibernateUtil.getSessionFactory().close();
    }
}
