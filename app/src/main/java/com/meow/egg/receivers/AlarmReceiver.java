package com.meow.egg.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.meow.egg.R;

public final class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final CharSequence readyText = context.getText(R.string.eggs_ready);
        // just make a toast for now
        Toast.makeText(context, readyText, Toast.LENGTH_SHORT)
                .show();
    }
}
