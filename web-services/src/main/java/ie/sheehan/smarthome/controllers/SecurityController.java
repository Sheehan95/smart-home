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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ie.sheehan.smarthome.models.Alarm;
import ie.sheehan.smarthome.models.IntrusionReading;
import ie.sheehan.smarthome.repositories.IntrusionReadingRepository;

@RestController
@RequestMapping(value = "/security")
public class SecurityController {
	
	// ==== DEFINING CONSTANTS ================================================
	private static final String MQTT_BROKER = "192.168.0.101";
	
	private static final String TOPIC_SECURITY_CAMERA_FEED = "/ie/sheehan/smart-home/security/camera/feed";
	
	private static final String TOPIC_SECURITY_ALARM_REQUEST = "/ie/sheehan/smart-home/security/alarm/request";
	private static final String TOPIC_SECURITY_ALARM_RESPONSE = "/ie/sheehan/smart-home/security/alarm/response";
	private static final String TOPIC_SECURITY_ALARM_ARM = "/ie/sheehan/smart-home/security/alarm/arm";
	// ========================================================================
	
	
	@Autowired
	IntrusionReadingRepository repository;
	
	
	// ==== REST API ==========================================================
	@ResponseBody
	@RequestMapping(value = "/alarm/status", method = RequestMethod.GET)
	public Alarm getAlarmStatus() {
		return alarmStatus();
	}
	
	@ResponseBody
	@RequestMapping(value = "/alarm/arm", method = RequestMethod.POST)
	public boolean armAlarm(@RequestBody String body) {
		try {
			boolean arm = new JSONObject(body).getBoolean("arm");
			return toggleAlarmArmed(arm);
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/camera/feed", method = RequestMethod.POST)
	public boolean cameraFeed(@RequestBody String body) {
		try {
			boolean stream = new JSONObject(body).getBoolean("stream");
			return toggleCameraStream(stream);
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	@RequestMapping(value = "/intrusion/add", method = RequestMethod.POST)
	public void addIntrusion(@RequestBody IntrusionReading intrusion){
		repository.add(intrusion);
	}
	
	@ResponseBody
	@RequestMapping(value = "/intrusion/get/{id}", method = RequestMethod.GET)
	public IntrusionReading getById(@PathVariable String id) {
		return repository.get(id);
	}
	
	@ResponseBody
	@RequestMapping(value = "/intrusion/get/latest", method = RequestMethod.GET)
	public IntrusionReading getLatest() {
		return repository.getLatest();
	}
	
	@ResponseBody
	@RequestMapping(value = "/intrusion/get/unseen", method = RequestMethod.GET)
	public List<IntrusionReading> getUnseen() {
		return repository.getUnseen();
	}
	
	@ResponseBody
	@RequestMapping(value = "/intrusion/get/all", method = RequestMethod.GET)
	public List<IntrusionReading> getAll() {
		return repository.getAll();
	}
	
	@ResponseBody
	@RequestMapping(value = "/intrusion/get/unseen/count", method = RequestMethod.GET)
	public int getUnseenCount() {
		return repository.getUnseenCount();
	}
	
	@ResponseBody
	@RequestMapping(value = "/intrusion/get/count", method = RequestMethod.GET)
	public int getCount() {
		return repository.getCount();
	}
	
	@RequestMapping(value = "/intrusion/view/{id}", method = RequestMethod.POST)
	public void markAsViewed(@PathVariable String id) {
		IntrusionReading intrusion = repository.get(id);
		intrusion.viewed = true;
		repository.update(intrusion);
	}
	
	@RequestMapping(value = "/intrusion/view/all", method = RequestMethod.POST)
	public void markAllAsViewed() {
		List<IntrusionReading> intrusionReadings = repository.getUnseen();
		
		if (! intrusionReadings.isEmpty()) {
			for (IntrusionReading intrusion : intrusionReadings) {
				intrusion.viewed = true;
				repository.update(intrusion);
			}
		}
	}
	
	@RequestMapping(value = "/intrusion/remove/{id}", method = RequestMethod.POST)
	public void removeIntrusion(@PathVariable String id) {
		repository.remove(repository.get(id));
	}
	
	@RequestMapping(value = "/intrusion/remove/all", method = RequestMethod.POST)
	public void removeAll() {
		List<IntrusionReading> intrusionReadings = repository.getAll();
		
		for (IntrusionReading reading : intrusionReadings) {
			repository.remove(reading);
		}
	}
	// ========================================================================
	
	
	// ==== PRIVATE METHODS ===================================================
	private Alarm alarmStatus() {
		Alarm alarm = new Alarm();
		JSONObject json;
		
		try {
			MQTT client = new MQTT();
			client.setHost(MQTT_BROKER, 1883);
			client.setConnectAttemptsMax(1);
			client.setReconnectAttemptsMax(1);
			
			BlockingConnection connection = client.blockingConnection();
			connection.connect();
			connection.subscribe(new Topic[]{ new Topic(TOPIC_SECURITY_ALARM_RESPONSE, QoS.AT_LEAST_ONCE) });
			connection.publish(TOPIC_SECURITY_ALARM_REQUEST, "Request".getBytes(), QoS.AT_LEAST_ONCE, false);
			
			Message message = connection.receive();
			json = new JSONObject(new String(message.getPayload()));
			
			alarm.armed = json.getBoolean("armed");
			alarm.timestamp = json.getLong("timestamp");
			
			message.ack();
			connection.disconnect();
		} catch (URISyntaxException e) {
			System.out.println("Broker incorrectly configured.");
			return new Alarm();
		} catch (JSONException e) {
			System.out.println("Unable to parse value from JSON.");
			return new Alarm();
		} catch (Exception e) {
			System.out.println("MQTT error.");
			return new Alarm();
		}
		
		return alarm;
	}
	
	private boolean toggleAlarmArmed(boolean arm) {
		try {
			MQTT client = new MQTT();
			client.setHost(MQTT_BROKER, 1883);
			client.setConnectAttemptsMax(1);
			client.setReconnectAttemptsMax(1);
			
			JSONObject payload = new JSONObject();
			payload.put("arm", arm);
			
			BlockingConnection connection = client.blockingConnection();
			connection.connect();
			connection.publish(TOPIC_SECURITY_ALARM_ARM, payload.toString().getBytes(), QoS.AT_LEAST_ONCE, false);
			
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
	
	private boolean toggleCameraStream(boolean status) {
		try {
			MQTT client = new MQTT();
			client.setHost(MQTT_BROKER, 1883);
			client.setConnectAttemptsMax(1);
			client.setReconnectAttemptsMax(1);
			
			JSONObject payload = new JSONObject();
			payload.put("stream", status);
			
			BlockingConnection connection = client.blockingConnection();
			connection.connect();
			connection.publish(TOPIC_SECURITY_CAMERA_FEED, payload.toString().getBytes(), QoS.AT_LEAST_ONCE, false);
			
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
	// ========================================================================
	
}
