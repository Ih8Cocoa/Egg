package com.meow.egg.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.BigPictureStyle;
import androidx.core.app.NotificationCompat.Builder;

import com.meow.egg.MainActivity;
import com.meow.egg.R;
import com.meow.egg.receivers.SnoozeReceiver;

public final class NotificationUtils {
    private NotificationUtils() {
    }

    private static final int NOTIFICATION_ID = 0, REQUEST_CODE = 0, FLAGS = 0;

    public static void sendNotification(
            final NotificationManager manager,
            final String messageBody,
            final Context appContext
    ) {
        // make an Intent and a PendingIntent
        // An Intent is used by the devs to request an action from another app or app component.
        // The PendingIntent will grant a "right" for another app, or the system itself,
        // to execute an action on behalf of your application.
        // Basically, an Intent wrapper with a token.
        final Intent contentIntent = new Intent(appContext, MainActivity.class);
        // FLAG_UPDATE_CURRENT will update the existing intent if there is one.
        final PendingIntent contentPendingIntent = PendingIntent.getActivity(
                appContext, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        // also add a snooze intent for snoozing notification
        final Intent snoozeIntent = new Intent(appContext, SnoozeReceiver.class);
        final PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(
                appContext, REQUEST_CODE, snoozeIntent, FLAGS
        );

        // Adding a Big Picture style for the notification
        final Resources currentResource = appContext.getResources();
        final Bitmap eggImage = BitmapFactory.decodeResource(currentResource, R.drawable.cooked_egg);
        final BigPictureStyle bigPicture = new BigPictureStyle()
                .bigPicture(eggImage)
                .bigLargeIcon(null);

        // Build the notification
        final String channelId = appContext.getString(R.string.egg_notification_channel_id),
                title = appContext.getString(R.string.notification_title),
                snoozeStr = appContext.getString(R.string.snooze);
        final Builder builder = new Builder(appContext, channelId)
                // then set the icon, title and text
                .setSmallIcon(R.drawable.cooked_egg)
                .setContentTitle(title)
                .setContentText(messageBody)
                // and set the pending intent
                .setContentIntent(contentPendingIntent)
                // also enable auto-cancel to dismiss the notification after navigation
                .setAutoCancel(true)
                // add the style and the icon to the builder
                .setStyle(bigPicture)
                .setLargeIcon(eggImage)
                // add a button to snooze
                .addAction(R.drawable.egg_icon, snoozeStr, snoozePendingIntent)
                // this is for backward compatibility with API 25 and older
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // now then, let's start notifying
        manager.notify(NOTIFICATION_ID, builder.build());
    }
}
