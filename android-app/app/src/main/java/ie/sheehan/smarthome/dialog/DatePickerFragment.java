package ie.sheehan.smarthome.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * An implementation of a {@link DialogFragment} that displays a date-picker dialog.
 */
public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;

    /**
     * Adds an instance of {@link android.app.DatePickerDialog.OnDateSetListener} so that the
     * selected date value can be retrieved.
     *
     * @param listener to be assigned to the dialog
     */
    public void addOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = null;
        Calendar calendar = Calendar.getInstance();
        Bundle arguments = getArguments();

        if (arguments.containsKey("date")) {
            date = (Date) arguments.getSerializable("date");
        }

        if (date != null) {
            calendar.setTime(date);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }

}
