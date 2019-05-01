package comp1206.sushi.server;

import comp1206.sushi.common.*;
import comp1206.sushi.comms.OrderManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;

public class Server implements ServerInterface {

    private static final Logger logger = LogManager.getLogger("Server");

    public Restaurant restaurant;
    public ArrayList<Dish> dishes = new ArrayList<>();
    public ArrayList<Drone> drones = new ArrayList<>();
    public ArrayList<Ingredient> ingredients = new ArrayList<>();
    public ArrayList<Order> orders = new ArrayList<>();
    public ArrayList<Staff> staff = new ArrayList<>();
    public ArrayList<Supplier> suppliers = new ArrayList<>();
    public ArrayList<User> users = new ArrayList<>();
    public ArrayList<Postcode> postcodes = new ArrayList<>();
    public ArrayList[] lists = {dishes, drones, ingredients, orders, staff, suppliers, users, postcodes};
    private ArrayList<UpdateListener> listeners = new ArrayList<>();

    private HashMap<String, User> availableUsers = new HashMap<>();
    private HashMap<String, Ingredient> availableIngredients = new HashMap<>();
    private HashMap<String, Integer> ordersId = new HashMap<>();

    private Inventory inventory = new Inventory(staff, drones);
    private OrderManager orderManager = new OrderManager(orders, drones);

    public Server() {
        logger.info("Starting up server...");

        Postcode restaurantPostcode = new Postcode("SO17 1BJ");
        restaurant = new Restaurant("Mock Restaurant", restaurantPostcode);

        Postcode postcode1 = addPostcode("SO17 1TJ");
        Postcode postcode2 = addPostcode("SO17 1BX");
        Postcode postcode3 = addPostcode("SO17 2NJ");
        Postcode postcode4 = addPostcode("SO17 1TW");
        Postcode postcode5 = addPostcode("SO17 2LB");

        Supplier supplier1 = addSupplier("Supplier 1", postcode1);
        Supplier supplier2 = addSupplier("Supplier 2", postcode2);
        Supplier supplier3 = addSupplier("Supplier 3", postcode3);

        Ingredient ingredient1 = addIngredient("Ingredient 1", "grams", supplier1, 1, 5, 1);
        Ingredient ingredient2 = addIngredient("Ingredient 2", "grams", supplier2, 1, 5, 1);
        Ingredient ingredient3 = addIngredient("Ingredient 3", "grams", supplier3, 1, 5, 1);

        Dish dish1 = addDish("Dish 1", "Dish 1", 1, 1, 10);
        Dish dish2 = addDish("Dish 2", "Dish 2", 2, 1, 10);
        Dish dish3 = addDish("Dish 3", "Dish 3", 3, 1, 10);

        addIngredientToDish(dish1, ingredient1, 1);
        addIngredientToDish(dish1, ingredient2, 2);
        addIngredientToDish(dish2, ingredient2, 3);
        addIngredientToDish(dish2, ingredient3, 1);
        addIngredientToDish(dish3, ingredient1, 2);
        addIngredientToDish(dish3, ingredient3, 1);

        addStaff("Staff 1");
        addStaff("Staff 2");
        addStaff("Staff 3");

        addDrone(1);
        addDrone(2);
        addDrone(3);

        User user1 = new User("john", "john123", "elm street", postcode5);
        users.add(user1);

        //startStaff();
        inventory.start();
        orderManager.start();

//        /**
//         * On close save all the data DATA PERSISTENCE PART 9 */
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            public void run() {
//                DataPersistence dataPersistence=new DataPersistence(dishes,ingredients,suppliers,staff,postcodes,users,orders,drones,inventory);
//                dataPersistence.saveToStaffFile();
//            }
//        });
    }

    @Override
    public List<Dish> getDishes() {
        return this.dishes;
    }

    @Override
    public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
        Dish newDish = new Dish(name, description, price, restockThreshold, restockAmount);
        this.dishes.add(newDish);
        inventory.putDish(newDish, 0);
        this.notifyUpdate();
        return newDish;
    }

    @Override
    public void removeDish(Dish dish) {
        this.dishes.remove(dish);
        this.notifyUpdate();
    }

    @Override
    public Map<Dish, Number> getDishStockLevels() {
        return inventory.getDishes();
    }

    @Override
    public void setRestockingIngredientsEnabled(boolean enabled) {
        inventory.setRestockIngredients(enabled);
    }

    @Override
    public void setRestockingDishesEnabled(boolean enabled) {
        inventory.setRestockDishes(enabled);
    }

    @Override
    public void setStock(Dish dish, Number stock) {
        inventory.putDish(dish, stock.intValue());
    }

    @Override
    public void setStock(Ingredient ingredient, Number stock) {
        inventory.putIngredient(ingredient, stock.intValue());
    }

    @Override
    public List<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public Ingredient addIngredient(String name, String unit, Supplier supplier,
                                    Number restockThreshold, Number restockAmount, Number weight) {
        Ingredient mockIngredient = new Ingredient(name, unit, supplier, restockThreshold, restockAmount, weight);
        this.ingredients.add(mockIngredient);
        availableIngredients.put(name, mockIngredient);
        inventory.putIngredient(mockIngredient, 0);
        this.notifyUpdate();
        return mockIngredient;
    }

    @Override
    public void removeIngredient(Ingredient ingredient) throws UnableToDeleteException {
//        if (dishes.stream().anyMatch(dish -> dish.getRecipe().containsKey(ingredient)))
//            throw new UnableToDeleteException("part of a recipe.");
        availableIngredients.remove(ingredient.getName());
        this.ingredients.remove(ingredient);
        this.notifyUpdate();
    }

    @Override
    public List<Supplier> getSuppliers() {
        return this.suppliers;
    }

    @Override
    public Supplier addSupplier(String name, Postcode postcode) {
        Supplier mock = new Supplier(name, postcode);
        this.suppliers.add(mock);
        return mock;
    }


    @Override
    public void removeSupplier(Supplier supplier) throws UnableToDeleteException {
        if (ingredients.stream().anyMatch(ingredient -> ingredient.getSupplier().equals(supplier)))
            throw new UnableToDeleteException("ingredients depend on this supplier.");
        this.suppliers.remove(supplier);
        this.notifyUpdate();
    }

    @Override
    public List<Drone> getDrones() {
        return this.drones;
    }

    @Override
    public Drone addDrone(Number speed) {
        Drone mock = new Drone(speed);
        this.drones.add(mock);
        return mock;
    }

    @Override
    public void removeDrone(Drone drone) throws UnableToDeleteException {
        if (drone.getStatus().equals("Flying"))
            throw new UnableToDeleteException("drone mid-flight.");
        this.drones.remove(drone);
        this.notifyUpdate();
    }

    @Override
    public List<Staff> getStaff() {
        return this.staff;
    }

    @Override
    public Staff addStaff(String name) {
        Staff mock = new Staff(name);
        this.staff.add(mock);
        return mock;
    }

    @Override
    public void removeStaff(Staff staff) throws UnableToDeleteException {
        if (staff.getStatus().equals("Working"))
            throw new UnableToDeleteException("must be idle.");
        this.staff.remove(staff);
        this.notifyUpdate();
    }

    @Override
    public List<Order> getOrders() {
        return this.orders;
    }

    @Override
    public void removeOrder(Order order) throws UnableToDeleteException {
        if (order.getStatus().equals("Pending"))
            throw new UnableToDeleteException("must be complete.");
        this.orders.remove(order);
        this.notifyUpdate();
    }

    @Override
    public Number getOrderCost(Order order) {
        return order.getCost();
    }

    @Override
    public Map<Ingredient, Number> getIngredientStockLevels() {
        return inventory.getIngredients();
    }

    @Override
    public Number getSupplierDistance(Supplier supplier) {
        return supplier.getDistance();
    }

    @Override
    public Number getDroneSpeed(Drone drone) {
        return drone.getSpeed();
    }

    @Override
    public Number getOrderDistance(Order order) {
        return order.getDistance();
    }

    @Override
    public void addIngredientToDish(Dish dish, Ingredient ingredient, Number quantity) {
        if (quantity == Integer.valueOf(0)) {
            removeIngredientFromDish(dish, ingredient);
        } else {
            dish.getRecipe().put(ingredient, quantity);
        }
    }

    @Override
    public void removeIngredientFromDish(Dish dish, Ingredient ingredient) {
        dish.getRecipe().remove(ingredient);
        this.notifyUpdate();
    }

    @Override
    public Map<Ingredient, Number> getRecipe(Dish dish) {
        return dish.getRecipe();
    }

    @Override
    public List<Postcode> getPostcodes() {
        return this.postcodes;
    }

    @Override
    public Postcode addPostcode(String code) {
        Postcode mock = new Postcode(code, restaurant);
        this.postcodes.add(mock);
        this.notifyUpdate();
        return mock;
    }

    //COMPLETED
    @Override
    public void removePostcode(Postcode postcode) throws UnableToDeleteException {
        if (suppliers.stream().anyMatch(supplier -> supplier.getPostcode().equals(postcode)))
            throw new UnableToDeleteException("used by a supplier.");
        if (users.stream().anyMatch(user -> user.getPostcode().equals(postcode)))
            throw new UnableToDeleteException("used by a user.");
        this.postcodes.remove(postcode);
        this.notifyUpdate();
    }

    @Override
    public List<User> getUsers() {
        return this.users;
    }

    @Override
    public void removeUser(User user) {
        this.users.remove(user);
        this.notifyUpdate();
    }

    @Override
    public void loadConfiguration(String filename) {
        for (ArrayList list : lists) {
            list.clear();
        }
        new Configuration(filename, this);
        System.out.println("Loaded configuration: " + filename);
    }

    @Override
    public void setRecipe(Dish dish, Map<Ingredient, Number> recipe) {
        for (Entry<Ingredient, Number> recipeItem : recipe.entrySet()) {
            addIngredientToDish(dish, recipeItem.getKey(), recipeItem.getValue());
        }
        this.notifyUpdate();
    }

    @Override
    public boolean isOrderComplete(Order order) {
        return order.getOrderComplete();
    }

    @Override
    public String getOrderStatus(Order order) {
        return order.getStatus();
    }

    @Override
    public String getDroneStatus(Drone drone) {
        return drone.getStatus();
    }

    @Override
    public String getStaffStatus(Staff staff) {
        return staff.getStatus();
    }

    @Override
    public void setRestockLevels(Dish dish, Number restockThreshold, Number restockAmount) {
        dish.setRestockThreshold(restockThreshold);
        dish.setRestockAmount(restockAmount);
        this.notifyUpdate();
    }

    @Override
    public void setRestockLevels(Ingredient ingredient, Number restockThreshold, Number restockAmount) {
        ingredient.setRestockThreshold(restockThreshold);
        ingredient.setRestockAmount(restockAmount);
        this.notifyUpdate();
    }

    @Override
    public Number getRestockThreshold(Dish dish) {
        return dish.getRestockThreshold();
    }

    @Override
    public Number getRestockAmount(Dish dish) {
        return dish.getRestockAmount();
    }

    @Override
    public Number getRestockThreshold(Ingredient ingredient) {
        return ingredient.getRestockThreshold();
    }

    @Override
    public Number getRestockAmount(Ingredient ingredient) {
        return ingredient.getRestockAmount();
    }

    @Override
    public void addUpdateListener(UpdateListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void notifyUpdate() {
        this.listeners.forEach(listener -> listener.updated(new UpdateEvent()));
    }

    @Override
    public Postcode getDroneSource(Drone drone) {
        return drone.getSource();
    }

    @Override
    public Postcode getDroneDestination(Drone drone) {
        return drone.getDestination();
    }

    @Override
    public Number getDroneProgress(Drone drone) {
        return drone.getProgress();
    }

    @Override
    public String getRestaurantName() {
        return restaurant.getName();
    }

    @Override
    public Postcode getRestaurantPostcode() {
        return restaurant.getLocation();
    }

    @Override
    public Restaurant getRestaurant() {
        return restaurant;
    }

//    private void startStaff() {
//        for (Staff currentStaff : staff) {
//            new Thread(currentStaff).start();
//        }
//    }
}
