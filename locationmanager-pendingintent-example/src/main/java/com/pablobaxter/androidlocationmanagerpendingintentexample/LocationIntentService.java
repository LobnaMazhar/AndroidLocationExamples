package com.pablobaxter.androidlocationmanagerpendingintentexample;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.LocalBroadcastManager;

/**
 * @author Pablo Baxter
 * GitHub: github.com/pablobaxter
 *
 * This is where your location points will come in at, when you use an IntentService.
 * It is important to note that onHandleIntent will be called in a background thread that will live only as long
 * as you are still running within onHandleIntent. This is what you would want to use if you need to handle long
 * running tasks on location, so you don't block the main (UI) thread.
 *
 * @deprecated This will no longer work on devices targeting API 26 (Android Oreo).
 *
 */

public class LocationIntentService extends IntentService {

    public LocationIntentService() {
        super("LocationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);

        //Handle any location updates you need here before broadcasting out to all local receivers.
        //NOTE: This is all done in a background thread.

        //Now we broadcast out to any receiver that registered the "locationReceiver" filter with the LocalBroadcastManager.
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("locationReceiver").putExtra("locationUpdate", location));
    }
}
