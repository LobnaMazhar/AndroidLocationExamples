package com.pablobaxter.androidlocationmanagerpendingintentexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.LocalBroadcastManager;

/**
 * @author Pablo Baxter
 * GitHub: github.com/pablobaxter
 *
 * This is where your location points will come in at, when you use a BroadcastReceiver.
 * It is important to note that onReceive will be called in the main thread, so you shouldn't perform
 * any blocking function calls here.
 */

public class LocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);

        //Handle any location updates you need here before broadcasting out to all local receivers.
        //NOTE: This is all done in the UI (main) thread.

        //Now we broadcast out to any receiver that registered the "locationReceiver" filter with the LocalBroadcastManager.
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("locationReceiver").putExtra("locationUpdate", location));
    }
}
