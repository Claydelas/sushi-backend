package comp1206.sushi.common;

import java.io.Serializable;
import java.util.HashMap;

public class User extends Model implements Serializable {

    private String username;
    private String password;
    private String address;
    private Postcode postcode;
    private HashMap<Dish, Number> basket = new HashMap<>();

    public User(String username, String password, String address, Postcode postcode) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.postcode = postcode;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public HashMap<Dish, Number> getBasket() {
        return basket;
    }

    public String getName() {
        return username;
    }

    @Override
    public void setName(String name) {
        this.username = name;
    }

    public Number getDistance() {
        return postcode.getDistance();
    }

    public Postcode getPostcode() {
        return this.postcode;
    }

    public void setPostcode(Postcode postcode) {
        this.postcode = postcode;
    }

    public void addToBasket(Dish dish, Number quantity) {
        basket.put(dish, quantity);
        notifyUpdate();
    }

    public void clearBasket() {
        basket.clear();
        notifyUpdate();
    }

}
