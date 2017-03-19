package ie.sheehan.smarthome;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

import ie.sheehan.smarthome.model.EnvironmentReading;

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

        BarChart barChart = (BarChart) findViewById(R.id.chart);

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0 ; i < environmentReadings.size() ; i++){
            float temperature = (float) environmentReadings.get(i).getTemperature();
            entries.add(new BarEntry(temperature, i));
        }

        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0 ; i < environmentReadings.size() ; i++){
            labels.add(Integer.toString(i));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Average Temperature");

        BarData data = new BarData(labels, dataSet);
        barChart.setData(data);
        barChart.setDescription("");
        barChart.animateY(1000);
    }

}
