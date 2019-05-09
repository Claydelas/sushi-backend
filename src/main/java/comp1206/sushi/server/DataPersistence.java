package comp1206.sushi.server;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

class DataPersistence {

    private Server server;

    DataPersistence(Server server) {
        this.server = server;
    }

    void backup() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("backup.txt"))) {
            server.getPostcodes().forEach(p -> writer.printf("POSTCODE:%s\n", p));
            writer.println();
            writer.printf("RESTAURANT:%s:%s\n", server.getRestaurantName(), server.getRestaurantPostcode());
            writer.println();
            server.getSuppliers().forEach(s -> writer.printf("SUPPLIER:%s:%s\n", s, s.getPostcode()));
            writer.println();
            server.getIngredients().forEach(i -> writer.printf("INGREDIENT:%s:%s:%s:%d:%d:%d\n",
                    i, i.getUnit(), i.getSupplier(), i.getRestockThreshold().intValue(), i.getRestockAmount().intValue(), i.getWeight().intValue()));
            writer.println();
            server.getDishes().forEach(d -> {
                writer.printf("DISH:%s:%s:%.2f:%d:%d:",
                        d, d.getDescription(), d.getPrice().doubleValue(), d.getRestockThreshold().intValue(), d.getRestockAmount().intValue());
                d.getRecipe().forEach((ingredient, amount) -> writer.printf("%d * %s,", amount.intValue(), ingredient));
                writer.println();
            });
            writer.println();
            server.getUsers().forEach(u -> writer.printf("USER:%s:%s:%s:%s\n", u, u.getPassword(), u.getAddress(), u.getPostcode()));
            writer.println();
            server.getOrders().forEach(o -> {
                writer.printf("ORDER:%s:", o.getUser());
                o.getItems().forEach((dish, amount) -> writer.printf("%d * %s,", amount.intValue(), dish));
                writer.println();
            });
            writer.println();
            server.getDishStockLevels().forEach((dish, stock) -> writer.printf("STOCK:%s:%d\n", dish, stock.intValue()));
            server.getIngredientStockLevels().forEach((ingredient, stock) -> writer.printf("STOCK:%s:%d\n", ingredient, stock.intValue()));
            writer.println();
            server.getStaff().forEach(staff -> writer.printf("STAFF:%s\n", staff));
            writer.println();
            server.getDrones().forEach(drone -> writer.printf("DRONE:%d\n", drone.getSpeed().intValue()));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}