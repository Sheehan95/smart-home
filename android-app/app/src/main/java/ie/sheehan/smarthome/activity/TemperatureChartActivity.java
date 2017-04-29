package ie.sheehan.smarthome.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ie.sheehan.smarthome.R;
import ie.sheehan.smarthome.model.EnvironmentReading;

import static ie.sheehan.smarthome.R.string.text_temperature_display_celsius;
import static ie.sheehan.smarthome.model.EnvironmentReading.getAverageTemperatureInRange;
import static ie.sheehan.smarthome.model.EnvironmentReading.getLargestTemperatureInRange;
import static ie.sheehan.smarthome.model.EnvironmentReading.getSmallestTemperatureInRange;
import static ie.sheehan.smarthome.utility.DateUtility.compareDateIgnoreTime;
import static ie.sheehan.smarthome.utility.DateUtility.getShortDateFormat;
import static ie.sheehan.smarthome.utility.DateUtility.getUniqueDateRange;

public class TemperatureChartActivity extends AppCompatActivity {

    Resources resources;

    LineChart lineChart;

    List<EnvironmentReading> environmentReadings;


    // ============================================================================================
    // ACTIVITY LIFECYCLE METHODS
    // ============================================================================================
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_temperature_chart);

        resources = getResources();
        lineChart = (LineChart) findViewById(R.id.chart);

        Bundle arguments = getIntent().getExtras();
        environmentReadings = (List<EnvironmentReading>) arguments.getSerializable("envReadings");
    }

    @Override
    protected void onStart() {
        super.onStart();

        List<EnvironmentReading> chartData = generateChartData();

        populateChart(chartData);
        populateStatistics(chartData);
    }


    // ============================================================================================
    // DECLARING METHODS
    // ============================================================================================
    /**
     * Gets a list of {@link EnvironmentReading} values to be used as data for the {@link BarChart}.
     *
     * @return a list of {@link EnvironmentReading} for chart data
     */
    private List<EnvironmentReading> generateChartData() {
        List<EnvironmentReading> graphData = new ArrayList<>();

        Date startDate = environmentReadings.get(0).getDate();
        Date endDate = environmentReadings.get(environmentReadings.size() - 1).getDate();
        List<Date> uniqueDates = getUniqueDateRange(startDate, endDate);

        for (Date date : uniqueDates) {
            List<EnvironmentReading> readingsForDay = new ArrayList<>();

            for (EnvironmentReading reading : environmentReadings) {
                if (compareDateIgnoreTime(date, reading.getDate()) == 0){
                    readingsForDay.add(reading);
                }
            }

            EnvironmentReading averageReading = new EnvironmentReading();
            averageReading.setTemperature(getAverageTemperatureInRange(readingsForDay));
            averageReading.setDate(date);
            graphData.add(averageReading);
        }

        return graphData;
    }

    /**
     * Takes a list of {@link EnvironmentReading} values and converts them into a list of
     * {@link BarEntry} values.
     *
     * @param chartData the list of {@link EnvironmentReading} values used to generate the entries
     * @return a list of {@link BarEntry} values that can be used in a {@link BarChart}
     */
    private List<Entry> getChartEntries(List<EnvironmentReading> chartData) {
        List<Entry> entries = new ArrayList<>();

        for (int i = 0 ; i < chartData.size() ; i++){
            float temperature = (float) chartData.get(i).getTemperature();
            entries.add(new Entry(temperature, i));
        }

        return entries;
    }

    /**
     * Takes a list of {@link EnvironmentReading} values and converts them into a list of
     * String values.
     *
     * @param chartData the list of {@link EnvironmentReading} values used to generate the labels
     * @return a list of String values that can be used as labels for a {@link BarChart}
     */
    private List<String> getChartLabels(List<EnvironmentReading> chartData) {
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0 ; i < chartData.size() ; i++){
            labels.add(getShortDateFormat().format(chartData.get(i).getDate()));
        }

        return labels;
    }

    /**
     * Populates the chart with data from a list of {@link EnvironmentReading} objects.
     *
     * @param chartData to populate the chart with
     */
    private void populateChart(List<EnvironmentReading> chartData) {
        List<Entry> entries = getChartEntries(chartData);
        List<String> labels = getChartLabels(chartData);

        LineDataSet dataSet = new LineDataSet(entries, "Average Temperature");

        LineData data = new LineData(labels, dataSet);
        lineChart.setData(data);
        lineChart.setDescription("");
        lineChart.animateY(1000);
        lineChart.getXAxis().setLabelRotationAngle(280);
        YAxis yAxis = lineChart.getAxisLeft();
        YAxis axis = lineChart.getAxisRight();
        axis.setEnabled(false);
        yAxis.setAxisMinValue((long) (getSmallestTemperatureInRange(chartData).getTemperature() - 1));
        yAxis.setAxisMaxValue((long) (getLargestTemperatureInRange(chartData).getTemperature() + 4));

        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int dpHeight = (int) (displayMetrics.heightPixels / displayMetrics.density);

        lineChart.setMinimumHeight(dpHeight);
        lineChart.invalidate();
    }

    /**
     * Uses existing chart data to populate the statistics.
     *
     * @param chartData used to populate the chart
     */
    private void populateStatistics(List<EnvironmentReading> chartData) {
        TextView warmestValue = (TextView) findViewById(R.id.stat_warmest);
        TextView warmestDate = (TextView) findViewById(R.id.stat_warmest_date);
        TextView coldestValue = (TextView) findViewById(R.id.stat_coldest);
        TextView coldestDate = (TextView) findViewById(R.id.stat_coldest_date);

        EnvironmentReading warmest = getLargestTemperatureInRange(chartData);
        EnvironmentReading coldest = getSmallestTemperatureInRange(chartData);

        warmestValue.setText(resources.getString(text_temperature_display_celsius, warmest.getTemperature()));
        warmestDate.setText(getShortDateFormat().format(warmest.getDate()));

        coldestValue.setText(resources.getString(text_temperature_display_celsius, coldest.getTemperature()));
        coldestDate.setText(getShortDateFormat().format(coldest.getDate()));
    }

}
