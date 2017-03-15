package project.com.vehiclessharing.fragment;

import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project.com.vehiclessharing.custom.CustomToast;
import project.com.vehiclessharing.R;
import project.com.vehiclessharing.constant.Utils;
import project.com.vehiclessharing.activity.MainActivity;

public class ForgotPassword_Fragment extends Fragment implements
        OnClickListener {
    private static View view;


    private static EditText txtEmail;
    private static TextView txtForgot, txtBack;

    public ForgotPassword_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.forgotpassword_layout, container,
                false);
        addControls();
        addEvents();
        return view;
    }

    // Initialize the views
    private void addControls() {
        txtEmail = (EditText) view.findViewById(R.id.txtEmail);
        txtForgot = (TextView) view.findViewById(R.id.txtForgot);
        txtBack = (TextView) view.findViewById(R.id.txtBack);

        // Setting text selector over textviews
        XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            txtBack.setTextColor(csl);
            txtForgot.setTextColor(csl);

        } catch (Exception e) {
        }

    }

    // Set Listeners over buttons
    private void addEvents() {
        txtBack.setOnClickListener(this);
        txtForgot.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtBack:

                // Replace Login Fragment on Back Presses
                new MainActivity().replaceLoginFragment();
                break;

            case R.id.txtForgot:

                // Call Submit button task
                submitButtonTask();
                break;

        }

    }

    private void submitButtonTask() {
        String getEmailId = txtEmail.getText().toString();

        // Pattern for email id validation
        Pattern p = Pattern.compile(Utils.regEx);

        // Match the pattern
        Matcher m = p.matcher(getEmailId);

        // First check if email id is not null else show error toast
        if (getEmailId.equals("") || getEmailId.length() == 0)

            new CustomToast().Show_Toast(getActivity(), view,
                    "Please enter your Email Id.");

            // Check if email id is valid or not
        else if (!m.find())
            new CustomToast().Show_Toast(getActivity(), view,
                    "Your Email Id is Invalid.");

            // Else submit email id and fetch passwod or do your stuff
        else
            Toast.makeText(getActivity(), "Get Forgot Password.",
                    Toast.LENGTH_SHORT).show();
    }
}