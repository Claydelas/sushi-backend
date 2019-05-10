package comp1206.sushi.client;

import comp1206.sushi.common.*;
import comp1206.sushi.comms.ClientComms;
import comp1206.sushi.comms.Message;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client implements ClientInterface {

    private static final Logger logger = LogManager.getLogger("Client");

    public User user;
    public Restaurant restaurant;
    public ArrayList<Order> orders = new ArrayList<>();
    public ArrayList<Dish> dishes = new ArrayList<>();
    public ArrayList<Postcode> postcodes = new ArrayList<>();
    private ArrayList<UpdateListener> listeners = new ArrayList<>();
    private ClientComms client;

    public Client() {
        logger.info("Starting up client...");
        client = new ClientComms("127.0.0.1", 8888);
        //Runtime.getRuntime().addShutdownHook(new Thread(() -> client.disconnect()));
    }

    //complete
    @Override
    public Restaurant getRestaurant() {
        if (client.isConnected()) {
            client.sendMessage(new Message(Message.RESTAURANT, "GET"));
            Object restaurant = client.receiveMessage();
            if (restaurant instanceof Restaurant) this.restaurant = (Restaurant) restaurant;
        }
        return this.restaurant;
    }

    //complete
    @Override
    public String getRestaurantName() {
        return getRestaurant().getName();
    }

    //complete
    @Override
    public Postcode getRestaurantPostcode() {
        return getRestaurant().getLocation();
    }

    //complete
    @Override
    public User register(String username, String password, String address, Postcode postcode) {
        if (username.isBlank() || password.isBlank() || address.isBlank() || postcode == null) return null;
        if (client.isConnected()) {
            client.sendMessage(new Message(Message.REGISTER, username + ":" + password + ":" + address + ":" + postcode.getName()));
            Object user = client.receiveMessage();
            if (user instanceof User) this.user = (User) user;
        }
        return this.user;
    }

    //complete
    @Override
    public User login(String username, String password) {
        if (username.isBlank() || password.isBlank()) return null;
        if (client.isConnected()) {
            client.sendMessage(new Message(Message.LOGIN,username + ":" + password));
            Object user = client.receiveMessage();
            if (user instanceof User) this.user = (User) user;
        }
        return this.user;
    }

    //complete
    @Override
    public List<Postcode> getPostcodes() {
        if (client.isConnected()) {
            client.sendMessage(new Message(Message.POSTCODES, "GET"));
            Object postcodes = client.receiveMessage();
            if (postcodes instanceof List) this.postcodes = (ArrayList<Postcode>) postcodes;
        }
        return this.postcodes;
    }

    //complete
    @Override
    public List<Dish> getDishes() {
        if (client.isConnected()) {
            client.sendMessage(new Message(Message.DISHES, "GET"));
            Object dishes = client.receiveMessage();
            if (dishes instanceof List) this.dishes = (ArrayList<Dish>) dishes;
        }
        return this.dishes;
    }

    //complete
    @Override
    public String getDishDescription(Dish dish) {
        return dish.getDescription();
    }

    //complete
    @Override
    public Number getDishPrice(Dish dish) {
        return dish.getPrice();
    }

    //complete
    @Override
    public Map<Dish, Number> getBasket(User user) {
        return user.getBasket();
    }

    //complete
    @Override
    public Number getBasketCost(User user) {
        double totalCost = 0.0;
        HashMap<Dish, Number> basket = user.getBasket();
        for (Dish dish : basket.keySet()) {
            totalCost += (dish.getPrice().doubleValue() * basket.get(dish).intValue());
        }
        return totalCost;
    }

    //complete
    @Override
    public void addDishToBasket(User user, Dish dish, Number quantity) {
        if (client.isConnected()) {
            client.sendMessage(new Message(Message.UPDATE_BASKET, user.getName() + ":" + dish.getName() + ":" + quantity));
        }
        user.addToBasket(dish, quantity);
    }

    //complete
    @Override
    public void updateDishInBasket(User user, Dish dish, Number quantity) {
        if (client.isConnected()) {
            client.sendMessage(new Message(Message.UPDATE_BASKET, user.getName() + ":" + dish.getName() + ":" + quantity));
        }
        user.addToBasket(dish, quantity);
    }

    @Override
    public Order checkoutBasket(User user) {
        return null;
    }

    @Override
    public void clearBasket(User user) {
        user.clearBasket();
    }

    // FIXME: 08/05/2019
    @Override
    public List<Order> getOrders(User user) {

        return orders;
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
        client.sendMessage(new Message(Message.BACKUP, "UPDATE"));
    }
}
