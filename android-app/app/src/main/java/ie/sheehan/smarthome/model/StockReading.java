package ie.sheehan.smarthome.model;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class StockReading implements Serializable {

    public String product;

    public int weight;

    public int capacity;

    public long timestamp;


    public StockReading(String product, int weight, int capacity) {
        this.product = product;
        this.weight = weight;
        this.capacity = capacity;
    }

    public Date getDate() { return new Date(timestamp * 1000L); }

    public static double getLargestStockReadingInRange(List<StockReading> readings) {
        double largest = Integer.MIN_VALUE;

        for (StockReading reading : readings) {
            if (reading.weight > largest) {
                largest = reading.weight;
            }
        }

        return largest;
    }

}
