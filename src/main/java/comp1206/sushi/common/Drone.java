package comp1206.sushi.common;

import comp1206.sushi.common.Drone;
import comp1206.sushi.server.Server;

import java.util.Map;

public class Drone extends Model implements Runnable {

	private Number speed;
	private Number progress;
	private boolean running;
	private IngredientStock ingredientStock;
	private DishStock dishStock;
	Server server;
	
	private Number capacity;
	private Number battery;
	
	private String status;
	
	private Postcode source;
	private Postcode destination;

	public Drone(Server server, IngredientStock ingredientStock, DishStock dishStock, Number speed) {
		this.setSpeed(speed);
		this.server = server;
		this.ingredientStock = ingredientStock;
		this.dishStock = dishStock;
		status = "Idle";
		running = true;
		this.setBattery(100);
	}

	public Number getSpeed() {
		return speed;
	}

	
	public Number getProgress() {
		return progress;
	}
	
	public void setProgress(Number progress) {
		this.progress = progress;
	}
	
	public void setSpeed(Number speed) {
		this.speed = speed;
	}
	
	@Override
	public String getName() {
		return "Drone (" + getSpeed() + " speed)";
	}

	public Postcode getSource() {
		return source;
	}

	public void setSource(Postcode source) {
		this.source = source;
	}

	public Postcode getDestination() {
		return destination;
	}

	public void setDestination(Postcode destination) {
		this.destination = destination;
	}

	public Number getCapacity() {
		return capacity;
	}

	public void setCapacity(Number capacity) {
		this.capacity = capacity;
	}

	public Number getBattery() {
		return battery;
	}

	public void setBattery(Number battery) {
		this.battery = battery;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
	}
	@Override
	public void run() {
		while (running) {
            /* Check if any dishes need delivering - this is prioritised over stocking up on ingredients to
               reduce client wait times */
			Order order = server.orderQueue.poll();

			if (order != null) {
				Boolean deliver = true;
				// Check if we have enough stock
				for (Map.Entry<Dish, Number> entry : order.getItems().entrySet()) {
					Dish dish = entry.getKey();
					Number amount = entry.getValue();
					if (dishStock.getStock().get(dish).intValue() < amount.intValue()) {
						deliver = false;
					}
				}
				if (deliver) {
					status = "Delivering: " + order.getName();
					notifyUpdate();
					try {
						// Add 5 seconds to factor for loading and offloading dishes
						Thread.sleep((long)(order.getDistance().doubleValue() / speed.doubleValue() * 20000) + 5000);

						for (Map.Entry<Dish, Number> entry : order.getItems().entrySet()) {
							Dish dish = entry.getKey();
							Number amount = entry.getValue();
							for (int i = 0; i < amount.intValue(); i++) {
								dishStock.removeDish(dish);
							}
						}
						order.setComplete();
//						// Trigger a backup to be saved
//						server.dataPersistence.backup(server);
					} catch (InterruptedException e) {
						System.err.println("Drone failed to deliver order: " + order.getName());
					}
					status = "Idle";
					notifyUpdate();
				} else {
					server.orderQueue.add(order);
				}
			}

			// Check if ingredients need restocking
			Ingredient ingredient = server.restockIngredientQueue.poll();

			if (ingredient != null) {
				status = "Restocking: " + ingredient.getName();
				notifyUpdate();
				try {
					Thread.sleep((long)(ingredient.getSupplier().getDistance().doubleValue() / speed.doubleValue() * 20000));
					ingredientStock.addStock(ingredient, ingredient.getRestockAmount());
				} catch (InterruptedException e) {
					System.err.println("Drone failed to restock ingredient: " + ingredient.getName());
				}
				status = "Idle";
				notifyUpdate();
			}
            /* Wait 0.1 seconds before checking again to decrease CPU load as the spec requires this to be
                continually checked */
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.err.println("Failed to wait before checking dish stock");
			}
		}
	}

}
