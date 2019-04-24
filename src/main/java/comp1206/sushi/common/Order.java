package comp1206.sushi.common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Random;

import comp1206.sushi.common.Order;

public class Order extends Model implements Serializable {

    private User user;
    private int id;
    private String status;
    private float cost;
    private boolean orderComplete;
    private HashMap<Dish, Number> orders;

    public Order(User user, HashMap<Dish, Number> orders, int id) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        this.name = dtf.format(now);
        this.user = user;
        this.id = id;
        this.orders = orders;
        status = "Pending";
        cost = calculateCost();
        orderComplete = false;
    }

    public Number getDistance() {
        return user.getDistance();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public synchronized String getStatus() {
        return status;
    }

    public synchronized void setStatus(String status) {
        notifyUpdate("status", this.status, status);
        this.status = status;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        notifyUpdate("Cost", this.cost, cost);
        this.cost = cost;
    }

    public HashMap<Dish, Number> getOrders() {
        return orders;
    }

    public void setOrders(HashMap<Dish, Number> orders) {
        notifyUpdate("Order", this.orders, orders);
        this.orders = orders;
    }

    public synchronized boolean getOrderComplete() {
        return orderComplete;
    }

    public synchronized void setOrderComplete(boolean orderComplete) {
        this.orderComplete = orderComplete;
    }

    //calculates cost and returns as float for rounding
    private float calculateCost() {
        double sum = 0;
        for (HashMap.Entry<Dish, Number> entry : orders.entrySet()) {
            sum += entry.getKey().getPrice().doubleValue() * entry.getValue().doubleValue();
        }
        return (float) sum;
    }

    public User getUser() {
        return user;
    }

    public int getId() {
        return id;
    }

}
