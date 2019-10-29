package com.meow.egg.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.format.DateUtils;

import androidx.core.app.AlarmManagerCompat;

public final class SnoozeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final int REQUEST_CODE = 0;
        final long triggerTime = SystemClock.elapsedRealtime() + DateUtils.MINUTE_IN_MILLIS;
        final Intent notifyIntent = new Intent(context, AlarmReceiver.class);
        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(
                context, REQUEST_CODE, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        final AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager, AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, notifyPendingIntent
            );
        }
    }
}
