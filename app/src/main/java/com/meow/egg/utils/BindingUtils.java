package com.meow.egg.utils;

import android.text.format.DateUtils;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

public final class BindingUtils {
    private BindingUtils() {}

    @BindingAdapter("elapsedTime")
    public static void setElapsedTime(final TextView view, final long value) {
        final long seconds = value / 1000;
        final String text;
        if (seconds < 60) {
            text = Long.toString(seconds);
        } else {
            text = DateUtils.formatElapsedTime(seconds);
        }
        view.setText(text);
    }
}
