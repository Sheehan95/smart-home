package ie.sheehan.smarthome.models;

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
	
	
	@Override
	public String toString() {
		return id + " | " + timestamp + "\nTemperature: " + temperature + "\tHumidity: " + humidity;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof Temperature)){
			return false;
		}
		
		Temperature other = (Temperature) obj;
		return this.id.equals(other.id);
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
