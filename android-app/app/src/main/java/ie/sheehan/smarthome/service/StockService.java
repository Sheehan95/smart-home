package ie.sheehan.smarthome.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import ie.sheehan.smarthome.R;
import ie.sheehan.smarthome.activity.IntrusionViewActivity;
import ie.sheehan.smarthome.model.IntrusionReading;
import ie.sheehan.smarthome.model.StockReading;
import ie.sheehan.smarthome.utility.HttpRequestHandler;

import static ie.sheehan.smarthome.fragment.SettingsFragment.KEY_PREF_NOTIFICATION;

/**
 * A service that runs when the phone is booted. Checks for newly reported {@link IntrusionReading}
 * objects and sends a notification if one is found.
 */
public class StockService extends Service {

    Timer timer;
    TimerTask task;
    SharedPreferences preferences;


    Queue<StockReading> stockReadings;


    @Override
    public void onCreate() {
        super.onCreate();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        stockReadings = new LinkedList<>();

        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                new CheckScaleReading().execute();
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.schedule(task, 0, 5000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    /**
     * Creates and sends a notification. When clicked, the notification starts the
     * {@link IntrusionViewActivity} containing the given {@link IntrusionReading}.
     */
    public void sendNotification(StockReading stockReading) {

        if (! preferences.getBoolean(KEY_PREF_NOTIFICATION, true)){ return; }

        Notification.Builder notification = new Notification.Builder(StockService.this);
        notification.setSmallIcon(R.drawable.ic_tab_stock);
        notification.setContentTitle("STOCK");
        notification.setContentText("Your scale is empty - did you forget to put your " + stockReading.getProduct() + " back?");
        notification.setVibrate(new long[]{1000, 1000, 1000});
        notification.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification.build());
    }


    private class CheckScaleReading extends AsyncTask<Void, Void, StockReading> {
        @Override
        protected StockReading doInBackground(Void... params) {
            return HttpRequestHandler.getInstance().getStockReading();
        }

        @Override
        protected void onPostExecute(StockReading stockReading) {
            super.onPostExecute(stockReading);

            Log.e("LOGGING", "WEIGHT: " + stockReading.getWeight());
            Log.e("SIZE", "" + stockReadings.size());

            if (stockReadings.size() == 10) {
                int totalWeight = 0;

                for (StockReading reading : stockReadings) {
                    totalWeight += reading.getWeight();
                }

                if (totalWeight == 0) {
                    Log.e("SENDING", "NOTIFICATION");
                    sendNotification(stockReading);
                }

                Log.e("TOTAL WEIGHT", "" + totalWeight);

                stockReadings.poll();
            }

            stockReadings.offer(stockReading);
        }
    }

}
