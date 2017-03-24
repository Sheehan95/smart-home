package ie.sheehan.smarthome.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stock")
public class StockReading {
	
	@Id
	public String id;
	
	public String productName;
	
	public double weight;
	
	public long timestamp;
	
	
	@Override
	public String toString() {
		return String.format("ID: %s\n%s\nProduct: %s\tWeight: %f", 
				id, new Date(timestamp * 1000L).toString(), productName, weight);
	}
	
}
