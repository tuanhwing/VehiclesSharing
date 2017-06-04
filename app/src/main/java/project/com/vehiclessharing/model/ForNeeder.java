package project.com.vehiclessharing.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import project.com.vehiclessharing.activity.MainActivity;
import project.com.vehiclessharing.asynctask.IconMarkerAsync;
import project.com.vehiclessharing.utils.UserCallback;

/**
 * Created by Hihihehe on 5/23/2017.
 */

public class ForNeeder {
    private static ForNeeder instance;
    private static DatabaseReference mDatabase, mDatabseUser;
    private ValueEventListener requestNeederListener;
    private Context mContext;
    private static GoogleMap mGoogleMap;
    public static ChildEventListener childEventListenerForGraber;
    private HashMap<String, Marker> markerHashMap = new HashMap<>();

    public static ForNeeder getInstance() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabseUser = FirebaseDatabase.getInstance().getReference();
        mGoogleMap = MainActivity.mGoogleMap;
        if (instance == null) {
            instance = new ForNeeder();
        }
        return instance;
    }


    public void getAllGraberNear(Context context, final String userId) {

        mContext = context;
        final List<RequestFromGraber> list = new ArrayList<>();

        //mDatabase = mDatabase.child("theneeder_near_graber").child(userId);
        mDatabase = mDatabase.child("requestfromgraber");
        childEventListenerForGraber = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("onchildadd", String.valueOf(dataSnapshot.getValue()));

                final RequestFromGraber graber = dataSnapshot.getValue(RequestFromGraber.class);
                final String key = dataSnapshot.getKey();
                if (!userId.equals(graber.getUserId())) {
                    final List<User> userGraber = new ArrayList<User>();
                    final UserCallback userCallback = new UserCallback() {
                        @Override
                        public void onSuccess(User user) {
                            Log.e("usercallback success", String.valueOf(user.getImage()));
                            Marker marker = makeMarkerForGraber(graber, user);
                            markerHashMap.put(key, marker);
                        }

                        @Override
                        public void onError(DatabaseError e) {

                        }
                    };
                    UserInfomation userInfomation = new UserInfomation();
                    userInfomation.getInfoUserById(graber.getUserId(), userCallback);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("child_listener_changed", String.valueOf(dataSnapshot.getValue()));
                Marker marker = markerHashMap.get(String.valueOf(dataSnapshot.getKey()));
                final RequestFromGraber graber = dataSnapshot.getValue(RequestFromGraber.class);
                if (!graber.getUserId().equals(userId)) {
                    Log.d("changChild","onchange");
                    marker.setPosition(new LatLng(graber.getSourceLocation().getLatidude()
                            , graber.getSourceLocation().getLongtitude()));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Marker marker = markerHashMap.get(String.valueOf(dataSnapshot.getKey()));
                marker.remove();
                markerHashMap.remove(String.valueOf(dataSnapshot.getKey()));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
            // mDatabase.addListenerForSingleValueEvent(requestNeederListener);
        };
        mDatabase.addChildEventListener(childEventListenerForGraber);
    }

    private Marker makeMarkerForGraber(RequestFromGraber graber, User user) {
        LatLng source = new LatLng(graber.getSourceLocation().getLatidude(), graber.getSourceLocation().getLongtitude());
        // CustomMarker iconAvatar = new CustomMarker(mContext);
        //iconAvatar.customMarkerWithAvatar(user.getImage());
        IconMarkerAsync markerAsync = new IconMarkerAsync(mContext);
        markerAsync.execute(user.getImage());
        Bitmap bitmap = null;
        try {
            bitmap = markerAsync.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Marker customMarker = mGoogleMap.addMarker(new MarkerOptions().position(source).title(user.getFullName())
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
        customMarker.setTag(graber);
        return customMarker;
    }
}
