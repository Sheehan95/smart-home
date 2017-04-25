package ie.sheehan.smarthome.utility;

import android.util.Log;

import org.joda.time.Duration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ie.sheehan.smarthome.model.AlarmStatus;
import ie.sheehan.smarthome.model.EnvironmentReading;
import ie.sheehan.smarthome.model.HeatingStatus;
import ie.sheehan.smarthome.model.IntrusionReading;
import ie.sheehan.smarthome.model.StockReading;

/**
 * A Singleton class for managing connections and HTTP requests to the web services.
 */
public class HttpRequestHandler {

    // ============================================================================================
    // DECLARING STATIC VARIABLES
    // ============================================================================================
    private static final String DOMAIN = "192.167.1.31";

    private static final String ENDPOINT_ENVIRONMENT = "/environment";
    private static final String ENDPOINT_SECURITY = "/security";
    private static final String ENDPOINT_STOCK = "/stock";

    private static final HttpRequestHandler instance = new HttpRequestHandler();



    // ============================================================================================
    // DECLARING CONSTRUCTORS
    // ============================================================================================
    /**
     * Default constructor. Set to private to enforce the Singleton pattern.
     */
    private HttpRequestHandler() {}

    /**
     * Retrieves the single instance of {@link HttpRequestHandler}.
     *
     * @return the single instance of {@link HttpRequestHandler}
     */
    public static HttpRequestHandler getInstance() { return instance; }



    // ============================================================================================
    // DEFINING METHODS
    // ============================================================================================
    /**
     * Makes a HTTP request to /environment/get to retrieve the latest {@link EnvironmentReading}.
     *
     * @return the latest {@link EnvironmentReading}
     */
    public EnvironmentReading getEnvironmentReading() {
        JSONObject json;
        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();
        EnvironmentReading envReading = null;

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_ENVIRONMENT, "/get");

        try {
            connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            json = new JSONObject(response.toString());
            envReading = new EnvironmentReading(json);

        } catch (MalformedURLException e) {
            Log.e("ERROR", "MALFORMED URL ERROR");
        } catch (IOException e) {
            Log.e("ERROR", "IO ERROR");
        } catch (JSONException e) {
            Log.e("ERROR", "JSON ERROR");
        } catch (Exception e) {
            Log.e("ERROR", "UNKNOWN ERROR");
        }

        return envReading;
    }

    /**
     * Makes a HTTP request to /environment/get/range to retrieve a list of
     * {@link EnvironmentReading} values in a range between the two {@link Date} values.
     *
     * @param from the lower bound {@link Date} value for the range
     * @param to the upper bound {@link Date} value for the range
     * @return a list of {@link EnvironmentReading}s within the range of dates
     */
    public List<EnvironmentReading> getEnvironmentReadingsInRange(Date from, Date to) {
        JSONArray json;
        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();
        List<EnvironmentReading> envReadings = new ArrayList<>();

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_ENVIRONMENT, "/get/range");
        target += String.format(Locale.getDefault(), "?from=%d&to=%d", (from.getTime() / 1000L), (to.getTime() / 1000L));

        try {
            connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null){
                response.append(nextLine);
            }

            json = new JSONArray(response.toString());

            for (int i = 0 ; i < json.length() ; i++){
                envReadings.add(new EnvironmentReading(json.getJSONObject(i)));
            }

        } catch (Exception e){
            Log.e("HTTP REQUEST ERROR", e.toString());
        }

        return envReadings;
    }

    public HeatingStatus getHeatingStatus() {
        HeatingStatus heatingStatus = null;
        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_ENVIRONMENT, "/heating/status");

        try {
            connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            JSONObject json = new JSONObject(response.toString());

            boolean on = json.getBoolean("on");
            Date lastOn = new Date(json.getLong("timestamp") * 1000L);
            Duration duration = new Duration(json.getInt("duration") * 1000L);

            heatingStatus = new HeatingStatus(on, lastOn, duration);

        } catch (Exception e) {
            Log.e("PANIC", e.toString());
        }

        return heatingStatus;
    }

    public boolean toggleHeating(boolean on) {
        boolean confirmation;

        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_ENVIRONMENT, "/heating/activate");

        try {
            connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            OutputStream outputStream = connection.getOutputStream();
            JSONObject output = new JSONObject();
            output.put("on", on);

            outputStream.write(output.toString().getBytes("UTF-8"));
            outputStream.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            confirmation = Boolean.parseBoolean(response.toString());
        } catch (Exception e) {
            Log.e("HELP", e.toString());
            confirmation = false;
        }

        return confirmation;
    }

    /**
     * Makes a HTTP request to /security/camera/feed to open or close the camera feed on port 8081.
     *
     * @param stream true to turn on the stream, false to turn it off
     * @return true if successful, false otherwise
     */
    public boolean toggleCameraFeed(boolean stream) {
        boolean confirmation;

        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/camera/feed");

        try {
            connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            OutputStream outputStream = connection.getOutputStream();
            JSONObject output = new JSONObject();
            output.put("stream", stream);

            outputStream.write(output.toString().getBytes("UTF-8"));
            outputStream.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null){
                response.append(nextLine);
            }

            confirmation = Boolean.parseBoolean(response.toString());

        } catch (Exception e){
            Log.e("HTTP REQUEST ERROR", e.toString());
            return false;
        }

        return confirmation;
    }

    /**
     * Makes a HTTP POST request to /security/alarm/arm to to arm or disarm the alarm. A boolean
     * value is returned indicating whether the command was successful or not.
     *
     * @param arm true to arm the alarm, false to disarm it
     * @return true if successful, false otherwise
     */
    public boolean armAlarm(boolean arm) {
        boolean confirmation;

        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/alarm/arm");

        try {
            connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            JSONObject output = new JSONObject();
            output.put("arm", arm);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(output.toString().getBytes("UTF-8"));
            outputStream.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null){
                response.append(nextLine);
            }

            confirmation = Boolean.parseBoolean(response.toString());
        } catch (Exception e){
            Log.e("HTTP REQUEST ERROR", e.toString());
            confirmation = false;
        }

        return confirmation;
    }

    /**
     * Makes a HTTP request to /security/alarm/status to retrieve an object of type
     * {@link AlarmStatus} that indicates whether the alarm is armed or disarmed, as well as the
     * last time it was armed.
     *
     * @return an up to date {@link AlarmStatus} object
     */
    public AlarmStatus getAlarmStatus() {
        AlarmStatus alarm = null;

        JSONObject json;
        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/alarm/status");

        try {
            connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null){
                response.append(nextLine);
            }

            json = new JSONObject(response.toString());

            boolean armed = json.getBoolean("armed");
            Date lastArmed = new Date(json.getLong("timestamp") * 1000L);

            alarm = new AlarmStatus(armed, lastArmed);
        } catch (Exception e){
            Log.e("HTTP REQUEST ERROR", e.toString());
        }

        return alarm;
    }

    /**
     * Makes a HTTP request to /security/intrusion/get/all to retrieve a list of every
     * {@link IntrusionReading} object stored on the web server.
     *
     * @return a list of {@link IntrusionReading} objects
     */
    public List<IntrusionReading> getAllIntrusions() {
        List<IntrusionReading> intrusionReadings = new ArrayList<>();

        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/intrusion/get/all");

        try {
            connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            JSONArray jsonArray = new JSONArray(response.toString());

            for (int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject json = jsonArray.getJSONObject(i);

                Log.e("ID", json.getString("id"));

                intrusionReadings.add(new IntrusionReading(json));
            }

        } catch (Exception e) {
            Log.e("HTTP", e.toString());
        }

        return intrusionReadings;
    }

    public IntrusionReading getLatestIntrusionReading() {
        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();
        IntrusionReading intrusionReading = null;

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/intrusion/get/latest");

        try {
            connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            JSONObject json = new JSONObject(response.toString());

            intrusionReading = new IntrusionReading(json);
        } catch (Exception e) {
            Log.e("INTRUSION", e.toString());
        }

        return intrusionReading;
    }

    public boolean markIntrusionAsViewed(IntrusionReading intrusionReading) {
        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();

        String id = intrusionReading.id;
        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/intrusion/view/" + id);

        try {
            connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            return true;

        } catch (Exception e) {
            Log.e("VIEW", e.toString());
            return false;
        }
    }

    public boolean markAllIntrusionsAsViewed() {
        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/intrusion/view/all");

        try {
            connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            return true;

        } catch (Exception e) {
            Log.e("MARKALL", e.toString());
            return false;
        }
    }

    public boolean removeIntrusion(IntrusionReading intrusionReading) {
        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();

        String id = intrusionReading.id;
        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/intrusion/remove/" + id);

        try {
            connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                return true;
            }
            else {
                Log.e("CODE", Integer.toString(responseCode));
                return false;
            }

        } catch (Exception e) {
            Log.e("REMOVE", e.toString());
            return false;
        }
    }

    public boolean removeAllIntrusions() {
        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/intrusion/remove/all");

        try {
            connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("POST");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            return true;

        } catch (Exception e) {
            Log.e("REMOVEALL", e.toString());
            return false;
        }
    }

    public StockReading getStockReading() {
        HttpURLConnection connection;
        StockReading stockReading = null;

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_STOCK, "/get");

        try {
            connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            JSONObject json = new JSONObject(response.toString());
            String product = json.getString("product");
            int weight = (int) json.getDouble("weight");
            int capacity = (int) json.getDouble("capacity");

            stockReading = new StockReading(product, weight, capacity);
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }

        return stockReading;
    }


    public boolean calibrateScale(String product) {
        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_STOCK, "/scale/calibrate");

        try {
            connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            JSONObject output = new JSONObject();
            output.put("product", product);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(output.toString().getBytes("UTF-8"));
            outputStream.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            return Boolean.parseBoolean(response.toString());
        } catch (Exception e) {
            Log.e("CALIBRATE", e.toString());
            return false;
        }
    }

}
