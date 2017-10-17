package com.pablobaxter.location_pending_intent_example;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

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
 */

public class LocationIntentService extends IntentService {

    public LocationIntentService() {
        super("LocationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LocationResult locationResult = LocationResult.extractResult(intent);
        //Do what you need here before sending out location results.
        Intent locationResultIntent = new Intent("googleLocation"); //Note the intent filter used
        locationResultIntent.putExtra("result", locationResult); //Map this result in the intent.
        LocalBroadcastManager.getInstance(this).sendBroadcast(locationResultIntent);
    }
}
