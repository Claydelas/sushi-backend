package comp1206.sushi;

import comp1206.sushi.client.Client;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Restaurant;
import comp1206.sushi.common.User;
import comp1206.sushi.comms.ClientComms;
import comp1206.sushi.comms.Message;
import comp1206.sushi.comms.ServerComms;
import comp1206.sushi.server.Server;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Unit test for simple App.
 */
public class AppTest
    extends TestCase
{
    private static final Logger logger = LogManager.getLogger("Testing");
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
        //loadConfig();
        //testClient();
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    public void loadConfig() {
        Server server = new Server();
        server.loadConfiguration("Configuration.txt");

        logger.log(Level.INFO, "Server status:");
        logger.log(Level.INFO, "Postcodes : " + server.getPostcodes());
        logger.log(Level.INFO, "Restaurant : " + server.getRestaurantName() + " <---> " + server.getRestaurantPostcode());
        server.getSuppliers().forEach(supplier -> logger.log(Level.INFO, "Supplier : " + supplier.getName() + " <---> " + supplier.getPostcode()));
        server.getIngredients().forEach(ingredient -> logger.log(Level.INFO, "Ingredient : " + ingredient.getName() + " -> " + ingredient.getUnit() + " -> " + ingredient.getSupplier() + " -> " + ingredient.getRestockThreshold() + " -> " + ingredient.getRestockAmount() + " -> " + ingredient.getWeight()));
        server.getDishes().forEach(dish -> logger.log(Level.INFO, "Dish : " + dish.getName() + " -> " + dish.getDescription() + " -> " + dish.getPrice() + " -> " + dish.getRestockThreshold() + " -> " + dish.getRestockAmount() + " -> " + dish.getRecipe()));
        server.getUsers().forEach(user -> logger.log(Level.INFO, "User : " + user.getName() + " -> " + user.getPassword() + " -> " + user.getAddress() + " -> " + user.getPostcode()));
        server.getOrders().forEach(order -> logger.log(Level.INFO, "Order : " + order.getUser() + " -> " + order.getItems()));
        logger.log(Level.INFO, "Staff : " + server.getStaff());
        logger.log(Level.INFO, "Postcodes : " + server.getDrones());
    }
    public void testComms(){
        Server server = new Server();
        Client client = new Client();
//        System.out.println(client.getRestaurantPostcode() + " <---> " + server.getRestaurantPostcode());
//        System.out.println(client.register("hi", "hi", "hi", new Postcode("SO16 3ZE")));
//        System.out.println(client.register("hi", "hi", "hi", new Postcode("SO17 1BJ")));
//        server.loadConfiguration("Configuration.txt");
//        assertEquals(client.getRestaurantName(),server.getRestaurantName());
//        assertEquals(client.getRestaurantPostcode().getName(),server.getRestaurantPostcode().getName());
//        client.getOrders(new User("Admin","password","hi",new Postcode("SO16 3ZE"))).forEach(order -> System.out.println(order.getItems()));
        server.getOrders().forEach(Order::setComplete);
        server.getOrders().forEach(order -> {
            if(client.isOrderComplete(order))
                System.out.println("true");
            else System.out.println("false");
        });
//        server.getUsers().forEach(user -> System.out.println(client.getOrders(user)));
//        System.out.println(client.getPostcodes());
//        System.out.println(client.getDishes());
//        System.out.println(client.getRestaurantPostcode() + " <---> " + server.getRestaurantPostcode());
//        System.out.println(client.register("hi", "hi", "hi", new Postcode("SO16 3ZE",client.getRestaurant())).getDistance());
//        System.out.println(client.register("hi", "hi", "hi", new Postcode("SO17 1BJ",client.getRestaurant())).getDistance());
    }
    public void testBasicComms(){
        Server server = new Server();
        server.loadConfiguration("Configuration.txt");
        ClientComms clientComms = new ClientComms("127.0.0.1",8888);
        clientComms.sendMessage(new Message(Message.USERS,"GET"));
        System.out.println(clientComms.receiveMessage());
    }

    public void testConnect(){
        //ClientComms clientComms = new ClientComms("127.0.0.1",8888);
        //clientComms.sendMessage(new Message(Message.GET_RESTAURANT,"bye"));
    }
}
