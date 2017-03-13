package ie.sheehan.smarthome.model;


public class Temperature {

    private String _id;
    private double temperature;
    private double humidity;
    private long timestamp;


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
