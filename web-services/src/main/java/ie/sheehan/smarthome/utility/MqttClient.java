package ie.sheehan.smarthome.utility;

import java.net.URISyntaxException;

import org.fusesource.mqtt.client.MQTT;

public class MqttClient {
	
	public static final String MQTT_BROKER = "192.167.1.101";
	public static final int MQTT_PORT = 1883;
	
	public static final String TOPIC_ENVIRONMENT_READING_REQUEST = "/ie/sheehan/smart-home/envreading/request";
	public static final String TOPIC_ENVIRONMENT_READING_RESPONSE = "/ie/sheehan/smart-home/envreading/response";
	
	public static final String TOPIC_SECURITY_CAMERA_FEED = "/ie/sheehan/smart-home/security/camera/feed";
	public static final String TOPIC_SECURITY_ALARM_STATUS = "/ie/sheehan/smart-home/security/alarm/status";
	public static final String TOPIC_SECURITY_ALARM_ARM = "/ie/sheehan/smart-home/security/alarm/arm";
	
	
	private MQTT client;
	
	public MqttClient() throws URISyntaxException {
		client = new MQTT();
		client.setHost(MQTT_BROKER, MQTT_PORT);
		client.setConnectAttemptsMax(1);
		client.setReconnectAttemptsMax(1);
	}
	
}
