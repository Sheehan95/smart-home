package ie.sheehan.smarthome.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ie.sheehan.smarthome.R;
import ie.sheehan.smarthome.model.StockReading;

import static ie.sheehan.smarthome.model.StockReading.getLargestStockReadingInRange;
import static ie.sheehan.smarthome.utility.DateUtility.compareDateIgnoreTime;
import static ie.sheehan.smarthome.utility.DateUtility.getShortDateFormat;
import static ie.sheehan.smarthome.utility.DateUtility.getUniqueDateRange;

public class StockChartActivity extends AppCompatActivity {

    String product;
    List<StockReading> stockReadings;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_stock_chart);

        Bundle arguments = getIntent().getExtras();
        stockReadings = (List<StockReading>) arguments.getSerializable("stockReadings");
        product = arguments.getString("product");
    }

    @Override
    protected void onStart() {
        super.onStart();

        LineChart lineChart = (LineChart) findViewById(R.id.chart);

        List<StockReading> chartData = generateChartData();

        List<Entry> entries = getChartEntries(chartData);
        List<String> labels = getChartLabels(chartData);

        LineDataSet dataSet = new LineDataSet(entries, "Weight");

        LineData data = new LineData(labels, dataSet);
        lineChart.setData(data);
        lineChart.setDescription("");
        lineChart.animateY(1000);
        lineChart.getXAxis().setLabelRotationAngle(280);
        YAxis yAxis = lineChart.getAxisLeft();
        YAxis axis = lineChart.getAxisRight();
        axis.setLabelCount(0, true);
        yAxis.setAxisMinValue(0);
        yAxis.setAxisMaxValue((long) (getLargestStockReadingInRange(chartData) + 4));
    }


    private List<StockReading> generateChartData() {
        List<StockReading> graphData = new ArrayList<>();

        Date startDate = stockReadings.get(0).getDate();
        Date endDate = stockReadings.get(stockReadings.size() - 1).getDate();

        for (Date date : getUniqueDateRange(startDate, endDate)) {
            List<StockReading> readingsForDay = new ArrayList<>();

            for (StockReading reading : stockReadings) {
                if (compareDateIgnoreTime(date, reading.getDate()) == 0) {
                    readingsForDay.add(reading);
                }
            }

            int lowestWeight = Integer.MAX_VALUE;

            for (StockReading reading : readingsForDay) {
                if (reading.weight < lowestWeight) {
                    lowestWeight = reading.weight;
                }
            }

            StockReading averageReading = new StockReading(product, lowestWeight, stockReadings.get(0).capacity);
            averageReading.timestamp = (date.getTime() / 1000L);
            graphData.add(averageReading);
        }

        return graphData;
    }

    private List<Entry> getChartEntries(List<StockReading> chartData) {
        List<Entry> entries = new ArrayList<>();

        for (int i = 0 ; i < chartData.size() ; i++){
            float weight = (float) chartData.get(i).weight;
            entries.add(new Entry(weight, i));
        }

        return entries;
    }

    private List<String> getChartLabels(List<StockReading> chartData) {
        List<String> labels = new ArrayList<>();

        for (int i = 0 ; i < chartData.size() ; i++) {
            labels.add(getShortDateFormat().format(chartData.get(i).getDate()));
        }

        return labels;
    }

}
