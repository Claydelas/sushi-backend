package comp1206.sushi.common;

import java.util.HashMap;

public class User extends Model {

    private String name;
    private String password;
    private String address;
    private Postcode postcode;
    private HashMap<Dish, Number> basket;

    public User(String username, String password, String address, Postcode postcode) {
        this.name = username;
        this.password = password;
        this.address = address;
        this.postcode = postcode;
        basket = new HashMap<>();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        notifyUpdate("User", this.password, password);
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        notifyUpdate("Address", this.address, address);
        this.address = address;
    }

    public synchronized HashMap<Dish, Number> getBasket() {
        return basket;
    }

    public void setBasket(HashMap<Dish, Number> basket) {
        notifyUpdate("Dishes", this.basket, basket);
        this.basket = basket;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        notifyUpdate("Name", this.name, name);
        super.setName(name);
    }

    public Number getDistance() {
        return postcode.getDistance();
    }

    public Postcode getPostcode() {
        return this.postcode;
    }

    public void setPostcode(Postcode postcode) {
        notifyUpdate("Postcode", this.postcode, postcode);
        this.postcode = postcode;
    }

    public void addToBasket(Dish dish, Number quantity) {
        basket.put(dish, quantity);
    }

    public void clearBasket() {
        basket.clear();
    }

}
