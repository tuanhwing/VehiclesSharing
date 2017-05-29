package project.com.vehiclessharing.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import project.com.vehiclessharing.application.ApplicationController;

import static project.com.vehiclessharing.constant.Utils.DEVICE_TOKEN;

/**
 * Created by Tuan on 13/03/2017.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIDService";


    @Override
    public void onTokenRefresh() {

        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // save token into sharedPreferences
        ApplicationController.sharedPreferences.edit().putString(DEVICE_TOKEN,refreshedToken).commit();
//        ApplicationController.sharedPreferences.getString(DEVICE_TOKEN,null);
        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);

    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {


    }
}
