package project.com.vehiclessharing.model;

import com.google.android.gms.maps.model.LatLng;

import java.security.Timestamp;

/**
 * Created by Hihihehe on 5/13/2017.
 */

public class RequestFromNeeder {
    private String userId;
    private LatLng sourceLocation;
    private LatLng destinationLocation;
    private String timeStart;
    private String dateStart;

    public RequestFromNeeder() {
    }

    public RequestFromNeeder(String userId, LatLng sourceLocation, LatLng destinationLocation, String timeStart, String dateStart) {
        this.userId = userId;
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        this.timeStart = timeStart;
        this.dateStart = dateStart;
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

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }
}
