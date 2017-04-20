package ie.sheehan.smarthome.model;

import org.joda.time.Duration;

import java.util.Date;


public class HeatingStatus {

    public boolean on;

    public Date lastOn;

    public Duration duration;


    public HeatingStatus(boolean on, Date lastOn, Duration duration) {
        this.on = on;
        this.lastOn = lastOn;
        this.duration = duration;
    }

}
