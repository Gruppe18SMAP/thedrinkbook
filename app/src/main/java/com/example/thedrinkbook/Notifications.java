package com.example.thedrinkbook;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

/**
 * Created by Bruger on 27-04-2018.
 */

public class Notifications {
    public static final String CHANNEL_ID = "DrinkBook";
    PendingIntent pendingIntent;

    public Notifications(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "DrinkBook";
            String description = "Notifikation fra DrinkBook";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);

        }
    }
}