package comp1206.sushi.client;

import comp1206.sushi.common.*;
import comp1206.sushi.comms.ClientComms;
import comp1206.sushi.comms.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client implements ClientInterface {

    private static final Logger logger = LogManager.getLogger("Client");

    public ArrayList<User> users = new ArrayList<>();
    public ArrayList<Order> orders = new ArrayList<>();
    public ArrayList<Dish> dishes = new ArrayList<>();
    private ArrayList<UpdateListener> listeners = new ArrayList<>();
    private ClientComms client;

    public Client() {
        logger.info("Starting up client...");
        client = new ClientComms("127.0.0.1", 8888);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> client.disconnect()));
    }

    @Override
    public Restaurant getRestaurant() {
        //client.sendMessage("");
        return null;
    }

    @Override
    public String getRestaurantName() {
        return getRestaurant().getName();
    }

    @Override
    public Postcode getRestaurantPostcode() {
        return getRestaurant().getLocation();
    }

    @Override
    public User register(String username, String password, String address, Postcode postcode) {
        return null;
    }

    @Override
    public User login(String username, String password) {
        return null;
    }

    @Override
    public List<Postcode> getPostcodes() {
        return null;
    }

    @Override
    public List<Dish> getDishes() {
        return null;
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
        return null;
    }

    @Override
    public void clearBasket(User user) {
        user.clearBasket();
    }

    // FIXME: 08/05/2019
    @Override
    public List<Order> getOrders(User user) {
        return null;
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
