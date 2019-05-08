package comp1206.sushi.server;

import comp1206.sushi.common.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server implements ServerInterface {

    private static final Logger logger = LogManager.getLogger("Server");

    // Responsible for backing up the server state to file when a change is made
    //public DataPersistence dataPersistence = new DataPersistence();

    // Instantiate the stock controllers
    private IngredientStock ingredients = new IngredientStock(this);
    private DishStock dishes = new DishStock(this);

    //private data models
    private ArrayList<Supplier> suppliers = new ArrayList<>();
    private ArrayList<Postcode> postcodes = new ArrayList<>();
    private ArrayList<Staff> staff = new ArrayList<>();
    private ArrayList<Drone> drones = new ArrayList<>();

    //public data models
    public ArrayList<User> users = new ArrayList<>();
    public ArrayList<Order> orders = new ArrayList<>();
    public Restaurant restaurant;

    public ConcurrentLinkedQueue<Order> orderQueue = new ConcurrentLinkedQueue<>();
    // A queue used by the drones to check for ingredients that need to be restocked
    public ConcurrentLinkedQueue<Ingredient> restockIngredientQueue = new ConcurrentLinkedQueue<>();
    // A queue used by the staff to check for dishes that need to be restocked
    public ConcurrentLinkedQueue<Dish> restockDishQueue = new ConcurrentLinkedQueue<>();

    // Stores a reference to all update listeners added so the UI can be updated when changes take place
    private ArrayList<UpdateListener> listeners = new ArrayList<>();

    public ArrayList[] lists = {dishes.getDishes(), drones, ingredients.getIngredients(), orders, staff, suppliers, users, postcodes};

    public Server() {
        logger.info("Starting up server...");
        restaurant = new Restaurant("Southampton Sushi", new Postcode("SO17 1BJ"));
        new ServerComms(this);
    }

    @Override
    public List<Dish> getDishes() {
        return dishes.getDishes();
    }

    @Override
    public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
        Dish newDish = new Dish(name, description, price, restockThreshold, restockAmount);
        dishes.addDishToStock(newDish,0);
        this.notifyUpdate();
        return newDish;
    }

    @Override
    public void removeDish(Dish dish) {
        dishes.removeDish(dish);
        this.notifyUpdate();
    }

    @Override
    public Map<Dish, Number> getDishStockLevels() {
        return dishes.getStock();
    }

    @Override
    public void setRestockingIngredientsEnabled(boolean enabled) {
        ingredients.setRestockingEnabled(enabled);
    }

    @Override
    public void setRestockingDishesEnabled(boolean enabled) {
        dishes.setRestockingEnabled(enabled);
    }

    @Override
    public void setStock(Dish dish, Number stock) {
        dishes.setStockLevel(dish,stock);
        this.notifyUpdate();
    }

    @Override
    public void setStock(Ingredient ingredient, Number stock) {
        ingredients.setStockLevel(ingredient, stock);
        this.notifyUpdate();
    }

    @Override
    public List<Ingredient> getIngredients() {
        return ingredients.getIngredients();
    }

    @Override
    public Ingredient addIngredient(String name, String unit, Supplier supplier,
                                    Number restockThreshold, Number restockAmount, Number weight) {

        Ingredient mockIngredient = new Ingredient(name, unit, supplier, restockThreshold, restockAmount, weight);
        ingredients.addIngredientToStock(mockIngredient,0);

        this.notifyUpdate();
        return mockIngredient;
    }

    @Override
    public void removeIngredient(Ingredient ingredient) throws UnableToDeleteException {
//        if (dishes.getDishes().stream().anyMatch(dish -> dish.getRecipe().containsKey(ingredient)))
//            throw new UnableToDeleteException("part of a recipe.");
        ingredients.removeStock(ingredient);
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
//        if (ingredients.getIngredients().stream().anyMatch(ingredient -> ingredient.getSupplier().equals(supplier)))
//            throw new UnableToDeleteException("ingredients depend on this supplier.");
        this.suppliers.remove(supplier);
        this.notifyUpdate();
    }

    @Override
    public List<Drone> getDrones() {
        return this.drones;
    }

    @Override
    public Drone addDrone(Number speed) {
        Drone drone = new Drone(this, ingredients, dishes, speed.intValue());
        new Thread(drone).start();
        drones.add(drone);
        notifyUpdate();
        return drone;
    }

    @Override
    public void removeDrone(Drone drone) throws UnableToDeleteException {
//        if (drone.getStatus().equals("Flying"))
//            throw new UnableToDeleteException("drone mid-flight.");
        this.drones.remove(drone);
        this.notifyUpdate();
    }

    @Override
    public List<Staff> getStaff() {
        return this.staff;
    }

    @Override
    public Staff addStaff(String name) {
        Staff staff = new Staff(this, name, dishes, ingredients);
        new Thread(staff).start();
        this.staff.add(staff);
        return staff;
    }

    @Override
    public void removeStaff(Staff staff) throws UnableToDeleteException {
//        if (staff.getStatus().equals("Working"))
//            throw new UnableToDeleteException("must be idle.");
        this.staff.remove(staff);
        this.notifyUpdate();
    }

    @Override
    public List<Order> getOrders() {
        return this.orders;
    }

    public List<Order> getUserOrders(String username){
        List<Order> orders = new ArrayList<>();
        this.orders.forEach(order -> {
            if (order.getUser().getName().equals(username))
                orders.add(order);
        });
        return orders;
    }

    @Override
    public void removeOrder(Order order) throws UnableToDeleteException {
//        if (order.getStatus().equals("Pending"))
//            throw new UnableToDeleteException("must be complete.");
        this.orders.remove(order);
        this.notifyUpdate();
    }

    @Override
    public Number getOrderCost(Order order) {
        return order.getCost();
    }

    @Override
    public Map<Ingredient, Number> getIngredientStockLevels() {
        return ingredients.getStock();
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
        Postcode mock;
        if (postcodes.isEmpty() || restaurant == null) mock = new Postcode(code);
        else mock = new Postcode(code, restaurant);

        this.postcodes.add(mock);
        this.notifyUpdate();
        return mock;
    }

    @Override
    public void removePostcode(Postcode postcode) throws UnableToDeleteException {
//        if (suppliers.stream().anyMatch(supplier -> supplier.getPostcode().equals(postcode)))
//            throw new UnableToDeleteException("used by a supplier.");
//        if (users.stream().anyMatch(user -> user.getPostcode().equals(postcode)))
//            throw new UnableToDeleteException("used by a user.");
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
        logger.log(Level.INFO,"Loaded configuration: " + filename);
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
        return order.getComplete();
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

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        postcodes.forEach(postcode -> postcode.calculateDistance(restaurant));
        this.notifyUpdate();
    }
}
