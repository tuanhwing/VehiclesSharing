package project.com.vehiclessharing.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import io.realm.Realm;
import project.com.vehiclessharing.R;
import project.com.vehiclessharing.constant.Utils;
import project.com.vehiclessharing.fragment.DatePicker_Fragment;
import project.com.vehiclessharing.model.BirthDay;
import project.com.vehiclessharing.model.Validation;
import project.com.vehiclessharing.utils.ImageClass;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher{

    private FirebaseStorage storage;//Instance reference Storage Firebase

    private ImageView avatarUser;//Instance reference imageview inside layout
    private EditText edFullName;//Instance reference edittext inside layout
    private EditText edPhoneNumber;//Instance reference edittext inside layout
    private RadioButton rdMale;//Instance reference radio button inside layout
    private RadioButton rdFemale;//Instance reference radio button inside layout
    public static TextView txtBirthday;//Instance reference textview inside layout
    private EditText edAddress;//Instance reference edittext inside layout
    private static Button btnSave;//Instance reference button inside layout

    private static int REQUEST_IMAGE_SDCARD = 100;//Value request activity pick image in SDcard
    private static Bitmap bmImageUser;//Instance to save image picked by user in SDcard

    private static boolean isImageChanged = false;//Controll avatar's user is changed or not
    private static boolean isFullNameChanged = false;//Controll fullname's user is changed or not
    private static boolean isPhoneNumberChanged = false;//Controll phonenumber's user is changed or not
    private static boolean isSexChanged = false;//Controll fullname's user is changed or not
    public static boolean isBirthdayChanged = false;//Controll fullname's user is changed or not
    public static boolean isAddressChanged = false;//Controll fullname's user is changed or not

    private static BirthDay birthDayTemp;//Instance reference Date picker

    private Realm realm;//Instance to work with Realm
    private DatabaseReference mDatabase;//Instance reference Realtime Database Firebase
    private static Drawable mDrawable;//Icon edittext when text invalid



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addControlls();
        setContentUI();
        addEvents();
    }

    /**
     * Set content to all fields
     */
    private void setContentUI() {
        edFullName.setText(HomeActivity.currentUser.getUser().getFullName());
        edPhoneNumber.setText(HomeActivity.currentUser.getUser().getPhoneNumber());
        txtBirthday.setText(HomeActivity.currentUser.getUser().getBirthDay().getDay() + "/" +
                HomeActivity.currentUser.getUser().getBirthDay().getMonth() + "/" +
                HomeActivity.currentUser.getUser().getBirthDay().getYear());
        edAddress.setText(HomeActivity.currentUser.getUser().getAddress().getDistrict() + " , " +
                HomeActivity.currentUser.getUser().getAddress().getProvince() + " , " +
                HomeActivity.currentUser.getUser().getAddress().getCountry());

        String url = HomeActivity.currentUser.getUser().getImage();
        if(!url.isEmpty() || !url.equals("null")){
            if(isOnline())
                Picasso.with(EditProfileActivity.this).load(url).into(avatarUser);
            else Picasso.with(getApplicationContext())
                    .load(url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(avatarUser);

        }

        if(HomeActivity.currentUser.getUser().getSex().equals("Male")) rdMale.setChecked(true);
        else rdFemale.setChecked(true);
    }

    /**
     * Handle event the fields layout
     */
    private void addEvents() {
        rdFemale.setOnClickListener(this);
        rdMale.setOnClickListener(this);
        avatarUser.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        edFullName.addTextChangedListener(this);
        edPhoneNumber.addTextChangedListener(this);
        rdMale.setOnClickListener(this);
        rdFemale.setOnClickListener(this);
        txtBirthday.setOnClickListener(this);
    }

    private void addControlls() {
        storage = FirebaseStorage.getInstance();
        realm = Realm.getDefaultInstance();

        avatarUser = (ImageView) findViewById(R.id.img_user);
        edFullName = (EditText) findViewById(R.id.ed_full_name);
        edPhoneNumber = (EditText) findViewById(R.id.ed_phone_number);
        edAddress = (EditText) findViewById(R.id.ed_address);
        txtBirthday = (TextView) findViewById(R.id.txt_birthday);
        rdMale = (RadioButton) findViewById(R.id.rd_male);
        rdFemale = (RadioButton) findViewById(R.id.rd_female);
        btnSave = (Button) findViewById(R.id.btn_save);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDrawable = getResources().getDrawable(R.drawable.errorvalid);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
    }

    /**
     * Pick image in SDcard
     */
    private void callIntentPickImg() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_SDCARD);
    }
    /**
     * Visible button when user changed profile
     */
    public static void hideOrShowButton(){
        if(isImageChanged || isFullNameChanged || isPhoneNumberChanged ||
                isSexChanged || isBirthdayChanged || isAddressChanged)
            btnSave.setVisibility(View.VISIBLE);
        else btnSave.setVisibility(View.INVISIBLE);
    }

    /**
     * Update data profile
     */
    private void uploadProfileData(){
        if(isImageChanged){
            updateImage(getByteFromImageView());
            isImageChanged = false;
        }
        if(isFullNameChanged){
            updateFullName(edFullName.getText().toString());
            isFullNameChanged = false;
        }
        if(isPhoneNumberChanged){
            updatePhoneNumber(edPhoneNumber.getText().toString());
            isPhoneNumberChanged =false;
        }
        if(isSexChanged){
            updateSex();
            isSexChanged = false;
        }
        if(isBirthdayChanged){
            updateBirthday();
            isBirthdayChanged =false;
            birthDayTemp = new BirthDay(0,0,0);
        }
        Toast.makeText(EditProfileActivity.this,"Update profile successful!",Toast.LENGTH_SHORT).show();
        hideOrShowButton();
    }

    /**
     * Update image to Storage Firebase
     * @param data byte[] from imageView
     */
    private void updateImage(byte[] data){
        if(data != null){
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            // Create a child reference
            // imagesRef now points to "images"
            StorageReference imagesRef = storageRef.child("avatar").child(HomeActivity.currentUser.getUserId() + ".jpg");

            UploadTask uploadTask = imagesRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(EditProfileActivity.this,"Uploads image unsuccessful!",Toast.LENGTH_SHORT).show();
                    Log.e(Utils.TAG_ERROR_UPLOAD_IMAGE_FIREBASE,String.valueOf(exception.getMessage()));
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    //Update url avatar's user Firebase
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUrl)
                            .build();
                    HomeActivity.mUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(Utils.TAG_UPLOAD_IMAGE, "User profile updated.");
                                    }
                                    else Log.d(Utils.TAG_UPLOAD_IMAGE, "User profile update failed.");
                                }
                            });

                    //Update url avatar's user data on device
                    realm.beginTransaction();
                    HomeActivity.currentUser.getUser().setImage(String.valueOf(downloadUrl));
                    realm.commitTransaction();

                }
            });
        }

    }


    /**
     * // Get the data from an ImageView as bytes
     * @return byte[]
     */
    private byte[] getByteFromImageView() {
        byte[] data = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmImageUser.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data = baos.toByteArray();
        } catch (Exception e){
            Log.d(Utils.TAG_ERROR_GET_BYTE_IMAGE,String.valueOf(e.getMessage()));
        }
        return data;
    }

    /**
     * Update fullname's user
     * @param fullname
     */
    private void updateFullName(String fullname){
        try {
            //Update data Firebase
            mDatabase.child("users").child(HomeActivity.currentUser.getUserId()).child("fullName").setValue(fullname);

            //Update Display name user Firebase
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(fullname)
                    .build();
            HomeActivity.mUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(Utils.TAG_UPDATE_PROFILE, "User profile updated.");
                            }
                            else Log.d(Utils.TAG_UPDATE_PROFILE, "User profile update failed.");
                        }
                    });

            //Update on divice
            realm.beginTransaction();
            HomeActivity.currentUser.getUser().setFullName(fullname);
            realm.commitTransaction();

        } catch (Exception e){
            Log.e(Utils.TAG_UPDATE_PROFILE,String.valueOf(e.getMessage()));
        }

    }

    /**
     * Update phonenumber's user
     * @param phonenumber
     */
    private void updatePhoneNumber(String phonenumber){
        try {
            //Update data Firebase
            mDatabase.child("users").child(HomeActivity.currentUser.getUserId()).child("phoneNumber").setValue(phonenumber);

            //Update on divice
            realm.beginTransaction();
            HomeActivity.currentUser.getUser().setPhoneNumber(phonenumber);
            realm.commitTransaction();

        } catch (Exception e){
            Log.e(Utils.TAG_UPDATE_PROFILE,String.valueOf(e.getMessage()));
        }

    }

    /**
     * Update sex's user
     */
    private void updateSex(){
        try {
            String sex;
            if(rdMale.isChecked()) sex = rdMale.getText().toString();
            else sex = rdFemale.getText().toString();

            //Update data Firebase
            mDatabase.child("users").child(HomeActivity.currentUser.getUserId()).child("sex").setValue(sex);

            //Update on divice
            realm.beginTransaction();
            HomeActivity.currentUser.getUser().setSex(sex);
            realm.commitTransaction();

        } catch (Exception e){
            Log.e(Utils.TAG_UPDATE_PROFILE,String.valueOf(e.getMessage()));
        }
    }

    /**
     * Handle date picker
     * @param day
     * @param month
     * @param year
     */
    public static void handleDatePick(int day,int month,int year){
        BirthDay birthDay = new BirthDay(HomeActivity.currentUser.getUser().getBirthDay().getDay(),
                HomeActivity.currentUser.getUser().getBirthDay().getMonth(),
                HomeActivity.currentUser.getUser().getBirthDay().getYear());
        Validation validation = Validation.checkValidBirthDay(new BirthDay(day,month,year));
        if(!birthDay.compareBirhtday(new BirthDay(day,month,year))) {
            if(validation.getIsValid()){
                isBirthdayChanged = true;
                txtBirthday.setText(day + "/" + month + "/" + year);
                birthDayTemp = new BirthDay(day,month + 1,year);
                txtBirthday.setError(null);

            } else {
                isBirthdayChanged = false;
                txtBirthday.setError(validation.getMessageValid(),mDrawable);
                birthDayTemp = new BirthDay(0,0,0);
            }

        } else {
            isBirthdayChanged = false;
            birthDayTemp = new BirthDay(0,0,0);
            txtBirthday.setError(null);
        }
        hideOrShowButton();
    }

    private void updateBirthday(){
        try {

            int day = birthDayTemp.getDay();
            int month = birthDayTemp.getMonth() + 1;
            int year = birthDayTemp.getYear();
            //Update data Firebase
            mDatabase.child("users").child(HomeActivity.currentUser.getUserId()).child("birthDay").child("day").setValue(day);
            mDatabase.child("users").child(HomeActivity.currentUser.getUserId()).child("birthDay").child("month").setValue(month);
            mDatabase.child("users").child(HomeActivity.currentUser.getUserId()).child("birthDay").child("year").setValue(year);

            //Update on divice
            realm.beginTransaction();
            HomeActivity.currentUser.getUser().getBirthDay().setDay(day);
            HomeActivity.currentUser.getUser().getBirthDay().setMonth(month);
            HomeActivity.currentUser.getUser().getBirthDay().setYear(year);
            realm.commitTransaction();

        } catch (Exception e){
            Log.e(Utils.TAG_UPDATE_PROFILE,String.valueOf(e.getMessage()));
        }
    }

    /**
     * Internet is avaibalility
     * @return true if can access internet
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_SDCARD){
            if(resultCode == RESULT_OK){

                Uri targetUri = data.getData();
                try {
                    bmImageUser = ImageClass.decodeUri(EditProfileActivity.this,targetUri,100);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                avatarUser.setImageBitmap(bmImageUser);
                isImageChanged = true;
                hideOrShowButton();//Visible button save profile
            }

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rd_male:{
                if(HomeActivity.currentUser.getUser().getSex().equals("FeMale")) isSexChanged = true;
                else isSexChanged = false;
                rdMale.setChecked(true);
                rdFemale.setChecked(false);
                hideOrShowButton();
                break;
            }
            case R.id.rd_female: {
                if(HomeActivity.currentUser.getUser().getSex().equals("Male")) isSexChanged = true;
                else isSexChanged = false;
                rdMale.setChecked(false);
                rdFemale.setChecked(true);
                hideOrShowButton();
                break;
            }
            case R.id.btn_save:{
                new AlertDialog.Builder(EditProfileActivity.this)
                        .setTitle("Confirm edit profile")
                        .setMessage("Are you sure you want to save profile?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                uploadProfileData();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
            }
            case R.id.img_user:{
                callIntentPickImg();
                break;
            }
            case R.id.txt_birthday: {
                android.app.DialogFragment newFragment = new DatePicker_Fragment();
                newFragment.show(getFragmentManager(),"Data Picker");
                break;
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(edFullName.hasFocus()){//text changed for edittext fullname
            String fullname = edFullName.getText().toString();
            if(fullname.isEmpty())
                edFullName.setError("Fullname is required", mDrawable);
            else {
                if(!fullname.equals(HomeActivity.currentUser.getUser().getFullName()))
                    isFullNameChanged = true;
                else isFullNameChanged = false;
            }
        }
        if(edPhoneNumber.hasFocus()){
            String phonenumber = edPhoneNumber.getText().toString();
            if(phonenumber.isEmpty()) edPhoneNumber.setError("Phone number is required", mDrawable);
            else {
                Validation validation = Validation.checkValidPhone(phonenumber);
                if(!validation.getIsValid())
                    edPhoneNumber.setError(validation.getMessageValid(),mDrawable);
                else {
                    if(phonenumber.equals(HomeActivity.currentUser.getUser().getPhoneNumber()))
                        isPhoneNumberChanged = false;
                    else isPhoneNumberChanged = true;
                }
            }
        }
        hideOrShowButton();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
