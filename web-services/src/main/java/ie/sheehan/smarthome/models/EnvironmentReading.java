package ie.sheehan.smarthome.models;

import java.util.Comparator;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import ie.sheehan.smarthome.repositories.EnvironmentReadingRepository;

@Document(collection = EnvironmentReadingRepository.ENVIRONMENT_READING_COLLECTION)
public class EnvironmentReading {
	
	@Id
	public String id;
	
	public double temperature;
	
	public double humidity;
	
	public long timestamp;
	
	
	@Override
	public String toString() {
		return id + " | " + timestamp + "\nTemperature: " + temperature + "\tHumidity: " + humidity;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof EnvironmentReading)){
			return false;
		}
		
		EnvironmentReading other = (EnvironmentReading) obj;
		return this.id.equals(other.id);
	}
	
	public static class EnvironmentReadingTimeComparator implements Comparator<EnvironmentReading> {
		@Override
		public int compare(EnvironmentReading o1, EnvironmentReading o2) {
			return 0;
		}
	}
	
}
