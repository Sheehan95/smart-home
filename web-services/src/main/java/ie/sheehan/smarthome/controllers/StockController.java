package ie.sheehan.smarthome.controllers;

import java.util.List;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ie.sheehan.smarthome.models.StockReading;
import ie.sheehan.smarthome.repositories.StockReadingRepository;

@RestController
@RequestMapping(value = "/stock")
public class StockController {
	
	private static final String MQTT_BROKER = "192.168.0.101";
	private static final int MQTT_PORT = 1883;
	
	private static final String TOPIC_STOCK_SCALE_REQUEST = "/ie/sheehan/smart-home/stock/scale/request";
	private static final String TOPIC_STOCK_SCALE_RESPONSE = "/ie/sheehan/smart-home/stock/scale/response";
	
	private static final String TOPIC_STOCK_SCALE_CALIBRATE = "/ie/sheehan/smart-home/stock/scale/calibrate";
	
	
	@Autowired
	StockReadingRepository repository;
	
	
	@ResponseBody
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public StockReading get() {
		return queryForStockReading();
	}
	
	@ResponseBody
	@RequestMapping(value = "/get/all", method = RequestMethod.GET)
	public List<StockReading> getAll() {
		return repository.getAll();
	}
	
	@ResponseBody
	@RequestMapping(value = "/get/{product}")
	public List<StockReading> getByProduct(@PathVariable String product) {
		return repository.getByProduct(product);
	}
	
	@ResponseBody
	@RequestMapping(value = "/get/{product}/latest")
	public List<StockReading> getLatestByProduct(@PathVariable String product) {
		return repository.getByProduct(product);
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public void add(@RequestBody StockReading stockReading) {
		repository.add(stockReading);
	}
	
	@ResponseBody
	@RequestMapping(value = "/get/products")
	public List<String> getProductNames() {
		return repository.getProducts();
	}
	
	@ResponseBody
	@RequestMapping(value = "/scale/calibrate", method = RequestMethod.POST)
	public boolean calibrate(@RequestBody String body) {
		try {
			String product = new JSONObject(body).getString("product");
			return calibrateScale(product);
		} catch(Exception e) {
			return false;
		}
	}
	
	
	private StockReading queryForStockReading() {
		StockReading stockReading = new StockReading();
		
		try {
			MQTT client = new MQTT();
			client.setHost(MQTT_BROKER, MQTT_PORT);
			client.setConnectAttemptsMax(1);
			client.setReconnectAttemptsMax(1);
			
			BlockingConnection connection = client.blockingConnection();
			connection.connect();
			connection.subscribe(new Topic[]{ new Topic(TOPIC_STOCK_SCALE_RESPONSE, QoS.AT_LEAST_ONCE) });
			connection.publish(TOPIC_STOCK_SCALE_REQUEST, "Request".getBytes(), QoS.AT_LEAST_ONCE, false);
			
			Message message = connection.receive();
			JSONObject json = new JSONObject(new String(message.getPayload()));
			
			stockReading.product = json.getString("product");
			stockReading.weight = json.getDouble("weight");
			stockReading.capacity = json.getDouble("capacity");
			stockReading.timestamp = json.getLong("timestamp");
			
			message.ack();
			connection.disconnect();
		} catch (Exception e){
			System.out.println("ERROR: " + e.toString());
		}
		
		return stockReading;
	}
	
	private boolean calibrateScale(String product) {
		try {
			MQTT client = new MQTT();
			client.setHost(MQTT_BROKER, MQTT_PORT);
			client.setConnectAttemptsMax(1);
			client.setReconnectAttemptsMax(1);
			
			JSONObject payload = new JSONObject();
			payload.put("product", product);
			
			BlockingConnection connection = client.blockingConnection();
			connection.connect();
			connection.publish(TOPIC_STOCK_SCALE_CALIBRATE, payload.toString().getBytes(), QoS.AT_LEAST_ONCE, false);
			
			connection.disconnect();
			return true;
		} catch (Exception e){
			System.out.println("ERROR: " + e.toString());
			return false;
		}
	}
	
}
