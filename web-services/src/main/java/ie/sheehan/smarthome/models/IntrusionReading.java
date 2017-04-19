package ie.sheehan.smarthome.models;

import java.util.Comparator;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import ie.sheehan.smarthome.repositories.IntrusionReadingRepository;

@Document(collection = IntrusionReadingRepository.COLLECTION_INTRUSION_READINGS)
public class IntrusionReading {
	
	@Id
	public String id;
	
	public String image;
	
	public long timestamp;
	
	public boolean viewed;
	
	
	public static class IntrusionReadingTimeComparator implements Comparator<IntrusionReading> {
		@Override
		public int compare(IntrusionReading o1, IntrusionReading o2) {
			Date d1 = new Date(o1.timestamp * 1000L);
			Date d2 = new Date(o2.timestamp * 1000L);
			
			return d1.compareTo(d2);
		}
	}
	
}
