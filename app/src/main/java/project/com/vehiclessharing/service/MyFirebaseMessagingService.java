package project.com.vehiclessharing.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import project.com.vehiclessharing.R;

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

//        sendNotification(null);
//        Log.d(TAG,"11111");
//        BitmapDrawable bitmapdraw;
//        bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.temp);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Log.d(TAG,"22222");
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.user)
//                .setLargeIcon(bitmapdraw.getBitmap())
//                .addAction(R.drawable.accept, "Accept", null)
//                .addAction(R.drawable.cancel, "Cancel", null)
//                .setOngoing(true)
//                .setContentTitle("Title demo")
//                .setContentText(remoteMessage.getNotification().getBody())
//                .setAutoCancel(false)
//                .setSound(defaultSoundUri)
//                //.setContentIntent(pendingIntent)
//                .setPriority(Notification.PRIORITY_HIGH);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//        Log.d(TAG,"end");


    }

    public void sendNotification(Map<String, String> dataMap){
//        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
//        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
//
//        String currentPackageName = componentInfo.getClassName();
//        if(currentPackageName.equals("project.com.vehiclessharing.activity.HomeActivity")) {
//            //Do whatever here
//            Log.d("FCM ServiceAAAAA","AAAAAAAAAA ne");
//        }
//        else {
        Log.d(TAG,"1");
            BitmapDrawable bitmapdraw;
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.temp);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Log.d(TAG,"2");
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.user)
                    .setLargeIcon(bitmapdraw.getBitmap())
                    .addAction(R.drawable.accept, "Accept", null)
                    .addAction(R.drawable.cancel, "Cancel", null)
                    .setOngoing(true)
                    .setContentTitle("Title demo")
                    .setContentText("Demo Notification")
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    //.setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_HIGH);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        Log.d(TAG,"end");
//        }
    }
}
