package ie.sheehan.smarthome.controllers;

import java.net.URISyntaxException;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/security")
public class SecurityController {
	
	private static final String MQTT_BROKER = "192.167.1.101";
	
	private static final String TOPIC_CAMERA_FEED = "ie/sheehan/smarthome/security/camera/feed";
	
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
	
	private boolean toggleCameraStream(boolean status){
		try {
			MQTT client = new MQTT();
			client.setHost(MQTT_BROKER, 1883);
			client.setConnectAttemptsMax(1);
			client.setReconnectAttemptsMax(1);
			
			JSONObject payload = new JSONObject();
			payload.put("stream", status);
			
			BlockingConnection connection = client.blockingConnection();
			connection.connect();
			connection.publish(TOPIC_CAMERA_FEED, payload.toString().getBytes(), QoS.AT_LEAST_ONCE, false);
			
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
