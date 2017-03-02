package ie.sheehan.smarthome.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ie.sheehan.smarthome.models.Temperature;

@Repository
public class TemperatureRepository {
	
	private MongoTemplate database;
	
	public static final String TEMPERATURE_COLLECTION = "temperature";
	
	@Autowired
	public TemperatureRepository(MongoTemplate database){
		this.database = database;
		
		if (!this.database.collectionExists(TEMPERATURE_COLLECTION)){
			this.database.createCollection(Temperature.class);
		}
	}
	
	
	public Temperature get(String id){
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		
		return database.findOne(query, Temperature.class, TEMPERATURE_COLLECTION);
	}
	
	public Temperature getLatest(){
		List<Temperature> temperatures = database.findAll(Temperature.class, TEMPERATURE_COLLECTION);
		temperatures.sort(new Temperature.TemperatureTimeComparator());
		return temperatures.get(0);
	}
	
	public List<Temperature> getAll(){
		return database.findAll(Temperature.class, TEMPERATURE_COLLECTION);
	}
	
	public List<Temperature> getRange(long from, long to){
		Query query = new Query();
		
		
		return database.find(query, Temperature.class, TEMPERATURE_COLLECTION);
	}
	
	
	public void add(Temperature temperature){
		database.insert(temperature, TEMPERATURE_COLLECTION);
	}
	
	public void update(Temperature temperature){
		database.save(temperature, TEMPERATURE_COLLECTION);
	}
	
}
