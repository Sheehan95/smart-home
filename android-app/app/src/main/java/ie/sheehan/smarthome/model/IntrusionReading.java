package ie.sheehan.smarthome.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class IntrusionReading implements Serializable {

    public String id;
    public String image;
    public long timestamp;
    public boolean viewed;

    public IntrusionReading() {}

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
