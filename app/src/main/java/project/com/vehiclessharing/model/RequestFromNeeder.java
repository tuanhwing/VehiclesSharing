package project.com.vehiclessharing.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by Hihihehe on 5/13/2017.
 */

public class RequestFromNeeder {
    private String userId;
    /*private LatLng sourceLocation;
    private LatLng destinationLocation;*/
    private LatLngAddress sourceLocation;
    private LatLngAddress destinationLocation;
    private String timeStart;
    private String dateStart;

    public RequestFromNeeder() {
    }

    public RequestFromNeeder(String userId, LatLngAddress sourceLocation, LatLngAddress destinationLocation, String timeStart, String dateStart) {
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

//    public HashMap<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("userId",userId);
//        result.put("sourceLocation",sourceLocation);
//        result.put("destinationLocation",destinationLocation);
//        result.put("timeStart",timeStart);
//        result.put("dateStart",dateStart);
//        return result;
//    }
}
