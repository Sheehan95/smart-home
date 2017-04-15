package ie.sheehan.smarthome.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import ie.sheehan.smarthome.repositories.BreakInRecordRepository;

@Document(collection = BreakInRecordRepository.COLLECTION_BREAK_IN_RECORDING)
public class BreakInRecord {
	
	@Id
	public String id;
	
	public String image;
	
	public long timestamp;
	
	public boolean viewed;
	
}
