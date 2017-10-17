package com.pablobaxter.androidlocationmanagerlistenerexample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

/**
 * @author Pablo Baxter
 * GitHub: github.com/pablobaxter
 *
 * This example shows how to request for location updates using Android's LocationManager with a LocationListener.
 * I've added examples on using both Provider and Criteria location manager calls,
 * and how to handle using a looper when passing into a location manager request.
 */

public class MainActivity extends AppCompatActivity implements LocationListener {

    private LocationManager mLocationManager;
    private TextView mTextView;
    private HandlerThread mHandlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mTextView = (TextView) findViewById(R.id.location_text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Permission check for Android 6.0+
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestUpdates();
        } else {
            //Request permissions if we need them
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Permission check for Android 6.0+
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(this);
        }

        //Always stop threads that are no longer needed.
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) { //Request code passed in by requestPermissions(Activity, String[], int)
            if (permissions.length > 0 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestUpdates();
                }
            }
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        //Handle your location updates here.

        /*
         * Note that we are checking for the looper we are currently using. The reason for this is because we have examples that handle updates in
         * a background thread, and modifying UI in a background thread will cause a crash of the app to occur. If you are ever unsure of the thread you
         * are currently in, use the following to ensure UI calls are made only in the UI (main) thread.
         */

        //In the main thread?
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mTextView.setText(String.format(Locale.US, "%s, %s, %f, %f", "Main Thread", location.getProvider(), location.getLatitude(), location.getLongitude()));
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextView.setText(String.format(Locale.US, "%s, %s, %f, %f", "Background Thread", location.getProvider(), location.getLatitude(), location.getLongitude()));
                }
            });
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //Simple helper method
    private void requestUpdates() {
        /* Requesting for locations using Providers */
        requestUsingProviders();

        /* Requesting for locations using Criteria */
//        requestUsingCriteria();

        /* Requesting for locations in a background thread */
//        requestUsingProvidersBackground();
//        requestUsingCriteriaBackground();
    }

    @SuppressWarnings("MissingPermission")
    private void requestUsingProviders() {
        //GPS Location
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this); //Time is set to fastest, with a minimum distance of 0m.

        //Network Location
//            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this); //Time is set to fastest, with a minimum distance of 0m.

        //Passive Location
//            mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this); //Time is set to fastest, with a minimum distance of 0m.
    }

    @SuppressWarnings("MissingPermission")
    private void requestUsingCriteria() {
        //Get the new criteria object
        Criteria criteria = new Criteria();

        //Adjust the criteria to whatever you require. However, don't use this if you don't want GPS used, as some criteria can only be met by using GPS.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); //We just want the best accuracy

        //We pass null to use the current thread. NOTE: This will not be the UI thread if called from a background thread!
        mLocationManager.requestLocationUpdates(0, 0, criteria, this, null);
    }

    @SuppressWarnings("MissingPermission")
    private void requestUsingProvidersBackground() {
        mHandlerThread = new HandlerThread("LocationThread"); //Create our new thread
        mHandlerThread.start();
        Looper looper = mHandlerThread.getLooper(); //Get the looper for the new thread.

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this, looper); //Location updates are done in a background thread.
    }

    @SuppressWarnings("MissingPermission")
    private void requestUsingCriteriaBackground() {
        mHandlerThread = new HandlerThread("LocationThread"); //Create our new thread.
        mHandlerThread.start();
        Looper looper = mHandlerThread.getLooper(); //Get the looper for the new thread.

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); //We just want the best accuracy

        //We pass null to use the current thread. NOTE: This will not be the UI thread if called from a background thread!
        mLocationManager.requestLocationUpdates(0, 0, criteria, this, looper);
    }
}
