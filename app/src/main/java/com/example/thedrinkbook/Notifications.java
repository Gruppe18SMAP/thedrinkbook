package com.example.thedrinkbook;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by Bruger on 27-04-2018.
 */

public class Notifications {
    public static final String CHANNEL_ID = "DrinkBook";
    PendingIntent pendingIntent;

    NotificationManager notificationManager;

    public Notifications(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "DrinkBook";
            String description = "Notifikation fra DrinkBook";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        } else {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        createIntent(context);
    }

    private void createIntent(Context context) {
        Intent notificationIntent = new Intent(context, AdminstratorOverViewActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

    }
}