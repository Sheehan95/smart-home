package ie.sheehan.smarthome.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Provides utility functions for dealing with {@link Date} objects.
 */
public class DateUtility {

    /**
     * Finds all unique {@link Date} values, ignoring the time, of all days between and including
     * the start and end dates.
     *
     * @param startDate the starting date
     * @param endDate the end date
     * @return a list of unique {@link Date} values
     */
    public static List<Date> getUniqueDateRange(Date startDate, Date endDate) {
        ArrayList<Date> dates = new ArrayList<>();

        Calendar c1 = Calendar.getInstance();
        c1.setTime(startDate);
        c1.set(Calendar.HOUR, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(endDate);
        c2.set(Calendar.HOUR, 0);
        c2.set(Calendar.MINUTE, 0);
        c2.set(Calendar.SECOND, 0);

        while (c1.before(c2) || c1.equals(c2)) {
            dates.add(c1.getTime());
            c1.add(Calendar.DATE, 1);
        }

        return dates;
    }

    /**
     * Compares two {@link Date} objects but ignores the time values.
     *
     * @param d1 date to be compared
     * @param d2 date to compare it to
     * @return 0 if the two dates fall on the same day
     */
    public static int compareDateIgnoreTime(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        c1.set(Calendar.HOUR, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        c2.set(Calendar.HOUR, 0);
        c2.set(Calendar.MINUTE, 0);
        c2.set(Calendar.SECOND, 0);

        return c1.getTime().compareTo(c2.getTime());
    }

    /**
     * Returns a {@link DateFormat} object for formatting dates in the style: 'EEE, dd MMM yyyy'
     * E.G.: Tue, 21 Mar 2017
     *
     * @return a {@link DateFormat} object for formatting dates
     */
    public static DateFormat getDateFormat() {
        return new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
    }

    /**
     * Returns a {@link DateFormat} object for formatting dates in the style: 'EEE, dd MMM'
     * E.G.: Tue, 21 Mar
     *
     * @return a {@link DateFormat} object for formatting dates
     */
    public static DateFormat getShortDateFormat() {
        return new SimpleDateFormat("EEE, dd MMM", Locale.getDefault());
    }

}
