package comp1206.sushi.client;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Restaurant;
import comp1206.sushi.common.UpdateListener;
import comp1206.sushi.common.User;

public class Client implements ClientInterface {

    private static final Logger logger = LogManager.getLogger("Client");
	
	public Client() {
        logger.info("Starting up client...");
	}
	
	@Override
	public Restaurant getRestaurant() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getRestaurantName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Postcode getRestaurantPostcode() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public User register(String username, String password, String address, Postcode postcode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User login(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Postcode> getPostcodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Dish> getDishes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDishDescription(Dish dish) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Number getDishPrice(Dish dish) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Dish, Number> getBasket(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Number getBasketCost(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addDishToBasket(User user, Dish dish, Number quantity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDishInBasket(User user, Dish dish, Number quantity) {
		// TODO Auto-generated method stub

	}

	@Override
	public Order checkoutBasket(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearBasket(User user) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Order> getOrders(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOrderComplete(Order order) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getOrderStatus(Order order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Number getOrderCost(Order order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancelOrder(Order order) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addUpdateListener(UpdateListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyUpdate() {
		// TODO Auto-generated method stub

	}

}
