package project.com.vehiclessharing.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import project.com.vehiclessharing.R;

/**
 * Created by Tuan on 14/03/2017.
 */

public class Home_Fragment extends Fragment implements OnMapReadyCallback {



    private static View view;
    private GoogleMap map;


    public Home_Fragment(){

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        ((SupportMapFragment)fm.findFragmentById(R.id.map)).getMapAsync(this);

//        runtime_permission();
    }

    private boolean runtime_permission() {
//        if(Build.VERSION.SDK_INT > 25 && ContextCompat.checkSelfPermission(t, android.Manifest.permission.ACCESS_FINE_LOCATION)){
//            return true;
//        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_layout, container, false);
        addControls();
        addEvents();
        return view;
    }

    private void addEvents() {


    }

    private void addControls() {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng marker = new LatLng(10.8819912,106.780436);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker,15));

        map.addMarker(new MarkerOptions().title("KTX khu B").position(marker));
    }
}
