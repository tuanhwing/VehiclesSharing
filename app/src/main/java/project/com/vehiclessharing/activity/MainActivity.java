package project.com.vehiclessharing.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.realm.Realm;
import project.com.vehiclessharing.R;
import project.com.vehiclessharing.constant.Utils;
import project.com.vehiclessharing.fragment.Login_Fragment;
import project.com.vehiclessharing.model.AddressOnDevice;
import project.com.vehiclessharing.model.BirthdayOnDevice;
import project.com.vehiclessharing.model.InformationUserOnDivce;
import project.com.vehiclessharing.model.User;
import project.com.vehiclessharing.model.UserOnDevice;
import project.com.vehiclessharing.sqlite.DatabaseHelper;
import project.com.vehiclessharing.sqlite.RealmDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static FirebaseAuth mAuth; // Instance Authentication used by all fragment inside MainActivity
    private FirebaseAuth.AuthStateListener mAuthListener;// Instance listener state user
    private FirebaseUser mUser;// Instance user to get information
    private DatabaseReference mUserReference;//Instance database firebase table users
    private Realm realm;

    public static ProgressDialog mProgress;//Progress to wait login

    //get Instance.
    private DatabaseHelper db;


    private static FragmentManager fragmentManager;// Instance fragmentManager to switch fragment
    ImageView imgClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up notitle
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

//        //set up full screen
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FacebookSdk.sdkInitialize(this);// This function initializes the Facebook SDK
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        // If savedinstnacestate is null then replace login fragment
        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frameContainer, new Login_Fragment(),
                            Utils.Login_Fragment).commit();
        }


        addControls();
        addEvents();


    }

    /**
     * Set Listeners
     */
    private void addEvents() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if(!mProgress.isShowing()) mProgress.show();
                    // User is signed in
                    for (UserInfo usera: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                        if(usera.getProviderId().equals(Utils.Email_Signin)){
                            String userId = user.getUid();
                            if(!RealmDatabase.isUserExists(userId)){
                                Log.d("real_database","true");
                                getProfileUser(userId);//get profile and save data on device
                            }
                            else {
                                Log.d("real_database","false");
                                switchActivity();//go to the Home Activity
                                break;
                            }

//                            // mapping device token with user
//                            String deviceToken = ApplicationController.sharedPreferences.getString(DEVICE_TOKEN, null);
//                            Log.d("real_database",deviceToken);
//                            if(db.isUserExists(userId)) {
//                                switchActivity();
//                                break;
//                            } else {
//                                getProfileUser(userId);
//                                break;
//                            }

                        } else if(usera.getProviderId().equals(Utils.Facebook_Signin)
                                || usera.getProviderId().equals(Utils.Google_Signin)){
                            switchActivity();//go to the Home Activity
                            break;
                        }
                    }
                } else {
                    // User is signed out
                }
                // ...
            }
        };

        // On close icon click finish activity
        imgClose.setOnClickListener(this);

    }


    /**
     * Handling button/textview click
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.close_activity:
                finish();
                break;
        }

    }

    /**
     * Initliaze Views
     */
    private void addControls() {
        mAuth = FirebaseAuth.getInstance();

        db = DatabaseHelper.getInstance(MainActivity.this);//Instance DatabaseHelper

        realm = Realm.getDefaultInstance();// The RealmConfiguration is created using the builder pattern.

        //[Start] Setup for progress
        mProgress =new ProgressDialog(this);
        mProgress.setTitle(Utils.SignIn);
        mProgress.setMessage(Utils.PleaseWait);
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        //[End] Setup for progress

        imgClose = (ImageView) findViewById(R.id.close_activity);
        //set animation Close
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pendulum);
        imgClose.startAnimation(animation);
    }

    /**
     * Replace Login Fragment with animation
     */
    public void replaceLoginFragment() {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new Login_Fragment(),
                        Utils.Login_Fragment).commit();
    }

    /**
     * Handling press Back in device
     */
    @Override
    public void onBackPressed() {

        // Find the tag of signup and forgot password fragment
        Fragment SignUp_Fragment = fragmentManager
                .findFragmentByTag(Utils.SignUp_Fragment);
        Fragment ForgotPassword_Fragment = fragmentManager
                .findFragmentByTag(Utils.ForgotPassword_Fragment);

        // Check if both are null or not
        // If both are not null then replace login fragment else do backpressed
        // task

        if (SignUp_Fragment != null)
            replaceLoginFragment();
        else if (ForgotPassword_Fragment != null)
            replaceLoginFragment();
        else
            super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Get user's profile in Database Firebase
     * @return
     */
    private void getProfileUser(final String userId) {
//        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId); //Instance database firebase
        ValueEventListener getProfileUser = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.d("DemoLogin", String.valueOf(dataSnapshot.getValue()));
                User user = dataSnapshot.getValue(User.class);
                storageProfileOnDevice(user,userId);//Save profile user on Sqlite
                // ...
                switchActivity();//go to the Home Activity
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Canceled", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mUserReference.addListenerForSingleValueEvent(getProfileUser);
    }

    /**
     * Switch to Home Activity when login succeed
     */
    private void switchActivity(){
        mProgress.dismiss();
        startActivity(new Intent(MainActivity.this,HomeActivity.class));
        finish();
    }


    /**
     * Storage user's profile in device
     * @param user object user
     */
    private void storageProfileOnDevice(User user,String userId) {
        BirthdayOnDevice birthdayOnDevice = new BirthdayOnDevice(
                user.getBirthDay().getDay(),
                user.getBirthDay().getMonth(),
                user.getBirthDay().getYear()
        );
        AddressOnDevice addressOnDevice = new AddressOnDevice(
                user.getAddress().getCountry(),
                user.getAddress().getDistrict(),
                user.getAddress().getProvince()
        );
        InformationUserOnDivce informationUserOnDivce = new InformationUserOnDivce(
                user.getEmail(),
                user.getImage(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getSex(),
                addressOnDevice,
                birthdayOnDevice
        );
        UserOnDevice temp = new UserOnDevice(userId,informationUserOnDivce);
        RealmDatabase.storageOnDiviceRealm(temp);
    }

}
