package project.com.vehiclessharing.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

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
import java.util.Map;

import project.com.vehiclessharing.activity.HomeActivity;
import project.com.vehiclessharing.custom.CustomMarker;
import project.com.vehiclessharing.utils.RequestFromGraberCallback;

/**
 * Created by Hihihehe on 5/23/2017.
 */

public class ForGraber {
    private static ForGraber instance;
    private static DatabaseReference mDatabase,mDatabseUser;
    private ValueEventListener requestNeederListener;
    private ChildEventListener requestAddNeeder;
    private RequestFromGraber graber;
    private static GoogleMap mGoogleMap;
    private Context mContext;

    public static ForGraber getInstance() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mGoogleMap = HomeActivity.mGoogleMap;
        return instance = new ForGraber();
    }

    public void getAllNeederNear(Context context, final String userId) {
        mContext=context;
        final List<RequestFromNeeder> list = new ArrayList<>();

        mDatabase = mDatabase.child("theneeder_near_graber").child(userId);
       // mDatabase=mDatabase.child("requestfromneeder")
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("onchildadd",String.valueOf(dataSnapshot.getValue()));
                RequestFromNeeder needer = dataSnapshot.getValue(RequestFromNeeder.class);

                Log.e("onchildadd_object",String.valueOf(needer.getSourceLocation().getLatidude()));


//                LatLngAddress source = LatLngAddress.getLatLongSourceFromFirebase(dataSnapshot);
//                LatLngAddress des = LatLngAddress.getLatLongDesFromFirebase(dataSnapshot);
//                needer.setSourceLocation(source);
//                needer.setDestinationLocation(des);

               /* UserInfomation userInfomation=new UserInfomation();
                userInfomation.getInfoUserById(needer.getUserId());
                String urlAvatar=userInfomation.getUrlAvatar();*/
//                String urlAvatar=FirebaseDatabase.getInstance().getReference().child("users").child(needer.getUserId()).child("image").toString();
//
//                makeMarkerForPeople(needer, urlAvatar,mContext);
               // String urlAvater=user.getImage();
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
        });
        /*requestNeederListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              //  Map<String, RequestFromNeeder> td = (HashMap<String, RequestFromNeeder>) dataSnapshot.getValue();
                // List<RequestFromNeeder> listneeder= (List<RequestFromNeeder>) td.values();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RequestFromNeeder needer = snapshot.getValue(RequestFromNeeder.class);
                    LatLngAddress source = LatLngAddress.getLatLongSourceFromFirebase(snapshot);
                    LatLngAddress des = LatLngAddress.getLatLongDesFromFirebase(snapshot);
                    needer.setSourceLocation(source);
                    needer.setDestinationLocation(des);
                    UserInfomation userInfomation=new UserInfomation();
                    userInfomation.getInfoUserById(userId);
                    User user=userInfomation.getmUser();
                    String urlAvater=user.getImage();
//                    Log.e("urlAv",urlAvater);
//                    makeMarkerForPeople(needer, urlAvater,mContext);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("load_data", databaseError.getMessage());
            }
        };
        mDatabase.addListenerForSingleValueEvent(requestNeederListener);*/
    }

    public void getInfoGraber(String uId, final RequestFromGraberCallback callback) {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("requestfromgraber").child(uId);
        mDatabase.addListenerForSingleValueEvent(requestNeederListener);
        requestNeederListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DemoLogin", String.valueOf(dataSnapshot.getValue()));
                graber = dataSnapshot.getValue(RequestFromGraber.class);
                callback.onSuccess(graber);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("load_data", databaseError.getMessage());
                callback.onError(databaseError);
            }
        };

    }

    private void makeMarkerForPeople(RequestFromNeeder needer, String urlAvatar,Context context) {
        LatLng sourceNeeder = new LatLng(needer.getSourceLocation().getLatidude(), needer.getSourceLocation().getLongtitude());
        CustomMarker iconAvatar=new CustomMarker(context);
        Bitmap bitmap=iconAvatar.customMarkerWithAvatar(urlAvatar);
        Marker customMarker = mGoogleMap.addMarker(new MarkerOptions().position(sourceNeeder).title("Needer")
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
        customMarker.setTag(needer);
    }
}
