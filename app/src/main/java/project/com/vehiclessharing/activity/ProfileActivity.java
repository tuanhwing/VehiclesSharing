package project.com.vehiclessharing.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.ArrayList;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.fragment.DatePicker_Fragment;
import project.com.vehiclessharing.model.User;
import project.com.vehiclessharing.model.UserAddress;
import project.com.vehiclessharing.model.Validation;
import project.com.vehiclessharing.sqlite.DatabaseHelper;

import static project.com.vehiclessharing.R.id.txt_birthday;
import static project.com.vehiclessharing.R.id.txt_fullname;
import static project.com.vehiclessharing.activity.HomeActivity.mUser;

/**
 * Created by Tuan on 03/04/2017.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    public static User userCurrent;
    private EditText txtFullName;
    private EditText txtEmail;
    private EditText txtPhone;
    private Spinner spinnerSex;
    private Spinner spinnerCountry;
    private Spinner spinnerProvince;
    private Spinner spinnerDistrict;
    public static TextView txtBirthday;
    private ImageView imgProfile;
    public static Button btnSaveProfile;
    private Button btnChangeImg;
    private Button btnDatePicker;
    private Drawable mDrawable;
    private String fullName;//fullname's user
    private String phone;//phonenumber's user
    private String sex;//sex's user
    private String country;//country's user
    private String province;//province's user
    private String district;//district's user
    public static int day;//day in birthday
    public static int month;//month in birthday
    public static int year;//year in birthday
    public static boolean isFullNameChanged;//Controll fullname's user is changed or not
    public static boolean isPhoneChanged;//Controll phonenumber's user is changed or not
    public static boolean isSexChanged;//Controll sex's user is changed or not
    public static boolean isCountryChanged;//Controll address's user is changed or not
    public static boolean isProvinceChanged;//Controll address's user is changed or not
    public static boolean isDistrictChanged;//Controll address's user is changed or not
    public static boolean isBirthDayChanged;//Controll birthday's user is changed or not
    public static boolean isImageChanged;//Controll avatar's user is changed or not
    private Validation validation = null;//Instance to check validation
    public static DatabaseReference mDatabase;//Instance Database Firebase
    public static DatabaseHelper db;//get Instance SQLite
    private static int REQUEST_IMAGE_SDCARD = 100;//Value request activity pick image in SDcard
    private static Bitmap bmImageUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        addControls();
        setContentUI();
        addEvents();
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

    private void addControls() {
        txtFullName = (EditText) findViewById(txt_fullname);
        txtEmail = (EditText) findViewById(R.id.txt_email);
        txtPhone = (EditText) findViewById(R.id.txt_phone);
        spinnerSex = (Spinner) findViewById(R.id.sex_spinner);
        spinnerCountry = (Spinner) findViewById(R.id.country_spinner);
        spinnerProvince = (Spinner) findViewById(R.id.province_spinner);
        spinnerDistrict = (Spinner) findViewById(R.id.district_spinner);
        txtBirthday = (TextView) findViewById(txt_birthday);
        imgProfile = (ImageView) findViewById(R.id.imgUser);
        btnSaveProfile = (Button) findViewById(R.id.btn_save_profile);
        btnDatePicker = (Button) findViewById(R.id.btn_date_picker);
        btnChangeImg = (Button) findViewById(R.id.btn_change_img);
        db = new DatabaseHelper(ProfileActivity.this);

        bmImageUser = null;
        isFullNameChanged = false;//Controll fullname's user is changed or not
        isPhoneChanged = false;//Controll phonenumber's user is changed or not
        isSexChanged = false;//Controll sex's user is changed or not
        isCountryChanged = false;//Controll address's user is changed or not
        isProvinceChanged = false;//Controll address's user is changed or not
        isDistrictChanged = false;//Controll address's user is changed or not
        isBirthDayChanged = false;//Controll birthday's user is changed or not
        isImageChanged = false;//Controll avatar's user is changed or not

        mDrawable = getResources().getDrawable(R.drawable.errorvalid);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        mDatabase = FirebaseDatabase.getInstance().getReference();

        db = DatabaseHelper.getInstance(ProfileActivity.this);//Instance DatabaseHelper
    }

    private void addEvents() {
        setTextChangedListener(txtFullName,fullName);
        setTextChangedListener(txtPhone,phone);
        btnSaveProfile.setOnClickListener(this);
        spinnerSex.setOnItemSelectedListener(this);
        spinnerCountry.setOnItemSelectedListener(this);
        spinnerDistrict.setOnItemSelectedListener(this);
        spinnerProvince.setOnItemSelectedListener(this);
        btnDatePicker.setOnClickListener(this);
        btnChangeImg.setOnClickListener(this);
    }

    /**
     * Set text changed listener for editText
     * @param edText instance editText
     * @param value Value of edit when load activity
     */
    private void setTextChangedListener(final EditText edText, final String value) {
        edText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switch (edText.getId()){
                    case R.id.txt_fullname: {
                        if(s.length() > 0){
                            if(!s.toString().equals(value)) isFullNameChanged = true;
                            else isFullNameChanged = false;
                        } else {
                            edText.setError("Fullname is required", mDrawable);
                        }
                        break;
                    }
                    case R.id.txt_phone: {
                        if(s.length() > 0){
                            if(!s.toString().equals(value)) {
                                validation = Validation.checkValidPhone(s.toString());
                                if(!validation.getIsValid()){
                                    edText.setError("Phone is invalid", mDrawable);
                                    isPhoneChanged = false;
                                } else isPhoneChanged = true;
                            }
                            else isPhoneChanged = false;
                        } else {
                            edText.setError("Phone is required", mDrawable);
                        }
                        break;
                    }
                }
                hideOrShowSave();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static void hideOrShowSave() {
        if(isFullNameChanged || isPhoneChanged || isSexChanged ||
                isCountryChanged || isProvinceChanged || isDistrictChanged ||
                isBirthDayChanged || isImageChanged) btnSaveProfile.setVisibility(View.VISIBLE);
        else btnSaveProfile.setVisibility(View.INVISIBLE);
    }


    private void setContentUI() {
        Log.d("ProfileAAAAAA", mUser.getUid());
        userCurrent = db.getUser(mUser.getUid());
        fullName = userCurrent.getFullName();
        phone = userCurrent.getPhoneNumber();
        txtEmail.setText(userCurrent.getEmail());
        txtFullName.setText(userCurrent.getFullName());

        //[START] sex
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sexs_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerSex.setAdapter(adapter);
        selectValue(spinnerSex,userCurrent.getSex());
        //[END] sex

        //[START] address
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterCountry = ArrayAdapter.createFromResource(this,
                R.array.country_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerCountry.setAdapter(adapterCountry);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterProvince = ArrayAdapter.createFromResource(this,
                R.array.province_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterProvince.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerProvince.setAdapter(adapterProvince);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterDistrict = ArrayAdapter.createFromResource(this,
                R.array.district_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterDistrict.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerDistrict.setAdapter(adapterDistrict);

        selectValueAddress(userCurrent.getAddress());
        //[END] address

        txtPhone.setText(userCurrent.getPhoneNumber());
        String url = String.valueOf(mUser.getPhotoUrl());//url avatar user
        if(url.equals("")){
            if(userCurrent.getSex().equals("Male")) imgProfile.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.image_male));
            else imgProfile.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.image_female));
        } else {
            if(isOnline())
                Picasso.with(getApplicationContext()).load(url).into(imgProfile);
            else Picasso.with(getApplicationContext())
                    .load(url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(imgProfile);
        }


        if(userCurrent.getBirthDay().getDay() == 0
                && userCurrent.getBirthDay().getMonth() == 0
                && userCurrent.getBirthDay().getMonth() == 0)
            txtBirthday.setText("SELECT BIRTHDAY...");
        else {
            String birthday = userCurrent.getBirthDay().getMonth() + "/"
                    + userCurrent.getBirthDay().getDay() + "/"
                    + userCurrent.getBirthDay().getYear();
            txtBirthday.setText(birthday);
        }
    }

    /**
     * show  address
     * @param address
     */
    private void selectValueAddress(UserAddress address) {
        String country = address.getCountry();
        String province = address.getProvince();
        String district = address.getDistrict();
        if(country.equals("")) spinnerCountry.setSelection(0);
        else {
            for (int i = 0; i < spinnerCountry.getCount(); i++) {
                if (spinnerCountry.getItemAtPosition(i).equals(country)) {
                    spinnerCountry.setSelection(i);
                    break;
                }
            }
        }

        if(province.equals("")) spinnerProvince.setSelection(0);
        else {
            for (int i = 0; i < spinnerProvince.getCount(); i++) {
                if (spinnerProvince.getItemAtPosition(i).equals(province)) {
                    spinnerProvince.setSelection(i);
                    break;
                }
            }
        }

        if(district.equals("")) spinnerDistrict.setSelection(0);
        else {
            for (int i = 0; i < spinnerDistrict.getCount(); i++) {
                if (spinnerDistrict.getItemAtPosition(i).equals(district)) {
                    spinnerDistrict.setSelection(i);
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save_profile:{
                ArrayList<String> fields = new ArrayList<String>();
                if(isFullNameChanged) fields.add("fullname");
                if(isPhoneChanged) fields.add("phone");
                if(isSexChanged) fields.add("sex");
                if(isCountryChanged) fields.add("country");
                if(isProvinceChanged) fields.add("province");
                if(isDistrictChanged) fields.add("district");
                if(isBirthDayChanged) fields.add("birthday");
                if(isImageChanged) fields.add("avatar");
                updateData(fields);
                break;
            }
            case R.id.btn_date_picker: {
                DialogFragment newFragment = new DatePicker_Fragment();
                newFragment.show(getSupportFragmentManager(), "date");
                break;
            }
            case R.id.btn_change_img: {
                callIntentPickImg();
                break;
            }
        }
    }

    /**
     * Update data firebase + device(SQLite)
     * @param fields the keys changed
     */
    private void updateData(ArrayList<String> fields) {
        if(fields != null){
            String userId = mUser.getUid();
            for (String key: fields) {
                if(key.equals("fullname")) {
                    try {
                        String fullname = String.valueOf(txtFullName.getText());
                        mDatabase.child("users").child(userId).child("fullName").setValue(fullname);
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fullname)
                                .build();

                        mUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("updateData", "User profile updated.");
                                        }
                                    }
                                });
                        if(db.updateFullName(userId,fullname))
                            isFullNameChanged = false;
                    } catch(Exception e){
                        Log.e("error_profile",e.getMessage());
                    }

                }
                if(key.equals("phone")){
                    try {
                        String phone = String.valueOf(txtPhone.getText());
                        mDatabase.child("users").child(userId).child("phoneNumber").setValue(phone);
                        if(db.updatePhoneNumber(userId,phone))
                            isPhoneChanged = false;
                    } catch(Exception e){
                        Log.e("error_profile",e.getMessage());
                    }

                }
                if(key.equals("sex")){
                    try {
                        mDatabase.child("users").child(userId).child("sex").setValue(sex);
                        if(db.updateSex(userId,sex))
                            isSexChanged = false;
                    } catch(Exception e){
                        Log.e("error_profile",e.getMessage());
                    }

                }

                //[START] address
                if(key.equals("country")){
                    try {
                        mDatabase.child("users").child(userId).child("address").child("country").setValue(country);
                        if(db.updateCountry(userId,country))
                            isCountryChanged = false;
                    } catch(Exception e){
                        Log.e("error_profile",e.getMessage());
                    }

                }

                if(key.equals("province")){
                    try {
                        mDatabase.child("users").child(userId).child("address").child("province").setValue(province);
                        if(db.updateProvince(userId,province))
                            isProvinceChanged = false;
                    } catch(Exception e){
                        Log.e("error_profile",e.getMessage());
                    }

                }

                if(key.equals("district")){
                    try {
                        mDatabase.child("users").child(userId).child("address").child("district").setValue(district);
                        if(db.updateDistrict(userId,district))
                            isDistrictChanged = false;
                    } catch(Exception e){
                        Log.e("error_profile",e.getMessage());
                    }

                }
                //[END] address

                //[START] birthday
                if(key.equals("birthday")){
                    try{
                        mDatabase.child("users").child(userId).child("birthDay").child("day").setValue(day);
                        mDatabase.child("users").child(userId).child("birthDay").child("month").setValue(month);
                        mDatabase.child("users").child(userId).child("birthDay").child("year").setValue(year);
                        if(db.updateDay(userId,day) && db.updateMonth(userId,month) && db.updateYear(userId,year)){
                            isBirthDayChanged = false;
                            day = month = year= 0;
                        }

                    } catch (Exception e){
                        Log.e("error_profile",e.getMessage());

                    }

                }
                //[END] birthday

                if(key.equals("avatar")){
                    uploadAvatar();
                    isImageChanged = false;
                }


            }

            if(!isFullNameChanged && !isPhoneChanged && !isSexChanged &&
                    !isCountryChanged && !isProvinceChanged && !isDistrictChanged){
                userCurrent = db.getUser(mUser.getUid());
                Toast.makeText(ProfileActivity.this,"Update profile success!",Toast.LENGTH_LONG).show();
                btnSaveProfile.setVisibility(View.INVISIBLE);
            }


        }

    }

    private void uploadAvatar(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmImageUser.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference fileRef =  FirebaseStorage.getInstance().getReference().child("avatar").child(mUser.getUid()+".jpg");
        UploadTask uploadTask = fileRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("error_upload_image",e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                String url = String.valueOf(taskSnapshot.getDownloadUrl());
                String userId = HomeActivity.mUser.getUid();
                //[START]Update profile profile user firebase
                mDatabase.child("users").child(userId).child("image").setValue(url);
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(taskSnapshot.getDownloadUrl())
                        .build();
                mUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    HomeActivity.imgUser.setImageBitmap(HomeActivity.bmImgUser);
                                }
                            }
                        });
                //[END]Update profile profile user firebase
                db.uploadURLImage(userId,url);

            }
        });
    }

    /**
     * Select sex for spinner Sexs
     * @param spinner instance spinner
     * @param value sex's user
     */
    private void selectValue(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    /**
     * Resize Image after upload Storage Firebase
     * @param c
     * @param uri
     * @param requiredSize 100
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth
                , height_tmp = o.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
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
     * Internet is avaibalility
     * @return true if can access internet
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_SDCARD){
            if(resultCode == RESULT_OK){

                Uri targetUri = data.getData();
                try {
                    bmImageUser = decodeUri(ProfileActivity.this,targetUri,100);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                imgProfile.setImageBitmap(bmImageUser);
                isImageChanged = true;
                hideOrShowSave();
            }

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        String value = String.valueOf(parent.getItemAtPosition(position));
        Spinner spinner = (Spinner) parent;
        switch (spinner.getId()){
            case R.id.sex_spinner: {
                if(!value.equals(userCurrent.getSex())) {
                    isSexChanged = true;
                    sex = value;
                } else {
                    isSexChanged = false;
                    sex = null;
                }
                break;
            }
            case R.id.country_spinner: {
                if(!value.equals(userCurrent.getAddress().getCountry())) {
                    isCountryChanged = true;
                    country = value;
                } else {
                    isCountryChanged = false;
                    country = null;
                }
                break;
            }
            case R.id.province_spinner: {
                if(!value.equals(userCurrent.getAddress().getProvince())) {
                    isProvinceChanged = true;
                    province = value;
                } else {
                    isProvinceChanged = false;
                    province = null;
                }
                break;
            }
            case R.id.district_spinner: {
                if(!value.equals(userCurrent.getAddress().getDistrict())) {
                    isDistrictChanged = true;
                    district = value;
                } else {
                    isDistrictChanged = false;
                    district = null;
                }
                break;
            }
        }
        hideOrShowSave();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
