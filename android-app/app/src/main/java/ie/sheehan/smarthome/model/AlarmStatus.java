package ie.sheehan.smarthome.model;

import java.util.Date;

/**
 * Created by Alan on 14/04/2017.
 */

public class AlarmStatus {

    public boolean armed;
    public Date lastArmed;


    public AlarmStatus(boolean armed, Date lastArmed) {
        this.armed = armed;
        this.lastArmed = lastArmed;
    }

}
