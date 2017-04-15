package ie.sheehan.smarthome.repositories;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.CommandResult;

import ie.sheehan.smarthome.models.IntrusionReading;

@Repository
public class IntrusionReadingRepository {
	
	public static final String COLLECTION_INTRUSION_READINGS = "intrusionreadings";
	
	private MongoTemplate database;
	
	
	@Autowired
	public IntrusionReadingRepository(MongoTemplate database) {
		this.database = database;
		
		if (! this.database.collectionExists(COLLECTION_INTRUSION_READINGS)) {
			this.database.createCollection(IntrusionReading.class);
		}
	}
	
	
	public IntrusionReading get(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
		
		return database.findOne(query, IntrusionReading.class, COLLECTION_INTRUSION_READINGS);
	}
	
	public IntrusionReading getLatest() {
		List<IntrusionReading> intrusionReadings = database.findAll(IntrusionReading.class, COLLECTION_INTRUSION_READINGS);
		return intrusionReadings.get(intrusionReadings.size() - 1);
	}
	
	public List<IntrusionReading> getAll() {
		return database.findAll(IntrusionReading.class, COLLECTION_INTRUSION_READINGS);
	}
	
	public List<IntrusionReading> getUnseen() {
		Query query = new Query();
		query.addCriteria(Criteria.where("viewed").is(false));
		
		return database.find(query, IntrusionReading.class, COLLECTION_INTRUSION_READINGS);
	}
	
	public int getCount() {
		CommandResult result = database.getCollection(COLLECTION_INTRUSION_READINGS).getStats();
		return result.getInt("count");
	}
	
	public int getUnseenCount() {
		Query query = new Query();
		query.addCriteria(Criteria.where("viewed").is(false));
		
		List<IntrusionReading> intrusionReadings = database.find(query, IntrusionReading.class, COLLECTION_INTRUSION_READINGS);
		return intrusionReadings.size();
	}
	
	public void add(IntrusionReading intrusion) {
		database.insert(intrusion, COLLECTION_INTRUSION_READINGS);
	}
	
	public void update(IntrusionReading intrusion) {
		database.save(intrusion, COLLECTION_INTRUSION_READINGS);
	}
	
}
