package com.pablobaxter.pathsensetruepathexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.pathsense.android.sdk.location.PathsenseInVehicleLocation;

/**
 * @author Pablo Baxter
 * GitHub: github.com/pablobaxter
 *
 * This is where your TruePath location points will come in at. It is important to note that onReceive will be called in the main thread, so you shouldn't perform
 * any blocking function calls here.
 */

public class LocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PathsenseInVehicleLocation inVehicleLocation = PathsenseInVehicleLocation.fromIntent(intent);

        //Handle anything you need to here, before sending out to all other receivers.

        //I suggest passing the entire intent if you'd like to receive the list of location points to generate the route traversed.
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("locationReceiver").putExtras(intent));

    }
}
