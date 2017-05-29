package project.com.vehiclessharing.model;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.com.vehiclessharing.utils.RequestFromGraberCallback;

/**
 * Created by Hihihehe on 5/23/2017.
 */

public class ForGraber {
    private static ForGraber instance;
    private static DatabaseReference mDatabase;
    private ValueEventListener requestNeederListener;
    private RequestFromGraber graber;

    public static ForGraber getInstance()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return instance=new ForGraber();
    }

    public List<RequestFromNeeder> getAllNeederNear(String userId)
    {
        final List<RequestFromNeeder> list=new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("theneeder_near_graber").child(userId);
       /* ChildEventListener childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                RequestFromNeeder requestFromNeeder=dataSnapshot.getValue(RequestFromNeeder.class);
                list.add(requestFromNeeder);
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
        };*/
        requestNeederListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, RequestFromNeeder> td = (HashMap<String,RequestFromNeeder>) dataSnapshot.getValue();

                List<RequestFromNeeder> values = new ArrayList<>(td.values());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("load_data",databaseError.getMessage());
            }
        };
        mDatabase.addListenerForSingleValueEvent(requestNeederListener);
        return list;
    }
    public void getInfoGraber(String uId, final RequestFromGraberCallback callback)
    {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("requestfromgraber").child(uId);
        requestNeederListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DemoLogin", String.valueOf(dataSnapshot.getValue()));
                graber= dataSnapshot.getValue(RequestFromGraber.class);
                callback.onSuccess(graber);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("load_data",databaseError.getMessage());
                callback.onError(databaseError);
            }
        };
        mDatabase.addListenerForSingleValueEvent(requestNeederListener);
      //  return graber;
    }
}
