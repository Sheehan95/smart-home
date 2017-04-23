package ie.sheehan.smarthome.models;

import java.util.Comparator;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import ie.sheehan.smarthome.repositories.StockReadingRepository;

@Document(collection = StockReadingRepository.COLLECTION_STOCK_READING)
public class StockReading {
	
	@Id
	public String id;
	
	public String product;
	
	public double weight;
	
	public long timestamp;
	
	
	@Override
	public String toString() {
		return String.format("ID: %s\n%s\nProduct: %s\tWeight: %f", 
				id, new Date(timestamp * 1000L).toString(), product, weight);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof StockReading)){
			return false;
		}
		
		StockReading other = (StockReading) obj;
		
		if (! this.id.isEmpty() && ! other.id.isEmpty()) {
			return this.id.equals(other.id);
		}
		else {
			return false;
		}
	}
	
	public static class StockReadingTimeComparator implements Comparator<StockReading> {
		@Override
		public int compare(StockReading o1, StockReading o2) {
			Date d1 = new Date(o1.timestamp * 1000L);
			Date d2 = new Date(o2.timestamp * 1000L);
			
			return d1.compareTo(d2);
		}
	}
	
}
