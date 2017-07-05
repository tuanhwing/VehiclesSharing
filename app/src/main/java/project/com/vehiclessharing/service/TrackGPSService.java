package project.com.vehiclessharing.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import project.com.vehiclessharing.constant.Utils;

import static project.com.vehiclessharing.activity.MainActivity.mGoogleMap;

/**
 * Created by Tuan on 22/04/2017.
 */

public class TrackGPSService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
    private static GoogleApiClient mGoogleApiClient;
    private static LocationRequest mLocationRequest;
    public static boolean isRunning = false;//Instance to check service is running or not

    boolean zoomOneTime = true;//Just zoom 1 time

    final private static int REQ_PERMISSION = 20;// Value permission locaiton



    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("service_aaaaaaa","create");
        buildGoogleApiClient();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MyService", "Received start id " + startId + ": " + intent);
        isRunning = true;
        return START_STICKY; // run until explicitly stopped.
    }

    /**
     * Build googleAPI Client
     */
    public synchronized void buildGoogleApiClient() {
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            Log.d("bug1111","1");
            mGoogleApiClient.connect();
        } catch (Exception e){
            Log.e(Utils.TAG_ERROR_LOCATION,String.valueOf(e.getMessage()));
        }
    }

    /**
     * Create location request
     */
    private void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setSmallestDisplacement(5); //5 meter
//        controllonLocationChanged(CONTROLL_ON);//Enable Listener
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("service_aaaaaaa","connect");
        try {
            Toast.makeText(this, "Location  service connected", Toast.LENGTH_SHORT).show();

            createLocationRequest();//Create location request
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        } catch (Throwable t) { //you should always ultimately catch all exceptions in timer tasks.
            Log.e("Google APi Connected", "Google APi Connected Failed.", t);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Every n s(setInterval + setFastest) will get location. This function called when the location has changed.
     * @param location current loacation
     */
    @Override
    public void onLocationChanged(Location location) {
        if(zoomOneTime && mGoogleMap != null && mGoogleMap.getCameraPosition().zoom == 2.0){//Just zoom 1 time
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),11));
            zoomOneTime =false;
        }
        Log.d("ONLOCATIONCHANGE","AAAAAAAAAAAAA");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
}
