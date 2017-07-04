package project.com.vehiclessharing.utils;

import android.content.Context;
import android.graphics.Color;
import android.location.LocationManager;
import android.util.Log;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.activity.MainActivity;
import project.com.vehiclessharing.permission.CheckInternetAndLocation;

import static android.content.Context.LOCATION_SERVICE;
import static project.com.vehiclessharing.constant.Utils.TAG_ERROR_ROUTING;

/**
 * Created by Hihihehe on 6/4/2017.
 */

public class DrawRoute implements RoutingListener {
    /**
     * Draw road between 2 location in google map
     */
    private Context mContext;
    public static Polyline polylineNotCurUser;
    private GoogleMap googleMap;
    private int mSubject=0;


    public DrawRoute(Context mContext) {
        this.mContext = mContext;
        polylineNotCurUser=null;
        googleMap= MainActivity.mGoogleMap;
    }

    /**
     *
     * @param latLng1 source location
     * @param latLng2 destion location
     * @param subject with 0 is curtent user and 1 is'nt current user (graber or needer). Subject to set color for route
     */
    public void drawroadBetween2Location(LatLng latLng1, LatLng latLng2,int subject) {
        mSubject=subject;
        LocationManager lm = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        CheckInternetAndLocation check=new CheckInternetAndLocation(mContext);
        if (!check.isOnline() || !check.checkLocationPermission() ||
                !lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) return;
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(latLng1, latLng2)
                .build();
        routing.execute();
    }

    /**
     * if subject equal 0 => current user route have black color
     * if subject equal 1 => not current user route have blue color
     * route color will different between two subject
     * @param arrayList
     * @param shortestRouteIndex
     */
    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int shortestRouteIndex) {
        Polyline polyline=null;
        try {
            if (polyline != null) polyline.remove();
            PolylineOptions polyoptions = new PolylineOptions();
            polyoptions.color(Color.BLUE);
            polyoptions.width(10);
            for (int i = 0; i < arrayList.size(); i++) {
                //In case of more than 5 alternative routes
                PolylineOptions polyOptions = new PolylineOptions();
                if(mSubject==0) {
                    polyOptions.color(mContext.getResources().getColor(R.color.black));
                } else {
                    polyOptions.color(mContext.getResources().getColor(R.color.com_facebook_blue));
                }

                polyOptions.width(10 + i * 3);
                polyOptions.addAll(arrayList.get(i).getPoints());
                polyline = googleMap.addPolyline(polyOptions);
                if(mSubject!=0)
                {
                    polylineNotCurUser=polyline;
                }
                // Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + arrayList.get(i).getDistanceValue() + ": duration - " + arrayList.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG_ERROR_ROUTING, e.getMessage());
        }
    }
    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingCancelled() {

    }
}
