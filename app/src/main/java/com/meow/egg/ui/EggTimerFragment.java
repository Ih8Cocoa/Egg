package com.meow.egg.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.meow.egg.R;
import com.meow.egg.databinding.FragmentEggTimerBinding;


public final class EggTimerFragment extends Fragment {
    private static final String TOPIC = "breakfast";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // using data binding
        final FragmentEggTimerBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_egg_timer, container, false
        );
        final EggTimerViewModel viewModel = ViewModelProviders.of(this)
                .get(EggTimerViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // when the view is created, make a new notification channel
        final String eggChannelId = getString(R.string.egg_notification_channel_id),
                eggChannelName = getString(R.string.egg_notification_channel_name);
        createChannel(eggChannelId, eggChannelName);

        // also create a channel for breakfast (Firebase Cloud Messaging)
        final String breakfastChannelId = getString(R.string.breakfast_notification_channel_id),
                breakfastChannelName = getString(R.string.breakfast_notification_channel_name);
        createChannel(breakfastChannelId, breakfastChannelName);

        // now subscribe
        subscribe();
        return binding.getRoot();
    }

    private void createChannel(final String channelId, final String channelName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Since API 26 (Oreo), all notifications must have a channel
            // so we need to make one
            final NotificationChannel channel = new NotificationChannel(
                    channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            );

            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setDescription("Time for breakfast!");

            // hide the badges
            channel.setShowBadge(false);

            // now, get the manager to add the created channel
            final NotificationManager manager =
                    requireActivity().getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void subscribe() {
        final OnCompleteListener<Void> listener = task -> {
            String message = getString(R.string.message_subscribed);
            if (!task.isSuccessful()) {
                message = getString(R.string.message_subscribe_failed);
            }
            // make a toast
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        };
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                .addOnCompleteListener(listener);

    }
}
