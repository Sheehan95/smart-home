package ie.sheehan.smarthome.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StockReading implements Serializable {

    // ============================================================================================
    // DECLARING CLASS VARIABLES
    // ============================================================================================
    private String product;
    private int weight;
    private int capacity;
    private Date date;


    // ============================================================================================
    // DECLARING CONSTRUCTORS
    // ============================================================================================
    /**
     * Default constructor.
     */
    public StockReading() {}

    public StockReading(String product, int weight, int capacity) {
        this.product = product;
        this.weight = weight;
        this.capacity = capacity;
    }

    /**
     * Creates a new object by parsing the values of a {@link JSONObject}.
     *
     * @param json representing an {@link HeatingStatus}.
     */
    public StockReading(JSONObject json) throws JSONException {
        setProduct(json.getString("product"));
        setWeight(json.getInt("weight"));
        setCapacity(json.getInt("capacity"));
        setDate(new Date(json.getLong("timestamp") * 1000L));
    }


    // ============================================================================================
    // DECLARING METHODS
    // ============================================================================================
    public double getWeightInOunces() { return weight * 0.035274; }

    public double getCapacityInOunces() { return capacity * 0.035274; }

    public int getDifferenceInWeight(StockReading reading) {
        return this.getWeight() - reading.getWeight();
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Product: %s\tWeight: %d/%d\nDate: %s",
                getProduct(), getWeight(), getCapacity(), getDate().toString());
    }


    // ============================================================================================
    // STATIC METHOD DECLARATION
    // ============================================================================================
    public static int getLargestStockReadingInRange(List<StockReading> readings) {
        int largest = Integer.MIN_VALUE;

        for (StockReading reading : readings) {
            if (reading.getWeight() > largest) {
                largest = reading.getWeight();
            }
        }

        return largest;
    }

    public static int getMostConsumedInRange(List<StockReading> readings) {
        int most = Integer.MIN_VALUE;

        for (int i = 1 ; i < readings.size() ; i++) {
            StockReading reading = readings.get(i);
            StockReading previous = readings.get(i - 1);

            int difference = previous.getDifferenceInWeight(reading);

            if (difference > most) { most = difference; }
        }

        return most;
    }

    public static int getLeastConsumedInRange(List<StockReading> readings) {
        int least = Integer.MAX_VALUE;

        for (int i = 1 ; i < readings.size() ; i++) {
            StockReading reading = readings.get(i);
            StockReading previous = readings.get(i - 1);

            int difference = previous.getDifferenceInWeight(reading);

            if (difference < least) { least = difference; }
        }

        return least;
    }

    public static int getAverageConsumedInRange(List<StockReading> readings) {
        int average = 0;

        for (int i = 1 ; i < readings.size() ; i++) {
            StockReading reading = readings.get(i);
            StockReading previous = readings.get(i - 1);

            average += previous.getDifferenceInWeight(reading);
        }

        return average / readings.size();
    }


    // ============================================================================================
    // DECLARING GETTERS AND SETTERS
    // ============================================================================================
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
