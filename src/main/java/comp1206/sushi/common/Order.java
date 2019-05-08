package comp1206.sushi.common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Random;

import comp1206.sushi.common.Order;

public class Order extends Model implements Serializable {

	private User user;
	private HashMap<Dish, Number> items;
	private boolean complete;
	private boolean cancelled;
	private String status;
	
	public Order(User user, HashMap<Dish, Number> items) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		this.name = dtf.format(now);
		this.items = items;
		this.user = user;
		this.complete = false;
		this.cancelled = false;
		this.status = "Processing";
	}

	public Number getDistance() {
		return user.getPostcode().getDistance();
	}

	@Override
	public String getName() {
		return this.name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
	}
	public User getUser() {
		return user;
	}
	public HashMap<Dish, Number> getItems() {
		return items;
	}
	public boolean getComplete() {
		return complete;
	}
	public double getCost() {
		double total = 0.0;
		if (!items.isEmpty()) {
			for (Dish dish : items.keySet()) {
				total += (dish.getPrice().doubleValue() * items.get(dish).intValue());
			}
		}
		return total;
	}
	/**
	 * Sets the order to complete, this also updates the status
	 */
	public void setComplete() {
		setStatus("Complete");
		notifyUpdate("complete", complete, true);
		this.complete = true;

	}
	/**
	 * Sets the order to cancelled, this also updates the status
	 */
	public void setCancelled() {
		setStatus("Cancelled");
		notifyUpdate("cancelled", cancelled, true);
		this.cancelled = true;
	}
	/**
	 * @return If the order has been cancelled
	 */
	public boolean getCancelled() {
		return cancelled;
	}
}
