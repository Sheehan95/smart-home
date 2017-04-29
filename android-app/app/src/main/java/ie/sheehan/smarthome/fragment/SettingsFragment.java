package ie.sheehan.smarthome.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;

import ie.sheehan.smarthome.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String KEY_PREF_METRIC_TEMPERATURE = "pref_metric_temperature";
    public static final String KEY_PREF_METRIC_WEIGHT = "pref_metric_weight";
    public static final String KEY_PREF_CONNECTION_FREQUENCY = "pref_connection_frequency";

    public static final int VALUE_METRIC_TEMPERATURE_CELSIUS = 0;
    public static final int VALUE_METRIC_TEMPERATURE_FAHRENHEIT = 1;

    public static final int VALUE_METRIC_WEIGHT_METRIC = 0;
    public static final int VALUE_METRIC_WEIGHT_IMPERIAL = 1;

    public static final int VALUE_CONNECTION_FREQUENCY_SHORTEST = 0;
    public static final int VALUE_CONNECTION_FREQUENCY_SHORT= 1;
    public static final int VALUE_CONNECTION_FREQUENCY_LONG = 2;
    public static final int VALUE_CONNECTION_FREQUENCY_LONGEST = 3;


    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }
}
