package ie.sheehan.smarthome;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ie.sheehan.smarthome.model.EnvironmentReading;
import ie.sheehan.smarthome.utility.HttpRequestHandler;

public class ChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_chart);

        HttpRequestHandler handler = HttpRequestHandler.getInstance();

        Date from = new Date(1451610000);
        Date to = new Date(1451628000);

        List<EnvironmentReading> readings = handler.getEnvironmentReadingsInRange(from, to);

        Log.e("COUNT", Integer.toString(readings.size()));
        Log.e("BEGIN", "HERE IS THE START");

        for (EnvironmentReading reading : readings){
            Log.e("READING", reading.toString());
        }

        BarChart barChart = (BarChart) findViewById(R.id.chart);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(4f, 0));
        entries.add(new BarEntry(8f, 1));
        entries.add(new BarEntry(6f, 2));
        entries.add(new BarEntry(2f, 3));
        entries.add(new BarEntry(18f, 4));
        entries.add(new BarEntry(9f, 5));
        entries.add(new BarEntry(4f, 6));
        entries.add(new BarEntry(8f, 7));
        entries.add(new BarEntry(6f, 8));
        entries.add(new BarEntry(2f, 9));
        entries.add(new BarEntry(18f, 10));
        entries.add(new BarEntry(9f, 11));

        ArrayList<String> labels = new ArrayList<>();
        labels.add("Jan");
        labels.add("Feb");
        labels.add("Mar");
        labels.add("Apr");
        labels.add("May");
        labels.add("Jun");
        labels.add("Jul");
        labels.add("Aug");
        labels.add("Sept");
        labels.add("Oct");
        labels.add("Nov");
        labels.add("Dec");

        BarDataSet dataSet = new BarDataSet(entries, "Average Temperature");

        BarData data = new BarData(labels, dataSet);
        barChart.setData(data);
        barChart.setDescription("");
        barChart.animateY(1000);
    }

}
