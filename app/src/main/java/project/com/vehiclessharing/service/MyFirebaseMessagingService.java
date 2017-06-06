package project.com.vehiclessharing.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.constant.Utils;

/**
 * Created by Tuan on 13/03/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM ServiceAAAAA";
    private static final int MY_NOTIFICATION_ID = 69;

    private static final int MY_REQUEST_CODE_NOTIFIATION_ACCEPT = 961;
    private static final int MY_REQUEST_CODE_NOTIFIATION_CANCEL = 962;

//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        // TODO: Handle FCM messages here.
//        // If the application is in the foreground handle both data and notification messages here.
//        // Also if you intend on generating your own notifications as a result of a received FCM
//        // message, here is where that should be initiated.
//
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
//
//        sendNotification(remoteMessage.getFrom(), remoteMessage.getNotification().getBody());
//
//
//    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);

        Bundle extras = intent.getExtras();
        String body = extras.getString("name");
        sendNotification("1123123",body);
    }

    public void sendNotification(String from, String body){
//        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
//        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
//
//        String currentPackageName = componentInfo.getClassName();
//        if(currentPackageName.equals("project.com.vehiclessharing.activity.MainActivity")) {
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
                    .setOngoing(true)
                    .setContentTitle(from)
                    .setContentText(body)
//                    .setAutoCancel(true)// Thông báo sẽ tự động bị hủy khi người dùng click vào Panel
                    .setSound(defaultSoundUri)
                    //.setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_HIGH);


        Intent intent1 = new Intent();
//        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("notificationId",MY_NOTIFICATION_ID);
        intent1.setAction(Utils.ACCEPT_ACTION);
        PendingIntent pendingIntentAccept = PendingIntent.getBroadcast(this, MY_REQUEST_CODE_NOTIFIATION_ACCEPT/* Request code */, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.addAction(R.drawable.accept,"Accept",pendingIntentAccept);


        Intent intent2 = new Intent();
        intent2.setAction(Utils.CANCEL_ACTION);
        intent2.putExtra("notificationId",MY_NOTIFICATION_ID);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this, MY_REQUEST_CODE_NOTIFIATION_CANCEL /* Request code */, intent2,
                PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder.addAction(R.drawable.cancel,"Cancel",pendingIntentCancel);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,  0, new Intent(), 0);
        notificationBuilder.setContentIntent(resultPendingIntent);

        Notification notification = notificationBuilder.build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MY_NOTIFICATION_ID/* ID of notification */, notification);
        Log.d(TAG,"end");
//        }
    }
}
