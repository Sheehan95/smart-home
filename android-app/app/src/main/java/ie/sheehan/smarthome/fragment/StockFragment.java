package ie.sheehan.smarthome.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ie.sheehan.smarthome.R;
import ie.sheehan.smarthome.activity.CalibrateScaleActivity;
import ie.sheehan.smarthome.activity.StockChartActivity;
import ie.sheehan.smarthome.model.StockReading;
import ie.sheehan.smarthome.utility.HttpRequestHandler;

/**
 * A simple {@link Fragment} subclass.
 */
public class StockFragment extends Fragment {

    static final long PERIOD = 1000;
    static final long DELAY = 500;

    Resources res;
    SharedPreferences preferences;

    ScheduledExecutorService executorService;

    TextView textProduct;
    TextView textWeight;

    Button openChartButton;

    Spinner productSpinner;
    ArrayAdapter<String> adapter;

    View cover;

    public StockFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stock, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        res = getResources();
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        textProduct = (TextView) getActivity().findViewById(R.id.label_product);
        textWeight = (TextView) getActivity().findViewById(R.id.label_weight);

        productSpinner = (Spinner) getActivity().findViewById(R.id.spinner_products);
        openChartButton = (Button) getActivity().findViewById(R.id.button_view_product_chart);

        cover = getActivity().findViewById(R.id.gui_cover);

        executorService = Executors.newScheduledThreadPool(10);

        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                new GetStock().execute();
            }
        }, DELAY, PERIOD, TimeUnit.MILLISECONDS);

        new GetProducts().execute();

        openChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProductChart((String) productSpinner.getSelectedItem());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        executorService.shutdown();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (executorService.isShutdown()) {
            executorService = Executors.newScheduledThreadPool(10);

            executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    new GetStock().execute();
                }
            }, DELAY, PERIOD, TimeUnit.MILLISECONDS);
        }
    }


    public void openScaleCalibrationActivity() {
        getActivity().startActivity(new Intent(getActivity(), CalibrateScaleActivity.class));
    }

    public void openProductChart(String product) {
        new GetStockByProduct().execute(product);
    }


    private class GetStock extends AsyncTask<Void, Void, StockReading> {
        @Override
        protected StockReading doInBackground(Void... params) {
            return HttpRequestHandler.getInstance().getStockReading();
        }

        @Override
        protected void onPostExecute(StockReading stockReading) {
            super.onPostExecute(stockReading);

            if (stockReading.weight < 0) {
                if (cover.getVisibility() == View.INVISIBLE) {
                    cover.setVisibility(View.VISIBLE);
                }
            }
            else {
                cover.setVisibility(View.INVISIBLE);
            }

            textProduct.setText(stockReading.product);

            String weightText = res.getString(R.string.text_stock_weight);
            weightText = String.format(weightText, stockReading.weight, stockReading.capacity);
            textWeight.setText(weightText);
        }
    }

    private class GetProducts extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... params) {
            return HttpRequestHandler.getInstance().getAllProducts();
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);

            if (strings.isEmpty()) {
                openChartButton.setEnabled(false);
                return;
            }
            else {
                openChartButton.setEnabled(true);
            }

            String[] productList = strings.toArray(new String[strings.size()]);

            adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, productList);
            productSpinner.setAdapter(adapter);
        }
    }

    private class GetStockByProduct extends AsyncTask<String, Void, List<StockReading>> {
        @Override
        protected List<StockReading> doInBackground(String... params) {
            String product = params[0];
            return HttpRequestHandler.getInstance().getStockReadingsByProduct(product);
        }

        @Override
        protected void onPostExecute(List<StockReading> stockReadings) {
            super.onPostExecute(stockReadings);

            if (stockReadings.isEmpty()) {
                Toast.makeText(getContext(), "NO DATA", Toast.LENGTH_SHORT).show();
                return;
            }

            Bundle arguments = new Bundle();
            arguments.putString("product", (String) productSpinner.getSelectedItem());
            arguments.putSerializable("stockReadings", (ArrayList) stockReadings);

            Intent intent = new Intent(getActivity(), StockChartActivity.class);
            intent.putExtras(arguments);

            getActivity().startActivity(intent);
        }
    }

}
