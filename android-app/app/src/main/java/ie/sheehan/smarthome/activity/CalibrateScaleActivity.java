package ie.sheehan.smarthome.activity;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ie.sheehan.smarthome.R;
import ie.sheehan.smarthome.model.StockReading;
import ie.sheehan.smarthome.utility.HttpRequestHandler;

public class CalibrateScaleActivity extends AppCompatActivity {

    Resources res;

    EditText productText;
    TextView weightText;

    ScheduledExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate_scale);
        res = getResources();

        productText = (EditText) findViewById(R.id.edit_product_name);
        weightText = (TextView) findViewById(R.id.text_weight_calibrate);

        executorService = Executors.newScheduledThreadPool(10);

        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                new GetWeight().execute();
            }
        }, 500, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (executorService.isShutdown()) {
            executorService = Executors.newScheduledThreadPool(10);

            executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    new GetWeight().execute();
                }
            }, 500, 1000, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        executorService.shutdown();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (! executorService.isShutdown()) {
            executorService.shutdown();
        }
    }


    public void calibrateScale(View view) {
        String productName = productText.getText().toString();

        if (productName.isEmpty()) {
            Toast.makeText(this, "No name", Toast.LENGTH_SHORT).show();
            return;
        }

        new CalibrateScale().execute(productName);
    }


    private class GetWeight extends AsyncTask<Void, Void, StockReading> {
        @Override
        protected StockReading doInBackground(Void... params) {
            return HttpRequestHandler.getInstance().getStockReading();
        }

        @Override
        protected void onPostExecute(StockReading stockReading) {
            super.onPostExecute(stockReading);

            if (stockReading == null) { return; }

            String weight = res.getString(R.string.text_scale_calibrate_weight_g);
            weight = String.format(weight, stockReading.weight);
            weightText.setText(weight);
        }
    }

    private class CalibrateScale extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String product = params[0];
            return HttpRequestHandler.getInstance().calibrateScale(product);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                onBackPressed();
            }
            else {
                Toast.makeText(CalibrateScaleActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
