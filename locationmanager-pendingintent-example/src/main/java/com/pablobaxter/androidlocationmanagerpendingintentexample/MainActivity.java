package com.pablobaxter.androidlocationmanagerpendingintentexample;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

/**
 * @author Pablo Baxter
 * GitHub: github.com/pablobaxter
 *
 * This example shows how to request for location updates using Android's LocationManager with a PendingIntent.
 * I've added examples on using both Provider and Criteria location manager calls,
 * and how to handle using a BroadcastReceiver and IntentService.
 */

public class MainActivity extends AppCompatActivity {

    private LocationManager mLocationManager;
    private PendingIntent mPendingIntent;
    private BroadcastReceiver mLocalReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        //Use this to get updates in the broadcast receiver.
//        mPendingIntent =  PendingIntent.getBroadcast(this, 0, new Intent(this, LocationBroadcastReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

        //Use this to get updates in the intent service.
        mPendingIntent =  PendingIntent.getBroadcast(this, 0, new Intent(this, LocationIntentService.class), PendingIntent.FLAG_UPDATE_CURRENT);

        mLocalReceiver = new InternalLocalReceiver(this);
    }

    @Override
    protected void onResume(){
        super.onResume();

        //Register our local receiver with the intent filter of "locationReceiver" to listen for updates broadcast from either the LocationIntentService or LocationBroadcastReceiver.
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, new IntentFilter("locationReceiver"));

        //Permission check for Android 6.0+
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestUpdates();
        }
        else {
            //Request permissions if we need them
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        //Unregister our local receiver.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);

        //Permission check for Android 6.0+
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(mPendingIntent);
        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == 0){ //Request code passed in by requestPermissions(Activity, String[], int)
            if(permissions.length > 0 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    requestUpdates();
                }
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    private void requestUsingProviders(){
        //GPS Location
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mPendingIntent); //Time is set to fastest, with a minimum distance of 0m.

        //Network Location
//            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this); //Time is set to fastest, with a minimum distance of 0m.

        //Passive Location
//            mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this); //Time is set to fastest, with a minimum distance of 0m.
    }

    //Simple helper method
    private void requestUpdates(){
        /* Requesting for locations using Providers */
        requestUsingProviders();

        /* Requesting for locations using Criteria */
//        requestUsingCriteria();
    }

    @SuppressWarnings("MissingPermission")
    private void requestUsingCriteria(){
        //Get the new criteria object
        Criteria criteria = new Criteria();

        //Adjust the criteria to whatever you require. However, don't use this if you don't want GPS used, as some criteria can only be met by using GPS.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); //We just want the best accuracy

        //Pass the criteria to use
        mLocationManager.requestLocationUpdates(0, 0, criteria, mPendingIntent);
    }

    //This is the local BroadcastReceiver we will use to get updates back from either the LocationBroadcastReceiver or the LocationIntentService.
    static class InternalLocalReceiver extends BroadcastReceiver{

        private MainActivity mActivity;

        InternalLocalReceiver(MainActivity activity){
            mActivity = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity activity = mActivity;
            TextView textView = activity != null ? (TextView)activity.findViewById(R.id.location_text) : null;
            if(activity != null && textView != null){
                Location location = intent.getParcelableExtra("locationUpdate");
                textView.setText(String.format(Locale.US, "%s, %f, %f", location.getProvider(), location.getLatitude(), location.getLongitude()));
            }
        }
    }
}
