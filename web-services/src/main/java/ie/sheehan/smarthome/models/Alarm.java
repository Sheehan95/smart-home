package ie.sheehan.smarthome.models;

import java.util.Date;

public class Alarm {
	
	public boolean armed;
	
	public long timestamp;
	
	@Override
	public String toString() {
		return String.format("Alarm: %b\nLast Armed: %s", armed, new Date(timestamp * 1000L).toString());
	}
	
}
