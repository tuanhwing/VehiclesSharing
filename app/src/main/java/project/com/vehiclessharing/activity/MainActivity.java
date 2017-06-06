package project.com.vehiclessharing.activity;

import android.Manifest;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import project.com.vehiclessharing.R;
import project.com.vehiclessharing.application.ApplicationController;
import project.com.vehiclessharing.constant.Utils;
import project.com.vehiclessharing.database.RealmDatabase;
import project.com.vehiclessharing.fragment.AddRequestFromGraber_Fragment;
import project.com.vehiclessharing.fragment.AddRequestFromNeeder_Fragment;
import project.com.vehiclessharing.fragment.Login_Fragment;
import project.com.vehiclessharing.model.AboutPlace;
import project.com.vehiclessharing.model.CheckerGPS;
import project.com.vehiclessharing.model.ForGraber;
import project.com.vehiclessharing.model.ForNeeder;
import project.com.vehiclessharing.model.LatLngAddress;
import project.com.vehiclessharing.model.RequestFromGraber;
import project.com.vehiclessharing.model.RequestFromNeeder;
import project.com.vehiclessharing.model.User;
import project.com.vehiclessharing.model.UserOnDevice;
import project.com.vehiclessharing.service.TrackGPSService;
import project.com.vehiclessharing.utils.ImageClass;

import static project.com.vehiclessharing.R.id.map;
import static project.com.vehiclessharing.constant.Utils.DEVICE_TOKEN;
import static project.com.vehiclessharing.constant.Utils.TAG_ERROR_ROUTING;

public class MainActivity extends AppCompatActivity
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
//        startService(new Intent(this,TrackGPSService.class));//Enable tracking GPS

    }

    private void addEvents() {
        btnFindVehicles.setOnClickListener(this);
        btnFindPeople.setOnClickListener(this);
        btnCancelRequest.setOnClickListener(this);
        btnRestartRequest.setOnClickListener(this);
        // mGoogleMap.setOnMarkerClickListener(this);

        //[START]add new
//        requestNeederListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d("demo_request",String.valueOf(dataSnapshot.getValue()));
//                if(dataSnapshot != null)
//                    for (DataSnapshot requestSnapshot: dataSnapshot.getChildren()) {
//                        final RequestDemo requestDemo = requestSnapshot.getValue(RequestDemo.class);
//                        mGoogleMap.addMarker(new MarkerOptions()
//                                .position(new LatLng(requestDemo.getLocationRequest().getLocationLat(),requestDemo.getLocationRequest().getLocationLong()))
//                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.temp))));
//                    }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//

        /* requestChildListener  = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("child_listener_add",String.valueOf(dataSnapshot.getValue()));
                Log.d("child_listener_add",String.valueOf(dataSnapshot.getKey()));
                final RequestDemo requestDemo = dataSnapshot.getValue(RequestDemo.class);
                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(requestDemo.getLocationRequest().getLocationLat()
                                ,requestDemo.getLocationRequest().getLocationLong()))
                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.temp))));
                markerHashMap.put(String.valueOf(dataSnapshot.getKey()),marker);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("child_listener_changed",String.valueOf(dataSnapshot.getValue()));
                Marker marker = markerHashMap.get(String.valueOf(dataSnapshot.getKey()));
                final RequestDemo requestDemo = dataSnapshot.getValue(RequestDemo.class);
                marker.setPosition(new LatLng(requestDemo.getLocationRequest().getLocationLat()
                        ,requestDemo.getLocationRequest().getLocationLong()));

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("child_listener_remove",String.valueOf(dataSnapshot.getValue()));
                Marker marker = markerHashMap.get(String.valueOf(dataSnapshot.getKey()));
                marker.remove();
                markerHashMap.remove(String.valueOf(dataSnapshot.getKey()));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("child_listener_move",String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("child_listener_cancel",String.valueOf(databaseError.getMessage()));
            }
        };*/
        //[END]add new
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
                //getVehicleNearMe();
                break;
            case R.id.btnFindPeople:
                //checkOnScreen = 2;
                DialogFragment dialogFindPeopleFragment;
                dialogTitle[0] = "If you have a vehicle and you want find a people together you can fill out the form to find it";
                dialogFindPeopleFragment = AddRequestFromGraber_Fragment.newIstance(dialogTitle[0]);
                dialogFindPeopleFragment.show(getFragmentManager(), "From Grabber");
                //getNeederNearMe();
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
        if (btnRestartRequest.getVisibility() == View.VISIBLE) {
            btnRestartRequest.setVisibility(View.GONE);
            if (btnCancelRequest.getVisibility() == View.GONE) {
                btnCancelRequest.setVisibility(View.VISIBLE);
            }
        }
    }

    private void cancelRequest() {
        visibleButtonFindVehicleAndPeople();
        if (checkOnScreen == 0 || checkOnScreen == 1) {
           // mDatabase.removeEventListener(ForNeeder.childEventListenerForGraber);
            Log.d("remove event","remove");
        } else {
            //mDatabase.removeEventListener(ForGraber.requestAddNeeder);
            Log.d("remove event","remove");
        }
        mGoogleMap.clear();
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
        startActivity(new Intent(MainActivity.this, SigninActivity.class));
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(this);
        if (mGoogleMap != null) {
            if(checkerGPS.checkPermission()) mGoogleMap.setMyLocationEnabled(true);//Enable mylocation
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
        }
        //makeCustomMaker(new LatLng(mGoogleMap.getMyLocation().getLatitude(),mGoogleMap.getMyLocation().getLongitude()),"I'm in here");
        //[START]add new
//        requestNeederRef = FirebaseDatabase.getInstance().getReference().child("requests_needer");
//        requestNeederRef.addValueEventListener(requestNeederListener);
//        requestNeederRef.addChildEventListener(requestChildListener);
        //[END]add new
//      makecustomMarkerAvatar(new LatLng(10.8719808, 106.790409),mUser.getUid(), "Nong Lam University");

    }

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
                imgVehicleType.setVisibility(View.VISIBLE);
                // txtVehicleType.setText(graber.getVehicleType());
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
            txtTime.setText(time);
            try {
                String sourceAddress = AboutPlace.getInstance().getAddressByLatLng(MainActivity.this, souceLocation);
                LatLng des = new LatLng(desLocation.getLatidude(), desLocation.getLongtitude());
                String destinationAddress = AboutPlace.getInstance().getAddressByLatLng(MainActivity.this, des);

                txtSourceLocation.setText(sourceAddress);
                txtDeslocation.setText(destinationAddress);
                txtFullname.setText(marker.getTitle());

                /*UserCallback userCallback = new UserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        // Toast.makeText(MainActivity.this,String.valueOf(user.getImage()), Toast.LENGTH_SHORT).show();
                        Log.e("fullname", user.getFullName());
                        if(txtFullname == null) Log.e("fullname","null");
                        txtFullname.setText("sdsdsdsd");
                    }

                    @Override
                    public void onError(DatabaseError e) {

                    }
                };
                UserInfomation userInfomation = new UserInfomation();
                userInfomation.getInfoUserById(idUser, userCallback);*/
                //String fullName = FirebaseDatabase.getInstance().getReference().child("users").child(idUser).child("fullName").toString();

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


   /* private void makeMaker(LatLng location, String title) {
        LatLng latLng = new LatLng(location.latitude, location.longitude);
        String url="https://firebasestorage.googleapis.com/v0/b/vehiclessharing-74957.appspot.com/o/avatar%2F0ea2kDnvz8VjkbqoBMAIIaChsni2.jpg?alt=media&token=1afa116e-3074-49c7-b0b1-d36a829a7add";

        Marker marker = mGoogleMap.addMarker(new MarkerOptions().title(title).position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getInstance(this).customMarkerWithAvatar(url))));
        marker.setTag(mUser.getUid());
    }

    private View getCustomMarkerView(String urlAvatar) {
        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        //get avatar of user and put it in imageview of custom_marker
        if (urlAvatar.equals("null") || urlAvatar.isEmpty()) {
            markerImageView.setImageResource(R.drawable.temp);
            //progressBar.setVisibility(View.GONE);
        } else {
            if (isOnline()) {
                //progressBar.setVisibility(View.VISIBLE);
                Picasso.with(this).load(urlAvatar).into(markerImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        //progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        *//*progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfileActivity.this,"Error load image",Toast.LENGTH_SHORT).show();*//*
                    }
                });
            } else Picasso.with(getApplicationContext())
                    .load(urlAvatar)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(markerImageView);
        }
        return customMarkerView;
    }*/

    private void makeCustomMarkerMyself(LatLng source, LatLng destination) {
        BitmapDescriptor bitmapSource = BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_red_700_36dp);
        BitmapDescriptor bitmapDestination = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_red_900_36dp);
        Marker sourceMarker = mGoogleMap.addMarker(new MarkerOptions().position(source).title("You're here").icon(bitmapSource));
        Marker destinationMarker = mGoogleMap.addMarker(new MarkerOptions().position(destination).title("Your destination").icon(bitmapDestination));
        sourceMarker.setTag(mUser.getUid());
        destinationMarker.setTag(mUser.getUid());
    }

    /*private void makeCustomMakerForVehicle(RequestFromGraber graber, String urlAvatar) {
        LatLng source=new LatLng(graber.getSourceLocation().getLatidude(),graber.getSourceLocation().getLongtitude());
        Marker customMarker = mGoogleMap.addMarker(new MarkerOptions().position(source).title("Graber")
                .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getInstance(this).customMarkerWithAvatar(String.valueOf(mUser.getPhotoUrl())))));
        customMarker.setTag(graber);
    }

    private void makeMarkerForPeople(RequestFromNeeder needer, String urlAvatar) {
        LatLng sourceLocation=new LatLng(needer.getSourceLocation().getLatidude(),needer.getSourceLocation().getLongtitude());
        Marker customMarker = mGoogleMap.addMarker(new MarkerOptions().position(sourceLocation).title("Needer")
                .icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.getInstance(this).customMarkerWithAvatar(urlAvatar))));
        customMarker.setTag(needer);
    }*/
    /*public Bitmap customMarkerView(View customMarkerView)
    {
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }*/

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

    //Check GPS + Internet is enable everytime
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
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
    };


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("FCM ServiceAAAAA","onStart");
        updateUIHeader(loginWith);//Update information user into header layout
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.d("notification_aaaaa", "Key: " + key + " Value: " + value);
            }
        }
        if(checkerGPS.checkLocationPermission() && !TrackGPSService.isRunning)
            startService(new Intent(this, TrackGPSService.class));//Enable tracking GPS
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
        LatLng curLocation = new LatLng(requestFromGraber.getSourceLocation().getLatidude(), requestFromGraber.getSourceLocation().getLongtitude());
        LatLng desLocation = new LatLng(requestFromGraber.getDestinationLocation().getLatidude(), requestFromGraber.getDestinationLocation().getLongtitude());

        makeCustomMarkerMyself(curLocation, desLocation);
        drawroadBetween2Location(curLocation, desLocation);
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
        drawroadBetween2Location(sourceLocation, desLocation);
        hideButtonFindVehicleAndPeople();
        checkOnScreen = 1;
        ForNeeder.getInstance().getAllGraberNear(this, mUser.getUid());

    }
   /* private User getInfoUser(String userId) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(userId);
        User user = new User();
        requestNeederListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = (User) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("load_data", databaseError.getMessage());
            }
        };
        mDatabase.addListenerForSingleValueEvent(requestNeederListener);
        return user;
    }*/

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
        // Retrieve the data from the marker.
        /*Integer clickCount = (Integer) marker.getTag();
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }
        return false;*/
        // Toast.makeText(this, String.valueOf(marker.getTag()), Toast.LENGTH_SHORT).show();
        marker.showInfoWindow();
        return true;
    }
}
