package project.com.vehiclessharing.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import project.com.vehiclessharing.R;

/**
 * Created by Hihihehe on 6/10/2017.
 */

public class HistoryOfNeeder_Fragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private View view;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static HistoryOfNeeder_Fragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        HistoryOfNeeder_Fragment fragment = new HistoryOfNeeder_Fragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      view=inflater.inflate(R.layout.history_of_needer,container,false);
        addControls();
        addEvents();
        return view;
    }

    private void addEvents() {

    }

    private void addControls() {
    }
}
