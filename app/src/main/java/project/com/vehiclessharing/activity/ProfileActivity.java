package project.com.vehiclessharing.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.model.User;
import project.com.vehiclessharing.sqlite.DatabaseHelper;

/**
 * Created by Tuan on 03/04/2017.
 */

public class ProfileActivity extends AppCompatActivity {

    private User userCurrent;
    private DatabaseHelper db;
    private EditText txt_fullname;
    private EditText txt_email;
    private EditText txt_phone;
    private EditText txt_sex;
    private EditText txt_address;
    private EditText txt_birthday;
    private ImageView img_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        addCOntrols();
        addEvents();
        setContentUI();
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

    private void addCOntrols() {
        txt_fullname = (EditText) findViewById(R.id.txt_fullname);
        txt_email = (EditText) findViewById(R.id.txt_email);
        txt_phone = (EditText) findViewById(R.id.txt_phone);
        txt_sex = (EditText) findViewById(R.id.txt_sex);
        txt_address = (EditText) findViewById(R.id.txt_address);
        txt_birthday = (EditText) findViewById(R.id.txt_birthday);
        img_user = (ImageView) findViewById(R.id.imgUser);
        db = new DatabaseHelper(ProfileActivity.this);
    }

    private void addEvents() {
    }


    private void setContentUI() {
        Log.d("ProfileAAAAAA",HomeActivity.mUser.getUid());
        userCurrent = db.getUser(HomeActivity.mUser.getUid());
        String sex = userCurrent.getSex();
        txt_email.setText(userCurrent.getEmail());
        txt_fullname.setText(userCurrent.getFullName());
        txt_sex.setText(sex);
        txt_phone.setText(userCurrent.getPhoneNumber());
        if(userCurrent.getImage().equals("")){
            if(sex.equals("Male")) img_user.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.image_male));
            else img_user.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.image_female));
        }

        if(userCurrent.getAddress().getProvince().equals("")
                && userCurrent.getAddress().getCountry().equals("")
                && userCurrent.getAddress().getDistrict().equals(""))
            txt_address.setText("SELECT ADDRESS...");
        else {
            String address = userCurrent.getAddress().getDistrict() + " "
                    + userCurrent.getAddress().getProvince() + " "
                    + userCurrent.getAddress().getCountry();
            txt_address.setText(address);
        }

        if(userCurrent.getBirthDay().getDay() == 0
                && userCurrent.getBirthDay().getMonth() == 0
                && userCurrent.getBirthDay().getMonth() == 0)
            txt_birthday.setText("SELECT BIRTHDAY...");
        else {
            String birthday = userCurrent.getBirthDay().getMonth() + "/"
                    + userCurrent.getBirthDay().getDay() + "/"
                    + userCurrent.getBirthDay().getYear();
            txt_birthday.setText(birthday);
        }
    }
}
