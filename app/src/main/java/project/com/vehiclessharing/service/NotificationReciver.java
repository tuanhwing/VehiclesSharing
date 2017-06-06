package project.com.vehiclessharing.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import project.com.vehiclessharing.activity.MainActivity;
import project.com.vehiclessharing.constant.Utils;

/**
 * Created by Tuan on 04/06/2017.
 */

public class NotificationReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int notificationId = intent.getIntExtra("notificationId", 0);
        if (Utils.ACCEPT_ACTION.equals(action)) {
            Intent intentone = new Intent(context.getApplicationContext(), MainActivity.class);
            intentone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentone);

            Toast.makeText(context, "ACCEPT CALLED", Toast.LENGTH_SHORT).show();
        }
        else  if (Utils.CANCEL_ACTION.equals(action)) {
            Toast.makeText(context, "CANCEL CALLED", Toast.LENGTH_SHORT).show();
        }


        // if you want cancel notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}
