package project.com.vehiclessharing.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import project.com.vehiclessharing.activity.MainActivity;
import project.com.vehiclessharing.application.ApplicationController;
import project.com.vehiclessharing.constant.Utils;

/**
 * Created by Tuan on 04/06/2017.
 */

public class NotificationReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("error_temp","reciver-1");
        String action = intent.getAction();
        // if you want cancel notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = intent.getIntExtra("notificationId", 0);
        manager.cancel(notificationId);
        MyFirebaseMessagingService.notificationIsShown = false;
        Log.e("error_temp","reciver0");
        if (Utils.ACCEPT_ACTION.equals(action)) {
            //Send deny request to all user send notification
            //.........

            ApplicationController.listNotification.clear();

            Intent intentone = new Intent(context.getApplicationContext(), MainActivity.class);
            intentone.setAction(Utils.ACCEPT_ACTION);
            //Send broadcast
            intentone.putExtra("infouser",intent.getStringExtra("infouser"));
            intentone.putExtra("inforequest",intent.getStringExtra("inforequest"));
            context.sendBroadcast(intentone);
            intentone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentone);

            Toast.makeText(context, "ACCEPT CALLED", Toast.LENGTH_SHORT).show();

        }
        else  if (Utils.CANCEL_ACTION.equals(action)) {
            if(ApplicationController.listNotification.size() > 0){
                Log.e("error_temp","reciver1");
                try {
                    Intent intenttemp = new Intent(Utils.CANCEL_ACTION);
                    intenttemp.putExtra("infouser",intent.getStringExtra("infouser"));
                    intenttemp.putExtra("inforequest",intent.getStringExtra("inforequest"));
                    context.sendBroadcast(intenttemp);
                    Log.e("error_temp","reciver2");
                } catch (Exception e){
                    Log.e("error_temp",String.valueOf(e.getMessage()));
                }
            }
            Toast.makeText(context, "CANCEL CALLED", Toast.LENGTH_SHORT).show();
        }
    }
}
