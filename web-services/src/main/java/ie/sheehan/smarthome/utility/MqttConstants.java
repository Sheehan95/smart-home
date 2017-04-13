package ie.sheehan.smarthome.utility;

public class MqttConstants {
	
	public static final String MQTT_BROKER = "192.167.1.101";
	public static final int MQTT_PORT = 1883;
	
	public static final String TOPIC_ENVIRONMENT_READING_REQUEST = "/ie/sheehan/smart-home/envreading/request";
	public static final String TOPIC_ENVIRONMENT_READING_RESPONSE = "/ie/sheehan/smart-home/envreading/response";
	
	public static final String TOPIC_SECURITY_CAMERA_FEED = "/ie/sheehan/smart-home/security/camera/feed";
	public static final String TOPIC_SECURITY_ALARM_STATUS = "/ie/sheehan/smart-home/security/alarm/status";
	public static final String TOPIC_SECURITY_ALARM_ARM = "/ie/sheehan/smart-home/security/alarm/arm";
	
	private MqttConstants() { throw new AssertionError(); }
	
}
