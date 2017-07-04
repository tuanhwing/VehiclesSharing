package project.com.vehiclessharing.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.activity.HistoryActivity;
import project.com.vehiclessharing.activity.MainActivity;
import project.com.vehiclessharing.adapter.HistoryItemAdapter;

import project.com.vehiclessharing.model.HistoryOfGraber;


/**
 * Created by Hihihehe on 6/10/2017.
 */

public class HistoryOfGraber_Fragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private View view;
    private TextView txtTitle;

    ListView lvHistory;
    ArrayList<HistoryOfGraber> arrNeeder;
    HistoryItemAdapter adapterHistory;

    private static Activity mContext;
    private DatabaseReference mDatabase;
    private ChildEventListener childEventListener;
    private String mUserId;
    public HistoryOfGraber_Fragment () {
        /*Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;*/
    }

    private int mPage;

    public static HistoryOfGraber_Fragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        HistoryOfGraber_Fragment fragment = new HistoryOfGraber_Fragment();
        fragment.setArguments(args);
        mContext= HistoryActivity.activity;
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        view = inflater.inflate(R.layout.history_of_graber, container, false);
        addControls();
        addEvents();


        return view;
    }

    private void addEvents() {

    }

    private void addControls() {
        mUserId= MainActivity.mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        lvHistory= (ListView) view.findViewById(R.id.lvNeederTogether);

        arrNeeder=new ArrayList<>();
        //arrNeeder.add(new HistoryOfGraber("ECnPCbv4O4fncadnRgvzdpdLAVv1","Thủ Đức","Quận 3","10/09/2017",5,"good"));
        adapterHistory=new HistoryItemAdapter(mContext,R.layout.item_history,arrNeeder);
        lvHistory.setAdapter(adapterHistory);
        adapterHistory.notifyDataSetChanged();

        mDatabase=mDatabase.child("history_of_graber").child(mUserId);
        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("gethistory",String.valueOf(dataSnapshot.getValue()));
                HistoryOfGraber history=dataSnapshot.getValue(HistoryOfGraber.class);
                    arrNeeder.add(history);
                Log.e("lvhistory",String.valueOf(arrNeeder));
                adapterHistory.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addChildEventListener(childEventListener);

    }

}
