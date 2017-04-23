package project.com.vehiclessharing.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.constant.Utils;
import project.com.vehiclessharing.fragment.Login_Fragment;
import project.com.vehiclessharing.model.User;
import project.com.vehiclessharing.service.TrackGPSService;
import project.com.vehiclessharing.sqlite.DatabaseHelper;
import project.com.vehiclessharing.utils.ReadTask;

import static project.com.vehiclessharing.R.id.map;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener, OnMapReadyCallback{

    private NavigationView navigationView = null;
    private Toolbar toolbar = null;
    private View viewHeader = null; // View header
    private TextView txtFullName,txtEmail;
    private FirebaseUser mUser; //CurrentUser
    public static ImageView imgUser; // Avatar of user
    public static Bitmap bmImgUser = null; // Bitmap of avatar
    public static User userCurrent;
    private static int CONTROLL_ON = 1;//Controll to on Locationchanged
    private static int CONTROLL_OFF = -1;//Controll to off Locationchanged
    private FloatingActionButton fab; // button fab action
    public static int loginWith; //Determine user authen email/facebook/google
    private DatabaseReference mUserReference;//Instance database firebase
    private ValueEventListener mUserListener;//Instance to Listener value events information user change

    public static GoogleMap mGoogleMap = null;//Instance google map API
    GoogleApiClient mGoogleApiClient;//Instance googleAPIclient
    private static TrackGPSService trackgps;

    LatLng latLng;//Location current
    Marker currLocationMarker;//Marker current location

    final private static int REQ_PERMISSION = 20;//Value request permission
    private static String DIRECTION_KEY_API = "AIzaSyAGjxiNRAHypiFYNCN-qcmUgoejyZPtS9c";



//    private static FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //set fragment initially
//        fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.frameContainer, new Home_Fragment(), Utils.Home_Fragment).commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // User signed in by account Email/Facebook/Google
        for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if(user.getProviderId().equals(Utils.Email_Signin)){
                loginWith = 0;
            } else if(user.getProviderId().equals(Utils.Facebook_Signin)){
                loginWith = 1;
            } else if(user.getProviderId().equals(Utils.Google_Signin)){
                loginWith = 2;
            }
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);


        addControls();
        addEvents();





    }

    private void addEvents() {
//        txtFullName.setText(mUser.getDisplayName());
//        txtEmail.setText(mUser.getEmail());
        //downloadInformationUser();
        fab.setOnClickListener(this);
//        updateUIHeader(loginWith);//Update information user into header layout
//        //[Start]Listen for value events
//        mUserListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d("Demo","0");
////                UserAddress address = dataSnapshot.getValue(UserAddress.class);
////                String name = dataSnapshot.getValue().toString();
//                user = dataSnapshot.getValue(User.class);
//                Log.d("Demo",user.getEmail());
//                Log.d("Demo",user.getFullName());
//                Log.d("Demo",user.getAddress().getCountry());
//                Log.d("Demo",user.getAddress().getDistrict());
//                Log.d("Demo",user.getImage());
//                Log.d("Demo",user.getPhoneNumber());
//                Log.d("Demo","1");
//                txtFullName.setText(user.getFullName());
//                txtEmail.setText(user.getEmail());
//                Log.d("Demo","3");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w("Warning", "loadPost:onCancelled", databaseError.toException());
//                // [START_EXCLUDE]
//                Toast.makeText(getApplicationContext(), "Failed to load post.",
//                        Toast.LENGTH_SHORT).show();
//                // [END_EXCLUDE]
//            }
//        };
//        mUserReference.addValueEventListener(mUserListener);
//        //[End]Listen for value events


    }

    private void addControls() {


        mUser = FirebaseAuth.getInstance().getCurrentUser();//Get currentuser
        userCurrent = getUserCurrentSQLite(mUser.getUid());//Get information user from SQlite

        viewHeader = navigationView.getHeaderView(0);
        txtEmail = (TextView) viewHeader.findViewById(R.id.txtEmail);
        txtFullName = (TextView) viewHeader.findViewById(R.id.txtFullName);
        imgUser = (ImageView) viewHeader.findViewById(R.id.imgUser);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid());//Instance reference database firebase
        Log.d("Demo",mUser.getUid());
//        Log.d("UPLOADAAAA", String.valueOf(mUser.getPhotoUrl()));
//
//        //Storage iamge in Firebase
//        StorageReference fileRef =  FirebaseStorage.getInstance().getReference().child("avatar").child(mUser.getUid()+".jpg");
//        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.temp);
//        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Log.d("UPLOADDDDAAAAA",taskSnapshot.getDownloadUrl().toString());
//                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                        .setPhotoUri(taskSnapshot.getDownloadUrl())
//                        .build();
//                mUser.updateProfile(profileUpdates)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d("UPLOADDDDAAAAA", "User URL updated.");
//                                }
//                            }
//                        });
//            }
//        });
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));

        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_signout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                logout();
                break;
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        if(loginWith == 1)
            //Sign out Facebook
            LoginManager.getInstance().logOut();
        else
            //Sign out Google plus
            Auth.GoogleSignInApi.signOut(Login_Fragment.session.mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {

                        }
                    });
        startActivity(new Intent(HomeActivity.this,MainActivity.class));
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
//        makeMaker(new LatLng(10.8719808, 106.790409), "Nong Lam University");

        trackgps = new TrackGPSService(HomeActivity.this);
    }

    /**
     * get Url to request the Google Directions API
     * @param origin start point location
     * @param dest destination point location
     * @return
     */
    private String getMapsApiDirectionsUrl(LatLng origin, LatLng dest, ArrayList<LatLng> waypoints) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        //Waypoints
        String str_waypoints = "waypoints=";
        boolean firts = false;
        for (LatLng latlng : waypoints) {
            if (!firts) {
                str_waypoints += "via:" + latlng.latitude + "," + latlng.longitude;
                firts = true;
            } else {
                str_waypoints += "|via:" + latlng.latitude + "," + latlng.longitude;
            }
        }

        //key
        String keyDirection = "key=" + DIRECTION_KEY_API;

        // Building the parameters to the web service
//        String parameters = str_origin+"&"+str_dest+"&"+sensor;
        String parameters = str_origin + "&" + str_dest + "&" + str_waypoints + "&" + keyDirection;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;

    }

    /**
     * Draw road between 2 location in google map
     */
    private void    drawroadBetween2Location(LatLng latLng1, LatLng latLng2) {
        ArrayList<LatLng> temp = new ArrayList<LatLng>();
        temp.add(new LatLng(10.8719808, 106.790409));
        String url = getMapsApiDirectionsUrl(latLng1, latLng2, temp);

        Log.d("onMapClick", url.toString());
        ReadTask downloadTask = new ReadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
        Log.d("Log12/4", "12");

        //To calculate distance between points
//        float[] results = new float[1];
//        Location.distanceBetween(latitude, longitude,
//                10.882323, 106.782625,
//                results);
//        Log.d("onMapClick",String.valueOf(results));
    }

    /**
     * Make a Maker in google map
     * @param location location maker
     * @param title title maker
     */
    private void makeMaker(LatLng location, String title) {
        LatLng maker = new LatLng(location.latitude, location.longitude);
        mGoogleMap.addMarker(new MarkerOptions().title(title).position(maker));
    }


    /**
     * Check permission Location service
     * @return
     */
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQ_PERMISSION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQ_PERMISSION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("Permission", "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode ) {
            case REQ_PERMISSION: {
                if ( grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    // permission was granted, yay!
                    if (ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mGoogleMap.setMyLocationEnabled(true);
                        trackgps.controllonLocationChanged(CONTROLL_ON);

                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    //Check GPS is enable everytime
    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //Do your stuff on GPS status change
                Toast.makeText(HomeActivity.this,"GPS enable!",Toast.LENGTH_LONG).show();
            }
            else  Toast.makeText(HomeActivity.this,"GPS disable!",Toast.LENGTH_LONG).show();
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        updateUIHeader(loginWith);//Update information user into header layout
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unregister receiver on destroy
        if (gpsReceiver != null)
            unregisterReceiver(gpsReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(gpsReceiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));//Register broadcast r
    }

    /**
     * Get data current user from SQlite
     * @param userId
     * @return object User
     */
    private User getUserCurrentSQLite(String userId){
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        return db.getUser(userId);
    }

    /**
     * Update information user into header layout
     * @param loginWith
     */
    private void updateUIHeader(int loginWith){
        String mfullName = null;
        String memail = null;
        if(loginWith == 0){
            mfullName = "LOL";
            memail ="LOL";
//            mfullName = userCurrent.getFullName();
//            memail = userCurrent.getEmail();
//            if(userCurrent.getImage().equals("")){
//                if(userCurrent.getSex().equals("Male"))
//                    imgUser.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.image_male));
//                else imgUser.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.image_female));
//            }
        }
        else {
            mfullName = mUser.getDisplayName();
            memail = mUser.getEmail();
        }

        txtEmail.setText(memail);
        txtFullName.setText(mfullName);


    }

}
