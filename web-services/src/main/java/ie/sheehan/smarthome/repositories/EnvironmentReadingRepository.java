package ie.sheehan.smarthome.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.CommandResult;

import ie.sheehan.smarthome.models.EnvironmentReading;

/**
 * DAO for {@link EnvironmentReading} objects saved in a MongoDB database.
 * 
 * @author Alan Sheehan
 */
@Repository
public class EnvironmentReadingRepository {
	
	public static final String ENVIRONMENT_READING_COLLECTION = "environmentreadings";
	
	private MongoTemplate database;
	
	
	@Autowired
	public EnvironmentReadingRepository(MongoTemplate database){
		this.database = database;
		
		if (!this.database.collectionExists(ENVIRONMENT_READING_COLLECTION)){
			this.database.createCollection(EnvironmentReading.class);
		}
	}
	
	
	public EnvironmentReading get(String id){
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		
		return database.findOne(query, EnvironmentReading.class, ENVIRONMENT_READING_COLLECTION);
	}
	
	public EnvironmentReading getLatest(){
		List<EnvironmentReading> envReadings = database.findAll(EnvironmentReading.class, ENVIRONMENT_READING_COLLECTION);
		envReadings.sort(new EnvironmentReading.EnvironmentReadingTimeComparator());
		return envReadings.get(envReadings.size() - 1);
	}
	
	public List<EnvironmentReading> getAll(){
		return database.findAll(EnvironmentReading.class, ENVIRONMENT_READING_COLLECTION);
	}
	
	public List<EnvironmentReading> getRange(long from, long to){
		Query query = new Query();
		query.addCriteria(Criteria.where("timestamp").gte(from).andOperator(Criteria.where("timestamp").lte(to)));
		
		return database.find(query, EnvironmentReading.class, ENVIRONMENT_READING_COLLECTION);
	}
	
	public int getCount(){
		CommandResult result = database.getCollection(ENVIRONMENT_READING_COLLECTION).getStats();
		return result.getInt("count");
	}
	
	public void add(EnvironmentReading envReading){
		database.insert(envReading, ENVIRONMENT_READING_COLLECTION);
	}
	
	public void update(EnvironmentReading envReading){
		database.save(envReading, ENVIRONMENT_READING_COLLECTION);
	}
	
}
