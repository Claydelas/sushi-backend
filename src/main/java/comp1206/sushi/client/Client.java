package comp1206.sushi.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp1206.sushi.common.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client implements ClientInterface {

    private static final Logger logger = LogManager.getLogger("Client");

    public ArrayList<User> users = new ArrayList<>();
    public ArrayList<Order> orders = new ArrayList<>();
    public ArrayList<Dish> dishes = new ArrayList<>();
    private ArrayList<UpdateListener> listeners = new ArrayList<>();
    private ClientComms client;

    public Client() {
        logger.info("Starting up client...");
        client = new ClientComms();
    }

    @Override
    public Restaurant getRestaurant() {
        client.sendMessage("GET RESTAURANT");
        return (Restaurant) client.receiveMessage();
    }

    @Override
    public String getRestaurantName() {
        client.sendMessage("GET RESTAURANT_NAME");
        return client.receiveMessage().toString();
    }

    @Override
    public Postcode getRestaurantPostcode() {
        client.sendMessage("GET RESTAURANT_LOCATION");
        return (Postcode) client.receiveMessage();
    }

    @Override
    public User register(String username, String password, String address, Postcode postcode) {
        client.sendMessage("USER REGISTER:" + username + ":" + password + ":" + address + ":" + postcode);
        Object response = client.receiveMessage();
        if (response.toString().equals("FAIL")){
            logger.log(Level.WARN,"User registration failed.");
            return null;
        }
        return (User) response;
    }

    @Override
    public User login(String username, String password) {
        client.sendMessage("USER LOGIN:" + username + ":" + password);
        Object response = client.receiveMessage();
        if (response.toString().equals("FAIL")){
            logger.log(Level.WARN,"User login failed.");
            return null;
        }
        return (User) response;
    }

    @Override
    public List<Postcode> getPostcodes() {
        client.sendMessage("GET POSTCODES");
        return (List<Postcode>) client.receiveMessage();
    }

    @Override
    public List<Dish> getDishes() {
        client.sendMessage("GET DISHES");
        return (List<Dish>) client.receiveMessage();
    }

    @Override
    public String getDishDescription(Dish dish) {
        return dish.getDescription();
    }

    @Override
    public Number getDishPrice(Dish dish) {
        return dish.getPrice();
    }

    @Override
    public Map<Dish, Number> getBasket(User user) {
        return user.getBasket();
    }

    @Override
    public Number getBasketCost(User user) {
        double totalCost = 0;
        HashMap<Dish, Number> basket = user.getBasket();
        for (Dish dish : basket.keySet()) {
            totalCost += (dish.getPrice().doubleValue() * basket.get(dish).intValue());
        }
        return totalCost;
    }

    @Override
    public void addDishToBasket(User user, Dish dish, Number quantity) {
        user.addToBasket(dish, quantity);
    }

    @Override
    public void updateDishInBasket(User user, Dish dish, Number quantity) {
        user.getBasket().put(dish, quantity);
    }

    @Override
    public Order checkoutBasket(User user) {
        client.sendMessage("BASKET CHECKOUT");
        return (Order) client.receiveMessage();
    }

    @Override
    public void clearBasket(User user) {
        user.clearBasket();
    }

    // FIXME: 08/05/2019
    @Override
    public List<Order> getOrders(User user) {
        client.sendMessage("GET ORDERS:" + user);
        return (List<Order>) client.receiveMessage();
    }

    @Override
    public boolean isOrderComplete(Order order) {
        return order.getComplete();
    }

    @Override
    public String getOrderStatus(Order order) {
        return order.getStatus();
    }

    @Override
    public Number getOrderCost(Order order) {
        return order.getCost();
    }

    @Override
    public void cancelOrder(Order order) {
        order.setCancelled();
    }

    @Override
    public void addUpdateListener(UpdateListener listener) {
        listeners.add(listener);
    }

    @Override
    public void notifyUpdate() {
        for (UpdateListener updateListener : listeners) {
            updateListener.updated(new UpdateEvent());
        }
    }
}
