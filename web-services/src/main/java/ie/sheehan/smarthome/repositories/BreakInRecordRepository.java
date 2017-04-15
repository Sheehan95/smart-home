package ie.sheehan.smarthome.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.CommandResult;

import ie.sheehan.smarthome.models.BreakInRecord;

@Repository
public class BreakInRecordRepository {
	
	public static final String COLLECTION_BREAK_IN_RECORDING = "breakin";
	
	private MongoTemplate database;
	
	
	@Autowired
	public BreakInRecordRepository(MongoTemplate database) {
		this.database = database;
		
		if (! this.database.collectionExists(COLLECTION_BREAK_IN_RECORDING)) {
			this.database.createCollection(BreakInRecord.class);
		}
	}
	
	
	public BreakInRecord get(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		
		return database.findOne(query, BreakInRecord.class, COLLECTION_BREAK_IN_RECORDING);
	}
	
	public BreakInRecord getLatest() {
		List<BreakInRecord> breakInRecords = database.findAll(BreakInRecord.class, COLLECTION_BREAK_IN_RECORDING);
		return breakInRecords.get(breakInRecords.size() - 1);
	}
	
	public List<BreakInRecord> getAll() {
		return database.findAll(BreakInRecord.class, COLLECTION_BREAK_IN_RECORDING);
	}
	
	public List<BreakInRecord> getUnseen() {
		Query query = new Query();
		query.addCriteria(Criteria.where("viewed").is(false));
		
		return database.find(query, BreakInRecord.class, COLLECTION_BREAK_IN_RECORDING);
	}
	
	public int getCount() {
		CommandResult result = database.getCollection(COLLECTION_BREAK_IN_RECORDING).getStats();
		return result.getInt("count");
	}
	
	public int getUnseenCount() {
		Query query = new Query();
		query.addCriteria(Criteria.where("viewed").is(false));
		
		List<BreakInRecord> breakInRecords = database.find(query, BreakInRecord.class, COLLECTION_BREAK_IN_RECORDING);
		return breakInRecords.size();
	}
	
	public void add(BreakInRecord breakInRecord) {
		database.insert(breakInRecord, COLLECTION_BREAK_IN_RECORDING);
	}
	
	public void update(BreakInRecord breakInRecord) {
		database.save(breakInRecord, COLLECTION_BREAK_IN_RECORDING);
	}
	
}
