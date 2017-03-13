package ie.sheehan.smarthome.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ie.sheehan.smarthome.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TemperatureFragment extends Fragment {

    TextView temperatureView;
    TextView humidityView;


    public TemperatureFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_temperature, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        temperatureView = (TextView) getActivity().findViewById(R.id.text_temperature);
        humidityView = (TextView) getActivity().findViewById(R.id.text_humidity);

        temperatureView.setText(Double.toString(15.32) + "℃");
        humidityView.setText("Humidity: " + Double.toString(70.01));
    }

}
