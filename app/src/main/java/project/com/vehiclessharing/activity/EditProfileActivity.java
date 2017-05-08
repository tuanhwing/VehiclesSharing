package project.com.vehiclessharing.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import project.com.vehiclessharing.R;

public class EditProfileActivity extends AppCompatActivity {

    ImageView avatarUser;
    EditText edFullName;
    EditText edPhoneNumber;
    RadioButton rdMale;
    RadioButton rdFemale;
    TextView txtBirthday;
    EditText edAddress;

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
    }

    private void addEvents() {

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
}
