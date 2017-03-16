package project.com.vehiclessharing.activity;

import android.content.Intent;
import android.os.Bundle;
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

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.constant.Utils;
import project.com.vehiclessharing.fragment.Login_Fragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //Add new
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    //End new

    private static FragmentManager fragmentManager;
    ImageView imgClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up notitle
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        //set up full screen
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FacebookSdk.sdkInitialize(this);// Add new
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        Log.d("MAINAAAAAAAAAAAA","AAAAAAAAAAA");
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
//            Log.d("MAINAAAAAAAAAAAA",String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid()));
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }

        // If savedinstnacestate is null then replace login fragment
        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frameContainer, new Login_Fragment(),
                            Utils.Login_Fragment).commit();
        }



//        Log.d("MAINssssss", "onAuthStateChanged:signed_out1");
        addControls();
        addEvents();
//        Log.d("MAINssssss", "onAuthStateChanged:signed_out2");

    }

    private void addEvents() {
//         mUser = mAuth.getCurrentUser();
//        if (mUser != null) {
//            // User is signed in
//            Log.d("ssssss", "onAuthStateChanged:signed_in1");
//            startActivity(new Intent(MainActivity.this, HomeActivity.class));
//            finish();
//            Log.d("ssssss", "onAuthStateChanged:signed_in2");
//        } else {
//            // User is signed out
//            Log.d("ssssss", "onAuthStateChanged:signed_out");
//        }
        // On close icon click finish activity
        Log.d("DemoAAAA1",imgClose.toString());
        imgClose.setOnClickListener(this);
        Log.d("DemoAAAA2",imgClose.toString());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.close_activity:
                Log.d("DemoAAAA3",imgClose.toString());
                finish();
                break;
        }

    }


    private void addControls() {
        mAuth = FirebaseAuth.getInstance();

        imgClose = (ImageView) findViewById(R.id.close_activity);
        //set animation Close
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pendulum);
        imgClose.startAnimation(animation);
        Log.d("DemoAAAA",imgClose.toString());
    }

    // Replace Login Fragment with animation
    public void replaceLoginFragment() {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new Login_Fragment(),
                        Utils.Login_Fragment).commit();
    }

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

}
