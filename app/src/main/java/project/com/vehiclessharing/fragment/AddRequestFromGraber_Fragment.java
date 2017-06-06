package project.com.vehiclessharing.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.application.ApplicationController;
import project.com.vehiclessharing.custom.CustomIconMarker;
import project.com.vehiclessharing.model.AboutPlace;
import project.com.vehiclessharing.model.LatLngAddress;
import project.com.vehiclessharing.model.RequestFromGraber;
import project.com.vehiclessharing.model.Validation;
import static project.com.vehiclessharing.constant.Utils.DEVICE_TOKEN;
/**
 * Created by Hihihehe on 5/15/2017.
 */

public class AddRequestFromGraber_Fragment extends DialogFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private View view;
    private TextView txtTitle;
    private EditText txtCurLocation, txtDesLocation;
    private Spinner spVehicleType;
    private Button btnOK, btnCancel;
    private Activity mActivity;
    protected GoogleApiClient mGoogleApiClient;
    private FirebaseUser mUser;
    private Context mContext;
    private DatabaseReference mDatabase;
   // private RecyclerView mRecyclerView;
   // private GooglePlacesAutocompleteAdapter mplacesAutocompleteAdapter;
    private ImageView imgClearTextCur, imgClearTextDes;
    private PlaceAutocompleteFragment autocompleteDesFragment, autocompleteCurFragment;
    RequestDataFromGraber requestDataFromGraber;
    private String[] vehicleType = new String[1];
    private Drawable mDrawable;
   // private AutocompleteFilter typeFilter;

    private static final LatLngBounds myBound = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));

    public static AddRequestFromGraber_Fragment newIstance(String title) {
        AddRequestFromGraber_Fragment frag = new AddRequestFromGraber_Fragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       requestDataFromGraber= (RequestDataFromGraber) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        view = inflater.inflate(R.layout.add_request_from_graber, container, false);
        addControls();
        addEvents();
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    private void addEvents() {

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateRequest()) {
                    addRequestIntoDB(vehicleType[0]);
                    Toast.makeText(mContext, "Create request success", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        spVehicleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vehicleType[0] = spVehicleType.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imgClearTextCur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtCurLocation.setText("");
            }
        });
        imgClearTextDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtDesLocation.setText("");
            }
        });


        autocompleteDesFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.d("DemoPlace", "Place: " + place.getName());
                txtDesLocation.setText(place.getAddress());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.d("DemoPlace", "An error occurred: " + status);
            }
        });

        autocompleteCurFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                txtCurLocation.setText(place.getAddress());
            }

            @Override
            public void onError(Status status) {

            }
        });
        /*
        txtDesLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                //Toast.makeText(mContext, "Can't connect google api", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRecyclerView.setVisibility(View.VISIBLE);
                if(!s.toString().equals("") && mGoogleApiClient.isConnected()){
                    mplacesAutocompleteAdapter.getFilter().filter(s.toString());
                    //Toast.makeText(mContext, "connect google api", Toast.LENGTH_SHORT).show();

                }
                else if(!mGoogleApiClient.isConnected())
                {
                    Toast.makeText(mContext, "Can't connect google api", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

    }

    private boolean validateRequest() {
        String curLocate = txtCurLocation.getText().toString();
        String desLocate = txtDesLocation.getText().toString();
        boolean checkNull = true;
        if (Validation.isEmpty(curLocate)) {
            txtCurLocation.setError("Current location must be not empty");
            checkNull=false;
        }
        if(Validation.isEmpty(desLocate))
        {
            txtDesLocation.setError("Destination location must be not empty");
            checkNull=false;
        }
        return checkNull;
    }

    private void addRequestIntoDB(String vehicleType) {
        // Toast.makeText(mActivity, "add DB", Toast.LENGTH_SHORT).show();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = mUser.getUid();

        LatLng latLngCurLocation = AboutPlace.getInstance().getLatLngByName(mContext, txtCurLocation.getText().toString());
        LatLng latLngDesLocation = AboutPlace.getInstance().getLatLngByName(mContext, txtDesLocation.getText().toString());
        LatLngAddress source=new LatLngAddress(latLngCurLocation.latitude,latLngCurLocation.longitude);
        LatLngAddress destination=new LatLngAddress(latLngDesLocation.latitude,latLngDesLocation.longitude);
        String deviceId=ApplicationController.sharedPreferences.getString(DEVICE_TOKEN,null);;
        Log.e("deviceID",deviceId);
        RequestFromGraber requestFromGraber = new RequestFromGraber(userId, source, destination, vehicleType,deviceId);
        if(CustomIconMarker.isOnline(mContext)) {
            mDatabase.child("requestfromgraber").child(userId).setValue(requestFromGraber);
            requestDataFromGraber.getRequestFromGraber(requestFromGraber);
        }
        else {
            Toast.makeText(mContext, "Internet disable", Toast.LENGTH_SHORT).show();
        }
    }

    private void addControls() {
        mContext = getActivity();
        String msg = "If you have a vehicle and you want find a people together you can fill out the form to find it";
        txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        txtTitle.setText(msg);
        txtCurLocation = (EditText) view.findViewById(R.id.txtCurLocation);
        txtDesLocation = (EditText) view.findViewById(R.id.txtDesLocation);
        spVehicleType = (Spinner) view.findViewById(R.id.spVehicleType);
        btnOK = (Button) view.findViewById(R.id.btnOK);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        //mRecyclerView= (RecyclerView) view.findViewById(R.id.rvDesLocation);
        imgClearTextCur = (ImageView) view.findViewById(R.id.imgClearCurLocation);
        imgClearTextDes = (ImageView) view.findViewById(R.id.imgClearDesLocation);


        //Context acctivity=getActivity();
        String fullAddress = AboutPlace.getInstance().getCurrentPlace(mContext);
        txtCurLocation.setText(fullAddress);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        autocompleteDesFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteCurFragment= (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_cur_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
        autocompleteDesFragment.setFilter(typeFilter);
        autocompleteCurFragment.setFilter(typeFilter);
        mDrawable = getResources().getDrawable(R.drawable.ic_warning_red_600_24dp);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        //autocompleteCurFragment.setFilter(typeFilter);

       /* mplacesAutocompleteAdapter=new GooglePlacesAutocompleteAdapter(mContext,R.layout.location_row,mGoogleApiClient,myBound,null);

        mRecyclerView.setAdapter(mplacesAutocompleteAdapter);*/
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // mGoogleApiClient.connect();
    }

    @Override
    public void onClick(View v) {
        switch (view.getId()) {
            case R.id.imgClearCurLocation:
                txtCurLocation.setText("");
                break;
            case R.id.imgClearDesLocation:
                txtDesLocation.setText("");
                break;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
          if (autocompleteCurFragment != null) {
            getFragmentManager().beginTransaction().remove(autocompleteCurFragment).commit();
        }
        if(autocompleteDesFragment!=null)
        {
            getFragmentManager().beginTransaction().remove(autocompleteDesFragment).commit();
        }
    }

    public interface RequestDataFromGraber
    {
        public void getRequestFromGraber(RequestFromGraber requestFromGraber);
    }

}
