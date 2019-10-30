package com.meow.egg.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.meow.egg.R;
import com.meow.egg.utils.NotificationUtils;

public final class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String readyText = context.getText(R.string.eggs_ready).toString();
        // get the notification manager to send a notification
        final NotificationManager manager =
                ContextCompat.getSystemService(context, NotificationManager.class);

        if (manager != null) {
            NotificationUtils.sendNotification(manager, readyText, context);
        }
    }
}
