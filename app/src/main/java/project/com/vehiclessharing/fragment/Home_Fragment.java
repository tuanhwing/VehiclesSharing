package project.com.vehiclessharing.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import project.com.vehiclessharing.R;

/**
 * Created by Tuan on 14/03/2017.
 */

public class Home_Fragment extends Fragment {



    private static View view;


    public Home_Fragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_layout, container, false);
        addControls();
        addEvents();
        return view;
    }

    private void addEvents() {


    }

    private void addControls() {

    }


}
