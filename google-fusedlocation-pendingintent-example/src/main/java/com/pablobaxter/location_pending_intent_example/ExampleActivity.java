package com.pablobaxter.location_pending_intent_example;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.location.LocationResult;

import java.util.Locale;

/**
 * @author Pablo Baxter
 * GitHub: github.com/pablobaxter
 *
 * This example is useful if you have many different classes that should be
 * receiving location updates, but want more granular control over which ones
 * listen to the updates.
 *
 * For example, this activity will stop getting updates when it is not visible, but a database
 * class with a registered local receiver will continue to receive updates, until "stopUpdates()" is called here.
 *
 */
public class ExampleActivity extends AppCompatActivity {

    private InternalLocationReceiver mInternalLocationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create internal receiver object in this method only.
        mInternalLocationReceiver = new InternalLocationReceiver(this);
    }

    @Override
    protected void onResume(){
        super.onResume();

        //Register to receive updates in activity only when activity is visible
        LocalBroadcastManager.getInstance(this).registerReceiver(mInternalLocationReceiver, new IntentFilter("googleLocation")); //Note the intent filter used

        //Start updates
        requestUpdates();
    }

    @Override
    protected void onPause(){
        super.onPause();

        //Unregister to stop receiving updates in activity when it is not visible.
        //NOTE: You will still receive updates even if this activity is killed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mInternalLocationReceiver);

        //Stop updates
        stopUpdates();
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == 0){ //Request code passed in by requestPermissions(Activity, String[], int)
            if(permissions.length > 0 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startService(new Intent(this, LocationService.class).putExtra("request", true));
                }
            }
        }
    }

    //Helper method to get updates
    private void requestUpdates(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startService(new Intent(this, LocationService.class).putExtra("request", true));
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

    }

    //Helper method to stop updates
    private void stopUpdates(){
        startService(new Intent(this, LocationService.class).putExtra("remove", true));
    }

    /*
     * Internal receiver used to get location updates for this activity.
     *
     * This receiver and any receiver registered with LocalBroadcastManager does
     * not need to be registered in the Manifest.
     *
     */
    private static class InternalLocationReceiver extends BroadcastReceiver {

        private ExampleActivity mActivity;

        InternalLocationReceiver(ExampleActivity activity){
            mActivity = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            final ExampleActivity activity = mActivity;
            final TextView textView = activity != null ? (TextView)activity.findViewById(R.id.location_text) : null;
            if(activity != null && textView != null) {
                LocationResult result = intent.getParcelableExtra("result"); //Get the LocationResult from the intent.
                if(result != null) {
                    Location location = result.getLastLocation();
                    //Handle location update here
                    textView.setText(String.format(Locale.US, "Location, %s, %f, %f", location.getProvider(), location.getLatitude(), location.getLongitude()));
                }
            }
        }
    }
}