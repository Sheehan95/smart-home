package ie.sheehan.smarthome.controllers;

import java.net.URISyntaxException;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ie.sheehan.smarthome.models.Alarm;

@RestController
@RequestMapping(value = "/security")
public class SecurityController {
	
	private static final String MQTT_BROKER = "192.167.1.101";
	
	private static final String TOPIC_SECURITY_CAMERA_FEED = "/ie/sheehan/smart-home/security/camera/feed";
	
	private static final String TOPIC_SECURITY_ALARM_REQUEST = "/ie/sheehan/smart-home/security/alarm/request";
	private static final String TOPIC_SECURITY_ALARM_RESPONSE = "/ie/sheehan/smart-home/security/alarm/response";
	private static final String TOPIC_SECURITY_ALARM_ARM = "/ie/sheehan/smart-home/security/alarm/arm";
	
	@ResponseBody
	@RequestMapping(value = "/alarm/status", method = RequestMethod.GET)
	public Alarm getAlarmStatus() {
		return alarmStatus();
	}
	
	@RequestMapping(value = "/alarm/arm", method = RequestMethod.POST)
	public void armAlarm(@RequestBody String body) {
		try {
			boolean arm = new JSONObject(body).getBoolean("arm");
			toggleAlarmArmed(arm);
		} catch (JSONException e) {
			e.printStackTrace();
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
	
}
