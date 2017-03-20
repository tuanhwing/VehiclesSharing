package project.com.vehiclessharing.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.activity.HomeActivity;

import static com.facebook.GraphRequest.TAG;

/**
 * Created by Tuan on 14/03/2017.
 */

public class Profile_Fragment extends Fragment implements View.OnClickListener{

    private static View view;

    private static final int RESULT_OK = -1;

    private FirebaseUser mUser;
    private TextView txtEmail, txtFullname,txtPhone, txtSex;
    private ImageView imgUser;
    private Button btnChangeImgSD;
    private ProgressBar prgImgUser;
//    private Bitmap bmImgUser;


    public Profile_Fragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.profile_layout, container, false);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("ProfileAAAAAAAA", String.valueOf(mUser.getPhotoUrl()));
//        Log.d("ProfileAAAAAAAA",mUser.getDisplayName());
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
        btnChangeImgSD.setOnClickListener(this);
        prgImgUser.setVisibility(View.INVISIBLE);
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
        btnChangeImgSD = (Button) view.findViewById(R.id.btnChangeImgSD);
        if(HomeActivity.loginWith != 0){
            btnChangeImgSD.setVisibility(View.GONE);
        }
        prgImgUser = (ProgressBar) view.findViewById(R.id.prgImgUser);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnChangeImgSD:

                // Call checkValidation method
                callIntentPickImg();
                break;

        }
    }

    // Pick image in SDcard
    private void callIntentPickImg() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            //GETTING IMAGE FROM GALLERY
            if (resultCode == RESULT_OK){
                prgImgUser.setVisibility(View.VISIBLE);
                HomeActivity.prgImgUser.setVisibility(View.VISIBLE);
                Uri targetUri = data.getData();
//                Log.d("PICK IMGGGGGG",targetUri.toString());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    HomeActivity.bmImgUser = decodeUri(getActivity(),targetUri,100);
//                    HomeActivity.bmImgUser = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(targetUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                HomeActivity.bmImgUser.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();
                StorageReference fileRef =  FirebaseStorage.getInstance().getReference().child("avatar").child(mUser.getUid()+".jpg");
                UploadTask uploadTask = fileRef.putBytes(data1);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.d("upload","failed");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.d("upload","Succeeded:" + downloadUrl);
                    }
                });
//                storageImg(decodeUri(getActivity(),targetUri,100));
//                try {
//                    HomeActivity.bmImgUser = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(targetUri));
//                } catch (FileNotFoundException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }

            }
        }

    private void storageImg(Uri targetUri) {
//        Storage iamge in Firebase
        StorageReference fileRef =  FirebaseStorage.getInstance().getReference().child("avatar").child(mUser.getUid()+".jpg");
        fileRef.putFile(targetUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("UPLOADDDDAAAAA",taskSnapshot.getDownloadUrl().toString());
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(taskSnapshot.getDownloadUrl())
                        .build();
                mUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    imgUser.setImageBitmap(HomeActivity.bmImgUser);
                                    HomeActivity.imgUser.setImageBitmap(HomeActivity.bmImgUser);
                                    prgImgUser.setVisibility(View.INVISIBLE);
                                    HomeActivity.prgImgUser.setVisibility(View.INVISIBLE);
                                    Log.d("UPLOADDDDAAAAA", "User URL updated.");
                                }
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT);
            }
        });
    }

    //[Start]Resize Image after upload Storage Firebase
    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth
                , height_tmp = o.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }
    //[End]Resize Image after upload Storage Firebase
}
