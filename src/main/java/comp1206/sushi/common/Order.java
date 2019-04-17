package comp1206.sushi.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import comp1206.sushi.common.Order;

public class Order extends Model {

	private String status;
	
	public Order() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		this.name = dtf.format(now);
	}

	public Number getDistance() {
		return 1;
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

}
