package ie.sheehan.smarthome.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * An object representing an intrusion on the premises.
 */
public class IntrusionReading implements Serializable {

    public String id;
    public String image;
    public long timestamp;
    public boolean viewed;


    /**
     * Default constructor that creates an {@link IntrusionReading} object from a {@link JSONObject}
     * with the properties id, image, timestamp & viewed.
     *
     * @param jsonObject with the properties id, image, timestamp & viewed
     */
    public IntrusionReading(JSONObject jsonObject) {
        try {
            id = jsonObject.getString("id");
            image = jsonObject.getString("image");
            timestamp = jsonObject.getLong("timestamp");
            viewed = jsonObject.getBoolean("viewed");
        } catch (JSONException e) {
            Log.d("ERROR", "Unable to construct from JSON object.");
        }
    }

    @Override
    public String toString() {
        return String.format("Date: %s\nViewed: %b", new Date(timestamp * 1000L), viewed);
    }
}
