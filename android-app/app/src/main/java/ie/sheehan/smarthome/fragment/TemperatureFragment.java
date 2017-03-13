package ie.sheehan.smarthome.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ie.sheehan.smarthome.R;
import ie.sheehan.smarthome.model.Temperature;
import ie.sheehan.smarthome.utility.HttpRequestHandler;

/**
 * A simple {@link Fragment} subclass.
 */
public class TemperatureFragment extends Fragment {

    ScheduledExecutorService executorService;

    TextView temperatureView;
    TextView humidityView;


    public TemperatureFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_temperature, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        temperatureView = (TextView) getActivity().findViewById(R.id.text_temperature);
        humidityView = (TextView) getActivity().findViewById(R.id.text_humidity);

        executorService = Executors.newScheduledThreadPool(10);

        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                new GetTemperature().execute();
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    @Override
    public void onPause() {
        super.onPause();
        executorService.shutdown();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (executorService.isShutdown()) {
            executorService = Executors.newScheduledThreadPool(10);

            executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    new GetTemperature().execute();
                }
            }, 0, 2, TimeUnit.SECONDS);
        }
    }

    private class GetTemperature extends AsyncTask<Void, Void, Temperature> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Temperature doInBackground(Void... params) {
            return HttpRequestHandler.getInstance().getTemperature();
        }

        @Override
        protected void onPostExecute(Temperature temperature) {
            NumberFormat formatter = new DecimalFormat("#0.00");

            temperatureView.setText(formatter.format(temperature.getTemperature()) + "°C");
            humidityView.setText(formatter.format(temperature.getHumidity()));
        }

    }

}
