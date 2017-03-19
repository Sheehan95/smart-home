package ie.sheehan.smarthome.model;

import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

public class EnvironmentReading implements Serializable {

    private String _id;
    private double temperature;
    private double humidity;
    private long timestamp;

    public EnvironmentReading(){}

    public EnvironmentReading(JSONObject jsonObject) {
        try {
            setTemperature(jsonObject.getDouble("temperature"));
            setHumidity(jsonObject.getDouble("humidity"));
            setTimestamp(jsonObject.getLong("timestamp"));
        } catch (JSONException e) {
            Log.d("ERROR", "Unable to construct from JSON object.");
        }
    }

    @Override
    public String toString() {
        Date date = new Date(timestamp * 1000L);
        return String.format(Locale.ENGLISH, "Temp: %f\tHum: %f\tTime: %d\nDate: %s",
                temperature, humidity, timestamp, date.toString());
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
