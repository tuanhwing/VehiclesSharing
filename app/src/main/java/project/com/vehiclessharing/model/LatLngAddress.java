package project.com.vehiclessharing.model;

import java.util.HashMap;

/**
 * Created by Hihihehe on 5/20/2017.
 */

public class LatLngAddress {
    private double latitude;
    private double longitude;

    public LatLngAddress() {

    }

    public LatLngAddress(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("latitude",latitude);
        result.put("longtitute",longitude);
        return result;
    }
}
