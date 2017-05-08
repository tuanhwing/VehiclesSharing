package project.com.vehiclessharing.service;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

/**
 * Created by Tuan on 13/03/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM ServiceAAAAA";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;

        String currentPackageName = componentInfo.getClassName();

//        ActivityManager am =(ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
//        ActivityManager.RunningTaskInfo task = tasks.get(0); // current task
//        ComponentName rootActivity = task.baseActivity;
//
//
//        String currentPackageName = rootActivity.getPackageName();
        if(currentPackageName.equals("project.com.vehiclessharing.activity.HomeActivity")) {
            //Do whatever here
            Log.d("FCM ServiceAAAAA","AAAAAAAAAA ne");
        }


    }
}
