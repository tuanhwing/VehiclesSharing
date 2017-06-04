package project.com.vehiclessharing.model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.com.vehiclessharing.utils.RequestFromGraberCallback;
import project.com.vehiclessharing.utils.RequestFromNeederCallback;

/**
 * Created by Hihihehe on 5/23/2017.
 */

public class ForNeeder {
    private static ForNeeder instance;
    private static DatabaseReference mDatabase;
    private ValueEventListener requestNeederListener;
    private RequestFromNeeder needer;

    public static ForNeeder getInstance()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return instance=new ForNeeder();
    }


    public void getAllFromRequestGraber()
    {

    }
    public void getInfoNeeder(String uId, final RequestFromNeederCallback callback)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("requestfromneeder").child(uId);
        requestNeederListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DemoLogin", String.valueOf(dataSnapshot.getValue()));
                needer= dataSnapshot.getValue(RequestFromNeeder.class);
                callback.onSuccess(needer);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("load_data",databaseError.getMessage());
                callback.onError(databaseError);
            }
        };
        mDatabase.addListenerForSingleValueEvent(requestNeederListener);
    }
}
