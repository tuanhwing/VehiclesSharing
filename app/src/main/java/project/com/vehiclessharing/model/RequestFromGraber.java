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
    private LatLngAddress sourceLocation;
    private LatLngAddress destinationLocation;
    private String vehicleType;

    public RequestFromGraber() {
    }

    public RequestFromGraber(String userId, LatLngAddress sourceLocation, LatLngAddress destinationLocation, String vehicleType) {

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

    public LatLngAddress getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(LatLngAddress sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public LatLngAddress getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(LatLngAddress destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId",userId);
        result.put("sourceLocation",sourceLocation);
        result.put("destinationLocation",destinationLocation);
        result.put("vehicleType",vehicleType);
        return result;
    }
}
