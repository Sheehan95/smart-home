package ie.sheehan.smarthome.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * Represents the current or most up-to-date state of the alarm, including whether or not it is
 * armed and the last time it was armed.
 */
public class AlarmStatus implements Serializable {

    // ============================================================================================
    // DECLARING CLASS VARIABLES
    // ============================================================================================
    private boolean armed;
    private Date lastArmed;


    // ============================================================================================
    // DECLARING CONSTRUCTORS
    // ============================================================================================
    /**
     * Default constructor.
     */
    public AlarmStatus() {}

    /**
     * Creates a new object by parsing the values of a {@link JSONObject}.
     *
     * @param json representing an {@link AlarmStatus}
     * @throws JSONException if the {@link JSONObject} isn't the correct format
     */
    public AlarmStatus(JSONObject json) throws JSONException {
        setArmed(json.getBoolean("armed"));
        setLastArmed(new Date(json.getLong("timestamp") * 1000L));
    }


    // ============================================================================================
    // DECLARING METHODS
    // ============================================================================================
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Armed: %b\nLast Armed: %s",
                isArmed(), getLastArmed().toString());
    }


    // ============================================================================================
    // DECLARING GETTERS AND SETTERS
    // ============================================================================================
    public boolean isArmed() {
        return armed;
    }

    public void setArmed(boolean armed) {
        this.armed = armed;
    }

    public Date getLastArmed() {
        return lastArmed;
    }

    public void setLastArmed(Date lastArmed) {
        this.lastArmed = lastArmed;
    }

}
