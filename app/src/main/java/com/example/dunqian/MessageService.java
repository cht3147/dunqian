package com.example.dunqian;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.dunqian.DunqianApp.CHANNEL_ID;

public class MessageService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("Message", "onMessageReceived");

        Log.e("Message", remoteMessage.getNotification().getBody());

        createNotif(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    private void createNotif(String title, String content) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(title)
                .setContentText(content);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        notificationManager.notify(1, builder.build());
    }
}
