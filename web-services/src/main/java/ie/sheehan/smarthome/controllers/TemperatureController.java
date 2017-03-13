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

import ie.sheehan.smarthome.models.Temperature;
import ie.sheehan.smarthome.repositories.TemperatureRepository;

@RestController
@RequestMapping(value = "/temperature")
public class TemperatureController {
	
	@Autowired
	TemperatureRepository repository;
	
	
	@ResponseBody
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
	public Temperature get(@PathParam("id") String id){
		return repository.get(id);
	}
	
	@ResponseBody
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public List<Temperature> get(@RequestParam("from") long from, @RequestParam("to") long to){
		return repository.getRange(from, to);
	}
	
	@ResponseBody
	@RequestMapping(value = "/get/latest", method = RequestMethod.GET)
	public Temperature getLatest(){
		return repository.getLatest();
	}
	
	@ResponseBody
	@RequestMapping(value = "/get/all", method = RequestMethod.GET)
	public List<Temperature> getAll(){
		return repository.getAll();
	}
	
	@ResponseBody
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public void add(@RequestParam("temperature") double temperature, @RequestParam("humidity") double humidity){
		Temperature temp = new Temperature();
		temp.temperature = temperature;
		temp.humidity = humidity;
		temp.setTimestamp(System.currentTimeMillis());
		
		repository.add(temp);
	}
	
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public Temperature testThatNewFeatureBrah(){
		return queryForTemperature();
	}
	
	
	/**
	 * Requests a live temperature reading via MQTT.
	 * 
	 * @return 
	 */
	private Temperature queryForTemperature(){
		Temperature temperature = new Temperature();
		JSONObject json;
		
		try {
			MQTT client = new MQTT();
			client.setHost("192.167.1.23", 1883);
			
			BlockingConnection connection = client.blockingConnection();
			connection.connect();
			connection.subscribe(new Topic[]{ new Topic("/ie/sheehan/smart-home/temperature/getit", QoS.AT_LEAST_ONCE) });
			connection.publish("/ie/sheehan/smart-home/temperature/request", "Hello".getBytes(), QoS.AT_LEAST_ONCE, false);
			
			Message message = connection.receive();
			json = new JSONObject(new String(message.getPayload()));
			
			temperature.temperature = json.getDouble("temperature");
			temperature.humidity = json.getDouble("humidity");
			
			message.ack();
			connection.disconnect();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		temperature.setTimestamp(System.currentTimeMillis());
		return temperature;
	}
	
}
