package project.com.vehiclessharing.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.custom.DialogChangePassword;
import project.com.vehiclessharing.model.AddressOnDevice;
import project.com.vehiclessharing.model.BirthdayOnDevice;

import static project.com.vehiclessharing.activity.HomeActivity.currentUser;
import static project.com.vehiclessharing.activity.HomeActivity.loginWith;
import static project.com.vehiclessharing.activity.HomeActivity.mUser;

/**
 * Created by Tuan on 03/04/2017.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    public static ImageView imgProfile;
    private TextView txtFullName;
    private TextView txtEmail;
    private TextView txtPassword;
    private TextView txtPhone;
    private TextView txtSex;
    public static ProgressBar progressBar;//Instance reference progress load image inside layout
    private Dialog dialogImageFullScreen;//Dialog to show image full screen
    public static TextView txtBirthday;
    private TextView txtAddress;
    private Button btnEditProfile;

    private DialogChangePassword dialogChangePassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
//        getSupportActionBar().hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addControls();//Initialize instance
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

        imgProfile = (ImageView) findViewById(R.id.img_user);
        progressBar = (ProgressBar) findViewById(R.id.loading_progress_img);
        txtFullName = (TextView) findViewById(R.id.txt_fullname);
        txtEmail = (TextView) findViewById(R.id.txt_email);
        txtSex = (TextView) findViewById(R.id.txt_sex);
        txtPhone = (TextView) findViewById(R.id.txt_phone);
        txtBirthday = (TextView) findViewById(R.id.txt_birthday);
        txtAddress = (TextView) findViewById(R.id.txt_address);
        txtPassword = (TextView) findViewById(R.id.txt_password);
        btnEditProfile = (Button) findViewById(R.id.btn_edit);

    }

    /**
     * Handler event for button
     */
    private void addEvents() {
        btnEditProfile.setOnClickListener(this);
        txtPassword.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
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
            if(url.equals("null") || url.isEmpty()){
                 imgProfile.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.temp));
            } else {

                if(isOnline()){
                    progressBar.setVisibility(View.VISIBLE);
                    Picasso.with(ProfileActivity.this).load(url).into(imgProfile, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileActivity.this,"Error load image",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else Picasso.with(getApplicationContext())
                        .load(url)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(imgProfile);
            }
            btnEditProfile.setVisibility(View.VISIBLE);
        } else {
            txtFullName.setText(mUser.getDisplayName());
            txtEmail.setText(mUser.getEmail());
            Picasso.with(ProfileActivity.this).load(mUser.getPhotoUrl()).into(imgProfile);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_edit: {
                startActivity(new Intent(ProfileActivity.this,EditProfileActivity.class));
                break;
            }
            case R.id.txt_password: {
                dialogChangePassword = new DialogChangePassword(ProfileActivity.this);
                dialogChangePassword.show();
                break;
            }
            case R.id.img_user: {
                dialogImageFullScreen = new Dialog(ProfileActivity.this,R.style.Theme_AppCompat_NoActionBar);
                dialogImageFullScreen.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogImageFullScreen.setContentView(R.layout.image_full_screen);
                final ImageView imgFullScreen = (ImageView) dialogImageFullScreen.findViewById(R.id.image_full_screen);
                final ProgressBar progressLoadImageFull = (ProgressBar) dialogImageFullScreen.findViewById(R.id.progress_load_full);
//                String urlhere = "https://firebasestorage.googleapis.com/v0/b/vehiclessharing-74957.appspot.com/o/images%2Fi9ulaadp43dAdFkZfpsgmN3TaXs2.jpg?alt=media&token=62e31c9a-102f-48ab-98fb-0efa33c84119";

                Picasso.with(ProfileActivity.this)
                        .load(currentUser.getUser().getImage())
                        .into(imgFullScreen, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressLoadImageFull.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                Log.d("download_full_image","failedS");
                            }
                        });

                dialogImageFullScreen.show();
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(dialogImageFullScreen.isShowing()) dialogImageFullScreen.dismiss();
        else {
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentUI();//Set content inside layout
    }
}
