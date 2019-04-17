package comp1206.sushi.common;

import java.util.HashMap;
import java.util.Map;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Ingredient;

public class Dish extends Model {

	private String name;
	private String description;
	private Number price;
	private Map <Ingredient,Number> recipe;
	private Number restockThreshold;
	private Number restockAmount;

	public Dish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.restockThreshold = restockThreshold;
		this.restockAmount = restockAmount;
		this.recipe = new HashMap<Ingredient,Number>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Number getPrice() {
		return price;
	}

	public void setPrice(Number price) {
		this.price = price;
	}

	public Map <Ingredient,Number> getRecipe() {
		return recipe;
	}

	public void setRecipe(Map <Ingredient,Number> recipe) {
		this.recipe = recipe;
	}

	public void setRestockThreshold(Number restockThreshold) {
		this.restockThreshold = restockThreshold;
	}
	
	public void setRestockAmount(Number restockAmount) {
		this.restockAmount = restockAmount;
	}

	public Number getRestockThreshold() {
		return this.restockThreshold;
	}

	public Number getRestockAmount() {
		return this.restockAmount;
	}

}
