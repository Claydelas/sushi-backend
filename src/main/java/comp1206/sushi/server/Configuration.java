package comp1206.sushi.server;

import comp1206.sushi.common.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration Class
 * Loads data from File
 */
public class Configuration {


    /**
     * Member variables
     */
    private String filename;
    private ArrayList<Supplier> suppliers;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Dish> dishes;
    private ArrayList<User> users;
    private ArrayList<Postcode> postcodes;
    private ArrayList<Staff> staff;
    private ArrayList<Drone> drones;
    private ArrayList<Order> orders;
    private Inventory inventory;

    private Map<String, Dish> availableDishes;
    private Map<String, Ingredient> availableIngredients;
    private Map<String, Postcode> availablePostcodes;
    private HashMap<String, User> availableUsers;
    private HashMap<String, Supplier> availableSuppliers;

    private HashMap<String, Integer> ordersId;


    /**
     * @param filename
     * @param suppliers
     * @param ingredients
     * @param dishes
     * @param users
     * @param postcodes
     * @param staff
     * @param drones
     * @param orders
     * @param inventory
     * @param availableUsers
     * @param availableIngredients
     * @param ordersId
     */
    public Configuration(String filename, ArrayList<Supplier> suppliers, ArrayList<Ingredient> ingredients, ArrayList<Dish> dishes, ArrayList<User> users, ArrayList<Postcode> postcodes, ArrayList<Staff> staff, ArrayList<Drone> drones, ArrayList<Order> orders, Inventory inventory, HashMap<String, User> availableUsers, Map<String, Ingredient> availableIngredients, HashMap<String, Integer> ordersId) {

        this.filename = filename;
        this.suppliers = suppliers;
        this.ingredients = ingredients;
        this.dishes = dishes;
        this.users = users;
        this.postcodes = postcodes;
        this.staff = staff;
        this.drones = drones;
        this.orders = orders;

        this.inventory = inventory;
        this.availableUsers = availableUsers;
        this.availableIngredients = availableIngredients;
        this.ordersId = ordersId;


        availableDishes = new HashMap<>();
        availablePostcodes = new HashMap<>();
        availableSuppliers = new HashMap<>();

    }

    /**
     * Sets up the configuration
     * Loads all the data from the text file to the arrayLists of the server
     *
     * @throws IOException
     */
    public void setUp() throws IOException {

        FileReader fileReader = new FileReader(filename);

        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // System.out.println(line);


                if (line.equals("")) continue;
                String[] splittedLine = line.split(":");

                String command = splittedLine[0];

                if (command.equals("SUPPLIER")) {

                    String name = splittedLine[1];
                    Postcode postcode = new Postcode(splittedLine[2], new Restaurant("hi", new Postcode("SO16 3ZE")));
                    Supplier supplier = new Supplier(name, postcode);
                    suppliers.add(supplier);
                    availableSuppliers.put(name, supplier);

                } else if (command.equals("INGREDIENT")) {

                    String name = splittedLine[1];
                    String unit = splittedLine[2];
                    String supplierName = splittedLine[3];
                    String restockThreshold = splittedLine[4];
                    String restockAmount = splittedLine[5];
                    String weight = splittedLine[6];

                    Supplier supplier = availableSuppliers.get(supplierName);
                    Ingredient ingredientToAdd = new Ingredient(name, unit, supplier, Integer.parseInt(restockThreshold), Integer.parseInt(restockAmount), Integer.parseInt(weight));

                    availableIngredients.put(name, ingredientToAdd);
                    ingredients.add(ingredientToAdd);
                    inventory.putIngredient(ingredientToAdd, 0);

                } else if (command.equals("DISH")) {
                    String name = splittedLine[1];
                    String description = splittedLine[2];
                    Integer price = Integer.parseInt(splittedLine[3]);
                    Integer restockThreshold = Integer.parseInt(splittedLine[4]);
                    Integer restockAmount = Integer.parseInt(splittedLine[5]);

                    String recipeContent = splittedLine[6];
                    Map<Ingredient, Number> recipe = getRecipe(recipeContent);

                    Dish dishToAdd = new Dish(name, description, price, restockThreshold, restockAmount);
                    dishToAdd.setRecipe(recipe);
                    availableDishes.put(name, dishToAdd);
                    dishes.add(dishToAdd);
                    inventory.putDish(dishToAdd, 0);

                } else if (command.equals("USER")) {
                    String name = splittedLine[1];
                    String password = splittedLine[2];
                    String location = splittedLine[3];
                    String postcode = splittedLine[4];

                    Postcode userPostcode = null;

                    if (availablePostcodes.containsKey(postcode)) userPostcode = availablePostcodes.get(postcode);

                    User userToAdd = new User(name, password, location, userPostcode);
                    users.add(userToAdd);
                    availableUsers.put(name, userToAdd);


                } else if (command.equals("POSTCODE")) {
                    String postcode = splittedLine[1];

                    Postcode postcodeToAdd = new Postcode(postcode, new Restaurant("hi", new Postcode("SO16 3ZE")));

                    availablePostcodes.put(postcodeToAdd.getName(), postcodeToAdd);

                    postcodes.add(postcodeToAdd);


                } else if (command.equals("STAFF")) {
                    String name = splittedLine[1];

                    Staff staffToAdd = new Staff(name);
                    staff.add(staffToAdd);

                } else if (command.equals("DRONE")) {
                    String speed = splittedLine[1];

                    Drone droneToAdd = new Drone(Integer.parseInt(speed));

                    //new Thread(droneToAdd).start();

                    drones.add(droneToAdd);

                } else if (command.equals("ORDER")) {

                    String user = splittedLine[1];
                    String val = splittedLine[2];

                    User orderUser = null;

                    if (availableUsers.containsKey(user)) {
                        orderUser = availableUsers.get(user);

                    }

                    Integer id = 0;

                    if (ordersId.containsKey(user)) {
                        id = ordersId.get(user);
                    }
                    id++;
                    Order orderToAdd = new Order(availableUsers.get(user), getOrder(val), id);
                    ordersId.put(user, id);

                    orders.add(orderToAdd);

                } else if (command.equals("STOCK")) {

                    String val = splittedLine[1];
                    String quantity = splittedLine[2];

                    if (availableIngredients.containsKey(val)) {

                        Ingredient ingredientToAdd = availableIngredients.get(val);
                        inventory.putIngredient(ingredientToAdd, Integer.parseInt(quantity));


                    } else if (availableDishes.containsKey(val)) {

                        Dish dishToAdd = availableDishes.get(val);
                        inventory.getDishes().put(dishToAdd, Integer.parseInt(quantity));

                    }

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Method that gets a Recipe from a string
     *
     * @param recipe
     * @return
     */
    public Map<Ingredient, Number> getRecipe(String recipe) {

        Map<Ingredient, Number> recipes = new HashMap<>();

        String[] ingredients = recipe.split(",");

        for (String currentIngredient : ingredients) {

            String quantity = currentIngredient.split("\\*")[0].trim();
            String ingredient = currentIngredient.split("\\*")[1].trim();

            if (availableIngredients.containsKey(ingredient)) {
                Ingredient ingredientToAdd = availableIngredients.get(ingredient);
                recipes.put(ingredientToAdd, Integer.parseInt(quantity));


            }

        }


        return recipes;

    }

    /**
     * Method that gets an order from a String
     *
     * @param order
     * @return
     */
    public HashMap<Dish, Number> getOrder(String order) {


        HashMap<Dish, Number> orders = new HashMap<>();
        String[] ingredients = order.split(",");

        for (String currentDish : ingredients) {

            String quantity = currentDish.split("\\*")[0].trim();
            String dish = currentDish.split("\\*")[1].trim();

            Dish dishToAdd = null;

            if (availableDishes.containsKey(dish)) dishToAdd = availableDishes.get(dish);
            orders.put(dishToAdd, Integer.parseInt(quantity));

        }


        return orders;


    }

}

