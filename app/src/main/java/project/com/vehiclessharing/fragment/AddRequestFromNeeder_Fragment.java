package project.com.vehiclessharing.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.model.AboutPlace;
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
    private PlaceAutocompleteFragment autocompleteCurFragment, autocompleteDesFragment;
    private ImageView imgClearCurLocation, imgClearDesLocation;
    private Drawable mDrawable;
    private RequestDataFromNeeder requestDataFromNeeder;

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

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        requestDataFromNeeder= (RequestDataFromNeeder) context;
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
        txtTimeStart.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        imgClearCurLocation.setOnClickListener(this);
        imgClearDesLocation.setOnClickListener(this);
        autocompleteCurFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                txtCurLocation.setText(place.getAddress());
            }

            @Override
            public void onError(Status status) {

            }
        });
        autocompleteDesFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                txtDesLocation.setText(place.getAddress());
            }

            @Override
            public void onError(Status status) {

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

        autocompleteCurFragment= (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_cur_fragment);
        autocompleteDesFragment= (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_destin_fragment);
        imgClearCurLocation= (ImageView) view.findViewById(R.id.imgClearCurLocation);
        imgClearDesLocation= (ImageView) view.findViewById(R.id.imgClearDesLocation);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
        autocompleteDesFragment.setFilter(typeFilter);
        autocompleteDesFragment.setFilter(typeFilter);
        mDrawable = getResources().getDrawable(R.drawable.errorvalid);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAddNeederCancel:{
               dismiss();
                break;
            }
            case R.id.btnNeederAddOK: {
                if(validateRequest())
                {
                    addRequestIntoDB();
                    Toast.makeText(mContext, "Create request success", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                break;
            }
            case R.id.imgClearCurLocation:
            {
                txtCurLocation.setText("");
                break;
            }
            case R.id.imgClearDesLocation:
            {
                txtDesLocation.setText("");
                break;
            }
            case R.id.txtTimeStart:
            {
                showTimePicker();
                break;
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
                mContext,
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
        boolean checkNull=true;
        if(Validation.isEmpty(curLocate))
        {
            txtCurLocation.setError("Vị trí bắt đầu không nên để trống");
            checkNull=false;
        }
        else if(Validation.isEmpty(desLocate))
        {
            txtDesLocation.setError("Vị trí kết thúc không nên để trống");
            checkNull=false;
        }
        return checkNull;
    }
    private void addRequestIntoDB() {
        // Toast.makeText(mActivity, "add DB", Toast.LENGTH_SHORT).show();
        mUser= FirebaseAuth.getInstance().getCurrentUser();
        String userId=mUser.getUid();

        LatLng latLngCurLocation=AboutPlace.getInstance().getLatLngByName(mContext,txtCurLocation.getText().toString());
        LatLng latLngDesLocation=AboutPlace.getInstance().getLatLngByName(mContext,txtDesLocation.getText().toString());

        //Date date=new Date();
        String curDate=sdf2.format(calendar.getTime());
        String timeStart=txtTimeStart.getText().toString();
        RequestFromNeeder requestFromNeeder=new RequestFromNeeder(userId,latLngCurLocation,latLngDesLocation,timeStart,curDate);
        mDatabase.child("requestfromneeder").child(userId).setValue(requestFromNeeder);
        requestDataFromNeeder.getRequestFromNeeder(requestFromNeeder);
     //   getTargetFragment().onActivityResult(getTargetRequestCode(),1);
    }
    public interface RequestDataFromNeeder{
        public void getRequestFromNeeder(RequestFromNeeder requestFromNeeder);
    }
}
