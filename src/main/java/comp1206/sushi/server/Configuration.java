package comp1206.sushi.server;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Restaurant;
import comp1206.sushi.common.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

public class Configuration {

    public Configuration(String filename, Server server) {
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            stream
                    .filter(s -> !s.isBlank())
                    .forEach(s -> {
                        System.out.println(s);
                        if (s.startsWith("POSTCODE")) {
                            server.addPostcode(s.split(":")[1]);
                        } else if (s.startsWith("RESTAURANT")) {
                            String[] restaurant = s.split(":");

                            server.getPostcodes()
                                    .stream()
                                    .filter(postcode -> postcode.getName().equals(restaurant[2]))
                                    .findFirst().ifPresent(postcode -> server.restaurant = new Restaurant(restaurant[1], postcode));
                        } else if (s.startsWith("SUPPLIER")) {
                            String[] supplier = s.split(":");

                            server.getPostcodes()
                                    .stream()
                                    .filter(postcode -> postcode.getName().equals(supplier[2]))
                                    .findFirst().ifPresent(postcode -> server.addSupplier(supplier[1], postcode));
                        } else if (s.startsWith("INGREDIENT")) {
                            String[] ingredient = s.split(":");

                            server.getSuppliers()
                                    .stream()
                                    .filter(supplier -> supplier.getName().equals(ingredient[3]))
                                    .findFirst().ifPresent(supplier ->
                                    server.addIngredient(ingredient[1],
                                            ingredient[2],
                                            supplier,
                                            Integer.parseInt(ingredient[4]),
                                            Integer.parseInt(ingredient[5]),
                                            Integer.parseInt(ingredient[6])));
                        } else if (s.startsWith("DISH")) {
                            String[] dish = s.split(":");
                            Dish newdish = server.addDish(dish[1], dish[2],
                                    Double.parseDouble(dish[3]),
                                    Integer.parseInt(dish[4]),
                                    Integer.parseInt(dish[5]));

                            String[] recipe = dish[6].split(",");

                            for (String ingredient : recipe) {
                                String[] tuple = ingredient.split(" \\* ");
                                server.getIngredients()
                                        .stream()
                                        .filter(i -> i.getName().equals(tuple[1]))
                                        .findFirst().ifPresent(i -> server.addIngredientToDish(newdish, i, Double.parseDouble(tuple[0])));
                            }
                        } else if (s.startsWith("USER")) {
                            String[] user = s.split(":");
                            server.getPostcodes()
                                    .stream()
                                    .filter(postcode -> postcode.getName().equals(user[4]))
                                    .findFirst().ifPresent(postcode -> server.users.add(new User(user[1], user[2], user[3], postcode)));
                        } else if (s.startsWith("ORDER")) {
                            String[] order = s.split(":");
                            HashMap<Dish, Number> neworder = new HashMap<>();

                            String[] orderedDishes = order[2].split(",");

                            for (String orderedDish : orderedDishes) {
                                String[] dish = orderedDish.split(" \\* ");

                                server.getDishes()
                                        .stream()
                                        .filter(i -> i.getName().equals(dish[1]))
                                        .findFirst().ifPresent(i -> neworder.put(i, Integer.parseInt(dish[0])));
                            }
                            server.getUsers()
                                    .stream()
                                    .filter(i -> i.getName().equals(order[1]))
                                    .findFirst().ifPresent(user -> {
                                Order newOrder = new Order(user, neworder);
                                server.orders.add(newOrder);
                                server.orderQueue.add(newOrder);
                            });
                        } else if (s.startsWith("STOCK")) {
                            String[] stock = s.split(":");
                            server.getDishes()
                                    .stream()
                                    .filter(dish -> dish.getName().equals(stock[1]))
                                    .findFirst().ifPresent(dish -> server.setStock(dish, Integer.parseInt(stock[2])));
                            server.getIngredients()
                                    .stream()
                                    .filter(ingredient -> ingredient.getName().equals(stock[1]))
                                    .findFirst().ifPresent(ingredient -> server.setStock(ingredient, Integer.parseInt(stock[2])));
                        } else if (s.startsWith("STAFF")) {
                            server.addStaff(s.split(":")[1]);

                        } else if (s.startsWith("DRONE")) {
                            server.addDrone(Integer.parseInt(s.split(":")[1]));
                        }
                    });
        } catch (IOException e) {
            System.err.println("Invalid config.");
        }
    }
}
