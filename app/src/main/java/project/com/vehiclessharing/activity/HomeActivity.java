package project.com.vehiclessharing.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.application.ApplicationController;
import project.com.vehiclessharing.constant.Utils;
import project.com.vehiclessharing.fragment.AddRequestFromGraber_Fragment;
import project.com.vehiclessharing.fragment.AddRequestFromNeeder_Fragment;
import project.com.vehiclessharing.fragment.Login_Fragment;
import project.com.vehiclessharing.model.LatLngAddress;
import project.com.vehiclessharing.model.RequestFromGraber;
import project.com.vehiclessharing.model.User;
import project.com.vehiclessharing.model.UserOnDevice;
import project.com.vehiclessharing.service.TrackGPSService;
import project.com.vehiclessharing.sqlite.RealmDatabase;
import project.com.vehiclessharing.utils.LocationCallback;

import static project.com.vehiclessharing.R.id.map;
import static project.com.vehiclessharing.constant.Utils.TAG_ERROR_ROUTING;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener, OnMapReadyCallback, RoutingListener{

    private NavigationView navigationView = null;
    private Toolbar toolbar = null;
    private View viewHeader = null; // View header
    private TextView txtFullName,txtEmail;
    public static FirebaseUser mUser; //CurrentUser
    public static ImageView imgUser; // Avatar of user
    public static ProgressBar progressBar;
    public static Bitmap bmImgUser = null; // Bitmap of avatar
    private static int CONTROLL_ON = 1;//Controll to on Locationchanged
    private static int CONTROLL_OFF = -1;//Controll to off Locationchanged
    public static int loginWith; //Determine user authen email/facebook/google

    public static GoogleMap mGoogleMap = null;//Instance google map API
    public static Polyline polyline = null;//Instance
    private static TrackGPSService trackgps;
    private DatabaseReference mDatabase;

    private FloatingActionButton btnFindPeople;
    private FloatingActionButton btnFindVihecle;

    private ValueEventListener requestNeederListener;
    private DatabaseReference requestNeederRef;
    private String mRequestKey;
    private ArrayList<RequestDemo> arrRequest;


    public static UserOnDevice currentUser;//Instace current user logined
    final private static int REQ_PERMISSION = 20;//Value request permission
    private static String DIRECTION_KEY_API = "AIzaSyAGjxiNRAHypiFYNCN-qcmUgoejyZPtS9c";


    private FloatingActionButton btnFindPeople; // button fab action
    private FloatingActionButton btnFindVehicles;
    private static  DialogFragment dialogFragment;// Instance fragmentManager to switch fragment
    private DatabaseReference mDatabase;


//    private static FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
      //  fragmentManager = getSupportFragmentManager();
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
        btnFindPeople.setOnClickListener(this);
        btnFindVihecle.setOnClickListener(this);

        final String[] dialogTitle =new String[1];
        btnFindPeople.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //addRequestFromGraber();
                dialogTitle[0] ="If you have avehicle and you want find a people together you can fill out the form to find it";
                dialogFragment = AddRequestFromGraber_Fragment.newIstance(dialogTitle[0]);
                dialogFragment.show(getFragmentManager(),"From Grabber");
                //fragmentManager.beginTransaction().add(R.id.addRequestFromGraber,fragmentManager).commit();
                return false;

            }
        });
        btnFindVehicles.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //addRequestFromNeeder();
                dialogTitle[0]="";
                dialogFragment=new AddRequestFromNeeder_Fragment();
                dialogFragment.show(getFragmentManager(),"From Needer");
                return false;
            }
        });
        btnFindVehicles.setOnClickListener(this);
    }


    private void addControls() {

        btnFindPeople=(FloatingActionButton) findViewById(R.id.btnFindPeople);
        btnFindVehicles=(FloatingActionButton) findViewById(R.id.btnFindVehicle);

        mUser = FirebaseAuth.getInstance().getCurrentUser();//Get currentuser
        //[Start]Send verification
        mUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d("send_verification", "Email sent.");
                        } else {
                            Log.d("send_verification1", "Email sent unsuccessful!");
                            Log.d("send_verification1", String.valueOf(task.getException().getMessage()));
                        }
                    }
                });
        //[END]Send verification
        currentUser = RealmDatabase.getCurrentUser(mUser.getUid());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        arrRequest = new ArrayList<RequestDemo>();

        viewHeader = navigationView.getHeaderView(0);
        txtEmail = (TextView) viewHeader.findViewById(R.id.txtEmail);
        txtFullName = (TextView) viewHeader.findViewById(R.id.txtFullName);
        imgUser = (ImageView) viewHeader.findViewById(R.id.imgUser);
        progressBar = (ProgressBar) viewHeader.findViewById(R.id.loading_progress_img);
        trackgps = new TrackGPSService(HomeActivity.this);

        btnFindVihecle = (FloatingActionButton) findViewById(R.id.btn_find_vehicle);
        btnFindPeople = (FloatingActionButton) findViewById(R.id.btn_find_people);

        //Listener request of vehicle-sharing from database Firebase
        requestNeederListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    for(DataSnapshot temp : dataSnapshot.getChildren()) {
                        RequestDemo requestDemo = temp.getValue(RequestDemo.class);
                        arrRequest.add(requestDemo);
                    }
                    mGoogleMap.clear();
                    for(RequestDemo a : arrRequest){
                        makeMaker(new LatLng(a.getLocationRequest().getLocationLat(),a.getLocationRequest().getLocationLong()),
                                a.getGraberId());
                    }
                } catch (Exception e){
                    Log.d("database_firebaseaaaaa",String.valueOf(e.getMessage()));
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
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

        if (id == R.id.nav_home) {
            // Handle the camera action
//            fragmentManager.beginTransaction().replace(R.id.frameContainer, new Home_Fragment(), Utils.Home_Fragment).commit();
        } else if (id == R.id.nav_profile) {
         //use activity
            startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
          //  fragmentManager.beginTransaction().replace(R.id.frameContainer, new Profile_Fragment(), Utils.Profile_Fragment).commit();
        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_about) {
           // fab.callOnClick();
        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFindPeople:
                //
                //displayMenuHowFindPeople();
                break;
            case R.id.btnFindVehicle:
                //
               displayAllVehicleOnMap();
                break;
        }
    }

    private void displayAllVehicleOnMap() {
        Toast.makeText(this, "on disPlay all vehicle into map", Toast.LENGTH_SHORT).show();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("requestfromgraber");
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // Log.d("load_data","datachanged");
               // makeMaker(new LatLng(10.8719808, 106.790409), "Nong Lam University");
               Log.d("DemoLogin", String.valueOf(dataSnapshot.getValue()));

               Map<String, RequestFromGraber> td = (HashMap<String,RequestFromGraber>) dataSnapshot.getValue();
               //List<RequestFromGraber> fromGraber = new ArrayList<>(td.values());

              //  Toast.makeText(HomeActivity.this, fromGraber.get(0).getUserId(), Toast.LENGTH_SHORT).show();
                /*for (RequestFromGraber fromAGraber:fromGraber) {


                    Toast.makeText(HomeActivity.this, "sfsdfdsf", Toast.LENGTH_SHORT).show();fromAGraber.getUserId();
                }*/
               /* LatLngAddress curLocation=fromGraber.getSourceLocation();
                LatLng latLng=new LatLng(curLocation.getLatitude(),curLocation.getLongitude());

               Toast.makeText(HomeActivity.this, "Location"+latLng.latitude+","+latLng.longitude, Toast.LENGTH_SHORT).show();
                */ // makeMaker(latLng,"khanhhoi");

               // makeMaker(new Latq                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    Lng(10.8719808, 106.790409), "Nong Lam University");
                //storageProfileOnDevice(user,userId);//Save profile user on realm
                // ...
                //switchActivity();//go to the Home Activity
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("load_data",databaseError.getMessage());
            }
        };
        mDatabase.addListenerForSingleValueEvent(valueEventListener);
    }


    private void logout() {
        FirebaseAuth.getInstance().signOut();
        if(loginWith == 1)
            //Sign out Facebook
            LoginManager.getInstance().logOut();
        if(loginWith == 2)
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
        btnFindVehicles.setVisibility(View.VISIBLE);
        btnFindPeople.setVisibility(View.VISIBLE);
        requestNeederRef = FirebaseDatabase.getInstance().getReference().child("requests_needer");
        requestNeederRef.addValueEventListener(requestNeederListener);

//       makeMaker(new LatLng(10.8719808, 106.790409), "Nong Lam University");

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
     * Make a Maker in google map
     * @param location location maker
     * @param title title maker
     */
    private void makeMaker(LatLng location, String title) {
        LatLng latLng = new LatLng(location.latitude, location.longitude);
        Marker  marker = mGoogleMap.addMarker(new MarkerOptions().title(title).position(latLng));
        marker.setTag(title);
    }


    /**
     * Check permission Location service
     * @return true - can
     */
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

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
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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

    //Check GPS + Internet is enable everytime
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            if ((lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) &&
                    isOnline()) {
                //Do your stuff on GPS status change
                Toast.makeText(HomeActivity.this,"GPS + Internet enable!",Toast.LENGTH_LONG).show();
            }
            else {
                if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    mGoogleMap.setMyLocationEnabled(true);
                    Toast.makeText(HomeActivity.this,"GPS enable!",Toast.LENGTH_LONG).show();
                }
                else  {
                    mGoogleMap.setMyLocationEnabled(false);
                    Toast.makeText(HomeActivity.this,"GPS disable!",Toast.LENGTH_LONG).show();
                }
                if(isOnline()) Toast.makeText(HomeActivity.this,"Internet enable!",Toast.LENGTH_LONG).show();
                else Toast.makeText(HomeActivity.this,"Internet disable!",Toast.LENGTH_LONG).show();
            }


        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        updateUIHeader(loginWith);//Update information user into header layout
//        trackgps.getCurrentLocation(new LocationCallback() {
//            @Override
//            public void onSuccess() {
//                drawroadBetween2Location(new LatLng(TrackGPSService.mLocation.getLatitude(),
//                        TrackGPSService.mLocation.getLongitude()),new LatLng(10.8719808,106.790409));
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Log.e(TAG_ERROR_ROUTING, e.getMessage());
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unregister receiver on destroy
        trackgps.controllonLocationChanged(CONTROLL_OFF);
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Register broadcast reciver Location/Network state
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.location.PROVIDERS_CHANGED");
        registerReceiver(mReceiver, filter);
    }

    /**
     * Update information user into header layout
     * @param loginWith
     */
    private void updateUIHeader(int loginWith){
        String mfullName = "";
        String memail = "";
        String url = "";
        if(loginWith == 0){
            mfullName = currentUser.getUser().getFullName();
            memail = currentUser.getUser().getEmail();
            url = String.valueOf(currentUser.getUser().getImage());//url avatar user
        }
        else {
            mfullName = mUser.getDisplayName();
            memail = mUser.getEmail();
            url = String.valueOf(mUser.getPhotoUrl());
        }

        txtEmail.setText(memail);
        txtFullName.setText(mfullName);


        if(url.equals("null") || url.isEmpty()){
            imgUser.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.temp));
        } else {
            if(isOnline()) {
                progressBar.setVisibility(View.VISIBLE);
                Picasso.with(HomeActivity.this).load(url).into(imgUser, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(HomeActivity.this,"Error load image",Toast.LENGTH_SHORT).show();
                    }
                });
            } else Picasso.with(getApplicationContext())
                    .load(url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(imgUser);
        }
    }

    /**
     * Internet is avaibalility
     * @return true if can access internet
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    /**
     * Draw road between 2 location in google map
     */
    private void drawroadBetween2Location(LatLng latLng1, LatLng latLng2) {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!isOnline() || !checkLocationPermission() ||
                !lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) return;
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(latLng1, latLng2)
                .build();
        routing.execute();
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int shortestRouteIndex) {
        try {
            if(polyline != null) polyline.remove();
            PolylineOptions polyoptions = new PolylineOptions();
            polyoptions.color(Color.BLUE);
            polyoptions.width(10);
            for (int i = 0; i <arrayList.size(); i++) {

                //In case of more than 5 alternative routes
                PolylineOptions polyOptions = new PolylineOptions();
                polyOptions.color(getResources().getColor(R.color.black));
                polyOptions.width(10 + i * 3);
                polyOptions.addAll(arrayList.get(i).getPoints());
                polyline = mGoogleMap.addPolyline(polyOptions);
                Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ arrayList.get(i).getDistanceValue()+": duration - "+ arrayList.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Log.e(TAG_ERROR_ROUTING,e.getMessage());
        }
    }

    @Override
    public void onRoutingCancelled() {

    }



}
