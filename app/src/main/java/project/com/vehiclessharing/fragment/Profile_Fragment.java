package project.com.vehiclessharing.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.activity.HomeActivity;

import static com.facebook.GraphRequest.TAG;

/**
 * Created by Tuan on 14/03/2017.
 */

public class Profile_Fragment extends Fragment {

    private static View view;

    private FirebaseUser mUser;
    private TextView txtEmail, txtFullname,txtPhone, txtSex;
    private ImageView imgUser;
//    private Bitmap bmImgUser;


    public Profile_Fragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.profile_layout, container, false);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("ProfileAAAAAAAA", String.valueOf(mUser.getPhotoUrl()));
        Log.d("ProfileAAAAAAAA",mUser.getDisplayName());
//        txtEmail = (TextView) view.findViewById(R.id.txtEmail);
//        txtFullname = (TextView) view.findViewById(R.id.txtFullName);
//        txtPhone = (TextView) view.findViewById(R.id.txtPhone);
//        txtSex = (TextView) view.findViewById(R.id.txtSex);

//        txtFullname.setText(mUser.getDisplayName());
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

//                user = dataSnapshot.getValue(User.class);
//                txtEmail.setText(user.getEmail());
//                txtFullname.setText(user.getFullName());
//                txtPhone.setText(user.getPhoneNumber());
//                txtSex.setText(user.getSex());
//                Log.d(TAG, "User name: " + user.getFullName() + ", email " + user.getEmail() + ", phone " + user.getPhoneNumber() + ", sex " + user.getSex());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        addControls();
        addEvents();

        return view;
    }

    private void addEvents() {
        imgUser.setImageBitmap(HomeActivity.bmImgUser);
//       downloadImageUser();
    }

//    private void downloadImageUser() {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                try {
//                    InputStream in = new URL(mUser.getPhotoUrl().toString()).openStream();
//                    bmImgUser = BitmapFactory.decodeStream(in);
//                } catch (Exception e) {
//                    // log error
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void result) {
//                if (bmImgUser != null)
//
//            }
//
//        }.execute();
//    }

    private void addControls() {
        imgUser = (ImageView) view.findViewById(R.id.imgUser);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

}
