package project.com.vehiclessharing.model;

/**
 * Created by Tuan on 13/05/2017.
 */

public class RequestDemoFirebase {
    String id;
    RequestDemo requestDemo;

    public RequestDemoFirebase() {
    }

    public RequestDemoFirebase(String id, RequestDemo requestDemo) {
        this.id = id;
        this.requestDemo = requestDemo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RequestDemo getRequestDemo() {
        return requestDemo;
    }

    public void setRequestDemo(RequestDemo requestDemo) {
        this.requestDemo = requestDemo;
    }
}
