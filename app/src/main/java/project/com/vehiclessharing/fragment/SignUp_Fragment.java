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

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.activity.SigninActivity;
import project.com.vehiclessharing.constant.Utils;
import project.com.vehiclessharing.custom.CustomToast;
import project.com.vehiclessharing.model.BirthDay;
import project.com.vehiclessharing.model.User;
import project.com.vehiclessharing.model.UserAddress;
import project.com.vehiclessharing.model.Validation;
import project.com.vehiclessharing.database.DatabaseHelper;

import static com.google.android.gms.internal.zzt.TAG;


/**
 * Created by Tuan on 07/03/2017.
 */

public class SignUp_Fragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth2;
    private ProgressDialog mProgress;
    private Drawable mDrawable;
    private DatabaseReference mDatabase;

    private static View view;
    private static EditText txtFullName;
    private static EditText txtEmail;
    private static EditText txtPhone;
    private static EditText txtPassword;
    private static EditText txtConfirmPassword;
    private static RadioButton rdMale, rdFemale;
    private static TextView login;
    private static Button btnSignup;
    private static CheckBox terms_conditions;

    DatabaseHelper db;//Instance to using SQLITE

    private Validation validation = null;//Instance to check validation

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

    /**
     * Initialize all views
     */
    private void addControls() {

        mAuth2 = FirebaseAuth.getInstance(Utils.myApp); //Instance second FirebaseAuth

        mDatabase = FirebaseDatabase.getInstance(Utils.myApp).getReference(); //Instance Database

        db = DatabaseHelper.getInstance(getActivity());//get Instance SQLITE
        //[Start] Setup for progress
        mProgress =new ProgressDialog(getActivity());
        mProgress.setTitle(Utils.SignUp);
        mProgress.setMessage(Utils.PleaseWait);
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        //[End] Setup for progress

        txtFullName = (EditText) view.findViewById(R.id.txtFullName);
        rdMale = (RadioButton) view.findViewById(R.id.rdMale);
        rdFemale = (RadioButton) view.findViewById(R.id.rdFemale);
        txtEmail = (EditText) view.findViewById(R.id.txtEmail);
        txtPhone = (EditText) view.findViewById(R.id.txtPhone);
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) view.findViewById(R.id.txtConfirmPassword);
        btnSignup = (Button) view.findViewById(R.id.btnSignup);
        login = (TextView) view.findViewById(R.id.already_user);
        terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);
        mDrawable = getResources().getDrawable(R.drawable.ic_warning_red_600_24dp);
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

    /**
     * Set Listeners
     */
    private void addEvents() {
        btnSignup.setOnClickListener(this);
        login.setOnClickListener(this);
        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(Validation.isEmpty(txtFullName.getText().toString()))
                    txtFullName.setError("Fullname is required", mDrawable);
            }
        });
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(Validation.isEmpty(txtFullName.getText().toString()))
                    txtFullName.setError("Fullname is required", mDrawable);
                if(Validation.isEmpty(txtEmail.getText().toString())){
                    txtEmail.setError("Email is required",mDrawable);
                } else {
                    validation = Validation.checkValidEmail(txtEmail.getText().toString());
                    if(!validation.getIsValid()){
                        txtEmail.setError(validation.getMessageValid(),mDrawable);
                    }
                }
                if(Validation.isEmpty(txtPhone.getText().toString())){
                    txtPhone.setError("Phone is required",mDrawable);
                } else {
                    validation = Validation.checkValidPhone(txtPhone.getText().toString());
                    if(!validation.getIsValid()){
                        txtPhone.setError(validation.getMessageValid(),mDrawable);
                    }
                }
            }
        });

        txtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(Validation.isEmpty(txtFullName.getText().toString()))
                    txtFullName.setError("Fullname is required", mDrawable);
                if(Validation.isEmpty(txtEmail.getText().toString())){
                    txtEmail.setError("Email is required",mDrawable);
                } else {
                    validation = Validation.checkValidEmail(txtEmail.getText().toString());
                    if(!validation.getIsValid()){
                        txtEmail.setError(validation.getMessageValid(),mDrawable);
                    }
                }
            }
        });

        txtConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(Validation.isEmpty(txtFullName.getText().toString()))
                    txtFullName.setError("Fullname is required", mDrawable);
                if(Validation.isEmpty(txtEmail.getText().toString())){
                    txtEmail.setError("Email is required",mDrawable);
                } else {
                    validation = Validation.checkValidEmail(txtEmail.getText().toString());
                    if(!validation.getIsValid()){
                        txtEmail.setError(validation.getMessageValid(),mDrawable);
                    }
                }
                if(Validation.isEmpty(txtPhone.getText().toString())){
                    txtPhone.setError("Phone is required",mDrawable);
                } else {
                    validation = Validation.checkValidPhone(txtPhone.getText().toString());
                    if(!validation.getIsValid()){
                        txtPhone.setError(validation.getMessageValid(),mDrawable);
                    }
                }
                if(Validation.isEmpty(txtPassword.getText().toString())){
                    txtPassword.setError("Password is required",mDrawable);
                } else {
                    validation = Validation.checkValidPassword(txtPassword.getText().toString());
                    if(!validation.getIsValid()) {
                        txtPassword.setError(validation.getMessageValid(), mDrawable);
                    }
                }
            }
        });

        rdFemale.setOnClickListener(this);
        rdMale.setOnClickListener(this);
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
                new SigninActivity().replaceLoginFragment();
                break;
            case R.id.rdFemale:
                rdMale.setChecked(false);
                rdFemale.setChecked(true);
                break;

            case R.id.rdMale:
                rdMale.setChecked(true);
                rdFemale.setChecked(false);
                break;
        }

    }

    /**
     * Check Validation Method
     */
    private void checkValidation() {

        // Get all edittext texts
        final String getFullName = txtFullName.getText().toString();
        final String getEmailId = txtEmail.getText().toString();
        final String getMobileNumber = txtPhone.getText().toString();
        final String getPassword = txtPassword.getText().toString();
        String getConfirmPassword = txtConfirmPassword.getText().toString();
        Validation validation;//Instance to check feild is valid

        // Check if all strings are null or not
        if (Validation.isEmpty(getFullName)
                || (!rdMale.isChecked() && !rdFemale.isChecked())
                || Validation.isEmpty(getEmailId)
                || Validation.isEmpty(getMobileNumber)
                || Validation.isEmpty(getPassword)
                || Validation.isEmpty(getConfirmPassword))

            new CustomToast().Show_Toast(getActivity(), view,
                    "All fields are required.");


            // Check if email id valid or not
        else {
            validation = Validation.checkValidEmail(getEmailId);
            if (!validation.getIsValid())
                new CustomToast().Show_Toast(getActivity(), view,
                        validation.getMessageValid());

                //Check if phone valid or not
            else {
                validation = Validation.checkValidPhone(getMobileNumber);
                if (!validation.getIsValid())
                    new CustomToast().Show_Toast(getActivity(), view,
                            validation.getMessageValid());

                    //Check if password must be at least 6 characters
                else {
                    validation = Validation.checkValidPassword(getPassword);
                    if(!validation.getIsValid())
                        new CustomToast().Show_Toast(getActivity(), view,
                                validation.getMessageValid());

                        // Check if both password should be equal
                    else {
                        validation = Validation.checkValidConfirmPassword(getPassword,getConfirmPassword);
                        if (!validation.getIsValid())
                            new CustomToast().Show_Toast(getActivity(), view,
                                   validation.getMessageValid());

                            // Make sure user should check Terms and Conditions checkbox
                        else if (!terms_conditions.isChecked())
                            new CustomToast().Show_Toast(getActivity(), view,
                                    "Please select Terms and Conditions.");

                            // Else do signup or do your stuff
                        else {

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
//                                                UserAddress userAddress = new UserAddress();
                                                writeNewUser(mAuth2.getCurrentUser().getUid(),
                                                        mAuth2.getCurrentUser().getEmail(),
                                                        "",
                                                        getFullName.toString(),
                                                        getMobileNumber.toString(),
                                                        getSex.toString(), new UserAddress(), new BirthDay());
                                                Toast.makeText(getActivity(), "Sign up successed!",
                                                        Toast.LENGTH_SHORT).show();
                                                login.callOnClick();
                                                mAuth2.signOut();//Sign out user
                                            }

                                            // ...

                                        }
                                    });
                        }
                    }
                }
            }
        }

    }

    /**
     * Storage profile's user into both Firebase Database and SQLite
     * @param userId - userid
     * @param email - user's email
     * @param image - user's url image
     * @param fullname - user's fullname
     * @param phone - user's phoneNumber
     * @param sex - user's sex
     * @param address - user's address
     */

    private void writeNewUser(String userId, String email, String image, String fullname, String phone,
                              String sex, UserAddress address, BirthDay birthDay) {
        User user = new User(email, image, fullname, phone, sex, address, birthDay);
        //[START]Storage in Firebase Database
        mDatabase.child("users").child(userId).setValue(user);
        FirebaseUser mUser = mAuth2.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullname)
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
