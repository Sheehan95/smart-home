package ie.sheehan.smarthome.models;

import java.sql.Timestamp;
import java.util.Comparator;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import ie.sheehan.smarthome.repositories.TemperatureRepository;

/**
 * Represents a temperature and humidity reading at a given time.
 * 
 * @author Alan Sheehan
 */
@Document(collection = TemperatureRepository.TEMPERATURE_COLLECTION)
public class Temperature {
	
	@Id
	public String id;
	
	public double temperature;
	
	public double humidity;
	
	private long timestamp;
	
	
	public Timestamp getTime(){
		return new Timestamp(timestamp);
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
	public static class TemperatureTimeComparator implements Comparator<Temperature> {

		@Override
		public int compare(Temperature o1, Temperature o2) {
			return 0;
		}
		
	}
	
}
