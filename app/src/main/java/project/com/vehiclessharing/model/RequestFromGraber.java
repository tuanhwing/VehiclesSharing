package project.com.vehiclessharing.model;

import android.location.*;
import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by Hihihehe on 5/13/2017.
 */

public class RequestFromGraber {
    private String userId;
    private LatLng sourceLocation;
    private LatLng destinationLocation;
    private String vehicleType;



    public RequestFromGraber(String userId, LatLng sourceLocation, LatLng destinationLocation, String vehicleType) {
        this.userId = userId;
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        this.vehicleType = vehicleType;

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LatLng getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(LatLng sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public LatLng getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(LatLng destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

}
