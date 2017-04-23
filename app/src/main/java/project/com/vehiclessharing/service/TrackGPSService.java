package project.com.vehiclessharing.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import project.com.vehiclessharing.activity.HomeActivity;
import project.com.vehiclessharing.utils.ReadTask;

import static project.com.vehiclessharing.activity.HomeActivity.mGoogleMap;

/**
 * Created by Tuan on 22/04/2017.
 */

public class TrackGPSService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
    private static GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private static LocationRequest mLocationRequest;

    public static boolean canGetLocation = false;

    boolean zoomOneTime = true;//Just zoom 1 time

    private static String DIRECTION_KEY_API = "AIzaSyAGjxiNRAHypiFYNCN-qcmUgoejyZPtS9c";


    private static int CONTROLL_ON = 1;
    private static int CONTROLL_OFF = -1;

    final private static int REQ_PERMISSION = 20;// Value permission locaiton

    public TrackGPSService(Context context){
        mContext = context;
        buildGoogleApiClient();
    }

    public synchronized void buildGoogleApiClient() {
        Toast.makeText(mContext, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        Log.d("bug1111","0");
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.d("bug1111","1");
        mGoogleApiClient.connect();
    }

    /**
     * Check permission to using location
     * @return true - can
     */
    public boolean checkLocationPermission() {

        if (ActivityCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((HomeActivity) mContext,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {


                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions((HomeActivity) mContext,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQ_PERMISSION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQ_PERMISSION);
            }
            canGetLocation = false;
            return false;
        } else {
            mGoogleMap.setMyLocationEnabled(true);
            canGetLocation = true;
            return true;
        }
    }
    /**
     * get Current Location
     * @return location
     */
    public Location getCurrentLocation(){
        Location mLastLocation = null;
        if(checkLocationPermission()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }

        //        Log.d("CurrentLocation",mLastLocation.getLatitude() + " | " + mLastLocation.getLongitude());
        return mLastLocation;
    }

    /**
     * Register / Unregister the listener location changed
     * @param value
     */
    public void controllonLocationChanged(int value){
        if(value == CONTROLL_OFF)// unregister the listener
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if(value == CONTROLL_ON) {//register the listener to listen location change
            if(checkLocationPermission()){
                mGoogleMap.setMyLocationEnabled(true);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//        mLocationRequest.setSmallestDisplacement(5); //5 meter
        controllonLocationChanged(CONTROLL_ON);//Enable Listener
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
        if(zoomOneTime){//Just zoom 1 time
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),11));
            zoomOneTime =false;
            drawroadBetween2Location(new LatLng(location.getLatitude(),location.getLongitude()),new LatLng(10.8719808,106.790409));
        }
        Log.d("ONLOCATIONCHANGE","AAAAAAAAAAAAA");
    }

    /**
     * Draw road between 2 location in google map
     */
    private void    drawroadBetween2Location(LatLng latLng1, LatLng latLng2) {
//        ArrayList<LatLng> temp = new ArrayList<LatLng>();
//        temp.add(new LatLng(10.8719808, 106.790409));
        String url = getMapsApiDirectionsUrl(latLng1, latLng2, null);

        Log.d("onMapClick", url.toString());
        ReadTask downloadTask = new ReadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
        Log.d("Log12/4", "12");

        //To calculate distance between points
//        float[] results = new float[1];
//        Location.distanceBetween(latitude, longitude,
//                10.882323, 106.782625,
//                results);
//        Log.d("onMapClick",String.valueOf(results));
    }

    private String getMapsApiDirectionsUrl(LatLng origin, LatLng dest, ArrayList<LatLng> waypoints) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        //Waypoints
        String str_waypoints = "";
        if(waypoints != null){
            str_waypoints = "waypoints=";
            boolean firts = false;
            for (LatLng latlng : waypoints) {
                if (!firts) {
                    str_waypoints += "via:" + latlng.latitude + "," + latlng.longitude;
                    firts = true;
                } else {
                    str_waypoints += "|via:" + latlng.latitude + "," + latlng.longitude;
                }
            }
        }


        //key
        String keyDirection = "key=" + DIRECTION_KEY_API;

        // Building the parameters to the web service
//        String parameters = str_origin+"&"+str_dest+"&"+sensor;
        String parameters = str_origin + "&" + str_dest + "&" + str_waypoints + "&" + keyDirection;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;

    }
}
