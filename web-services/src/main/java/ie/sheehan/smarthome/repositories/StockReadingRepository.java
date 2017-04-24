package ie.sheehan.smarthome.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.CommandResult;

import ie.sheehan.smarthome.models.StockReading;
import ie.sheehan.smarthome.models.StockReading.StockReadingTimeComparator;

/**
 * DAO for {@link StockReading} objects saved in a MongoDB database.
 * 
 * @author Alan Sheehan
 */
@Repository
public class StockReadingRepository {
	
	public static final String COLLECTION_STOCK_READING = "stockreadings";
	
	private MongoTemplate database;
	
	
	@Autowired
	public StockReadingRepository(MongoTemplate database){
		this.database = database;
		
		if (!this.database.collectionExists(COLLECTION_STOCK_READING)){
			this.database.createCollection(StockReading.class);
		}
	}
	
	
	public StockReading get(String id){
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		
		return database.findOne(query, StockReading.class, COLLECTION_STOCK_READING);
	}
	
	public StockReading getLatest(){
		List<StockReading> stockReadings = database.findAll(StockReading.class, COLLECTION_STOCK_READING);
		stockReadings.sort(new StockReadingTimeComparator());
		
		return stockReadings.get(stockReadings.size() - 1);
	}
	
	public List<StockReading> getAll() {
		return database.findAll(StockReading.class, COLLECTION_STOCK_READING);
	}
	
	public List<StockReading> getRange(long from, long to){
		Query query = new Query();
		query.addCriteria(Criteria.where("timestamp").gte(from).andOperator(Criteria.where("timestamp").lte(to)));
		
		return database.find(query, StockReading.class, COLLECTION_STOCK_READING);
	}
	
	public List<StockReading> getByProduct(String product) {
		Query query = new Query();
		query.addCriteria(Criteria.where("product").is(product));
		
		return database.find(query, StockReading.class, COLLECTION_STOCK_READING);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getProducts() {
		return database.getCollection(COLLECTION_STOCK_READING).distinct("product");
	}
	
	public int getCount(){
		CommandResult result = database.getCollection(COLLECTION_STOCK_READING).getStats();
		return result.getInt("count");
	}
	
	public void add(StockReading stockReading){
		database.insert(stockReading, COLLECTION_STOCK_READING);
	}
	
	public void update(StockReading stockReading){
		database.save(stockReading, COLLECTION_STOCK_READING);
	}
	
}
