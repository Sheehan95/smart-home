package ie.sheehan.smarthome.controllers;

import java.net.URISyntaxException;
import java.util.List;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ie.sheehan.smarthome.models.Alarm;
import ie.sheehan.smarthome.models.EnvironmentReading;
import ie.sheehan.smarthome.models.Heating;
import ie.sheehan.smarthome.repositories.EnvironmentReadingRepository;

@RestController
@RequestMapping(value = "/environment")
public class EnvironmentController {
	
	private static final String MQTT_BROKER = "192.167.1.101";
	private static final int MQTT_PORT = 1883;
	
	private static final String TOPIC_ENVIRONMENT_READING_REQUEST = "/ie/sheehan/smart-home/envreading/request";
	private static final String TOPIC_ENVIRONMENT_READING_RESPONSE = "/ie/sheehan/smart-home/envreading/response";
	
	private static final String TOPIC_ENVIRONMENT_HEATING_REQUEST = "/ie/sheehan/smart-home/envreading/heating/request";
	private static final String TOPIC_ENVIRONMENT_HEATING_RESPONSE = "/ie/sheehan/smart-home/envreading/heating/response";
	private static final String TOPIC_ENVIRONMENT_HEATING_ACTIVATE = "/ie/sheehan/smart-home/envreading/heating/activate";
	
	@Autowired
	EnvironmentReadingRepository repository;
	
	
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public EnvironmentReading get(){
		return queryForEnvironmentReading();
	}
	
	@ResponseBody
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
	public EnvironmentReading getById(@PathVariable String id){
		return repository.get(id);
	}
	
	@ResponseBody
	@RequestMapping(value = "/get/range", method = RequestMethod.GET)
	public List<EnvironmentReading> getRange(@RequestParam("from") long from, @RequestParam("to") long to){
		return repository.getRange(from, to);
	}
	
	@ResponseBody
	@RequestMapping(value = "/get/latest", method = RequestMethod.GET)
	public EnvironmentReading getLatest(){
		return repository.getLatest();
	}
	
	@ResponseBody
	@RequestMapping(value = "/get/all", method = RequestMethod.GET)
	public List<EnvironmentReading> getAll(){
		return repository.getAll();
	}
	
	@ResponseBody
	@RequestMapping(value = "/get/count", method = RequestMethod.GET)
	public int getCount(){
		return repository.getCount();
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public void add(@RequestBody EnvironmentReading envReading){
		repository.add(envReading);
	}
	
	@ResponseBody
	@RequestMapping(value = "/heating/activate", method = RequestMethod.POST)
	public boolean armAlarm(@RequestBody String body) {
		try {
			boolean on = new JSONObject(body).getBoolean("on");
			return toggleHeatingOn(on);
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/heating/status", method = RequestMethod.GET)
	public Heating getHeatingStatus() {
		return heatingStatus();
	}
	
	
	
	/**
	 * Sets up an MQTT client to query the broker for the latest environment
	 * reading.
	 * 
	 * @return the latest {@link EnvironmentReading} from the sensor
	 */
	private EnvironmentReading queryForEnvironmentReading(){
		EnvironmentReading envReading = new EnvironmentReading();
		JSONObject json;
		
		try {
			MQTT client = new MQTT();
			client.setHost(MQTT_BROKER, MQTT_PORT);
			client.setConnectAttemptsMax(1);
			client.setReconnectAttemptsMax(1);
			
			BlockingConnection connection = client.blockingConnection();
			connection.connect();
			connection.subscribe(new Topic[]{ new Topic(TOPIC_ENVIRONMENT_READING_RESPONSE, QoS.AT_LEAST_ONCE) });
			connection.publish(TOPIC_ENVIRONMENT_READING_REQUEST, "Request".getBytes(), QoS.AT_LEAST_ONCE, false);
			
			Message message = connection.receive();
			json = new JSONObject(new String(message.getPayload()));
			
			envReading.temperature = json.getDouble("temperature");
			envReading.humidity = json.getDouble("humidity");
			envReading.timestamp = json.getLong("timestamp");
			
			message.ack();
			connection.disconnect();
		} catch (URISyntaxException e) {
			System.out.println("Broker incorrectly configured.");
			return new EnvironmentReading();
		} catch (JSONException e) {
			System.out.println("Unable to parse value from JSON.");
			return new EnvironmentReading();
		} catch (Exception e) {
			System.out.println("MQTT error.");
			return new EnvironmentReading();
		}
		
		return envReading;
	}
	
	
	private Heating heatingStatus() {
		Heating heating = new Heating();
		JSONObject json;
		
		try {
			MQTT client = new MQTT();
			client.setHost(MQTT_BROKER, 1883);
			client.setConnectAttemptsMax(1);
			client.setReconnectAttemptsMax(1);
			
			BlockingConnection connection = client.blockingConnection();
			connection.connect();
			connection.subscribe(new Topic[]{ new Topic(TOPIC_ENVIRONMENT_HEATING_RESPONSE, QoS.AT_LEAST_ONCE) });
			connection.publish(TOPIC_ENVIRONMENT_HEATING_REQUEST, "Request".getBytes(), QoS.AT_LEAST_ONCE, false);
			
			Message message = connection.receive();
			json = new JSONObject(new String(message.getPayload()));
			
			heating.on = json.getBoolean("on");
			heating.timestamp = json.getLong("timestamp");
			heating.duration = (int) json.getLong("duration");
			
			message.ack();
			connection.disconnect();
		} catch (URISyntaxException e) {
			System.out.println("Broker incorrectly configured.");
			return new Heating();
		} catch (JSONException e) {
			System.out.println("Unable to parse value from JSON.");
			System.out.println(e.toString());
			return new Heating();
		} catch (Exception e) {
			System.out.println("MQTT error.");
			return new Heating();
		}
		
		return heating;
	}
	
	
	private boolean toggleHeatingOn(boolean on) {
		try {
			MQTT client = new MQTT();
			client.setHost(MQTT_BROKER, 1883);
			client.setConnectAttemptsMax(1);
			client.setReconnectAttemptsMax(1);
			
			JSONObject payload = new JSONObject();
			payload.put("on", on);
			
			BlockingConnection connection = client.blockingConnection();
			connection.connect();
			connection.publish(TOPIC_ENVIRONMENT_HEATING_ACTIVATE, payload.toString().getBytes(), QoS.AT_LEAST_ONCE, false);
			
			connection.disconnect();
			return true;
		} catch (URISyntaxException e) {
			System.out.println("Broker incorrectly configured.");
			return false;
		} catch (JSONException e) {
			System.out.println("Unable to parse value from JSON.");
			return false;
		} catch (Exception e) {
			System.out.println("MQTT error.");
			return false;
		}
	}
	
}
