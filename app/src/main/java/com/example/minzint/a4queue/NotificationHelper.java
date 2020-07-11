package com.example.minzint.a4queue;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {

    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            createChannels();

        }
        //createChannels();

    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannels(){

        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(R.color.colorAccent);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager(){

        if (manager == null)
        {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
          return manager;

    }

    public NotificationCompat.Builder getChannelNotification(String title, String message){

        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.chat).setColor(getResources().getColor(R.color.colorAccent2));

    }
}
