package ie.sheehan.smarthome.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ie.sheehan.smarthome.repositories.StockReadingRepository;

@RestController
@RequestMapping(value = "/stock")
public class StockController {
	
	private static final String MQTT_BROKER = "192.167.1.101";
	private static final int MQTT_PORT = 1883;
	
	private static final String TOPIC_ENVIRONMENT_READING_REQUEST = "/ie/sheehan/smart-home/envreading/request";
	private static final String TOPIC_ENVIRONMENT_READING_RESPONSE = "/ie/sheehan/smart-home/envreading/response";
	
	private static final String TOPIC_ENVIRONMENT_HEATING_REQUEST = "/ie/sheehan/smart-home/envreading/heating/request";
	private static final String TOPIC_ENVIRONMENT_HEATING_RESPONSE = "/ie/sheehan/smart-home/envreading/heating/response";
	private static final String TOPIC_ENVIRONMENT_HEATING_ACTIVATE = "/ie/sheehan/smart-home/envreading/heating/activate";
	
	@Autowired
	StockReadingRepository repository;
	
	
	
	
}
