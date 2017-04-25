package ie.sheehan.smarthome.fragment;


import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ie.sheehan.smarthome.R;
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

        cover = getActivity().findViewById(R.id.gui_cover);

        executorService = Executors.newScheduledThreadPool(10);

        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                new GetStock().execute();
            }
        }, DELAY, PERIOD, TimeUnit.MILLISECONDS);
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

}
