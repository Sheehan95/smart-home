package ie.sheehan.smarthome.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import ie.sheehan.smarthome.R;

public class TemperatureActivity extends AppCompatActivity {

    private Timer timer;

    private TextView temperatureView;
    private TextView humidityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        temperatureView = (TextView) findViewById(R.id.text_current_temperature);
        humidityView = (TextView) findViewById(R.id.text_current_humidity);
    }

    @Override
    protected void onStart() {
        super.onStart();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("NOTE", "Getting temperature and humidity...");
                        new GetTemperature().execute();
                    }
                });
            }
        }, 0, 5000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
        timer.purge();
    }

    private class GetTemperature extends AsyncTask<Void, Void, JSONObject> {

        private static final String ENDPOINT = "http://192.167.1.31:8080/temperature/get/latest";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject json = null;
            HttpURLConnection connection;
            StringBuffer response = new StringBuffer();

            try {
                connection = (HttpURLConnection) new URL(ENDPOINT).openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String nextLine;

                while ((nextLine = reader.readLine()) != null){
                    response.append(nextLine);
                }

                json = new JSONObject(response.toString());
            } catch (Exception e){
                Log.e("ERROR", e.getStackTrace().toString());
                Log.e("ERROR", "THIS ISN'T EVEN AN ERROR IS IT?");
            }

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {

                double temperature = jsonObject.getDouble("temperature");
                double humidity = jsonObject.getDouble("humidity");

                temperatureView.setText(Double.toString(temperature) + " °C");
                humidityView.setText(Double.toString(humidity));

            } catch (Exception e){
                Log.e("ERROR", e.toString());
            }
        }


    }

}
