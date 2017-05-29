package project.com.vehiclessharing.model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Hihihehe on 5/28/2017.
 */

public class UserInfomation {
    private static UserInfomation instance;
    private static DatabaseReference mDatabase;
    private ValueEventListener requestNeederListener;
    public static UserInfomation getInstance()
    {
        return instance=new UserInfomation();
    }
    public User getInfoUserById(String userId)
    {
        final User[] user = {new User()};
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
            requestNeederListener=new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user[0] =dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("load_data",databaseError.getMessage());
                }
            };
            mDatabase.addListenerForSingleValueEvent(requestNeederListener);
            return user[0];


    }
}
