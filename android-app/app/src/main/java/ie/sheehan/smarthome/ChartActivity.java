package ie.sheehan.smarthome;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ie.sheehan.smarthome.model.EnvironmentReading;

import static ie.sheehan.smarthome.model.EnvironmentReading.getAverageTemperatureInRange;
import static ie.sheehan.smarthome.model.EnvironmentReading.getLargestTemperatureValueInRange;
import static ie.sheehan.smarthome.utility.DateUtility.compareDateIgnoreTime;
import static ie.sheehan.smarthome.utility.DateUtility.getShortDateFormat;
import static ie.sheehan.smarthome.utility.DateUtility.getUniqueDateRange;

public class ChartActivity extends AppCompatActivity {

    List<EnvironmentReading> environmentReadings;



    // ============================================================================================
    // ACTIVITY LIFECYCLE METHODS
    // ============================================================================================
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_chart);

        Bundle arguments = getIntent().getExtras();
        environmentReadings = (List<EnvironmentReading>) arguments.getSerializable("envReadings");
    }

    @Override
    protected void onStart() {
        super.onStart();

        BarChart barChart = (BarChart) findViewById(R.id.chart);

        List<EnvironmentReading> chartData = generateChartData();

        List<BarEntry> entries = getChartEntries(chartData);
        List<String> labels = getChartLabels(chartData);

        BarDataSet dataSet = new BarDataSet(entries, "Average Temperature");

        BarData data = new BarData(labels, dataSet);
        barChart.setData(data);
        barChart.setDescription("");
        barChart.animateY(1000);
        barChart.getXAxis().setLabelRotationAngle(280);
        barChart.setDrawValueAboveBar(false);
        YAxis yAxis = barChart.getAxisLeft();
        YAxis axis = barChart.getAxisRight();
        axis.setLabelCount(0, true);
        yAxis.setAxisMinValue(0);
        yAxis.setAxisMaxValue((long) (getLargestTemperatureValueInRange(chartData) + 2));
    }



    // ============================================================================================
    // DECLARING METHODS
    // ============================================================================================
    /**
     * Gets a list of {@link EnvironmentReading} values to be used as data for the {@link BarChart}.
     *
     * @return a list of {@link EnvironmentReading} for chart data
     */
    public List<EnvironmentReading> generateChartData() {
        List<EnvironmentReading> graphData = new ArrayList<>();

        Date startDate = environmentReadings.get(0).getDate();
        Date endDate = environmentReadings.get(environmentReadings.size() - 1).getDate();
        List<Date> uniqueDates = getUniqueDateRange(startDate, endDate);

        for (Date date : uniqueDates) {
            Log.e("UNIQUE", date.toString());
        }

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
    public List<BarEntry> getChartEntries(List<EnvironmentReading> chartData) {
        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0 ; i < chartData.size() ; i++){
            float temperature = (float) chartData.get(i).getTemperature();
            entries.add(new BarEntry(temperature, i));
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
    public List<String> getChartLabels(List<EnvironmentReading> chartData) {
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0 ; i < chartData.size() ; i++){
            labels.add(getShortDateFormat().format(chartData.get(i).getDate()));
        }

        return labels;
    }

}
