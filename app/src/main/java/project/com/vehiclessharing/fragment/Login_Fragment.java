package project.com.vehiclessharing.fragment;


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
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.constant.Utils;
import project.com.vehiclessharing.custom.CustomToast;
import project.com.vehiclessharing.model.UserSessionManager;
import project.com.vehiclessharing.model.Validation;

import static com.google.android.gms.internal.zzt.TAG;
import static project.com.vehiclessharing.activity.MainActivity.mProgress;
import static project.com.vehiclessharing.constant.Utils.SignUp_Fragment;


public class Login_Fragment extends Fragment implements View.OnClickListener {
    private static View view;

    /* Client used to interact with Google APIs. */
    public static UserSessionManager session;

    //Facebook
    private CallbackManager mCallbackManager;

    /* Request code used to invoke sign in user interactions. */
    public static final int RC_SIGN_IN = 0;

    private FirebaseAuth mAuth;//Instance authentication firebase


    private static EditText txtEmail;
    private static EditText txtPassword;
    private static Button btnLogin;
    private static LoginButton loginfbButton;
    private static SignInButton loginggButton;
    private static TextView forgotPassword;
    private static TextView signUp;
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
        return view;
    }


    /**
     * Initiate Views
     */
    private void addControls() {
        fragmentManager = getActivity().getSupportFragmentManager(); // fragement manager to switch fragment another

        session = new UserSessionManager(getActivity());//session login with google

//        //[Start] Setup for progress
//        mProgress =new ProgressDialog(getActivity());
//        mProgress.setTitle(Utils.SignIn);
//        mProgress.setMessage(Utils.PleaseWait);
//        mProgress.setCancelable(false);
//        mProgress.setCanceledOnTouchOutside(false);
//        //[End] Setup for progress

        mAuth = FirebaseAuth.getInstance(); // instance Authentication firebase

        //[Start] Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        loginfbButton = (LoginButton) view.findViewById(R.id.btnFbLogin);
        loginfbButton.setReadPermissions("email", "public_profile");
        loginfbButton.setFragment(this);
        //[End] Initialize Facebook Login button


        //[Start]setText Google Login button
        loginggButton = (SignInButton) view.findViewById(R.id.btnGgLogin);
        setGooglePlusButtonText(loginggButton,Utils.TextButtonGG);
        //[End] setText Google Login button


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

//        //Swipe Left -> Right
//        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
//
//            @Override
//            public void onClick() {
//                super.onClick();
//                // your on click here
//            }
//
//            @Override
//            public void onDoubleClick() {
//                super.onDoubleClick();
//                // your on onDoubleClick here
//            }
//
//            @Override
//            public void onLongClick() {
//                super.onLongClick();
//                // your on onLongClick here
//            }
//
//            @Override
//            public void onSwipeUp() {
//                super.onSwipeUp();
//                // your swipe up here
//            }
//
//            @Override
//            public void onSwipeDown() {
//                super.onSwipeDown();
//                // your swipe down here.
//            }
//
//            @Override
//            public void onSwipeLeft() {
//                super.onSwipeLeft();
//                // your swipe left here.
//                signUp.callOnClick();
//
//            }
//
//            @Override
//            public void onSwipeRight() {
//                super.onSwipeRight();
//                // your swipe right here.
//            }
//        });
//
    }

    /**
     * Set text for button google login
     * @param loginggButton instance reference to button google in layout
     * @param s content button for button
     */
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

    /**
     * Set Listeners
     */
    private void addEvents() {
        btnLogin.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);
        loginggButton.setOnClickListener(this);

        // Initialize Facebook Login button
        loginfbButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
//                mProgress.dismiss();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
//                mProgress.dismiss();
            }
        });

        // Set check listener over checkbox for showing and hiding password
        show_hide_password
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

                        // If it is checked then show password else hide password
                        if (isChecked) {

                            show_hide_password.setText(R.string.hide_pwd);// change checkbox text

                            txtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                            txtPassword.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_password.setText(R.string.show_pwd);// change checkbox text

                            txtPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            txtPassword.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });
    }

    /**
     * Handling login facebook
     * @param accessToken
     */
    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken:" + accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        mProgress.dismiss();
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    /**
     * Handling button/textview click
     * @param v
     */
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
            case R.id.btnGgLogin:
                signInGoogle();
                break;
        }

    }


    /**
     * Sign in google plus
     */
    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(session.mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Handling Result for login with google/facebook
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            mProgress.show();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
//                mProgress.dismiss();
                Toast.makeText(getActivity(),"Sign in google failed!",Toast.LENGTH_SHORT).show();
                Log.d("RESULTAAAAA", String.valueOf(result.getStatus()));
            }
        }
        else {
            mProgress.show();
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * FirebaseAuthentication with Gooogle
     * @param acct account selected from signInIntent
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        mProgress.dismiss();
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {

                        }
                        // ...
                    }
                });
    }

    /**
     * Check validation before login
     */
    private void checkValidation() {
        // Get email and password
        String getEmailId = txtEmail.getText().toString();
        String getPassword = txtPassword.getText().toString();

        // Check for both field is empty or not
        if (Validation.isEmpty(getEmailId) || Validation.isEmpty(getPassword)) {
            loginLayout.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(getActivity(), view,
                    Utils.EnterBothCredentials);

        }
        // Check if email id is valid or not
        else {
            Validation validation = Validation.checkValidEmail(getEmailId);
            if (!validation.getIsValid())
                new CustomToast().Show_Toast(getActivity(), view,
                        validation.getMessageValid());

                // Else do login with email and password
            else {

                signInWithEmailAndPassword(getEmailId,getPassword);

            }
        }
    }
    //[End]Check Validation before login

    /**
     * Sign in with email and password firebase
     * @param getEmailId user's email
     * @param getPassword password's email
     */
    private void signInWithEmailAndPassword(String getEmailId, String getPassword){
        mProgress.show();
        mAuth.signInWithEmailAndPassword(getEmailId, getPassword)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        mProgress.dismiss();
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            new CustomToast().Show_Toast(getActivity(), view,
                                    task.getException().getMessage());
                        }
//                        else getProfileUser();
                    }
                });
    }

}
