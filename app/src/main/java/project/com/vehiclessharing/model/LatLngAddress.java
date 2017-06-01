package project.com.vehiclessharing.model;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Hihihehe on 5/30/2017.
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

    public double getLatidude() {
        return latitude;
    }

    public void setLatidude(double latidude) {
        this.latitude = latidude;
    }

    public double getLongtitude() {
        return longitude;
    }

    public void setLongtitude(double longtitude) {
        this.longitude = longtitude;
    }

    public static LatLngAddress getLatLongSourceFromFirebase(DataSnapshot snapshot)
    {
        DataSnapshot sourceSnap=snapshot.child("sourceLocation");
        Double sourceLatitude= (Double) sourceSnap.child("latitude").getValue();
        Double sourceLongtitude= (Double) sourceSnap.child("longitude").getValue();
        LatLngAddress lngAddress;
        return lngAddress=new LatLngAddress(sourceLatitude,sourceLongtitude);
    }
    public static LatLngAddress getLatLongDesFromFirebase(DataSnapshot snapshot)
    {
        DataSnapshot des=snapshot.child("destinationLocation");
        Double desLatitude= (Double) des.child("latitude").getValue();
        Double desLongitude= (Double) des.child("longitude").getValue();
        LatLngAddress lngAddress;
        return lngAddress=new LatLngAddress(desLatitude,desLongitude);
    }
}
