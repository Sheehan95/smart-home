package ie.sheehan.smarthome.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

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

import static ie.sheehan.smarthome.model.StockReading.getAverageConsumedInRange;
import static ie.sheehan.smarthome.model.StockReading.getLargestStockReadingInRange;
import static ie.sheehan.smarthome.model.StockReading.getLeastConsumedInRange;
import static ie.sheehan.smarthome.model.StockReading.getMostConsumedInRange;
import static ie.sheehan.smarthome.utility.DateUtility.compareDateIgnoreTime;
import static ie.sheehan.smarthome.utility.DateUtility.getShortDateFormat;
import static ie.sheehan.smarthome.utility.DateUtility.getUniqueDateRange;

public class StockChartActivity extends AppCompatActivity {

    Resources resources;

    String product;
    List<StockReading> stockReadings;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_stock_chart);

        resources = getResources();

        Bundle arguments = getIntent().getExtras();
        stockReadings = (List<StockReading>) arguments.getSerializable("stockReadings");
        product = arguments.getString("product");
    }

    @Override
    protected void onStart() {
        super.onStart();

        LineChart lineChart = (LineChart) findViewById(R.id.chart);

        List<StockReading> chartData = generateChartData();

        Log.e("AVERAGE", "CONSUMED PER DAY: " + getAverageConsumedInRange(chartData));

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

        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int dpHeight = (int) (displayMetrics.heightPixels / displayMetrics.density);

        lineChart.setMinimumHeight(dpHeight);
        lineChart.invalidate();

        populateStatistics(chartData);
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
                if (reading.getWeight() < lowestWeight) {
                    lowestWeight = reading.getWeight();
                }
            }

            StockReading averageReading = new StockReading();
            averageReading.setProduct(product);
            averageReading.setWeight(lowestWeight);
            averageReading.setCapacity(stockReadings.get(0).getCapacity());
            averageReading.setDate(date);

            graphData.add(averageReading);
        }

        return graphData;
    }

    private List<Entry> getChartEntries(List<StockReading> chartData) {
        List<Entry> entries = new ArrayList<>();

        for (int i = 0 ; i < chartData.size() ; i++){
            float weight = (float) chartData.get(i).getWeight();
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

    private void populateStatistics(List<StockReading> chartData) {
        TextView estFinished = (TextView) findViewById(R.id.stat_finished);
        TextView mostValue = (TextView) findViewById(R.id.stat_most);
        TextView mostDate = (TextView) findViewById(R.id.stat_most_date);
        TextView leastValue = (TextView) findViewById(R.id.stat_least);
        TextView leastDate = (TextView) findViewById(R.id.stat_least_date);

        int most = getMostConsumedInRange(chartData);
        int least = getLeastConsumedInRange(chartData);

        mostValue.setText(resources.getString(R.string.text_stock_display_weight, most));
        leastValue.setText(resources.getString(R.string.text_stock_display_weight, least));

        int average = getAverageConsumedInRange(chartData);
        int currentWeight = chartData.get(chartData.size() - 1).getWeight();

        int days = currentWeight / average;

        if (days == 0) {
            estFinished.setText("Today");
        }
        else {
            estFinished.setText(String.format("%d days", days));
        }

        Log.e("AVERAGE", "" + average);
    }

}
