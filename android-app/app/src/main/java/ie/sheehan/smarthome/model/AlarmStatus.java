package ie.sheehan.smarthome.model;

import java.util.Date;

/**
 * Represents the current or most up-to-date state of the alarm, including whether or not it is
 * armed and the last time it was armed.
 */
public class AlarmStatus {

    public boolean armed;
    public Date lastArmed;


    /**
     * Default constructor.
     *
     * @param armed whether the alarm is armed or not
     * @param lastArmed the date the alarm was last armed
     */
    public AlarmStatus(boolean armed, Date lastArmed) {
        this.armed = armed;
        this.lastArmed = lastArmed;
    }

}
