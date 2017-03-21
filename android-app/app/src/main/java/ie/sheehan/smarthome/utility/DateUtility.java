package ie.sheehan.smarthome.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DateUtility {

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

    public static DateFormat getDateFormat() {
        return new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
    }

    public static DateFormat getShortDateFormat() {
        return new SimpleDateFormat("EEE, dd MMM", Locale.getDefault());
    }

}
