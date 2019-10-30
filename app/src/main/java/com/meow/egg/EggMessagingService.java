package com.meow.egg;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.RemoteMessage.Notification;
import com.meow.egg.utils.NotificationUtils;

import org.json.JSONObject;

public final class EggMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        Log.i("Meow", "Token is: " + s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.i("Meow", "Message is from: " + remoteMessage.getFrom());

        // output the message payload
        Log.d("Meow", "Payload is " + new JSONObject(remoteMessage.getData()).toString());

        // and let's notify the user
        final Notification notification = remoteMessage.getNotification();
        if (notification == null) return;
        final String body = notification.getBody();
        if (body != null) {
            Log.i("Meow", "Message is " + body);
            sendNotification(body);
        }
    }

    private void sendNotification(final String message) {
        final Context context = getApplicationContext();
        final NotificationManager manager = ContextCompat.getSystemService(
                context, NotificationManager.class
        );
        if (manager != null) {
            NotificationUtils.sendNotification(manager, message, context);
        }
    }
}
