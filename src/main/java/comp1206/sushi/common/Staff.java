package comp1206.sushi.common;

import comp1206.sushi.server.Server;

public class Staff extends Model implements Runnable {

	private String name;
	private String status;
	private volatile boolean working;
	private IngredientStock ingredientStock;
	private DishStock dishStock;
	private Number fatigue;
	private Server server;

	public Staff(Server server, String name, DishStock dishStock, IngredientStock ingredientStock) {
		setName(name);
		status = "Idle";
		working = true;
		this.ingredientStock = ingredientStock;
		this.dishStock = dishStock;
		this.server = server;
		setFatigue(0);
	}

	@Override
	public void run() {
		while (working) {
			boolean makeDish = true;

			Dish dish = server.restockDishQueue.poll();

			if (dish != null) {
				// Check there are enough ingredients
				for (Ingredient ingredient : dish.getRecipe().keySet()) {
					if (ingredient != null) {
						//if(ingredientStock.getStock().keySet().contains(ingredient))
						if (ingredientStock.getStock().get(ingredient).intValue() <
								dish.getRecipe().get(ingredient).intValue()) {
							makeDish = false;
						}
					} else {
						makeDish = false;
					}
				}
				// If there are enough ingredients prepare the dish otherwise put the dish back in the queue
				if (makeDish) {
					prepareDish(dish);
				} else {
					server.restockDishQueue.add(dish);
				}
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
	private void prepareDish(Dish dish) {
		try {
			status = "Preparing " + dish.getName();
			notifyUpdate();

			// Remove the ingredients used from the stock system
			ingredientStock.removeStock(dish.getRecipe());

			Thread.sleep((long) (Math.random() * ((60000 - 20000) + 1)) + 20000);

			// Add the newly prepared dish to the stock system
			dishStock.addStock(dish);

			status = "Idle";
			notifyUpdate();
		} catch (Exception e) {
			System.err.println("Staff member: " + getName() + " was unable to prepare " + dish.getName());
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Number getFatigue() {
		return fatigue;
	}

	public void setFatigue(Number fatigue) {
		this.fatigue = fatigue;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
	}
	public void stopWorking(){
		working = false;
	}

}
