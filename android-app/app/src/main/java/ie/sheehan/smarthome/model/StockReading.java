package ie.sheehan.smarthome.model;


public class StockReading {

    public String product;

    public int weight;

    public int capacity;


    public StockReading(String product, int weight, int capacity) {
        this.product = product;
        this.weight = weight;
        this.capacity = capacity;
    }

}
