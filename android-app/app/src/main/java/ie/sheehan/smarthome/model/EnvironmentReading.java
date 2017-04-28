package ie.sheehan.smarthome.model;

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
    private String id;
    private double temperature;
    private double humidity;
    private Date date;


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
     * @param json representing an {@link EnvironmentReading}.
     */
    public EnvironmentReading(JSONObject json) throws JSONException {
        setTemperature(json.getDouble("temperature"));
        setHumidity(json.getDouble("humidity"));
        setDate(new Date(json.getLong("timestamp") * 1000L));
    }


    // ============================================================================================
    // DECLARING METHODS
    // ============================================================================================
    /**
     * Converts the temperature read from celsius to fahrenheit.
     *
     * @return the temperature value in fahrenheit
     */
    public double getTemperatureInFahrenheit() {
        return 32 + (getTemperature() * 9 / 5);
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Temperature: %f\tHumidity: %f\nDate: %s",
                getTemperature(), getHumidity(), getDate().toString());
    }


    // ============================================================================================
    // STATIC METHOD DECLARATION
    // ============================================================================================
    /**
     * Gets the average temperature value from a list of {@link EnvironmentReading} objects.
     *
     * @param readings a list of {@link EnvironmentReading} objects
     * @return the average temperature from the list
     */
    public static double getAverageTemperatureInRange(List<EnvironmentReading> readings) {
        double average = 0;

        for (EnvironmentReading reading : readings) {
            average += reading.getTemperature();
        }

        average /= readings.size();

        return average;
    }

    /**
     * Gets the largest temperature value from a list of {@link EnvironmentReading} objects.
     *
     * @param readings a list of {@link EnvironmentReading} objects
     * @return the largest temperature from the list
     */
    public static double getLargestTemperatureInRange(List<EnvironmentReading> readings) {
        double largest = Integer.MIN_VALUE;

        for (EnvironmentReading reading : readings) {
            if (reading.getTemperature() > largest) {
                largest = reading.getTemperature();
            }
        }

        return largest;
    }

    /**
     * Gets the largest temperature value from a list of {@link EnvironmentReading} objects.
     *
     * @param readings a list of {@link EnvironmentReading} objects
     * @return the largest temperature from the list
     */
    public static double getLowestTemperatureInRange(List<EnvironmentReading> readings) {
        double lowest = Integer.MAX_VALUE;

        for (EnvironmentReading reading : readings) {
            if (reading.getTemperature() < lowest) {
                lowest = reading.getTemperature();
            }
        }

        return lowest;
    }

    /**
     * Gets the average temperature value from a list of {@link EnvironmentReading} objects.
     *
     * @param readings a list of {@link EnvironmentReading} objects
     * @return the average humidity from the list
     */
    public static double getAverageHumidityInRange(List<EnvironmentReading> readings) {
        double average = 0;

        for (EnvironmentReading reading : readings) {
            average += reading.getHumidity();
        }

        average /= readings.size();

        return average;
    }

    /**
     * Gets the largest humidity value from a list of {@link EnvironmentReading} objects.
     *
     * @param readings a list of {@link EnvironmentReading} objects
     * @return the largest humidity from the list
     */
    public static double getLargestHumidityInRange(List<EnvironmentReading> readings) {
        double largest = Integer.MIN_VALUE;

        for (EnvironmentReading reading : readings) {
            if (reading.getTemperature() > largest) {
                largest = reading.getHumidity();
            }
        }

        return largest;
    }

    /**
     * Gets the largest temperature value from a list of {@link EnvironmentReading} objects.
     *
     * @param readings a list of {@link EnvironmentReading} objects
     * @return the largest temperature from the list
     */
    public static double getLowestHumidityInRange(List<EnvironmentReading> readings) {
        double lowest = Integer.MAX_VALUE;

        for (EnvironmentReading reading : readings) {
            if (reading.getHumidity() < lowest) {
                lowest = reading.getHumidity();
            }
        }

        return lowest;
    }

    // ============================================================================================
    // DECLARING GETTERS AND SETTERS
    // ============================================================================================
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getTemperature() { return temperature; }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
