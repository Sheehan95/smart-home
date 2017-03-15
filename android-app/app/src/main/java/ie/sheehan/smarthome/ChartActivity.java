package ie.sheehan.smarthome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        LineChart lineChart = (LineChart) findViewById(R.id.chart);

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(4f, 0));
        entries.add(new Entry(8f, 1));
        entries.add(new Entry(6f, 2));
        entries.add(new Entry(2f, 3));
        entries.add(new Entry(18f, 4));
        entries.add(new Entry(9f, 5));
        entries.add(new Entry(4f, 6));
        entries.add(new Entry(8f, 7));
        entries.add(new Entry(6f, 8));
        entries.add(new Entry(2f, 9));
        entries.add(new Entry(18f, 10));
        entries.add(new Entry(9f, 11));

        LineDataSet dataSet = new LineDataSet(entries, "EnvironmentReading");

        ArrayList<Entry> e1 = new ArrayList<>();
        entries.add(new Entry(9f, 0));
        entries.add(new Entry(11f, 1));
        entries.add(new Entry(3f, 2));
        entries.add(new Entry(6f, 3));
        entries.add(new Entry(10f, 4));
        entries.add(new Entry(5f, 5));
        entries.add(new Entry(9f, 6));
        entries.add(new Entry(3f, 7));
        entries.add(new Entry(5f, 8));
        entries.add(new Entry(6f, 9));
        entries.add(new Entry(8f, 10));
        entries.add(new Entry(7f, 11));

        LineDataSet d1 = new LineDataSet(e1, "EnvironmentReading");

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

        LineData data = new LineData(labels, dataSet);
        data.addDataSet(d1);
        lineChart.setData(data);
        lineChart.setDescription("EnvironmentReading values for the year");
    }

}
