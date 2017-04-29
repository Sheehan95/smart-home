package ie.sheehan.smarthome.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ie.sheehan.smarthome.service.IntrusionService;
import ie.sheehan.smarthome.service.StockService;

/**
 * An implementation of {@link BroadcastReceiver} that starts the {@link IntrusionService} when the
 * phone is booted.
 */
public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, IntrusionService.class));
        context.startService(new Intent(context, StockService.class));
    }

}
