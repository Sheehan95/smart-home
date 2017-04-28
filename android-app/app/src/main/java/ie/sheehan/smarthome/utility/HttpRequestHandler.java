package ie.sheehan.smarthome.utility;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
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
    private static final String TAG = "HTTP_REQ";

    private static final String DOMAIN = "192.168.0.30";

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
    // ENVIRONMENT-RELATED HTTP REQUESTS
    // ============================================================================================
    /**
     * Makes a HTTP request to /environment/get to retrieve the latest {@link EnvironmentReading}.
     *
     * @return the latest {@link EnvironmentReading}
     */
    public EnvironmentReading getEnvironmentReading() {
        EnvironmentReading environmentReading = null;

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_ENVIRONMENT, "/get");

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            environmentReading = new EnvironmentReading(new JSONObject(response.toString()));

        } catch (IOException | JSONException e) {
            Log.e(TAG, "error in getEnvironmentReading");
            Log.e(e.getClass().getName(), e.getMessage());
        }

        return environmentReading;
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
        List<EnvironmentReading> environmentReadings = new ArrayList<>();

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_ENVIRONMENT, "/get/range");
        target += String.format(Locale.getDefault(), "?from=%d&to=%d", (from.getTime() / 1000L), (to.getTime() / 1000L));

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null){
                response.append(nextLine);
            }

            JSONArray array = new JSONArray(response.toString());

            for (int i = 0 ; i < array.length() ; i++){
                environmentReadings.add(new EnvironmentReading(array.getJSONObject(i)));
            }

        } catch (IOException | JSONException e) {
            Log.e(TAG, "error in getEnvironmentReadingsInRange");
            Log.e(e.getClass().getName(), e.getMessage());
        }

        return environmentReadings;
    }


    public HeatingStatus getHeatingStatus() {
        HeatingStatus heatingStatus = null;

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_ENVIRONMENT, "/heating/status");

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            heatingStatus = new HeatingStatus(new JSONObject((response.toString())));

        } catch (IOException | JSONException e) {
            Log.e(TAG, "error in getHeatingStatus");
            Log.e(e.getClass().getName(), e.getMessage());
        }

        return heatingStatus;
    }

    public boolean toggleHeating(boolean on) {
        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_ENVIRONMENT, "/heating/activate");

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            OutputStream outputStream = connection.getOutputStream();
            JSONObject output = new JSONObject();
            output.put("on", on);

            outputStream.write(output.toString().getBytes("UTF-8"));
            outputStream.close();

            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            return Boolean.parseBoolean(response.toString());

        } catch (IOException | JSONException e) {
            Log.e(TAG, "error in toggleHeating");
            Log.e(e.getClass().getName(), e.getMessage());

            return false;
        }
    }


    // ============================================================================================
    // SECURITY-RELATED HTTP REQUESTS
    // ============================================================================================
    /**
     * Makes a HTTP request to /security/camera/feed to open or close the camera feed on port 8081.
     *
     * @param stream true to turn on the stream, false to turn it off
     * @return true if successful, false otherwise
     */
    public boolean toggleCameraFeed(boolean stream) {
        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/camera/feed");

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            OutputStream outputStream = connection.getOutputStream();
            JSONObject output = new JSONObject();
            output.put("stream", stream);

            outputStream.write(output.toString().getBytes("UTF-8"));
            outputStream.close();

            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null){
                response.append(nextLine);
            }

            return Boolean.parseBoolean(response.toString());

        } catch (IOException | JSONException e) {
            Log.e(TAG, "error in toggleCameraFeed");
            Log.e(e.getClass().getName(), e.getMessage());

            return false;
        }
    }

    /**
     * Makes a HTTP POST request to /security/alarm/arm to to arm or disarm the alarm. A boolean
     * value is returned indicating whether the command was successful or not.
     *
     * @param arm true to arm the alarm, false to disarm it
     * @return true if successful, false otherwise
     */
    public boolean armAlarm(boolean arm) {
        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/alarm/arm");

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            JSONObject output = new JSONObject();
            output.put("arm", arm);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(output.toString().getBytes("UTF-8"));
            outputStream.close();

            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null){
                response.append(nextLine);
            }

            return Boolean.parseBoolean(response.toString());

        } catch (IOException | JSONException e) {
            Log.e(TAG, "error in armAlarm");
            Log.e(e.getClass().getName(), e.getMessage());

            return false;
        }
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

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/alarm/status");

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null){
                response.append(nextLine);
            }

            alarm = new AlarmStatus(new JSONObject(response.toString()));

        } catch (IOException | JSONException e) {
            Log.e(TAG, "error in getAlarmStatus");
            Log.e(e.getClass().getName(), e.getMessage());
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

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/intrusion/get/all");

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            JSONArray array = new JSONArray(response.toString());

            for (int i = 0 ; i < array.length() ; i++) {
                intrusionReadings.add(new IntrusionReading(array.getJSONObject(i)));
            }

        } catch (Exception e) {
            Log.e(TAG, "error in getAllIntrusions");
            Log.e(e.getClass().getName(), e.getMessage());
        }

        return intrusionReadings;
    }

    public IntrusionReading getLatestIntrusionReading() {
        IntrusionReading intrusionReading = null;

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/intrusion/get/latest");

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            intrusionReading = new IntrusionReading(new JSONObject(response.toString()));

        } catch (Exception e) {
            Log.e(TAG, "error in getLatestIntrusionReading");
            Log.e(e.getClass().getName(), e.getMessage());
        }

        return intrusionReading;
    }

    public boolean markIntrusionAsViewed(IntrusionReading intrusionReading) {
        String id = intrusionReading.getId();

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/intrusion/view/" + id);

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            Log.e(TAG, "error in markIntrusionAsViewed");
            Log.e(e.getClass().getName(), e.getMessage());

            return false;
        }
    }

    public boolean markAllIntrusionsAsViewed() {
        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/intrusion/view/all");

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            Log.e(TAG, "error in markAllIntrusionsAsViewed");
            Log.e(e.getClass().getName(), e.getMessage());

            return false;
        }
    }

    public boolean removeIntrusion(IntrusionReading intrusionReading) {
        String id = intrusionReading.getId();

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/intrusion/remove/" + id);

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;

        } catch (IOException e) {
            Log.e(TAG, "error in removeIntrusion");
            Log.e(e.getClass().getName(), e.getMessage());

            return false;
        }
    }

    public boolean removeAllIntrusions() {
        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_SECURITY, "/intrusion/remove/all");

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;

        } catch (IOException e) {
            Log.e(TAG, "error in removeAllIntrusions");
            Log.e(e.getClass().getName(), e.getMessage());

            return false;
        }
    }


    // ============================================================================================
    // STOCK-RELATED HTTP REQUESTS
    // ============================================================================================
    public StockReading getStockReading() {
        StockReading stockReading = null;

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_STOCK, "/get");

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            stockReading = new StockReading(new JSONObject(response.toString()));

        } catch (IOException | JSONException e) {
            Log.e(TAG, "error in getStockReading");
            Log.e(e.getClass().getName(), e.getMessage());
        }

        return stockReading;
    }

    public List<StockReading> getStockReadingsByProduct(String product) {
        List<StockReading> stockReadings = new ArrayList<>();

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_STOCK, "/get/");
        target += product;

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            JSONArray array = new JSONArray(response.toString());

            for (int i = 0 ; i < array.length() ; i++) {
                stockReadings.add(new StockReading(array.getJSONObject(i)));
            }

        } catch (IOException | JSONException e) {
            Log.e(TAG, "error in getStockReadingsByProduct");
            Log.e(e.getClass().getName(), e.getMessage());
        }

        return stockReadings;
    }

    public boolean calibrateScale(String product) {
        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_STOCK, "/scale/calibrate");

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            JSONObject output = new JSONObject();
            output.put("product", product);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(output.toString().getBytes("UTF-8"));
            outputStream.close();

            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            return Boolean.parseBoolean(response.toString());

        } catch (Exception e) {
            Log.e(TAG, "error in calibrateScale");
            Log.e(e.getClass().getName(), e.getMessage());

            return false;
        }
    }

    public List<String> getAllProducts() {
        List<String> productList = new ArrayList<>();

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENDPOINT_STOCK, "/get/products");

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            JSONArray json = new JSONArray(response.toString());

            for (int i = 0 ; i < json.length() ; i++) {
                productList.add(json.getString(i));
            }

        } catch (IOException | JSONException e) {
            Log.e(TAG, "error in getAllProducts");
            Log.e(e.getClass().getName(), e.getMessage());
        }

        return productList;
    }

}
