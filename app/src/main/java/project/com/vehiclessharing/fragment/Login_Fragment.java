package project.com.vehiclessharing.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.activity.HomeActivity;
import project.com.vehiclessharing.constant.Utils;
import project.com.vehiclessharing.custom.CustomToast;
import project.com.vehiclessharing.custom.OnSwipeTouchListener;

import static com.google.android.gms.internal.zzt.TAG;
import static project.com.vehiclessharing.constant.Utils.SignUp_Fragment;


public class Login_Fragment extends Fragment implements OnClickListener {
    private static View view;

    //Add new
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    //End new


    private static EditText txtEmail, txtPassword;
    private static Button btnLogin;
    private static LoginButton loginfbButton;
    private static SignInButton loginggButton;
    private static TextView forgotPassword, signUp;
    private static CheckBox show_hide_password;
    private static LinearLayout loginLayout;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;

    public Login_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login_layout, container, false);
        addControls();
        addEvents();
        Log.d("Sign inAAAAAAAAAAAAAA1", String.valueOf(FirebaseAuth.getInstance().getCurrentUser()));
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d("Sign inAAAAAAAAAAAAAA1", String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        }
//        FirebaseAuth.getInstance().signOut();
//        Log.d("Sign inAAAAAAAAAAAAAA2", String.valueOf(FirebaseAuth.getInstance().getCurrentUser()));
        return view;
    }

    // Initiate Views
    private void addControls() {
        fragmentManager = getActivity().getSupportFragmentManager();

        //Add new
        mProgress =new ProgressDialog(getActivity());
        String titleId="Signing in...";
        mProgress.setTitle(titleId);
        mProgress.setMessage("Please Wait...");
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);


        mAuth = FirebaseAuth.getInstance();

        loginfbButton = (LoginButton) view.findViewById(R.id.btnFbLogin);
        loginfbButton.setReadPermissions("email");
        loginfbButton.setFragment(this);

        loginggButton = (SignInButton) view.findViewById(R.id.btnGgLogin);
        setGooglePlusButtonText(loginggButton,"Log in with Google +");

        //End new

        txtEmail = (EditText) view.findViewById(R.id.txtEmail);
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
        signUp = (TextView) view.findViewById(R.id.createAccount);
        show_hide_password = (CheckBox) view
                .findViewById(R.id.show_hide_password);
        loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.shake);

        // Setting text selector over textviews
        XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            forgotPassword.setTextColor(csl);
            show_hide_password.setTextColor(csl);
            signUp.setTextColor(csl);
        } catch (Exception e) {
        }
        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {

            @Override
            public void onClick() {
                super.onClick();
                // your on click here
            }

            @Override
            public void onDoubleClick() {
                super.onDoubleClick();
                // your on onDoubleClick here
            }

            @Override
            public void onLongClick() {
                super.onLongClick();
                // your on onLongClick here
            }

            @Override
            public void onSwipeUp() {
                super.onSwipeUp();
                // your swipe up here
            }

            @Override
            public void onSwipeDown() {
                super.onSwipeDown();
                // your swipe down here.
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                // your swipe left here.
                signUp.callOnClick();

            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                // your swipe right here.
            }
        });

    }

    //Set text for button google login
    private void setGooglePlusButtonText(SignInButton loginggButton, String s) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < loginggButton.getChildCount(); i++) {
            View v = loginggButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(s);
                tv.setPadding(0,5,0,5);
                //tv.setPadding(0,5,50,5);
                return;
            }
        }
    }

    //End add new

    // Set Listeners
    private void addEvents() {
        btnLogin.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);

        // Set check listener over checkbox for showing and hiding password
        show_hide_password
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

                        // If it is checkec then show password else hide
                        // password
                        if (isChecked) {

                            show_hide_password.setText(R.string.hide_pwd);// change
                            // checkbox
                            // text

                            txtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                            txtPassword.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_password.setText(R.string.show_pwd);// change
                            // checkbox
                            // text

                            txtPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            txtPassword.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                checkValidation();
                break;

            case R.id.forgot_password:

                // Replace forgot password fragment with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer,
                                new ForgotPassword_Fragment(),
                                Utils.ForgotPassword_Fragment).commit();
                break;
            case R.id.createAccount:

                // Replace signup frgament with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new SignUp_Fragment(),
                                SignUp_Fragment).commit();
                break;
        }

    }

    // Check Validation before login
    private void checkValidation() {
        // Get email id and password
        String getEmailId = txtEmail.getText().toString();
        String getPassword = txtPassword.getText().toString();

        // Check patter for email id
        Pattern p = Pattern.compile(Utils.regEx);

        Matcher m = p.matcher(getEmailId);

        // Check for both field is empty or not
        if (getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0) {
            loginLayout.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(getActivity(), view,
                    "Enter both credentials.");

        }
        // Check if email id is valid or not
        else if (!m.find())
            new CustomToast().Show_Toast(getActivity(), view,
                    "Your Email Id is Invalid.");

            // Else do login and do your stuff
        else {
//          Toast.makeText(getActivity(), "Do Login.", Toast.LENGTH_SHORT).show();

            mProgress.show();
            mAuth.signInWithEmailAndPassword(getEmailId, getPassword)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgress.dismiss();
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                new CustomToast().Show_Toast(getActivity(), view,
                                        task.getException().getMessage());
                            }
                            else{
                                //Switch screen
                                startActivity(new Intent(getActivity(),HomeActivity.class));
                                getActivity().finish();
                                Log.d("LOGINssssssssss", "ssssssssssss");
                            }



                            // ...
                        }
                    });

        }

    }
}
