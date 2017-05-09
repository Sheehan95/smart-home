package ie.sheehan.smarthome.models;

import java.util.Date;

public class Task {
	
	public int id;
	
	public String type;
	
	public long timestamp;
	
	@Override
	public String toString() {
		return String.format("%d:\t%s @ %s", id, type, new Date(timestamp * 1000L));
	}
	
}
