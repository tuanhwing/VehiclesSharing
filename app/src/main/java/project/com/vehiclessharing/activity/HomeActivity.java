package project.com.vehiclessharing.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.constant.Utils;
import project.com.vehiclessharing.model.User;

import static project.com.vehiclessharing.model.UserSessionManager.mGoogleApiClient;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener, OnMapReadyCallback {

    private NavigationView navigationView = null;
    private Toolbar toolbar = null;
    private View viewHeader = null; // View header
    private TextView txtFullName,txtEmail;
    private FirebaseUser mUser; //CurrentUser
    public static ImageView imgUser; // Avatar of user
    public static Bitmap bmImgUser = null; // Bitmap of avatar
    public static User user;
    private FloatingActionButton fab; // button fab action
    public static int loginWith; //Determine user authen email/facebook/google
    private GoogleMap map;//Instance google map
    private DatabaseReference mUserReference;//Instance database firebase
    private ValueEventListener mUserListener;//Instance to Listener value events information user change



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
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        addControls();
        addEvents();


    }

    private void addEvents() {
//        txtFullName.setText(mUser.getDisplayName());
//        txtEmail.setText(mUser.getEmail());
        //downloadInformationUser();
        fab.setOnClickListener(this);
        //[Start]Listen for value events
        mUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Demo","0");
//                UserAddress address = dataSnapshot.getValue(UserAddress.class);
//                String name = dataSnapshot.getValue().toString();
                user = dataSnapshot.getValue(User.class);
                Log.d("Demo",user.getEmail());
                Log.d("Demo",user.getFullName());
                Log.d("Demo",user.getAddress().getCountry());
                Log.d("Demo",user.getAddress().getDistrict());
                Log.d("Demo",user.getImage());
                Log.d("Demo",user.getPhoneNumber());
                Log.d("Demo","1");
                txtFullName.setText(user.getFullName());
                txtEmail.setText(user.getEmail());
                Log.d("Demo","3");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Warning", "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(getApplicationContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mUserReference.addValueEventListener(mUserListener);
        //[End]Listen for value events


    }

    private void addControls() {

        mUser = FirebaseAuth.getInstance().getCurrentUser();//Get currentuser
        user = new User();
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
        //Sign out Google plus
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
        //Sign out Facebook
        LoginManager.getInstance().logOut();
        startActivity(new Intent(HomeActivity.this,MainActivity.class));
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng marker = new LatLng(10.8819912,106.780436);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker,15));

        map.addMarker(new MarkerOptions().title("KTX khu B").position(marker));
    }

    @Override
    public void onStop(){
        super.onStop();

        // Remove post value event listener
        if (mUserReference != null) {
            mUserReference.removeEventListener(mUserListener);
        }
    }
}
