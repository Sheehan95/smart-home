package ie.sheehan.smarthome.controllers;

import java.util.List;

import javax.websocket.server.PathParam;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ie.sheehan.smarthome.models.EnvironmentReading;
import ie.sheehan.smarthome.repositories.EnvironmentReadingRepository;

@RestController
@RequestMapping(value = "/environment")
public class EnvironmentController {
	
	private static final String BROKER = "192.167.1.23";
	
	private static final String TOPIC_ENVIRONMENT_READING_REQUEST = "/ie/sheehan/smart-home/envreading/request";
	private static final String TOPIC_ENVIRONMENT_READING_RESPONSE = "/ie/sheehan/smart-home/envreading/response";
	
	@Autowired
	EnvironmentReadingRepository repository;
	
	
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public EnvironmentReading get(){
		return queryForEnvironmentReading();
	}
	
	@ResponseBody
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
	public EnvironmentReading getById(@PathParam("id") String id){
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
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public void add(@RequestParam("temperature") double temperature, @RequestParam("humidity") double humidity){
		EnvironmentReading envReading = new EnvironmentReading();
		envReading.temperature = temperature;
		envReading.humidity = humidity;
		envReading.setTimestamp(System.currentTimeMillis());
		
		repository.add(envReading);
	}

	
	
	
	private EnvironmentReading queryForEnvironmentReading(){
		EnvironmentReading envReading = new EnvironmentReading();
		JSONObject json;
		
		try {
			MQTT client = new MQTT();
			client.setHost(BROKER, 1883);
			
			BlockingConnection connection = client.blockingConnection();
			connection.connect();
			connection.subscribe(new Topic[]{ new Topic(TOPIC_ENVIRONMENT_READING_RESPONSE, QoS.AT_LEAST_ONCE) });
			connection.publish(TOPIC_ENVIRONMENT_READING_REQUEST, "Request".getBytes(), QoS.AT_LEAST_ONCE, false);
			
			Message message = connection.receive();
			json = new JSONObject(new String(message.getPayload()));
			
			envReading.temperature = json.getDouble("temperature");
			envReading.humidity = json.getDouble("humidity");
			envReading.setTimestamp(System.currentTimeMillis());
			
			message.ack();
			connection.disconnect();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return envReading;
	}
	
}