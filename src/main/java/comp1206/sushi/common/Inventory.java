package comp1206.sushi.common;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Inventory Class
 * Runs as a thread
 * Constantly checks for dishes or ingredients needed to be restocked
 */
public class Inventory extends Thread {

    /**
     * Member variables
     */
    private Map<Ingredient, Number> ingredients;
    private Map<Dish, Number> dishes;

    private Boolean restockingIngredients;
    private Boolean restockingDish;

    private ArrayList<Staff> staff;
    private ArrayList<Drone> drones;


    /**
     * Constructor
     *
     * @param staff
     * @param drones
     */
    public Inventory(ArrayList<Staff> staff, ArrayList<Drone> drones) {
        this.ingredients = new HashMap<>();
        this.dishes = new HashMap<>();
        this.restockingIngredients = false;
        this.restockingDish = false;
        this.staff = staff;
        this.drones = drones;
        this.restockingDish = true;
        this.restockingIngredients = false;
    }

    /**
     * Getters and Setters
     *
     * @return
     */
    public Map<Dish, Number> getDishes() {
        return dishes;
    }

    public Map<Ingredient, Number> getIngredients() {
        return ingredients;
    }

    public void setRestockIngredient(Boolean restockingIngredients) {
        this.restockingIngredients = restockingIngredients;
    }

    public synchronized void setRestockThreshold(Boolean restockingDish) {
        this.restockingDish = restockingDish;
    }


    /**
     * Method that checks the dish levels
     */
    public void checkDishLevels() {

        for (Map.Entry<Dish, Number> entry : dishes.entrySet()) {

            if (dishToRestock(entry)) {

                int restockNumber = entry.getValue().intValue() - entry.getKey().getRestockThreshold().intValue() + entry.getKey().getRestockAmount().intValue();
                for (int i = 0; i < restockNumber; i++) {
                    if (availableIngredientsDish(entry.getKey())) {
                        sendToStaff(entry.getKey());

                    } else break;
                }
            }
        }
    }


    /**
     * Method that checks if a dish should be restocked
     *
     * @param entry
     * @return
     */
    private boolean dishToRestock(Map.Entry<Dish, Number> entry) {
        Dish dish = entry.getKey();
        synchronized (dish) {
            Number currentLevel = entry.getValue();
            if ((int) currentLevel < dish.getRestockThreshold().intValue()) return true;
            else return false;
        }
    }

    /**
     * Method to check if we have the neccessary ingredients to restock a dish
     *
     * @param dishToRestock
     * @return
     */
    private boolean availableIngredientsDish(Dish dishToRestock) {

        boolean flag = true;

        Map<Ingredient, Number> recipe = dishToRestock.getRecipe();
        for (Map.Entry<Ingredient, Number> ingredient : recipe.entrySet()) {

            if ((Integer) ingredient.getValue() < (Integer) ingredients.get(ingredient.getKey())) {
                System.out.println(ingredient.getValue() + " ingredient in stock =" + ingredients.get(ingredient));
                continue;
            } else {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * Remove the ingredients from the dish
     *
     * @param dishToRestock
     */
    private void removeIngredientsDish(Dish dishToRestock) {

        Map<Ingredient, Number> recipe = dishToRestock.getRecipe();
        for (Map.Entry<Ingredient, Number> ingredient : recipe.entrySet()) {

            Integer substract = (Integer) ingredients.get(ingredient) - (Integer) ingredient.getValue();
            ingredients.put(ingredient.getKey(), substract);

        }
    }

    /**
     * Send dish to staff
     *
     * @param dishToRestock
     */
    private void sendToStaff(Dish dishToRestock) {
        for (Staff currentStaff : staff) {

            if (currentStaff.getStatus().equals("Idle")) {

                //currentStaff.makeDish(dishToRestock, this);
                removeIngredientsDish(dishToRestock);

            } else continue;
            ;
        }
    }


    /**
     * Check ingredients level
     *
     * @throws InterruptedException
     */
    public synchronized void checkIngredientLevels() throws InterruptedException {

        for (Map.Entry<Ingredient, Number> entry : ingredients.entrySet()) {

            if (ingredientToRestock(entry)) {

                synchronized (entry.getKey()) {
                    int restockNumber = entry.getValue().intValue() + entry.getKey().getRestockThreshold().intValue() + entry.getKey().getRestockAmount().intValue();
                    sendDroneRestockIngredient(entry.getKey(), restockNumber);
                    break;

                }
            }
        }
    }


    /**
     * Check ingredient to restock
     *
     * @param entry
     * @return
     */
    public synchronized boolean ingredientToRestock(Map.Entry<Ingredient, Number> entry) {
        Ingredient ingredient = entry.getKey();
        Number currentLevel = entry.getValue();

        if ((int) currentLevel < ingredient.getRestockThreshold().intValue()) return true;
        else return false;
    }

    /**
     * Send drone to restock
     * If drone is idle then send to restock
     *
     * @param ingredientToRestock
     * @param restockLevel
     * @throws InterruptedException
     */
    public synchronized void sendDroneRestockIngredient(Ingredient ingredientToRestock, Integer restockLevel) throws InterruptedException {

        for (Drone currentDrone : drones) {

            if (currentDrone.getStatus().equals("Idle")) {

                //currentDrone.restockIngredient(ingredientToRestock, restockLevel, this);
            }
        }
    }


    /**
     * Thread Running always
     * Checks for ingredients and dishes to restock
     */
    @Override
    public void run() {
        while (true) {

            try {
                checkIngredientLevels();
                checkDishLevels();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Put Ingredient or Dish methods
     */
    public synchronized void putIngredient(Ingredient toAdd, Integer i) {
        ingredients.put(toAdd, i);
    }

    public synchronized void putDish(Dish dishToAdd, Integer i) {
        dishes.put(dishToAdd, i);
    }


}




