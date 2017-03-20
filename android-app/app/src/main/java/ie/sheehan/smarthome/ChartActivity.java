package ie.sheehan.smarthome;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ie.sheehan.smarthome.model.EnvironmentReading;

import static ie.sheehan.smarthome.utility.DateUtility.compareDateIgnoreTime;
import static ie.sheehan.smarthome.utility.DateUtility.getShortDateFormat;
import static ie.sheehan.smarthome.utility.DateUtility.getUniqueDateRange;

public class ChartActivity extends AppCompatActivity {

    List<EnvironmentReading> environmentReadings;

    @Override
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

        Date startDate = environmentReadings.get(0).getDate();
        Date endDate = environmentReadings.get(environmentReadings.size() - 1).getDate();
        List<Date> uniqueDates = getUniqueDateRange(startDate, endDate);

        List<EnvironmentReading> graphData = new ArrayList<>();

        for (Date date : uniqueDates) {
            List<EnvironmentReading> readingsForDay = new ArrayList<>();

            for (EnvironmentReading reading : environmentReadings) {
                if (compareDateIgnoreTime(date, reading.getDate()) == 0){
                    readingsForDay.add(reading);
                }
            }

            EnvironmentReading averageReading = new EnvironmentReading();
            averageReading.setTemperature(getAverageTemperatureInRange(readingsForDay));
            averageReading.setTimestamp(date.getTime());
            graphData.add(averageReading);
        }


        BarChart barChart = (BarChart) findViewById(R.id.chart);

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0 ; i < graphData.size() ; i++){
            float temperature = (float) graphData.get(i).getTemperature();
            entries.add(new BarEntry(temperature, i));
        }

        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0 ; i < graphData.size() ; i++){
            labels.add(getShortDateFormat().format(graphData.get(i).getDate()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Average Temperature");

        BarData data = new BarData(labels, dataSet);
        barChart.setData(data);
        barChart.setDescription("");
        barChart.animateY(1000);
    }


    public double getAverageTemperatureInRange(List<EnvironmentReading> readings) {
        double average = 0;

        for (EnvironmentReading reading : readings) {
            average += reading.getTemperature();
        }

        average /= readings.size();

        return average;
    }


}
