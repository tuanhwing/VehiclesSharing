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
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.model.AddressOnDevice;
import project.com.vehiclessharing.model.BirthdayOnDevice;
import project.com.vehiclessharing.model.Validation;
import project.com.vehiclessharing.sqlite.DatabaseHelper;

import static project.com.vehiclessharing.activity.HomeActivity.currentUser;
import static project.com.vehiclessharing.activity.HomeActivity.loginWith;
import static project.com.vehiclessharing.activity.HomeActivity.mUser;

/**
 * Created by Tuan on 03/04/2017.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imgProfile;
    private TextView txtFullName;
    private TextView txtEmail;
    private TextView txtPhone;
    private TextView txtSex;
    public static TextView txtBirthday;
    private TextView txtAddress;
    private Button btnEditProfile;

    private Drawable mDrawable;
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
        setContentView(R.layout.activity_profile);
//        getSupportActionBar().hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addControls();//Initialize instance
        setContentUI();//Set content inside layout
        addEvents();//Handler event for button
    }


    /**
     * Button back in tollbar
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialize instance
     */
    private void addControls() {
        db = new DatabaseHelper(ProfileActivity.this);

        imgProfile = (ImageView) findViewById(R.id.img_user);
        txtFullName = (TextView) findViewById(R.id.txt_fullname);
        txtEmail = (TextView) findViewById(R.id.txt_email);
        txtSex = (TextView) findViewById(R.id.txt_sex);
        txtPhone = (TextView) findViewById(R.id.txt_phone);
        txtBirthday = (TextView) findViewById(R.id.txt_birthday);
        txtAddress = (TextView) findViewById(R.id.txt_address);
        btnEditProfile = (Button) findViewById(R.id.btn_edit);


        mDrawable = getResources().getDrawable(R.drawable.errorvalid);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        mDatabase = FirebaseDatabase.getInstance().getReference();

        db = DatabaseHelper.getInstance(ProfileActivity.this);//Instance DatabaseHelper
    }

    /**
     * Handler event for button
     */
    private void addEvents() {
        btnEditProfile.setOnClickListener(this);
    }


    /**
     * Set content inside layout
     */
    private void setContentUI() {

        if(loginWith == 0){
            txtFullName.setText(currentUser.getUser().getFullName());
            txtEmail.setText(currentUser.getUser().getEmail());
            txtSex.setText(currentUser.getUser().getSex());
            txtPhone.setText(currentUser.getUser().getPhoneNumber());
            BirthdayOnDevice birthDay = currentUser.getUser().getBirthDay();
            txtBirthday.setText(birthDay.getDay() + "/" + birthDay.getMonth() + "/" + birthDay.getYear());
            AddressOnDevice address = currentUser.getUser().getAddress();
            txtAddress.setText(address.getDistrict() + " " + address.getProvince() + " " + address.getCountry());
            String url = String.valueOf(currentUser.getUser().getImage());//url avatar user
            if(url.equals("")){
                 imgProfile.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.temp));
            } else {
                Picasso.with(ProfileActivity.this).load(url).into(imgProfile);
            }
            btnEditProfile.setVisibility(View.VISIBLE);
        } else {
            txtFullName.setText(mUser.getDisplayName());
            txtEmail.setText(mUser.getEmail());
            Picasso.with(ProfileActivity.this).load(mUser.getPhotoUrl()).into(imgProfile);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_edit: {
                startActivity(new Intent(ProfileActivity.this,EditProfileActivity.class));
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
//                hideOrShowSave();
            }

        }
    }

}
