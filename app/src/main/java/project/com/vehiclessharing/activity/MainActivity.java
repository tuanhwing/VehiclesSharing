package project.com.vehiclessharing.activity;

import android.Manifest;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.application.ApplicationController;
import project.com.vehiclessharing.asynctask.CustomMarkerAsync;
import project.com.vehiclessharing.constant.Utils;
import project.com.vehiclessharing.database.RealmDatabase;
import project.com.vehiclessharing.fragment.AddRequestFromGraber_Fragment;
import project.com.vehiclessharing.fragment.AddRequestFromNeeder_Fragment;
import project.com.vehiclessharing.fragment.Login_Fragment;
import project.com.vehiclessharing.listener.InfoWindowTouchListener;
import project.com.vehiclessharing.model.AboutPlace;
import project.com.vehiclessharing.model.CheckerGPS;
import project.com.vehiclessharing.model.ForGraber;
import project.com.vehiclessharing.model.ForNeeder;
import project.com.vehiclessharing.model.LatLngAddress;
import project.com.vehiclessharing.model.RequestFromGraber;
import project.com.vehiclessharing.model.RequestFromNeeder;
import project.com.vehiclessharing.model.User;
import project.com.vehiclessharing.model.UserOnDevice;
import project.com.vehiclessharing.parseobject.ParseObject;
import project.com.vehiclessharing.service.TrackGPSService;
import project.com.vehiclessharing.utils.DrawRoute;
import project.com.vehiclessharing.utils.ImageClass;

import static project.com.vehiclessharing.R.id.map;
import static project.com.vehiclessharing.constant.Utils.DEVICE_TOKEN;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
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
    private static CheckerGPS checkerGPS;

//    private ValueEventListener requestNeederListener;
    private ChildEventListener requestChildListener;
    private HashMap<String,Marker> markerHashMap = new HashMap<>();//Hashmap store all the marker inside map
//    public static Activity mactivity;
    private DatabaseReference requestNeederRef;
    private String mRequestKey;
    private ArrayList<RequestFromGraber> arrRequest;
    public static UserOnDevice currentUser;//Instace current user logined
    final private static int REQ_PERMISSION = 20;//Value request permission
    private static String DIRECTION_KEY_API = "AIzaSyAGjxiNRAHypiFYNCN-qcmUgoejyZPtS9c";

    private FloatingActionButton btnFindPeople, btnFindVehicles, btnCancelRequest, btnRestartRequest; // button fab action
    private DatabaseReference mDatabase;
    private int checkOnScreen;
    private InfoWindowTouchListener infoButtonListener;

    private static RequestFromGraber requestFromGraber;
    private static RequestFromNeeder requestFromNeeder;
    private static User infoUser;

    //    private static FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
//            startActivity(new Intent(MainActivity.this,SigninActivity.class));
//            finish();
//            return;
//        }
        Log.d("FCM ServiceAAAAA","onCreate");
        int who =ApplicationController.sharedPreferences.getInt("who",0);

        ApplicationController.sharedPreferences.edit().putInt("who",Utils.IS_GRABER).commit();//temp demo
        Log.d("FCM_bugAAAAMain",String.valueOf(ApplicationController.sharedPreferences.getInt("who",0)));
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//DO NOT ROTATE the screen even if the user is shaking his phone like mad

        Log.d("device_id", ApplicationController.sharedPreferences.getString(DEVICE_TOKEN,null));
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
        updateUIHeader(loginWith);//Update information user into header layout
//        startService(new Intent(this,TrackGPSService.class));//Enable tracking GPS

    }

    private void addEvents() {
        btnFindVehicles.setOnClickListener(this);
        btnFindPeople.setOnClickListener(this);
        btnCancelRequest.setOnClickListener(this);
        btnRestartRequest.setOnClickListener(this);

    }
    private void addControls() {

//        mactivity = MainActivity.this;

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
//        arrRequest = new ArrayList<RequestFromGraber>();
        //arrRequestDemo = new ArrayList<RequestDemo>();

        viewHeader = navigationView.getHeaderView(0);
        txtEmail = (TextView) viewHeader.findViewById(R.id.txtEmail);
        txtFullName = (TextView) viewHeader.findViewById(R.id.txtFullName);
        imgUser = (ImageView) viewHeader.findViewById(R.id.imgUser);
        progressBar = (ProgressBar) viewHeader.findViewById(R.id.loading_progress_img);
        checkerGPS = new CheckerGPS(MainActivity.this);

        btnCancelRequest = (FloatingActionButton) findViewById(R.id.btnCancelRequest);
        btnRestartRequest = (FloatingActionButton) findViewById(R.id.btnRestartRequest);
        checkOnScreen = 0;

        //Listener request of vehicle-sharing from database Firebase

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            moveTaskToBack(true);
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
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
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
                dialogFragment = AddRequestFromNeeder_Fragment.newIstance(dialogTitle[0]);
                dialogFragment.show(getFragmentManager(), "From Needer");
                break;
            case R.id.btnFindPeople:
                //checkOnScreen = 2;
                DialogFragment dialogFindPeopleFragment;
                dialogTitle[0] = "If you have a vehicle and you want find a people together you can fill out the form to find it";
                dialogFindPeopleFragment = AddRequestFromGraber_Fragment.newIstance(dialogTitle[0]);
                dialogFindPeopleFragment.show(getFragmentManager(), "From Grabber");
                break;
            case R.id.btnCancelRequest:
                cancelRequest();
                break;
        }
    }

    /**
     * Some action when button cancel request be clicked
     */
    private void cancelRequest() {
        visibleButtonFindVehicleAndPeople();
        Log.d("checkonscreen",String.valueOf(checkOnScreen));
        if (checkOnScreen == 0 || checkOnScreen == 1) {

            Log.d("removeevent","remove");
            mDatabase.child("requestfromneeder").child(mUser.getUid()).removeValue();
            mDatabase.removeEventListener(ForNeeder.childEventListenerForGraber);
        } else {

            Log.d("removefirebase","remove");
            //mDatabase=mDatabase.child("requestfromgraber").child(mUser.getUid());
            mDatabase.child("requestfromgraber").child(mUser.getUid()).removeValue();

            mDatabase.removeEventListener(ForGraber.requestAddNeeder);
        }
        mGoogleMap.clear();
    }


    /**
     * Logout
     */
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
        startActivity(new Intent(MainActivity.this, SigninActivity.class));
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(this);
        if (mGoogleMap != null) {
            if(checkerGPS.checkPermission()) mGoogleMap.setMyLocationEnabled(true);//Enable mylocation
            //show info window when touch marker
            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v;
                    User user = new User();
                    String who = marker.getTitle();
                    if (who.equals("You're here") || who.equals("Your destination")) {
                        v = null;
                        //user = UserInfomation.getInstance().getInfoUserById(String.valueOf(marker.getTag()));
                        // souceLocation = marker.getPosition();
                    } else {
                        v = displayInfoMarkerClick(marker);
                    }

                    return v;
                }
            });
            /**
             * When user want sent to request to another user in map. They can click in infowindow
             * setOnInfoWindowClickListener contain dialog confirm send request to this user
             */
            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(MainActivity.this);
                    }
                    builder.setTitle("Send request")
                            .setMessage("Are you sure you want to send this request?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(R.drawable.ic_warning_red_600_24dp)
                            .show();
                }
            });
        }
    }

    /**
     * if user not current user custom info of marker. Info marker have info about fullname, source location, destination location
     * Time start
     * @param marker
     * @return
     */
    private View displayInfoMarkerClick(Marker marker) {
        View v = getLayoutInflater().inflate(R.layout.info_marker, null);
        final TextView txtFullname, txtSourceLocation, txtDeslocation, txtTime;
        final ImageView imgVehicleType;

        txtFullname = (TextView) v.findViewById(R.id.txtFullNameUser);
        txtSourceLocation = (TextView) v.findViewById(R.id.txtSourceLocationUser);
        txtDeslocation = (TextView) v.findViewById(R.id.txtDesLocationUser);
        txtTime = (TextView) v.findViewById(R.id.txtTimeUser);
        imgVehicleType = (ImageView) v.findViewById(R.id.imgVehicleTypeUser);
        //txtVehicleType.setVisibility(View.GONE);
        String time = "";
        LatLng souceLocation = null;
        LatLngAddress desLocation = null;
        String idUser = "";
        final String[] fullName = new String[1];
        Boolean checkNullObject = true;
        if (checkOnScreen == 1) {
            RequestFromGraber graber = (RequestFromGraber) marker.getTag();
            //user = UserInfomation.getInstance().getInfoUserById(graber.getUserId());
            if (graber != null) {
                souceLocation = marker.getPosition();
                desLocation = graber.getDestinationLocation();
                time = "now";
                if (imgVehicleType.getVisibility()==View.INVISIBLE) {
                    imgVehicleType.setVisibility(View.VISIBLE);
                }// txtVehicleType.setText(graber.getVehicleType());
                if (graber.getVehicleType().equals("Bike")) {
                    imgVehicleType.setImageResource(R.drawable.ic_motorcycle_green_900_24dp);
                }
                checkNullObject = false;
                idUser = graber.getUserId();
            }

        } else if (checkOnScreen == 2) {
            RequestFromNeeder needer = (RequestFromNeeder) marker.getTag();
            if (needer != null) {
                souceLocation = marker.getPosition();
                desLocation = needer.getDestinationLocation();
                time = needer.getTimeStart();
                idUser = needer.getUserId();
                checkNullObject = false;
            }
        }
        if (!checkNullObject) {

            try {
                String sourceAddress = AboutPlace.getInstance().getAddressByLatLng(MainActivity.this, souceLocation);
                LatLng des = new LatLng(desLocation.getLatidude(), desLocation.getLongtitude());
                String destinationAddress = AboutPlace.getInstance().getAddressByLatLng(MainActivity.this, des);

                txtFullname.setText(marker.getTitle());
                txtSourceLocation.setText(sourceAddress);
                txtDeslocation.setText(destinationAddress);
                txtTime.setText(time);
                DrawRoute draw=new DrawRoute(this);
                draw.drawroadBetween2Location(souceLocation,des,1);
                //Button btnSend= (Button) v.findViewById(R.id.btnSendRequest);
                /*btnSend.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Toast.makeText(MainActivity.this, "btnsend click", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //txtFullname.setText("hihihehehe");
        return v;
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
     * make marker for source and destination location of current user
     * @param source
     * @param destination
     */
    private void makeCustomMarkerMyself(LatLng source, LatLng destination) {
        BitmapDescriptor bitmapSource = BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_red_700_36dp);
        BitmapDescriptor bitmapDestination = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_red_900_36dp);
        Marker sourceMarker = mGoogleMap.addMarker(new MarkerOptions().position(source).title("You're here").icon(bitmapSource));
        Marker destinationMarker = mGoogleMap.addMarker(new MarkerOptions().position(destination).title("Your destination").icon(bitmapDestination));
        sourceMarker.setTag(mUser.getUid());
        destinationMarker.setTag(mUser.getUid());
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
                        if(!TrackGPSService.isRunning) startService(new Intent(this,TrackGPSService.class));

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

    //Check GPS + Internet is enable everytime and
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(Utils.ACCEPT_ACTION.equals(action)){//Action accept notification(request) from other user
                UpdateUIAcceptRequest(intent.getStringExtra("infouser"),intent.getStringExtra("inforequest"));//Update UI when user accept request from Graber or Needer
//                Toast.makeText(MainActivity.this, intent.getStringExtra("infouser"), Toast.LENGTH_SHORT).show();
            }
            if(Utils.CANCEL_ACTION.equals(action)){
                if(ApplicationController.listNotification.size() > 0){
                    Log.e("error_temp","main1");
                    try{
                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(1/* ID of notification */,
                                ApplicationController.listNotification.get(0));
                        ApplicationController.listNotification.remove(0);
                        Log.e("error_temp","main2");
                    } catch (Exception e){
                        Log.e("error_notification_main",String.valueOf(e.getMessage()));
                    }

                }
            }
            else {//action about network / location
                LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                if ((lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) &&
                        isOnline()) {
                    //Do your stuff on GPS status change
                    //Toast.makeText(MainActivity.this, "GPS + Internet enable!", Toast.LENGTH_LONG).show();
                } else {
                    if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                            lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        if (checkLocationPermission()) {
                            mGoogleMap.setMyLocationEnabled(true);
                            Toast.makeText(MainActivity.this, "GPS enable!", Toast.LENGTH_LONG).show();
                        } else {
                            mGoogleMap.setMyLocationEnabled(false);
                            Toast.makeText(MainActivity.this, "GPS disable!", Toast.LENGTH_LONG).show();
                        }
                        if (isOnline())
                            Toast.makeText(MainActivity.this, "Internet enable!", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Internet disable!", Toast.LENGTH_LONG).show();
                    }

                }
            }
        }

    };

    /**
     * Update ui when user accept request from graber/ needer
     * @param userJson json data information about user
     * @param requestJson json data information about request's user
     */
    private void UpdateUIAcceptRequest(String userJson, String requestJson){
        User infoUser = ParseObject.jsonToUser(userJson);
        Log.e("error_temp",userJson);
        Log.e("error_temp",String.valueOf(infoUser.getEmail()));
        Log.e("error_temp",String.valueOf(infoUser.getFullName()));
        DrawRoute drawRoute=new DrawRoute(this);
        switch (ApplicationController.sharedPreferences.getInt("who",0)){

            case 0:
                Log.e("error_temp","case 0");
                break;
            case 1: {//I am a graber
                Log.e("error_temp","case 1");
                Log.e("error_temp",requestJson);
                RequestFromNeeder inforequest = ParseObject.jsonToRequestNeeder(requestJson);
                try {
                    CustomMarkerAsync customMarker = new CustomMarkerAsync(inforequest.getUserId(), MainActivity.this);
                    customMarker.setNeeder(inforequest);
                    customMarker.execute(infoUser);

                    drawRoute.drawroadBetween2Location(
                            new LatLng(inforequest.getSourceLocation().getLatidude(),
                                    inforequest.getSourceLocation().getLongtitude())
                            ,new LatLng(inforequest.getDestinationLocation().getLatidude(),
                                    inforequest.getDestinationLocation().getLongtitude())
                            ,1);
//                    mGoogleMap.addMarker(new MarkerOptions()
//                            .position(new LatLng(10.8162006, 106.6321737))
//                            .title("lol")
//                    );
//                    mGoogleMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(inforequest.getSourceLocation().getLatidude(),
//                                inforequest.getSourceLocation().getLongtitude()))
//                            .title(infoUser.getFullName())
//                    );
//                    mGoogleMap.addMarker(new MarkerOptions()
//                            .position(new LatLng(inforequest.getDestinationLocation().getLatidude(),
//                                    inforequest.getDestinationLocation().getLongtitude()))
//                            .title(infoUser.getFullName())
//                    );
                } catch(Exception e){
                    Log.e("error_temp",String.valueOf(e.getMessage()));
                }
                break;
            }
            case 2: {//I am a helper
                Log.e("error_temp","case 2");
                RequestFromGraber inforequest = ParseObject.jsonToRequestGraber(requestJson);
                try {
                    CustomMarkerAsync customMarker = new CustomMarkerAsync(inforequest.getUserId(), MainActivity.this);
                    customMarker.setGraber(inforequest);
                    customMarker.execute(infoUser);
                    drawRoute.drawroadBetween2Location(
                            new LatLng(inforequest.getSourceLocation().getLatidude(),
                                    inforequest.getSourceLocation().getLongtitude())
                            ,new LatLng(inforequest.getDestinationLocation().getLatidude(),
                                    inforequest.getDestinationLocation().getLongtitude())
                            ,1);
                } catch(Exception e){
                    Log.e("error_temp",String.valueOf(e.getMessage()));
                }
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(checkerGPS.checkLocationPermission() && !TrackGPSService.isRunning)
            startService(new Intent(this, TrackGPSService.class));//Enable tracking GPS
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("wtf_AAAA","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("wtf_AAAA","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unregister receiver on destroy
//        trackgps.controllonLocationChanged(CONTROLL_OFF);
        if (mReceiver != null && FirebaseAuth.getInstance().getCurrentUser() != null)
            unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Register broadcast reciver Location/Network state
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.location.PROVIDERS_CHANGED");
        filter.addAction(Utils.ACCEPT_ACTION);//Accept request from a needer/graber
        filter.addAction(Utils.CANCEL_ACTION);//Accept request from a needer/graber
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
            progressBar.setVisibility(View.GONE);
        } else {
            if(!isOnline()) ImageClass.loadImageOffline(url,MainActivity.this,imgUser,progressBar);
            else ImageClass.loadImageOnline(url,MainActivity.this,imgUser,progressBar);
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


    @Override
    public void getRequestFromGraber(RequestFromGraber requestFromGraber) {
        LatLng curLocation = new LatLng(requestFromGraber.getSourceLocation().getLatidude(), requestFromGraber.getSourceLocation().getLongtitude());
        LatLng desLocation = new LatLng(requestFromGraber.getDestinationLocation().getLatidude(), requestFromGraber.getDestinationLocation().getLongtitude());

        makeCustomMarkerMyself(curLocation, desLocation);
        DrawRoute drawRoute=new DrawRoute(this);
        drawRoute.drawroadBetween2Location(curLocation,desLocation,0);
        //drawroadBetween2Location(curLocation, desLocation);
        hideButtonFindVehicleAndPeople();
        checkOnScreen = 2;
        ForGraber.getInstance().getAllNeederNear(this, mUser.getUid());
    }

    /**
     * when request is added firebase success. get all Graber near this User
     * @param requestFromNeeder
     */
    @Override
    public void getRequestFromNeeder(RequestFromNeeder requestFromNeeder) {
        LatLng sourceLocation = new LatLng(requestFromNeeder.getSourceLocation().getLatidude(), requestFromNeeder.getSourceLocation().getLongtitude());
        LatLng desLocation = new LatLng(requestFromNeeder.getDestinationLocation().getLatidude(), requestFromNeeder.getDestinationLocation().getLongtitude());

       /* makecustomMarkerAvatar(sourceLocation, requestFromNeeder.getUserId(), String.valueOf(mUser.getPhotoUrl()), "You're here");
        makeMaker(desLocation, "Destination");*/
        makeCustomMarkerMyself(sourceLocation, desLocation);
        DrawRoute draw=new DrawRoute(this);
        draw.drawroadBetween2Location(sourceLocation,desLocation,0);
        //drawroadBetween2Location(sourceLocation, desLocation);
        hideButtonFindVehicleAndPeople();
        checkOnScreen = 1;
        ForNeeder.getInstance().getAllGraberNear(this, mUser.getUid());

    }

    /**
     * Hide two button vehicle and people when new request is added
     */
    private void hideButtonFindVehicleAndPeople() {
        if (btnFindVehicles.getVisibility() == View.VISIBLE && btnFindPeople.getVisibility() == View.VISIBLE) {
            btnFindPeople.setVisibility(View.GONE);
            btnFindVehicles.setVisibility(View.GONE);
            if (btnCancelRequest.getVisibility() == View.GONE) {
                btnCancelRequest.setVisibility(View.VISIBLE);
            }
        }
    }
    private void visibleButtonFindVehicleAndPeople()
    {
        if (btnFindVehicles.getVisibility() == View.GONE && btnFindPeople.getVisibility() == View.GONE) {
            btnFindPeople.setVisibility(View.VISIBLE);
            btnFindVehicles.setVisibility(View.VISIBLE);
            if (btnCancelRequest.getVisibility() == View.VISIBLE) {
                btnCancelRequest.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }
}
