package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the ingredient stock. This includes adding new ingredients and triggering restocking by telling drones to
 * collect more.
 * @author Jack Corbett
 */
public class IngredientStock {

    private ConcurrentHashMap<Ingredient, Number> stock;
    private boolean restockingEnabled;
    private boolean running;

    /**
     * Constructor which starts a thread to monitor the stock level, adding an ingredient to the queue if it needs to
     * be restocked.
     *
     * @param server Reference to the server.
     */
    public IngredientStock(Server server) {
        stock = new ConcurrentHashMap<>();
        restockingEnabled = true;
        running = true;

        Thread ingredientStockMonitor = new Thread(() -> {
            /* Loop while the ingredient stock is running (this is always set to true as we want it to monitor all the
            time the server is running) */
            while (running) {
                if (restockingEnabled) {

                    // Loop for each ingredient in the stock system
                    for (Ingredient ingredient : stock.keySet()) {
                        // If the stock level is below the required level or restocking is triggered
                        if (stock.get(ingredient).intValue() < ingredient.getRestockThreshold().intValue() ||
                                ingredient.noRestocking > 0) {

                            /* If the drones that are currently restocking won't reach the required level also
                            add to the queue (we multiple by restock amount as this is the amount each drone carries)*/
                            if (stock.get(ingredient).intValue() +
                                    (ingredient.getRestockAmount().intValue() * ingredient.noRestocking) <
                                    ingredient.getRestockThreshold().intValue() + ingredient.getRestockAmount().intValue()) {

                                /* Check it isn't already in the queue.
                                (this could happen if no drone had started restocking it but it had already been
                                flagged for restocking) */
                                if (!server.restockIngredientQueue.contains(ingredient)) {
                                    server.restockIngredientQueue.add(ingredient);
                                    /* Increment the restocking count so we can check how many drones are restocking
                                    a given dish */
                                    ingredient.noRestocking++;
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
                    System.err.println("Failed to wait before checking ingredient stock");
                }
            }
        });
        ingredientStockMonitor.start();
    }

    /**
     * @param enabled Flag to say whether dishes can be restocked
     */
    public void setRestockingEnabled(boolean enabled) {
        this.restockingEnabled = enabled;
    }

    /**
     * @return A hash map of the current stock levels of each Ingredient and the corresponding amount
     */
    public ConcurrentHashMap<Ingredient, Number> getStock() {
        return stock;
    }

    /**
     * @return A list of the ingredients stocked
     */
    public ArrayList<Ingredient> getIngredients() {
        return new ArrayList<>(stock.keySet());
    }

    /**
     * @param ingredient The new ingredient to be added to the stock system
     * @param number     The amount of the ingredient to add
     */
    public void addIngredientToStock(Ingredient ingredient, Number number) {
        stock.put(ingredient, number);
    }

    /**
     * @param ingredient The ingredient stock to be updated
     * @param number     The new stock amount
     */
    public void setStockLevel(Ingredient ingredient, Number number) {
        stock.replace(ingredient, number);
    }

    /**
     * @param ingredient The ingredient stock to be added to
     * @param amount     The amount of ingredients
     */
    void addStock(Ingredient ingredient, Number amount) {
        stock.put(ingredient, stock.getOrDefault(ingredient, 0).intValue() + amount.intValue());
        if (ingredient.noRestocking > 0) ingredient.noRestocking--;
    }

    /**
     * @param ingredient The ingredient stock to be decremented
     */
    public void removeStock(Ingredient ingredient) {
        stock.put(ingredient, stock.get(ingredient).intValue() - 1);
    }

    /**
     * @param recipe A map containing all the ingredients and the amount of ingredients to be removed
     */
    void removeStock(Map<Ingredient, Number> recipe) {
        for (Map.Entry<Ingredient, Number> entry : recipe.entrySet()) {
            Ingredient ingredient = entry.getKey();
            Number amountUsed = entry.getValue();

            if (stock.containsKey(ingredient)) {
                stock.put(ingredient, stock.get(ingredient).intValue() - amountUsed.intValue());
            } else {
                System.err.println("Ingredient not found in stock system");
            }
        }
    }
}
