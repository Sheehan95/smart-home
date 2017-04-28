package ie.sheehan.smarthome.model;

import org.joda.time.Duration;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * Represents the current or most up-to-date state of the heating, including whether or not it is
 * on, the last time it was turned on and for how long it was on for.
 */
public class HeatingStatus implements Serializable {

    // ============================================================================================
    // DECLARING CLASS VARIABLES
    // ============================================================================================
    private boolean on;
    private Date lastOn;
    private Duration duration;


    // ============================================================================================
    // DECLARING CONSTRUCTORS
    // ============================================================================================
    /**
     * Default constructor.
     */
    public HeatingStatus() {}

    /**
     *
     *
     * @param on whether the heating is on or not
     * @param lastOn the date the heating was last turned on
     * @param duration for how long the heating was on for
     */
    public HeatingStatus(boolean on, Date lastOn, Duration duration) {
        this.on = on;
        this.lastOn = lastOn;
        this.duration = duration;
    }

    /**
     * Creates a new object by parsing the values of a {@link JSONObject}.
     *
     * @param json representing an {@link HeatingStatus}.
     */
    public HeatingStatus(JSONObject json) throws JSONException {
        setOn(json.getBoolean("on"));
        setLastOn(new Date(json.getLong("timestamp") * 1000L));
        setDuration(new Duration(json.getInt("duration") * 1000L));
    }


    // ============================================================================================
    // DECLARING METHODS
    // ============================================================================================
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "On: %b\nLast On: %s\nDuration: %s",
                isOn(), getLastOn().toString(), getDuration().toString());
    }


    // ============================================================================================
    // DECLARING GETTERS AND SETTERS
    // ============================================================================================
    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public Date getLastOn() {
        return lastOn;
    }

    public void setLastOn(Date lastOn) {
        this.lastOn = lastOn;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

}
