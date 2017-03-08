package ie.sheehan.smarthome.repositories;

import java.util.ArrayList;
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
	
	/**
	 * Retrieves a single temperature log by ID.
	 * 
	 * @param id of the temperature log to be retrieved
	 * @return a temperature log with the given ID
	 */
	public Temperature get(String id){
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		
		return database.findOne(query, Temperature.class, TEMPERATURE_COLLECTION);
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public Temperature getLatest(){
		List<Temperature> temperatures = database.findAll(Temperature.class, TEMPERATURE_COLLECTION);
		temperatures.sort(new Temperature.TemperatureTimeComparator());
		return temperatures.get(temperatures.size() - 1);
	}
	
	public List<Temperature> getAll(){
		return database.findAll(Temperature.class, TEMPERATURE_COLLECTION);
	}
	
	public List<Temperature> getRange(long from, long to){
		List<Temperature> fromValues = database.find(new Query(Criteria.where("timestamp").gte(from)), Temperature.class);
		List<Temperature> toValues = database.find(new Query(Criteria.where("timestamp").lte(to)), Temperature.class);

		List<Temperature> intersection = new ArrayList<Temperature>();
		
		for (Temperature tempFrom : fromValues){
			for (Temperature tempTo : toValues){
				if (tempFrom.equals(tempTo)){
					intersection.add(tempFrom);
				}
			}
		}
		
		return intersection;
	}
	
	
	public void add(Temperature temperature){
		database.insert(temperature, TEMPERATURE_COLLECTION);
	}
	
	public void update(Temperature temperature){
		database.save(temperature, TEMPERATURE_COLLECTION);
	}
	
}
