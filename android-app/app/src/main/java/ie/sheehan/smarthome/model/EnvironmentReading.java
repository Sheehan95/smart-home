package ie.sheehan.smarthome.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A POJO representing an environment reading of temperature and humidity.
 *
 * @author Alan Sheehan
 */
public class EnvironmentReading implements Serializable {

    // ============================================================================================
    // DECLARING CLASS VARIABLES
    // ============================================================================================
    private String _id;
    private double temperature;
    private double humidity;
    private long timestamp;



    // ============================================================================================
    // DECLARING CONSTRUCTORS
    // ============================================================================================
    /**
     * Default constructor.
     */
    public EnvironmentReading(){}

    /**
     * Creates a new object by parsing the values of a {@link JSONObject}.
     *
     * @param jsonObject representing an {@link EnvironmentReading}.
     */
    public EnvironmentReading(JSONObject jsonObject) {
        try {
            setTemperature(jsonObject.getDouble("temperature"));
            setHumidity(jsonObject.getDouble("humidity"));
            setTimestamp(jsonObject.getLong("timestamp"));
        } catch (JSONException e) {
            Log.d("ERROR", "Unable to construct from JSON object.");
        }
    }



    // ============================================================================================
    // DECLARING METHODS
    // ============================================================================================
    /**
     * Gets a {@link Date} object representing the time the {@link EnvironmentReading} was read.
     *
     * @return the date of the reading
     */
    public Date getDate() {
        return new Date(timestamp * 1000L);
    }

    public double getTemperatureInFahrenheit() {
        return 32 + (getTemperature() * 9 / 5);
    }

    @Override
    public String toString() {
        Date date = new Date(timestamp * 1000L);
        return String.format(Locale.ENGLISH, "Temp: %f\tHum: %f\tTime: %d\nDate: %s",
                temperature, humidity, timestamp, date.toString());
    }



    // ============================================================================================
    // STATIC METHOD DECLARATION
    // ============================================================================================
    public static double getAverageTemperatureInRange(List<EnvironmentReading> readings) {
        double average = 0;

        for (EnvironmentReading reading : readings) {
            average += reading.getTemperature();
        }

        average /= readings.size();

        return average;
    }

    public static double getLargestTemperatureValueInRange(List<EnvironmentReading> readings) {
        double largest = 0;

        for (EnvironmentReading reading : readings) {
            if (reading.getTemperature() > largest) {
                largest = reading.getTemperature();
            }
        }

        return largest;
    }



    // ============================================================================================
    // DECLARING GETTERS AND SETTERS
    // ============================================================================================
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
