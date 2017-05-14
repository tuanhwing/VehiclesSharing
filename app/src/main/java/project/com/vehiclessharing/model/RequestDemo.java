package project.com.vehiclessharing.model;

/**
 * Created by Tuan on 13/05/2017.
 */

public class RequestDemo {
    private String graberId;
    private String graberDiviceId;
    private LocationRequest locationRequest;

    public RequestDemo() {
    }

    public RequestDemo(String graberId, String graberDiviceId, LocationRequest locationRequest) {
        this.graberId = graberId;
        this.graberDiviceId = graberDiviceId;
        this.locationRequest = locationRequest;
    }

    public String getGraberId() {
        return graberId;
    }

    public void setGraberId(String graberId) {
        this.graberId = graberId;
    }

    public String getGraberDiviceId() {
        return graberDiviceId;
    }

    public void setGraberDiviceId(String graberDiviceId) {
        this.graberDiviceId = graberDiviceId;
    }

    public LocationRequest getLocationRequest() {
        return locationRequest;
    }

    public void setLocationRequest(LocationRequest locationRequest) {
        this.locationRequest = locationRequest;
    }
}
