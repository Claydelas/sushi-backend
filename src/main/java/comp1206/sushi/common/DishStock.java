package comp1206.sushi.common;


import comp1206.sushi.server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the dish stock. This includes adding new dishes to stock and triggering restocking by telling staff to
 * prepare more.
 * @author Jack Corbett
 */
public class DishStock {

    private ConcurrentHashMap<Dish, Number> stock;
    private boolean restockingEnabled;
    private boolean running;

    /**
     * Constructor which starts a thread to monitor the stock level, adding a dish to the queue if it needs to be
     * restocked.
     * @param server Reference to the server.
     */
    public DishStock(Server server) {
        stock = new ConcurrentHashMap<>();
        restockingEnabled = true;
        running = true;

        Thread dishStockMonitor = new Thread(() -> {
            /* Loop while the dish stock is running (this is always set to true as we want it to monitor all the time
               the server is running) */
            while (running) {
                if (restockingEnabled) {

                    // Check each dish currently in the stock system
                    for (Dish dish : stock.keySet()) {
                        // If the stock level is below the required level or restocking is triggered
                        if (stock.get(dish).intValue() <= dish.getRestockThreshold().intValue() ||
                                dish.noRestocking > 0) {

                            /* If the staff that are currently restocking won't reach the required level add to the
                            queue*/
                            if (stock.get(dish).intValue() + dish.noRestocking <
                                    dish.getRestockThreshold().intValue() +
                                            dish.getRestockAmount().intValue()) {

                                /* Check it isn't already in the queue.
                                (this could happen if no staff had started restocking it but it had already been
                                flagged for restocking) */
                                if (!server.restockDishQueue.contains(dish)) {
                                    server.restockDishQueue.add(dish);
                                    /* Increment the restocking count so we can check how many staff are restocking
                                    a given dish */
                                    dish.noRestocking ++;
                                }
                            }
                        }
                    }
                }
                /* Wait 0.1 seconds before checking again to decrease CPU load as the spec requires this to be
                continually checked */
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.err.println("Failed to wait before checking dish stock");
                }
            }
        });
        dishStockMonitor.start();
    }

    /**
     * @param enabled Flag to say whether dishes can be restocked
     */
    public void setRestockingEnabled(boolean enabled) {
        this.restockingEnabled = enabled;
    }

    /**
     * @return A hash map of the current stock levels of each Dish and the corresponding amount
     */
    public ConcurrentHashMap<Dish, Number> getStock() {
        return stock;
    }

    /**
     * @return Just a list of the stocked dishes without quantities
     */
    public ArrayList<Dish> getDishes() {
        return new ArrayList<>(stock.keySet());
    }

    /**
     * @param dish The new dish to be added to the stock system
     * @param number The amount of dishes to add
     */
    public void addDishToStock(Dish dish, Number number) {
        stock.put(dish, number);
    }

    /**
     * @param dish The dish stock to be updated
     * @param number The new stock amount
     */
    public void setStockLevel(Dish dish, Number number) {
        stock.replace(dish, number);
    }

    /**
     * Adds 1 to the stock level which is called when a staff member finishes preparing the dish
     * @param dish The dish to be added to
     */
    void addStock(Dish dish) {
        stock.put(dish, stock.getOrDefault(dish, 0).intValue() + 1);
        if (dish.noRestocking > 0) dish.noRestocking --;
    }

    /**
     * Decreases the stock level of a dish when it is delivered
     * @param dish The dish whose stock is to be decremented
     */
    public void removeDish(Dish dish) {
        stock.put(dish, stock.get(dish).intValue() - 1);
    }
}
