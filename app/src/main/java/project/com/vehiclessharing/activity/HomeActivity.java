package project.com.vehiclessharing.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.net.URL;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.constant.Utils;
import project.com.vehiclessharing.fragment.Home_Fragment;
import project.com.vehiclessharing.fragment.Profile_Fragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView = null;
    private Toolbar toolbar = null;
    private View viewHeader = null; // View header
    private TextView txtFullName,txtEmail;
    private FirebaseUser mUser;
    public static ImageView imgUser;
    public static ProgressBar prgImgUser;
    public static Bitmap bmImgUser = null;


    private static FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //set fragment initially
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameContainer, new Home_Fragment(), Utils.Home_Fragment).commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
                finish();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        addControls();
        addEvents();




//        fragmentManager.beginTransaction().replace(R.id.frameHome, new Home_Fragment(), Utils.Home_Fragment).commit();
    }

    private void addEvents() {
        if(bmImgUser == null){
            prgImgUser.setVisibility(View.VISIBLE);
        }
        txtFullName.setText(mUser.getDisplayName());
        txtEmail.setText(mUser.getEmail());
        downloadImageUser();
    }

    private void addControls() {

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        viewHeader = navigationView.getHeaderView(0);
        txtEmail = (TextView) viewHeader.findViewById(R.id.txtEmail);
        txtFullName = (TextView) viewHeader.findViewById(R.id.txtFullName);
        imgUser = (ImageView) viewHeader.findViewById(R.id.imgUser);
        prgImgUser = (ProgressBar) viewHeader.findViewById(R.id.prgImgUser);

        Log.d("UPLOADAAAA", String.valueOf(mUser.getPhotoUrl()));

//        //Storage iamge in Firebase
//        StorageReference fileRef =  FirebaseStorage.getInstance().getReference().child("avatar").child(mUser.getUid()+".jpg");
//        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.temp);
//        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Log.d("UPLOADDDDAAAAA",taskSnapshot.getDownloadUrl().toString());
//                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                        .setPhotoUri(taskSnapshot.getDownloadUrl())
//                        .build();
//                mUser.updateProfile(profileUpdates)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d("UPLOADDDDAAAAA", "User URL updated.");
//                                }
//                            }
//                        });
//            }
//        });
    }

    public void downloadImageUser() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    InputStream in = new URL(mUser.getPhotoUrl().toString()).openStream();
                    bmImgUser = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    // log error
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (bmImgUser != null){
                    imgUser.setImageBitmap(bmImgUser);
                    prgImgUser.setVisibility(View.INVISIBLE);
                }

            }

        }.execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            fragmentManager.beginTransaction().replace(R.id.frameContainer, new Home_Fragment(), Utils.Home_Fragment).commit();
        } else if (id == R.id.nav_gallery) {
            fragmentManager.beginTransaction().replace(R.id.frameContainer, new Profile_Fragment(), Utils.Profile_Fragment).commit();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
