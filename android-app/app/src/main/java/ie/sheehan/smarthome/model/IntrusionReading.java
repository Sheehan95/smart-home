package ie.sheehan.smarthome.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alan on 15/04/2017.
 */

public class IntrusionReading {

    public String _id;
    public String image;
    public long timestamp;
    public boolean viewed;

    public IntrusionReading() {}

    public IntrusionReading(JSONObject jsonObject) {
        try {
            image = jsonObject.getString("image");
            timestamp = jsonObject.getLong("timestamp");
            viewed = jsonObject.getBoolean("viewed");
        } catch (JSONException e) {
            Log.d("ERROR", "Unable to construct from JSON object.");
        }
    }

}
