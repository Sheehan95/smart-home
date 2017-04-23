package ie.sheehan.smarthome.model;

import org.joda.time.Duration;

import java.util.Date;

/**
 * Represents the current or most up-to-date state of the heating, including whether or not it is
 * on, the last time it was turned on and for how long it was on for.
 */
public class HeatingStatus {

    public boolean on;

    public Date lastOn;

    public Duration duration;

    /**
     * Default constructor.
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

}
