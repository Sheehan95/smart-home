package ie.sheehan.smarthome.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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

import static ie.sheehan.smarthome.model.EnvironmentReading.getAverageTemperatureInRange;
import static ie.sheehan.smarthome.model.EnvironmentReading.getLargestTemperatureValueInRange;
import static ie.sheehan.smarthome.utility.DateUtility.compareDateIgnoreTime;
import static ie.sheehan.smarthome.utility.DateUtility.getShortDateFormat;
import static ie.sheehan.smarthome.utility.DateUtility.getUniqueDateRange;

public class TemperatureChartActivity extends AppCompatActivity {

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

        Bundle arguments = getIntent().getExtras();
        environmentReadings = (List<EnvironmentReading>) arguments.getSerializable("envReadings");
    }

    @Override
    protected void onStart() {
        super.onStart();

        LineChart lineChart = (LineChart) findViewById(R.id.chart);

        List<EnvironmentReading> chartData = generateChartData();

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
        axis.setLabelCount(0, true);
        yAxis.setAxisMinValue(0);
        yAxis.setAxisMaxValue((long) (getLargestTemperatureValueInRange(chartData) + 4));
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
            averageReading.setTimestamp(date.getTime() / 1000L);
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

}
