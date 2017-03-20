package ie.sheehan.smarthome.fragment;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ie.sheehan.smarthome.ChartActivity;
import ie.sheehan.smarthome.R;
import ie.sheehan.smarthome.dialog.DatePickerFragment;
import ie.sheehan.smarthome.model.EnvironmentReading;
import ie.sheehan.smarthome.utility.HttpRequestHandler;

import static ie.sheehan.smarthome.utility.DateUtility.getDateFormat;

public class TemperatureFragment extends Fragment {

    static final int PERIOD = 2;
    static final int INITIAL_DELAY = 0;

    // ============================================================================================
    // DECLARING CLASS VARIABLES
    // ============================================================================================
    ScheduledExecutorService executorService;

    Date fromDate;
    Date toDate;

    TextView temperatureView;
    TextView humidityView;

    TextView fromDateView;
    TextView toDateView;


    /**
     * Default constructor.
     */
    public TemperatureFragment() {}


    // ============================================================================================
    // FRAGMENT LIFECYCLE METHODS
    // ============================================================================================
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

        fromDateView = (TextView) getActivity().findViewById(R.id.text_from_date);
        toDateView = (TextView) getActivity().findViewById(R.id.text_to_date);


        executorService = Executors.newScheduledThreadPool(10);

        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                new GetTemperature().execute();
            }
        }, INITIAL_DELAY, PERIOD, TimeUnit.SECONDS);
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
            }, INITIAL_DELAY, PERIOD, TimeUnit.SECONDS);
        }
    }


    // ============================================================================================
    // BUTTON LISTENER METHODS
    // ============================================================================================
    /**
     * Launches a {@link ChartActivity} to display a bar chart of {@link EnvironmentReading} values
     * between two specified dates.
     */
    public void openChart(){
        if (fromDate == null || toDate == null){
            Toast.makeText(getActivity(), "You must set a date range.", Toast.LENGTH_SHORT).show();
            return;
        }

        new GetTemperatureInRange().execute(fromDate, toDate);
    }

    /**
     * Opens a new {@link DatePickerFragment} dialog to select a date and set the value of
     * {@link TemperatureFragment#fromDate} to the result.
     */
    public void openSetFromDateDialog(){
        DatePickerFragment fragment = new DatePickerFragment();

        fragment.addOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth, 0, 0, 0);
                fromDate = calendar.getTime();

                fromDateView.setText(getDateFormat().format(fromDate));
            }
        });

        fragment.show(getActivity().getSupportFragmentManager(), "fromDatePicker");
    }

    /**
     * Opens a new {@link DatePickerFragment} dialog to select a date and set the value of
     * {@link TemperatureFragment#toDate} to the result.
     */
    public void openSetToDateDialog(){
        DatePickerFragment fragment = new DatePickerFragment();

        fragment.addOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth, 23, 59, 59);
                toDate = calendar.getTime();

                toDateView.setText(getDateFormat().format(toDate));
            }
        });

        fragment.show(getActivity().getSupportFragmentManager(), "toDatePicker");
    }


    // ============================================================================================
    // INNER CLASS DECLARATION
    // ============================================================================================
    /**
     * An {@link AsyncTask} that executes a HTTP request for the latest {@link EnvironmentReading}.
     */
    private class GetTemperature extends AsyncTask<Void, Void, EnvironmentReading> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected EnvironmentReading doInBackground(Void... params) {
            return HttpRequestHandler.getInstance().getEnvironmentReading();
        }

        @Override
        protected void onPostExecute(EnvironmentReading envReading) {
            NumberFormat formatter = new DecimalFormat("#0.00");

            temperatureView.setText(formatter.format(envReading.getTemperature()) + "°C");
            humidityView.setText(formatter.format(envReading.getHumidity()));
        }

    }

    /**
     * An {@link AsyncTask} that executes a HTTP request for a list of {@link EnvironmentReading}
     * values that were logged within a range of dates.
     */
    private class GetTemperatureInRange extends AsyncTask<Date, Void, List<EnvironmentReading>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<EnvironmentReading> doInBackground(Date... range) {
            Date from = range[0];
            Date to = range[1];

            return HttpRequestHandler.getInstance().getEnvironmentReadingsInRange(from, to);
        }

        @Override
        protected void onPostExecute(List<EnvironmentReading> environmentReadings) {
            super.onPostExecute(environmentReadings);

            Bundle arguments = new Bundle();
            arguments.putSerializable("envReadings", (ArrayList) environmentReadings);
            Intent intent = new Intent(getActivity(), ChartActivity.class);
            intent.putExtras(arguments);
            getActivity().startActivity(intent);
        }

    }

}
