package comp1206.sushi.common;

import comp1206.sushi.common.Staff;

public class Staff extends Model {

	private String name;
	private String status;
	private Number fatigue;
	
	public Staff(String name) {
		this.setName(name);
		this.setFatigue(0);
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

}
