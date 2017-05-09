package ie.sheehan.smarthome.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Represents a task scheduled to run.
 */
public class Task {

    private static final String TASK_ARM_ALARM = "arm_alarm";
    private static final String TASK_TURN_ON_HEATING = "turn_on_heating";

    private String displayName;
    private String type;
    private Date date;

    public Task() {}

    public Task(JSONObject json) throws JSONException {
        setType(json.getString("type"));
        setDate(new Date(json.getLong("timestamp") * 1000L));

        switch (getType()) {
            case TASK_ARM_ALARM:
                setDisplayName("Arm Alarm");
                break;

            case TASK_TURN_ON_HEATING:
                setDisplayName("Turn On Heating");
                break;

            default:
                setDisplayName("Undefined");
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
