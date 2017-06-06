package project.com.vehiclessharing.model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import project.com.vehiclessharing.utils.UserCallback;

/**
 * Created by Hihihehe on 5/28/2017.
 */

public class UserInfomation {
    private static DatabaseReference mDatabase;
    private ValueEventListener requestNeederListener;
    private User mUser=new User();
    private String urlAvatar;

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    public User getmUser() {
        return mUser;
    }

    public void setmUser(User mUser) {
        this.mUser = mUser;
    }

    public UserInfomation(ValueEventListener requestNeederListener, User mUser) {
        this.requestNeederListener = requestNeederListener;
        this.mUser = mUser;
    }

    public UserInfomation() {
    }

    /*  public static UserInfomation getInstance()
            {
                return instance=new UserInfomation();
            }*/
    public void getInfoUserById(String userId, final UserCallback userCallback)
    {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            requestNeederListener=new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                   //mUser=dataSnapshot.getValue(User.class);
                    urlAvatar=dataSnapshot.child("image").getValue(String.class);
                    //Log.e("dsds",urlAvatar);
                    User user = dataSnapshot.getValue(User.class);
                    // userNeeder.add(user);
                    Log.e("imagemarker",user.getImage());
                    try {
                        userCallback.onSuccess(user);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("load_data",databaseError.getMessage());
                }
            };
        mDatabase.addListenerForSingleValueEvent(requestNeederListener);

    }
}
