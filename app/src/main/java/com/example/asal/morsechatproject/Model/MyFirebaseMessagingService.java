package com.example.asal.morsechatproject.Model;





import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.example.asal.morsechatproject.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by asal on 2017-12-26.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.label1)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");


        // Sets an Random ID for the notification
        int mNotificationId = (int) System.currentTimeMillis();
       // Gets an instance of the NotificationManager service
         NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }
}
