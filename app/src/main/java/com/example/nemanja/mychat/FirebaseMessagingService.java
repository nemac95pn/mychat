package com.example.nemanja.mychat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by nemanja on 3/18/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService
{
  /*  @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


       // String notification_title = remoteMessage.getNotification().getTitle(); Ovako nije htelo da radi kad se aplikacija iskljuci
       // String notification_body = remoteMessage.getNotification().getBody();
       // String click_action = remoteMessage.getNotification().getClickAction();
       // String from_sender_id = remoteMessage.getData().get("from_sender_id");

        String from_sender_id = remoteMessage.getData().get("from_sender_id"); // Ovako radi
        String click_action = remoteMessage.getData().get("click_action");
        String notification_title = remoteMessage.getData().get("title");
        String notification_body = remoteMessage.getData().get("body");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.mychat)
                        .setContentTitle(notification_title)
                        .setContentText(notification_body);


        Intent resultIntent = new Intent(click_action);

        resultIntent.putExtra("visit_user_id", from_sender_id);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);



        int mNotificationId = (int) System.currentTimeMillis();
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    } */


        // For all Android 8.x
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "channel_id_01";

        String from_sender_id = remoteMessage.getData().get("from_sender_id");
        String click_action = remoteMessage.getData().get("click_action");
        String notification_title = remoteMessage.getData().get("title");
        String notification_body = remoteMessage.getData().get("body");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.mychat/*replace with your icon*/)
                .setTicker("Hearty365")
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(notification_title)
                .setContentText(notification_body)
                .setContentInfo("Info");


        Intent resultIntent = new Intent(click_action);

        resultIntent.putExtra("visit_user_id", from_sender_id);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(resultPendingIntent);


        int mNotificationId = (int) System.currentTimeMillis();
        notificationManager.notify(mNotificationId, notificationBuilder.build());
    }

}
