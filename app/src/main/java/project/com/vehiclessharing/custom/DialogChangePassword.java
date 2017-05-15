package project.com.vehiclessharing.custom;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.activity.HomeActivity;

/**
 * Created by Tuan on 15/05/2017.
 */

public class DialogChangePassword extends Dialog implements View.OnClickListener{
    public Activity c;
    public Dialog d;
    public  Button btnCancelChangePW;
    public Button btnSubmitChangePW;
    public EditText edCurrentPW;
    public EditText edNewPW;
    EditText edComfirmPW;

    public DialogChangePassword(Activity c){
        super(c);
        this.c = c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.change_password_dialog);

        addControlls();
        addEvents();

    }

    private void addControlls() {
        edCurrentPW = (EditText) findViewById(R.id.ed_current_password);
        edNewPW = (EditText) findViewById(R.id.ed_new_password);
        edComfirmPW = (EditText) findViewById(R.id.ed_comfirm_password);
        btnCancelChangePW = (Button) findViewById(R.id.btn_cancel);
        btnSubmitChangePW = (Button) findViewById(R.id.btn_submit);
    }

    private void addEvents() {
        btnCancelChangePW.setOnClickListener(this);
        btnSubmitChangePW.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel: {
                dismiss();
                break;
            }
            case R.id.btn_submit: {
                // Get auth credentials from the user for re-authentication. The example below shows
                // email and password credentials but there are multiple possible providers,
                // such as GoogleAuthProvider or FacebookAuthProvider.
                AuthCredential credential = EmailAuthProvider
                        .getCredential(HomeActivity.mUser.getEmail(), String.valueOf(edCurrentPW.getText().toString()));

                Log.d("authcre_aaaaaa",String.valueOf(credential));

                // Prompt the user to re-provide their sign-in credentials
                HomeActivity.mUser.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) Toast.makeText(c,"User re-authenticated.",Toast.LENGTH_SHORT).show();
                                else Toast.makeText(c,String.valueOf(task.getException().getMessage()),Toast.LENGTH_SHORT).show();

                            }
                        });

                break;
            }
        }

    }
}
