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
	 * Retrieves the latest temperature logged to the database.
	 * 
	 * @return the latest temperature value added
	 */
	public Temperature getLatest(){
		List<Temperature> temperatures = database.findAll(Temperature.class, TEMPERATURE_COLLECTION);
		temperatures.sort(new Temperature.TemperatureTimeComparator());
		return temperatures.get(temperatures.size() - 1);
	}
	
	/**
	 * Retrieves all temperature values logged.
	 * 
	 * @return a list of all temperature values
	 */
	public List<Temperature> getAll(){
		return database.findAll(Temperature.class, TEMPERATURE_COLLECTION);
	}
	
	/**
	 * Retrieves all temperature values that were logged between two times.
	 * 
	 * @param from base time, in milliseconds
	 * @param to max time, in milliseconds
	 * @return a list of all temperature values logged between the two times
	 */
	public List<Temperature> getRange(long from, long to){
		Query query = new Query();
		query.addCriteria(Criteria.where("timestamp").gte(from).andOperator(Criteria.where("timestamp").lte(to)));
		
		return database.find(query, Temperature.class, TEMPERATURE_COLLECTION);
	}
	
	/**
	 * Adds a new temperature value to the database.
	 * 
	 * @param temperature value to be logged
	 */
	public void add(Temperature temperature){
		database.insert(temperature, TEMPERATURE_COLLECTION);
	}
	
	/**
	 * Updates an existing temperature value in the database.
	 * 
	 * @param temperature value to be updated
	 */
	public void update(Temperature temperature){
		database.save(temperature, TEMPERATURE_COLLECTION);
	}
	
}
