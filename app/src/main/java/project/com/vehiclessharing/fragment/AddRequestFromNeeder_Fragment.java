package project.com.vehiclessharing.fragment;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.model.AboutPlace;
import project.com.vehiclessharing.model.BirthDay;
import project.com.vehiclessharing.model.LatLngAddress;
import project.com.vehiclessharing.model.RequestFromGraber;
import project.com.vehiclessharing.model.RequestFromNeeder;
import project.com.vehiclessharing.model.Validation;

/**
 * Created by Hihihehe on 5/15/2017.
 */

public class AddRequestFromNeeder_Fragment extends DialogFragment implements View.OnClickListener {
    private static View view;
    private TextView txtTitle;
    private EditText txtCurLocation, txtDesLocation,txtTimeStart;
    private Button btnOk, btnCancel;
    private FirebaseUser mUser;
    private Context mContext;
    private DatabaseReference mDatabase;

    java.util.Calendar calendar= java.util.Calendar.getInstance();
    java.text.SimpleDateFormat sdf1=new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat sdf2=new java.text.SimpleDateFormat("dd/MM/yyyy");
    private static final LatLngBounds myBound=new LatLngBounds(new LatLng(-0,0),new LatLng(0,0));

    public static AddRequestFromNeeder_Fragment newIstance(String title){
        AddRequestFromNeeder_Fragment frag=new AddRequestFromNeeder_Fragment();
        Bundle args=new Bundle();
        args.putString("title",title);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_request_from_needer, container, false);
        addControls();
        addEvents();
        return view;
    }

    private void addEvents() {

        txtTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "timestart", Toast.LENGTH_SHORT).show();
                showTimePicker();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateRequest())
                {
                    addRequestIntoDB();
                    Toast.makeText(mContext, "Create request success", Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }
        });

    }

    private void addControls() {
        mContext=getActivity();
        String msg="If you want find a vehicle together you can fill out the form to find it";
        txtTitle= (TextView) view.findViewById(R.id.txtTitle);
        txtTitle.setText(msg);
        txtCurLocation= (EditText) view.findViewById(R.id.txtCurLocate);
        txtDesLocation= (EditText) view.findViewById(R.id.txtDesLocate);
        txtTimeStart= (EditText) view.findViewById(R.id.txtTimeStart);
        btnOk= (Button) view.findViewById(R.id.btnNeederAddOK);
        btnCancel= (Button) view.findViewById(R.id.btnAddNeederCancel);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        String fullAddress= AboutPlace.getInstance().getCurrentPlace(mContext);
        txtCurLocation.setText(fullAddress);
        txtTimeStart.setText(sdf1.format(calendar.getTime()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAddNeederCancel:{
                dismiss();
            }
            case R.id.btnNeederAddOK: {
                if(validateRequest())
                {
                    addRequestIntoDB();
                }
                dismiss();
            }
        }

    }

    private void showTimePicker() {
        TimePickerDialog.OnTimeSetListener callBack = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                //Hiển thị sự thay đổi theo người dùng
                calendar.set(java.util.Calendar.HOUR_OF_DAY, i);
                calendar.set(java.util.Calendar.MINUTE, i1);
                txtTimeStart.setText(sdf1.format(calendar.getTime()));
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getActivity(),
                callBack,
                calendar.get(java.util.Calendar.HOUR_OF_DAY),
                calendar.get(java.util.Calendar.MINUTE), true
        );

        //nếu đối số cuối =true thì định dạng 24h, =false định dạng 12h
        timePickerDialog.show();
    }
    private boolean validateRequest() {
        String curLocate = txtCurLocation.getText().toString();
        String desLocate=txtDesLocation.getText().toString();
        boolean checkNull=false;
        if(!Validation.isEmpty(curLocate) && !Validation.isEmpty(desLocate))
        {
            checkNull=true;
        }
        return checkNull;
    }
    private void addRequestIntoDB() {
        // Toast.makeText(mActivity, "add DB", Toast.LENGTH_SHORT).show();
        mUser= FirebaseAuth.getInstance().getCurrentUser();
        String userId=mUser.getUid();

        LatLng latLngCurLocation=AboutPlace.getInstance().getLatLngByName(mContext,txtCurLocation.getText().toString());
        LatLng latLngDesLocation=AboutPlace.getInstance().getLatLngByName(mContext,txtDesLocation.getText().toString());
        LatLngAddress curLocation=new LatLngAddress(latLngCurLocation.latitude,latLngCurLocation.longitude);
        LatLngAddress desLocation=new LatLngAddress(latLngDesLocation.latitude,latLngDesLocation.longitude);
        //Date date=new Date();
        String curDate=sdf2.format(calendar.getTime());
        String timeStart=txtTimeStart.getText().toString();
        RequestFromNeeder requestFromNeeder=new RequestFromNeeder(userId,curLocation,desLocation,timeStart,curDate);
        mDatabase.child("requestfromneeder").child(userId).setValue(requestFromNeeder);
    }
}
