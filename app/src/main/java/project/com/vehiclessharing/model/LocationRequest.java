package project.com.vehiclessharing.model;

/**
 * Created by Tuan on 13/05/2017.
 */

public class LocationRequest {
    private double locationLat;
    private double locationLong;

    public LocationRequest() {
    }

    public LocationRequest(double locationLat, double locationLong) {
        this.locationLat = locationLat;
        this.locationLong = locationLong;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
    }

    public double getLocationLong() {
        return locationLong;
    }

    public void setLocationLong(double locationLong) {
        this.locationLong = locationLong;
    }
}
