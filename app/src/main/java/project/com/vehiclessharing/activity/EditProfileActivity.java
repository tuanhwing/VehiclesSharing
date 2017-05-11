package project.com.vehiclessharing.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import project.com.vehiclessharing.R;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView avatarUser;
    private EditText edFullName;
    private EditText edPhoneNumber;
    private RadioButton rdMale;
    private RadioButton rdFemale;
    private TextView txtBirthday;
    private EditText edAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addControlls();
        setContentUI();
        addEvents();
    }

    private void setContentUI() {
        edFullName.setText(HomeActivity.currentUser.getUser().getFullName());
        edPhoneNumber.setText(HomeActivity.currentUser.getUser().getPhoneNumber());
        txtBirthday.setText(HomeActivity.currentUser.getUser().getBirthDay().getDay() + "/" +
                HomeActivity.currentUser.getUser().getBirthDay().getMonth() + "/" +
                HomeActivity.currentUser.getUser().getBirthDay().getYear());
        edAddress.setText(HomeActivity.currentUser.getUser().getAddress().getDistrict() + " , " +
                HomeActivity.currentUser.getUser().getAddress().getProvince() + " , " +
                HomeActivity.currentUser.getUser().getAddress().getCountry());

        Picasso.with(EditProfileActivity.this).load(HomeActivity.currentUser.getUser().getImage()).into(avatarUser);
        if(HomeActivity.currentUser.getUser().getSex().equals("Male")) rdMale.setChecked(true);
        else rdFemale.setChecked(true);
    }

    private void addEvents() {
        rdFemale.setOnClickListener(this);
        rdMale.setOnClickListener(this);
    }

    private void addControlls() {
        avatarUser = (ImageView) findViewById(R.id.img_user);
        edFullName = (EditText) findViewById(R.id.ed_full_name);
        edPhoneNumber = (EditText) findViewById(R.id.ed_phone_number);
        edAddress = (EditText) findViewById(R.id.ed_address);
        txtBirthday = (TextView) findViewById(R.id.txt_birthday);
        rdMale = (RadioButton) findViewById(R.id.rd_male);
        rdFemale = (RadioButton) findViewById(R.id.rd_female);

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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rd_male:{
                rdMale.setChecked(true);
                rdFemale.setChecked(false);
                break;
            }
            case R.id.rd_female: {
                rdMale.setChecked(false);
                rdFemale.setChecked(true);
                break;
            }
        }
    }
}
