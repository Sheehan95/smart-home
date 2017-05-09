package ie.sheehan.smarthome.controllers;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ie.sheehan.smarthome.models.Task;

@RestController
@RequestMapping(value = "/schedule")
public class ScheduleController {
	
	private static final String MQTT_BROKER = "192.168.0.101";
	private static final int MQTT_PORT = 1883;
	
	private static final String TOPIC_SCHEDULE_GET = "/ie/sheehan/smart-home/task/get";
	private static final String TOPIC_SCHEDULE_RESPONSE = "/ie/sheehan/smart-home/task/response";
	
	private static final String TOPIC_SCHEDULE_NEW = "/ie/sheehan/smart-home/task/schedule";
	private static final String TOPIC_SCHEDULE_CANCEL = "/ie/sheehan/smart-home/task/cancel";
	
	
	@ResponseBody
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public List<Task> getAll() {
		return queryForSchedule();
	}
	
	@ResponseBody
	@RequestMapping(value = "/get/latest", method = RequestMethod.GET)
	public Task getLatest() {
		List<Task> taskList = queryForSchedule();
		
		Task latestTask = null;
		long latest = Long.MAX_VALUE;
		
		for (Task task : taskList) {
			if (task.timestamp < latest) {
				latest = task.timestamp;
				latestTask = task;
			}
		}
		
		return latestTask;
	}
	
	@ResponseBody
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public boolean schedule(@RequestBody Task task) {
		return scheduleTask(task);
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	public boolean cancel(@RequestBody Task task) {
		return cancelTask(task);
	}
	
	
	private List<Task> queryForSchedule() {
		List<Task> taskList = new ArrayList<>();
		
		try {
			MQTT client = new MQTT();
			client.setHost(MQTT_BROKER, MQTT_PORT);
			client.setConnectAttemptsMax(1);
			client.setReconnectAttemptsMax(1);
			
			BlockingConnection connection = client.blockingConnection();
			connection.connect();
			connection.subscribe(new Topic[]{ new Topic(TOPIC_SCHEDULE_RESPONSE, QoS.AT_LEAST_ONCE) });
			connection.publish(TOPIC_SCHEDULE_GET, "Request".getBytes(), QoS.AT_LEAST_ONCE, false);
			
			Message message = connection.receive();
			JSONArray array = new JSONArray(new String(message.getPayload()));
			
			for (int i = 0 ; i < array.length() ; i++) {
				Task task = new Task();
				JSONObject json = array.getJSONObject(i);
				
				task.id = json.getInt("id");
				task.type = json.getString("type");
				task.timestamp = json.getLong("timestamp");
				
				taskList.add(task);
			}
			
		} catch (Exception e) { System.out.println(e.toString()); }
		
		return taskList;
	}
	
	private boolean scheduleTask(Task task) {
		try {
			MQTT client = new MQTT();
			client.setHost(MQTT_BROKER, MQTT_PORT);
			client.setConnectAttemptsMax(1);
			client.setReconnectAttemptsMax(1);
			
			JSONObject json = new JSONObject();
			json.put("id", task.id);
			json.put("type", task.type);
			json.put("timestamp", task.timestamp);
			
			BlockingConnection connection = client.blockingConnection();
			connection.connect();
			connection.publish(TOPIC_SCHEDULE_NEW, json.toString().getBytes(), QoS.AT_LEAST_ONCE, false);
			connection.disconnect();
			return true;
		} catch (Exception e) { 
			System.out.println(e.toString());
			return false;
		}
	}
	
	private boolean cancelTask(Task task) {
		try {
			MQTT client = new MQTT();
			client.setHost(MQTT_BROKER, MQTT_PORT);
			client.setConnectAttemptsMax(1);
			client.setReconnectAttemptsMax(1);
			
			JSONObject json = new JSONObject();
			json.put("id", task.id);
			json.put("type", task.type);
			json.put("timestamp", task.timestamp);
			
			BlockingConnection connection = client.blockingConnection();
			connection.connect();
			connection.publish(TOPIC_SCHEDULE_CANCEL, json.toString().getBytes(), QoS.AT_LEAST_ONCE, false);
			connection.disconnect();
			return true;
		} catch (Exception e) { 
			System.out.println(e.toString());
			return false;
		}
	}
	
}
