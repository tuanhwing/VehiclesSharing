package project.com.vehiclessharing.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.model.User;
import project.com.vehiclessharing.sqlite.DatabaseHelper;

/**
 * Created by Tuan on 03/04/2017.
 */

public class ProfileActivity extends AppCompatActivity {

    private User userCurrent;
    private DatabaseHelper db;
    private TextView txt_fullname;
    private TextView txt_email;
    private TextView txt_phone;
    private TextView txt_sex;
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
        txt_fullname = (TextView) findViewById(R.id.txt_fullname);
        txt_email = (TextView) findViewById(R.id.txt_email);
        txt_phone = (TextView) findViewById(R.id.txt_phone);
        txt_sex = (TextView) findViewById(R.id.txt_sex);
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
    }
}
