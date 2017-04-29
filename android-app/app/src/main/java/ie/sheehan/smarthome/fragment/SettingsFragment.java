package ie.sheehan.smarthome.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;

import static ie.sheehan.smarthome.R.xml.preferences;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private static final long PERIOD_HALF_SECOND = 500;
    private static final long PERIOD_ONE_SECOND = 1000;
    private static final long PERIOD_TWO_SECONDS = 2000;
    private static final long PERIOD_FIVE_SECONDS = 5000;

    private static final String KEY_PREF_METRIC_TEMPERATURE = "pref_metric_temperature";
    private static final String KEY_PREF_METRIC_WEIGHT = "pref_metric_weight";
    private static final String KEY_PREF_CONNECTION_FREQUENCY = "pref_connection_frequency";
    private static final String KEY_PREF_NOTIFICATION = "pref_notifications_send";

    public static final int VALUE_METRIC_TEMPERATURE_CELSIUS = 0;
    public static final int VALUE_METRIC_TEMPERATURE_FAHRENHEIT = 1;

    public static final int VALUE_METRIC_WEIGHT_METRIC = 0;
    public static final int VALUE_METRIC_WEIGHT_IMPERIAL = 1;

    public static final int VALUE_CONNECTION_FREQUENCY_SHORTEST = 0;
    public static final int VALUE_CONNECTION_FREQUENCY_SHORT= 1;
    public static final int VALUE_CONNECTION_FREQUENCY_LONG = 2;
    public static final int VALUE_CONNECTION_FREQUENCY_LONGEST = 3;

    public static SharedPreferences prefs;


    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(preferences);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {}


    public static long getPreferredConnectionPeriod() {
        int preferredPeriod = Integer.parseInt(prefs.getString(KEY_PREF_CONNECTION_FREQUENCY, "0"));

        switch (preferredPeriod) {
            case VALUE_CONNECTION_FREQUENCY_SHORTEST:
                return PERIOD_HALF_SECOND;
            case VALUE_CONNECTION_FREQUENCY_SHORT:
                return PERIOD_ONE_SECOND;
            case VALUE_CONNECTION_FREQUENCY_LONG:
                return PERIOD_TWO_SECONDS;
            case VALUE_CONNECTION_FREQUENCY_LONGEST:
                return PERIOD_FIVE_SECONDS;
            default:
                return PERIOD_ONE_SECOND;
        }
    }

    public static int getPreferredTemperatureMetric() {
        return Integer.parseInt(prefs.getString(KEY_PREF_METRIC_TEMPERATURE, "0"));
    }

    public static int getPreferredWeightMetric() {
        return Integer.parseInt(prefs.getString(KEY_PREF_METRIC_WEIGHT, "0"));
    }

    public static boolean getPreferredNotifications() {
        return prefs.getBoolean(KEY_PREF_NOTIFICATION, true);
    }

}
