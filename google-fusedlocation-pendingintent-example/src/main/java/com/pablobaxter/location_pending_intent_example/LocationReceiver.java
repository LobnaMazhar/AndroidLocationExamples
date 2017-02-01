package com.pablobaxter.location_pending_intent_example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

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
        LocationResult locationResult = LocationResult.extractResult(intent);
        //Do what you need here before sending out location results.
        Intent locationResultIntent = new Intent("googleLocation"); //Note the intent filter used
        locationResultIntent.putExtra("result", locationResult); //Map this result in the intent.
        LocalBroadcastManager.getInstance(context).sendBroadcast(locationResultIntent);
    }
}
