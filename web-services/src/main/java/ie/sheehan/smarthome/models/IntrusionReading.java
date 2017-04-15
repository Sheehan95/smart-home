package ie.sheehan.smarthome.models;

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
	
}
