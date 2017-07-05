package project.com.vehiclessharing.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.application.ApplicationController;
import project.com.vehiclessharing.constant.Utils;
import project.com.vehiclessharing.model.User;
import project.com.vehiclessharing.parseobject.ParseObject;

/**
 * Created by Tuan on 12/06/2017.
 */

public class NotificationCustom {
    private static String TAG_NOTIFICATION = "error_notification";

    private static final int MY_REQUEST_CODE_NOTIFIATION_ACCEPT = 961;
    private static final int MY_REQUEST_CODE_NOTIFIATION_CANCEL = 962;
    public static Notification intizaleNotification(String infouser, String inforequest, Context context, int idNotification){
        Log.d(TAG_NOTIFICATION,"1");
        BitmapDrawable bitmapdraw;
        bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.temp);

        //Convert Json to Object
        User user = ParseObject.jsonToUser(infouser);

        //Message
        String message = "";
        int who = ApplicationController.sharedPreferences.getInt("who",0);
        Log.d("FCM_bugAAAA", String.valueOf(who));
        if(who == 0) return null;
        else {
            if(who == Utils.IS_GRABER) message = Utils.MESSAGE_FROM_NEEDER;
            else message = Utils.MESSAGE_FROM_GRABER;
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Log.d(TAG_NOTIFICATION,"2");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.user)
                .setLargeIcon(bitmapdraw.getBitmap())
                .setOngoing(true)
                .setContentTitle(user.getFullName())
                .setContentText(message)
//                    .setAutoCancel(true)// Thông báo sẽ tự động bị hủy khi người dùng click vào Panel
                .setSound(defaultSoundUri)
                //.setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH);


        Intent intent1 = new Intent();
//        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("notificationId",idNotification);
        intent1.putExtra("infouser",infouser);
        intent1.putExtra("inforequest",inforequest);
        intent1.setAction(Utils.ACCEPT_ACTION);
        PendingIntent pendingIntentAccept = PendingIntent.getBroadcast(context, MY_REQUEST_CODE_NOTIFIATION_ACCEPT/* Request code */, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.addAction(R.drawable.accept,"Accept",pendingIntentAccept);


        Intent intent2 = new Intent();
        intent2.setAction(Utils.CANCEL_ACTION);
        intent2.putExtra("notificationId",idNotification);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(context, MY_REQUEST_CODE_NOTIFIATION_CANCEL /* Request code */, intent2,
                PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder.addAction(R.drawable.cancel,"Cancel",pendingIntentCancel);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,  0, new Intent(), 0);
        notificationBuilder.setContentIntent(resultPendingIntent);

       return notificationBuilder.build();
    }
}
