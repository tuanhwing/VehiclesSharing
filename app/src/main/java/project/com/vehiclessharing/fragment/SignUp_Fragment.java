package project.com.vehiclessharing.fragment;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.activity.MainActivity;
import project.com.vehiclessharing.constant.Utils;
import project.com.vehiclessharing.custom.CustomToast;
import project.com.vehiclessharing.model.User;

import static com.google.android.gms.internal.zzt.TAG;


/**
 * Created by Tuan on 07/03/2017.
 */

public class SignUp_Fragment extends Fragment implements View.OnClickListener {
    //Add new;
    private FirebaseAuth mAuth2;
    private ProgressDialog mProgress;
    private Drawable mDrawable;
    private DatabaseReference mDatabase;

    //End new


    private static View view;
    private static EditText txtFullName, txtEmail, txtPhone, txtLocation,
            txtPassword, txtConfirmPassword;

    private static RadioButton rdMale, rdFemale;
    private static TextView login;
    private static Button btnSignup;
    private static CheckBox terms_conditions;

    public SignUp_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signup_layout, container, false);
        addControls();
        addEvents();
        return view;
    }

    // Initialize all views
    private void addControls() {
        //Add new
        //config second FirebaseAuth

//        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
//                .setDatabaseUrl("https://vehiclessharing-74957.firebaseio.com/")
//                .setApiKey("AIzaSyCe6VAITl-FDrMnbOxvhaMudE8RCR0dPSU")
//                .setApplicationId("1:1093380309543:android:56f0bc5cd958205a").build();
//        FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(),firebaseOptions,
//                "VehiclesSharing");
        mAuth2 = FirebaseAuth.getInstance(Utils.myApp);
        //End config second FirebaseAuth


        mDatabase = FirebaseDatabase.getInstance(Utils.myApp).getReference();
        mProgress =new ProgressDialog(getActivity());
        String titleId="Signing up...";
        mProgress.setTitle(titleId);
        mProgress.setMessage("Please Wait...");
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        //End new

        txtFullName = (EditText) view.findViewById(R.id.txtFullName);
        rdMale = (RadioButton) view.findViewById(R.id.rdMale);
        rdFemale = (RadioButton) view.findViewById(R.id.rdFemale);
        txtEmail = (EditText) view.findViewById(R.id.txtEmail);
        txtPhone = (EditText) view.findViewById(R.id.txtPhone);
        //txtLocation = (EditText) view.findViewById(R.id.txtLocation);
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) view.findViewById(R.id.txtConfirmPassword);
        btnSignup = (Button) view.findViewById(R.id.btnSignup);
        login = (TextView) view.findViewById(R.id.already_user);
        terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);
        mDrawable = getResources().getDrawable(R.drawable.errorvalid);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());

        // Setting text selector over textviews
        XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            login.setTextColor(csl);
            terms_conditions.setTextColor(csl);
        } catch (Exception e) {
        }
    }

    // Set Listeners
    private void addEvents() {
        btnSignup.setOnClickListener(this);
        login.setOnClickListener(this);
        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(txtFullName.getText().equals("") || txtFullName.getText().length() == 0)
                    txtFullName.setError("Fullname is required", mDrawable);
            }
        });
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(txtFullName.getText().equals("") || txtFullName.getText().length() == 0)
                    txtFullName.setError("Fullname is required", mDrawable);
                if(txtEmail.getText().length() == 0){
                    txtEmail.setError("Email is required",mDrawable);
                } else if(!checkValidEmail(txtEmail.getText().toString())){
                    txtEmail.setError("Your Email is Invalid",mDrawable);
                }
                if(txtPhone.getText().length() == 0){
                    txtPhone.setError("Phone is required",mDrawable);
                } else if(!checkValidPhone(txtPhone.getText().toString())){
                    txtPhone.setError("Your Phone is Invalid!",mDrawable);
                }
            }
        });

        txtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(txtFullName.getText().equals("") || txtFullName.getText().length() == 0)
                    txtFullName.setError("Fullname is required", mDrawable);
                if(txtEmail.getText().length() == 0){
                    txtEmail.setError("Email is required",mDrawable);
                } else if(!checkValidEmail(txtEmail.getText().toString())){
                    txtEmail.setError("Your Email is Invalid",mDrawable);
                }
            }
        });

        txtConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(txtFullName.getText().equals("") || txtFullName.getText().length() == 0)
                    txtFullName.setError("Fullname is required", mDrawable);
                if(txtEmail.getText().length() == 0){
                    txtEmail.setError("Email is required",mDrawable);
                } else if(!checkValidEmail(txtEmail.getText().toString())){
                    txtEmail.setError("Your Email is Invalid",mDrawable);
                }
                if(txtPhone.getText().length() == 0){
                    txtPhone.setError("Phone is required",mDrawable);
                } else if(!checkValidPhone(txtPhone.getText().toString())){
                    txtPhone.setError("Your Phone is Invalid!",mDrawable);
                }
                if(txtPassword.getText().length() == 0){
                    txtPassword.setError("Password is required cccccccccccccccccccc",mDrawable);
                } else if(txtPassword.getText().length() < 6) {
                    txtPassword.setError("Your Password must be at least 6 characters.", mDrawable);
                }
            }
        });

        rdFemale.setOnClickListener(this);
        rdMale.setOnClickListener(this);
    }

    private boolean checkValidEmail(String s) {
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(s);
        if(!m.find()){
            return false;
        }
        return true;
    }

    private boolean checkValidPhone(String number) {
        Pattern pattern = Pattern.compile(Utils.regPx);
        Matcher matcher = pattern.matcher(number);
        if (!matcher.matches()) {
            return false;
        } else
        if (number.length() == 10 || number.length() == 11) {
            if (number.length() == 10) {
                if (number.substring(0, 2).equals("09")) {
                    return true;
                } else {
                    return false;
                }
            } else
            if (number.substring(0, 2).equals("01")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignup:

                // Call checkValidation method
                checkValidation();
                break;

            case R.id.already_user:

                // Replace login fragment
                new MainActivity().replaceLoginFragment();
                break;
            case R.id.rdFemale:
                rdMale.setChecked(false);
                rdFemale.setChecked(true);
                Log.d("AAAAAAAAA",rdFemale.getText().toString());
                break;

            case R.id.rdMale:
                rdMale.setChecked(true);
                rdFemale.setChecked(false);
                Log.d("AAAAAAAAA",rdMale.getText().toString());
                break;
        }

    }

    // Check Validation Method
    private void checkValidation() {

        // Get all edittext texts
        final String getFullName = txtFullName.getText().toString();
        final String getEmailId = txtEmail.getText().toString();
        final String getMobileNumber = txtPhone.getText().toString();
        final String sex;
//        String getLocation = txtLocation.getText().toString();
        final String getPassword = txtPassword.getText().toString();
        String getConfirmPassword = txtConfirmPassword.getText().toString();

        // Pattern match for email id
//        Pattern p = Pattern.compile(Utils.regEx);
//        Matcher m = p.matcher(getEmailId);

        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0
                || (!rdMale.isChecked() && !rdFemale.isChecked())
                || getEmailId.equals("") || getEmailId.length() == 0
                || getMobileNumber.equals("") || getMobileNumber.length() == 0
//                || getLocation.equals("") || getLocation.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getConfirmPassword.equals("")
                || getConfirmPassword.length() == 0)

            new CustomToast().Show_Toast(getActivity(), view,
                    "All fields are required.");


            // Check if email id valid or not
        else if (!checkValidEmail(getEmailId))
            new CustomToast().Show_Toast(getActivity(), view,
                    "Your Email is Invalid.");

            //Check if phone valid or not
        else if (!checkValidPhone(getMobileNumber))
            new CustomToast().Show_Toast(getActivity(), view,
                    "Your Phone is Invalid.");

            //Check if password must be at least 6 characters
        else if(getPassword.length() < 6)
            new CustomToast().Show_Toast(getActivity(), view,
                    "Your Password must be at least 6 characters.");

            // Check if both password should be equal
        else if (!getConfirmPassword.equals(getPassword))
            new CustomToast().Show_Toast(getActivity(), view,
                    "Both password doesn't match.");

            // Make sure user should check Terms and Conditions checkbox
        else if (!terms_conditions.isChecked())
            new CustomToast().Show_Toast(getActivity(), view,
                    "Please select Terms and Conditions.");

            // Else do signup or do your stuff
        else {

//            Toast.makeText(getActivity(), "Do SignUp.", Toast.LENGTH_SHORT).show();
            final String getSex;
            if(rdMale.isChecked()){
                getSex = rdMale.getText().toString();
            }
            else getSex = rdFemale.getText().toString();

            mProgress.show();

            //Create new account
            mAuth2.createUserWithEmailAndPassword(getEmailId, getPassword)
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
                                Log.d("Sign uppppppppppp", task.getException().toString());
                            }
                            else {
//                                Log.d("USERID AAAAAA", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                                Log.d("EMAIL AAAAAA", FirebaseAuth.getInstance().getCurrentUser().getEmail());
//                                Log.d("PHONE AAAAAA", getMobileNumber.toString());
//                                Log.d("SEX AAAAAA",getSex.toString());
                                writeNewUser(mAuth2.getCurrentUser().getUid(),
                                        getFullName.toString(),
                                        mAuth2.getCurrentUser().getEmail(),
                                        getMobileNumber.toString(),
                                        getSex.toString());
                                Toast.makeText(getActivity(), "Sign up successed!",
                                        Toast.LENGTH_SHORT).show();
                                login.callOnClick();
                                mAuth2.signOut();//Sign out user
                                Log.d("sssssssss","Sign upppppppppppppp");
                            }

                            // ...

                        }
                    });
        }

    }

    private void writeNewUser(String userId, String name, String email, String phone,String sex) {
        User user = new User(name, email, phone, sex);
        mDatabase.child("users").child(userId).setValue(user);
        FirebaseUser mUser = mAuth2.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        mUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }
}
