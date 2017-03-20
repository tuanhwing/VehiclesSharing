package project.com.vehiclessharing.constant;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Tuan on 07/03/2017.
 */

public class Utils {
    //Email Validation pattern
    public static final String regEx = "\\b[A-Za-z0-9]+@[A-Za-z]+\\.[A-Za-z]{2,4}\\b";
    public static final String regPx = "^[0-9]*$";

    //Setting for FirebaseApp

    public static final FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(),
            new FirebaseOptions.Builder()
                    .setDatabaseUrl("https://vehiclessharing-74957.firebaseio.com/")
                    .setApiKey("AIzaSyCe6VAITl-FDrMnbOxvhaMudE8RCR0dPSU")
                    .setApplicationId("1:1093380309543:android:56f0bc5cd958205a").build(),
            "VehiclesSharing");

    //Fragments Tags
    public static final String Login_Fragment = "Login_Fragment";
    public static final String Home_Fragment = "Home_Fragment";
    public static final String Profile_Fragment = "Profile_Fragment";
    public static final String SignUp_Fragment = "SignUp_Fragment";
    public static final String ForgotPassword_Fragment = "ForgotPassword_Fragment";


    //Value Login each Facebook/Google+/Email
    public static final String Email_Signin = "password";
    public static final String Facebook_Signin = "facebook.com";
    public static final String Google_Signin = "google.com";
}
