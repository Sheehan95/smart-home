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
        setDate(new Date(json.getLong("timestamp")));
    }


    // ============================================================================================
    // DECLARING METHODS
    // ============================================================================================
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Product: %s\tWeight: %d/%d\nDate: %s",
                getProduct(), getWeight(), getCapacity(), getDate().toString());
    }


    // ============================================================================================
    // STATIC METHOD DECLARATION
    // ============================================================================================
    public static double getLargestStockReadingInRange(List<StockReading> readings) {
        double largest = Integer.MIN_VALUE;

        for (StockReading reading : readings) {
            if (reading.weight > largest) {
                largest = reading.weight;
            }
        }

        return largest;
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
