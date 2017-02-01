package com.pablobaxter.pathsensetruepathexample;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.pathsense.android.sdk.location.PathsenseInVehicleLocation;
import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;

import java.util.Locale;

/**
 * @author Pablo Baxter
 * GitHub: github.com/pablobaxter
 *
 * This example shows how to request for location updates using Pathsense's TruePath library. These locations are map matched to the nearest road
 * as they come in. This provider uses GPS and sensors, and should be managed like GPS if power conservation is a priority.
 */

public class MainActivity extends AppCompatActivity {

    private PathsenseLocationProviderApi mApi;
    private BroadcastReceiver mLocalReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApi = PathsenseLocationProviderApi.getInstance(this);
        mLocalReceiver = new InternalLocationReceiver(this);
    }

    @Override
    protected void onResume(){
        super.onResume();

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

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);

        mApi.removeInVehicleLocationUpdates();
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

    private void requestUpdates(){

        //Pathsense uses only a BroadcastReceiver to send locations to. Their example uses a helper class that passes the PathsenseInVehicleLocation object, instead of an intent.
        mApi.requestInVehicleLocationUpdates(LocationReceiver.class);

        //The int value here is a delay in receiving the intent. Since the library maps to the road you are on, giving it a delay gives the algorithm more time to give you a better location.
//        mApi.requestInVehicleLocationUpdates(300, LocationReceiver.class);
    }

    static class InternalLocationReceiver extends BroadcastReceiver{

        private MainActivity mActivity;

        InternalLocationReceiver(MainActivity activity){
            mActivity = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity activity = mActivity;
            TextView textView = activity != null ? (TextView)activity.findViewById(R.id.location_text) : null;
            if(activity != null && textView != null){
                PathsenseInVehicleLocation inVehicleLocation = PathsenseInVehicleLocation.fromIntent(intent);
                textView.setText(String.format(Locale.US, "%s, %f, %f, %s", inVehicleLocation.getProvider(), inVehicleLocation.getLatitude(), inVehicleLocation.getLongitude(), inVehicleLocation.getPoints()));
            }
        }
    }
}
