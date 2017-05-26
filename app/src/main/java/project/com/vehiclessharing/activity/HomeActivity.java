package project.com.vehiclessharing.activity;

import android.Manifest;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.asynctask.ImageTask;
import project.com.vehiclessharing.constant.Utils;
import project.com.vehiclessharing.database.RealmDatabase;
import project.com.vehiclessharing.fragment.AddRequestFromGraber_Fragment;
import project.com.vehiclessharing.fragment.AddRequestFromNeeder_Fragment;
import project.com.vehiclessharing.fragment.Login_Fragment;
import project.com.vehiclessharing.model.AboutPlace;
import project.com.vehiclessharing.model.ForGraber;
import project.com.vehiclessharing.model.ForNeeder;
import project.com.vehiclessharing.model.RequestFromGraber;
import project.com.vehiclessharing.model.RequestFromNeeder;
import project.com.vehiclessharing.model.User;
import project.com.vehiclessharing.model.UserOnDevice;
import project.com.vehiclessharing.service.TrackGPSService;

import project.com.vehiclessharing.utils.RequestFromGraberCallback;
import project.com.vehiclessharing.utils.RequestFromNeederCallback;

import static project.com.vehiclessharing.R.id.map;
import static project.com.vehiclessharing.R.id.thing_proto;
import static project.com.vehiclessharing.constant.Utils.TAG_ERROR_ROUTING;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        OnMapReadyCallback, RoutingListener, GoogleMap.OnMarkerClickListener,
        AddRequestFromNeeder_Fragment.RequestDataFromNeeder, AddRequestFromGraber_Fragment.RequestDataFromGraber {

    private NavigationView navigationView = null;
    private Toolbar toolbar = null;
    private View viewHeader = null; // View header
    private TextView txtFullName, txtEmail;
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

    private ValueEventListener requestNeederListener;
    private DatabaseReference requestNeederRef;
    private String mRequestKey;
    private ArrayList<RequestFromGraber> arrRequest;
    //private ArrayList<RequestDemo> arrRequestDemo;


    public static UserOnDevice currentUser;//Instace current user logined
    final private static int REQ_PERMISSION = 20;//Value request permission
    private static String DIRECTION_KEY_API = "AIzaSyAGjxiNRAHypiFYNCN-qcmUgoejyZPtS9c";


    private FloatingActionButton btnFindPeople, btnFindVehicles, btnCancelRequest, btnRestartRequest; // button fab action
    //private FloatingActionButton btnFindVehicles;
   // Instance fragmentManager to switch fragment
    private DatabaseReference mDatabase;
    private int checkOnScreen;


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
        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals(Utils.Email_Signin)) {
                loginWith = 0;
            } else if (user.getProviderId().equals(Utils.Facebook_Signin)) {
                loginWith = 1;
            } else if (user.getProviderId().equals(Utils.Google_Signin)) {
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
        btnFindVehicles.setOnClickListener(this);
        btnFindPeople.setOnClickListener(this);
        btnCancelRequest.setOnClickListener(this);
        btnRestartRequest.setOnClickListener(this);
    }

    private void checkOnScreen() {
        if (checkOnScreen == 0 || checkOnScreen == 1) {
            mGoogleMap.clear();
            Toast.makeText(this, "All Vehicle", Toast.LENGTH_SHORT).show();
            //AddRequestFromNeeder_Fragment.RequestDataFromNeeder requestDataFromNeeder=getRequestFromNeeder();
            /*ForNeeder.getInstance().getInfoNeeder(mUser.getUid(), new RequestFromNeederCallback() {
                @Override
                public void onSuccess(RequestFromNeeder requestFromNeeder) {
                    LatLng latLngSource = new LatLng(requestFromNeeder.getSourceLocation().getLatitude(), requestFromNeeder.getSourceLocation().getLongitude());
                    LatLng latLngDes = new LatLng(requestFromNeeder.getDestinationLocation().getLatitude(), requestFromNeeder.getDestinationLocation().getLongitude());
                    makeMaker(latLngSource, "Start location");
                    drawroadBetween2Location(latLngSource, latLngDes);
                    makeMaker(latLngDes, "Destination location");

                }

                @Override
                public void onError(DatabaseError e) {

                }
            });*/

            //get all request from graber
        } else if (checkOnScreen == 2) {
            mGoogleMap.clear();
            Toast.makeText(this, "All people", Toast.LENGTH_SHORT).show();

            ForGraber.getInstance().getInfoRequestNeeder(mUser.getUid(), new RequestFromGraberCallback() {
                @Override
                public void onSuccess(RequestFromGraber requestFromGraber) {
                    LatLng latLngCurLocation = requestFromGraber.getSourceLocation();
                    LatLng latLngDesLocation = requestFromGraber.getDestinationLocation();

                    makeMaker(latLngCurLocation, "Location Graber");
                    drawroadBetween2Location(latLngCurLocation, latLngDesLocation);
                    makeMaker(latLngDesLocation, "Destination Graber");
                }

                @Override
                public void onError(DatabaseError e) {

                }
            });
            //get all request from needer
        }
        if (btnFindPeople.getVisibility() == View.VISIBLE && btnFindVehicles.getVisibility() == View.VISIBLE) {
            btnFindPeople.setVisibility(View.GONE);
            btnFindVehicles.setVisibility(View.GONE);
            if(btnCancelRequest.getVisibility()==View.GONE)
            {
                btnCancelRequest.setVisibility(View.VISIBLE);
            }
        }
    }


    private void addControls() {

        btnFindPeople = (FloatingActionButton) findViewById(R.id.btnFindPeople);
        btnFindVehicles = (FloatingActionButton) findViewById(R.id.btnFindVehicle);

        mUser = FirebaseAuth.getInstance().getCurrentUser();//Get currentuser
        //[Start]Send verification
        mUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
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
        arrRequest = new ArrayList<RequestFromGraber>();
        //arrRequestDemo = new ArrayList<RequestDemo>();

        viewHeader = navigationView.getHeaderView(0);
        txtEmail = (TextView) viewHeader.findViewById(R.id.txtEmail);
        txtFullName = (TextView) viewHeader.findViewById(R.id.txtFullName);
        imgUser = (ImageView) viewHeader.findViewById(R.id.imgUser);
        progressBar = (ProgressBar) viewHeader.findViewById(R.id.loading_progress_img);
        trackgps = new TrackGPSService(HomeActivity.this);

        btnCancelRequest= (FloatingActionButton) findViewById(R.id.btnCancelRequest);
        btnRestartRequest= (FloatingActionButton) findViewById(R.id.btnRestartRequest);
        checkOnScreen = 0;

        //Listener request of vehicle-sharing from database Firebase

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
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
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
        final String[] dialogTitle = new String[1];
        switch (view.getId()) {
            case R.id.btnFindVehicle:
                DialogFragment dialogFragment;
                dialogTitle[0] = "If you want find a vehicle together, you can fill out the form";
                dialogFragment = new AddRequestFromNeeder_Fragment();
                //ialogFragment.setTargetFragment(dialogFragment,1);
                dialogFragment.show(getFragmentManager(), "From Needer");
                //checkOnScreen();
                break;
            case R.id.btnFindPeople:
                //checkOnScreen = 2;
                DialogFragment dialogFindPeopleFragment;
                dialogTitle[0] = "If you have a vehicle and you want find a people together you can fill out the form to find it";
                dialogFindPeopleFragment = AddRequestFromGraber_Fragment.newIstance(dialogTitle[0]);
                // dialogFragment.setTargetFragment(dialogFragment,1);
                dialogFindPeopleFragment.show(getFragmentManager(), "From Grabber");
                //checkOnScreen();
                break;
            case R.id.btnCancelRequest:
                cancelRequest();
                break;
            case R.id.btnRestartRequest:
                restartrequest();
                break;
        }
    }

    private void restartrequest() {
        if(btnRestartRequest.getVisibility()==View.VISIBLE)
        {
            btnRestartRequest.setVisibility(View.GONE);
            if(btnCancelRequest.getVisibility()==View.GONE)
            {
                btnCancelRequest.setVisibility(View.VISIBLE);
            }
        }

    }

    private void cancelRequest() {
        if(btnCancelRequest.getVisibility()==View.VISIBLE)
        {
            btnCancelRequest.setVisibility(View.GONE);
            if(btnRestartRequest.getVisibility()==View.GONE)
                btnRestartRequest.setVisibility(View.VISIBLE);
        }
        if(checkOnScreen==0 || checkOnScreen==1)
        {

        }
        else {

        }
    }


    private void logout() {
        FirebaseAuth.getInstance().signOut();
        if (loginWith == 1)
            //Sign out Facebook
            LoginManager.getInstance().logOut();
        if (loginWith == 2)
            //Sign out Google plus
            Auth.GoogleSignInApi.signOut(Login_Fragment.session.mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {

                        }
                    });
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        /*btnFindVehicles.setVisibility(View.VISIBLE);
        btnFindPeople.setVisibility(View.VISIBLE);*/
        BitmapDescriptor bitmapDescriptorFactory;
        try {
            String url = "https://firebasestorage.googleapis.com/v0/b/vehiclessharing-74957.appspot.com/o/avatar%2F0ea2kDnvz8VjkbqoBMAIIaChsni2.jpg?alt=media&token=1afa116e-3074-49c7-b0b1-d36a829a7add";
            Bitmap bitmap;
            ImageTask ima=new ImageTask();
            ima.execute(url);
            bitmap=ima.get();
            bitmapDescriptorFactory=BitmapDescriptorFactory.fromBitmap(bitmap);
            //bitmapDescriptorFactory=BitmapDescriptorFactory.fromResource(R.drawable.profile);

            //makeMaker(new LatLng(10.8719808, 106.790409), "Nong Lam University");
            LatLng cur=new LatLng(10.8719808, 106.790409);
            Marker marker = mGoogleMap.addMarker(new MarkerOptions().title("here").position(cur).icon(bitmapDescriptorFactory));

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        mGoogleMap.setOnMarkerClickListener(this);
        if(mGoogleMap!=null)
        {
            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    View v=getLayoutInflater().inflate(R.layout.info_marker,null);
                    ImageView imgAvatarUser= (ImageView) v.findViewById(R.id.imgAvataUser);
                    TextView txtNameUser= (TextView) v.findViewById(R.id.txtUserName);
                    TextView txtAddressUser= (TextView) v.findViewById(R.id.txtAddressUser);
                    LatLng latLng=marker.getPosition();
                    try {
                        txtAddressUser.setText(AboutPlace.getInstance().getAddressByLatLng(HomeActivity.this,latLng));
                        String infoUser= (String) marker.getTag();
                        //From uid of user get avatar and full name of user
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return v;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });
        }
        //makeCustomMaker(new LatLng(mGoogleMap.getMyLocation().getLatitude(),mGoogleMap.getMyLocation().getLongitude()),"I'm in here");
       /* requestNeederRef = FirebaseDatabase.getInstance().getReference().child("requests_needer");
        requestNeederRef.addValueEventListener(requestNeederListener);*/

//       makeMaker(new LatLng(10.8719808, 106.790409), "Nong Lam University");

    }

    /**
     * get Url to request the Google Directions API
     *
     * @param origin start point location
     * @param dest   destination point location
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
     *
     * @param location location maker
     * @param title    title maker
     */
    private void makeMaker(LatLng location, String title) {
        LatLng latLng = new LatLng(location.latitude, location.longitude);
        Marker marker = mGoogleMap.addMarker(new MarkerOptions().title(title).position(latLng));
        marker.setTag(mUser.getUid());
    }

    private void makeMarkerForMyself(LatLng location,String title)
    {

        /*mDatabase=FirebaseDatabase.getInstance().getReference().child("users");
        ValueEventListener listener;
        final List<User> list=new ArrayList<>();
        listener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot temp : dataSnapshot.getChildren()) {
                    String s=temp.getKey();
                    Map<String, User> td = (HashMap<String,User>) temp.getValue();

                    User requestFromGraber= (User) td.values();
                    list.add(requestFromGraber);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        String urlTest="";
        for(User graber:list)
        {
            if(graber.getEmail()=="tuan@g.com")
            {
                urlTest=graber.getImage();
            }
        }
*/
        BitmapDescriptor bitmapDescriptorFactory=BitmapDescriptorFactory.defaultMarker();
        String url=String.valueOf(mUser.getPhotoUrl());
        if (url.equals("null") || url.isEmpty()) {
                bitmapDescriptorFactory =BitmapDescriptorFactory.fromResource(R.drawable.ic_accessibility_orange_a700_24dp);
        }
        else {
            bitmapDescriptorFactory=BitmapDescriptorFactory.fromPath(url);
        }


        Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
        //Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(mUser.getPhotoUrl()).getContent());
        Marker marker = mGoogleMap.addMarker(new MarkerOptions().title(title).position(location).icon(bitmapDescriptorFactory));
        marker.setTag(mUser.getUid());
    }

    private void makeCustomMakerForVehicle(RequestFromGraber request, String title){
        LatLng latLng = request.getSourceLocation();
     //  photoURL=mUser.getPhotoUrl();
        BitmapDescriptor bitmapDescriptorFactory=BitmapDescriptorFactory.defaultMarker();
        switch (request.getVehicleType())
        {
            case "Car":
               bitmapDescriptorFactory =BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_car_light_blue_800_24dp);
                break;
            case "Bike":
                bitmapDescriptorFactory =BitmapDescriptorFactory.fromResource(R.drawable.ic_motorcycle_green_900_24dp);
                break;
        }

        Marker marker = mGoogleMap.addMarker(new MarkerOptions().title(title).position(latLng).icon(bitmapDescriptorFactory));
        marker.setTag(request.getUserId());
    }

    /**
     * Check permission Location service
     *
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
        switch (requestCode) {
            case REQ_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                //Toast.makeText(HomeActivity.this, "GPS + Internet enable!", Toast.LENGTH_LONG).show();
            } else {
                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    if (checkLocationPermission()) {
                        mGoogleMap.setMyLocationEnabled(true);
                        Toast.makeText(HomeActivity.this, "GPS enable!", Toast.LENGTH_LONG).show();
                    } else {
                        mGoogleMap.setMyLocationEnabled(false);
                        Toast.makeText(HomeActivity.this, "GPS disable!", Toast.LENGTH_LONG).show();
                    }
                    if (isOnline())
                        Toast.makeText(HomeActivity.this, "Internet enable!", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(HomeActivity.this, "Internet disable!", Toast.LENGTH_LONG).show();
                }

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
//        trackgps.controllonLocationChanged(CONTROLL_OFF);
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
     *
     * @param loginWith
     */
    private void updateUIHeader(int loginWith) {
        String mfullName = "";
        String memail = "";
        String url = "";
        if (loginWith == 0) {
            mfullName = currentUser.getUser().getFullName();
            memail = currentUser.getUser().getEmail();
            url = String.valueOf(currentUser.getUser().getImage());//url avatar user
        } else {
            mfullName = mUser.getDisplayName();
            memail = mUser.getEmail();
            url = String.valueOf(mUser.getPhotoUrl());
        }

        txtEmail.setText(memail);
        txtFullName.setText(mfullName);


        if (url.equals("null") || url.isEmpty()) {
            imgUser.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.temp));
        } else {
            if (isOnline()) {
                progressBar.setVisibility(View.VISIBLE);
                Picasso.with(HomeActivity.this).load(url).into(imgUser, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(HomeActivity.this, "Error load image", Toast.LENGTH_SHORT).show();
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
     *
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
        if (!isOnline() || !checkLocationPermission() ||
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
            if (polyline != null) polyline.remove();
            PolylineOptions polyoptions = new PolylineOptions();
            polyoptions.color(Color.BLUE);
            polyoptions.width(10);
            for (int i = 0; i < arrayList.size(); i++) {

                //In case of more than 5 alternative routes
                PolylineOptions polyOptions = new PolylineOptions();
                polyOptions.color(getResources().getColor(R.color.black));
                polyOptions.width(10 + i * 3);
                polyOptions.addAll(arrayList.get(i).getPoints());
                polyline = mGoogleMap.addPolyline(polyOptions);
               // Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + arrayList.get(i).getDistanceValue() + ": duration - " + arrayList.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG_ERROR_ROUTING, e.getMessage());
        }
    }

    @Override
    public void onRoutingCancelled() {

    }


    @Override
    public void getRequestFromGraber(RequestFromGraber requestFromGraber) {
        LatLng curLocation=requestFromGraber.getSourceLocation();
        LatLng desLocation=requestFromGraber.getDestinationLocation();
        //makeMaker(curLocation,"Source Location");
       // makeCustomMaker(curLocation,"Source");
        makeMarkerForMyself(curLocation,"source");
        makeMaker(desLocation,"Destination Location");
        drawroadBetween2Location(curLocation,desLocation);

        hideButtonFindVehicleAndPeople();
        checkOnScreen=2;
    }

    private void hideButtonFindVehicleAndPeople()
    {
        if(btnFindVehicles.getVisibility()==View.VISIBLE && btnFindPeople.getVisibility()==View.VISIBLE)
        {
            btnFindPeople.setVisibility(View.GONE);
            btnFindVehicles.setVisibility(View.GONE);
            if(btnCancelRequest.getVisibility()==View.GONE)
            {
                btnCancelRequest.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void getRequestFromNeeder(RequestFromNeeder requestFromNeeder) {
        LatLng sourceLocation=requestFromNeeder.getSourceLocation();
        LatLng desLocation=requestFromNeeder.getDestinationLocation();

        makeMaker(sourceLocation,"Source location");
        makeMaker(desLocation,"Destination");
        drawroadBetween2Location(sourceLocation,desLocation);
        hideButtonFindVehicleAndPeople();
        checkOnScreen = 1;

        // makeMaker(new RequestFromNeeder(requestFromNeeder.getSourceLocation().getLatitude(),requestFromNeeder.getSourceLocation().getLongitude())),"Souce");
        //Toast.makeText(this, "Request From needer"+(int) requestFromNeeder.getSourceLocation().getLatitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
