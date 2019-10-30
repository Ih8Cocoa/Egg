package com.meow.egg.ui;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.core.app.AlarmManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.meow.egg.R;
import com.meow.egg.receivers.AlarmReceiver;
import com.meow.egg.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public final class EggTimerViewModel extends AndroidViewModel {
    // static stuff
    private static final int REQUEST_CODE = 0;
    private static final String TRIGGER_TIME = "TRIGGER_AT";
    private static final long MINUTE = 60000, SECOND = 1000;

    // pending initializations
    private final Application app;
    private final int[] timerLengthOptions;
    private final PendingIntent notifyPendingIntent;
    private SharedPreferences prefs;
    private final Intent notifyIntent;
    private CountDownTimer timer;
    private final AlarmManager alarmManager;
    private final MutableLiveData<Boolean> alarmOn;

    // store disposables for cleanup
    private final List<Disposable> disposables = new ArrayList<>();

    public EggTimerViewModel(@NonNull Application application) {
        super(application);
        this.app = application;

        // setup
        notifyIntent = new Intent(app, AlarmReceiver.class);
        prefs = app.getSharedPreferences("com.meow.egg", Context.MODE_PRIVATE);
        notifyPendingIntent = PendingIntent.getBroadcast(
                getApplication(), REQUEST_CODE, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        timerLengthOptions = app.getResources().getIntArray(R.array.minutes_array);
        alarmManager = (AlarmManager) app.getSystemService(Context.ALARM_SERVICE);

        final PendingIntent noCreateIntent = PendingIntent.getBroadcast(
                getApplication(), REQUEST_CODE, notifyIntent, PendingIntent.FLAG_NO_CREATE
        );
        // set the LiveData
        alarmOn = new MutableLiveData<>(noCreateIntent != null);
        // If alarm is not null, resume the timer back for this alarm
        if (noCreateIntent != null) {
            createTimer();
        }
    }

    private final MutableLiveData<Integer> timerSelection = new MutableLiveData<>();
    private final MutableLiveData<Long> elapsedTime = new MutableLiveData<>();

    public LiveData<Boolean> getAlarmOn() {
        return alarmOn;
    }

    public LiveData<Integer> getTimerSelection() {
        return timerSelection;
    }

    public LiveData<Long> getElapsedTime() {
        return elapsedTime;
    }

    public void setSelectedTimer(final int selectedTimer) {
        timerSelection.setValue(selectedTimer);
    }

    public void setAlarm(final boolean isChecked) {
        if (isChecked) {
            final Integer timeValue = timerSelection.getValue();
            if (timeValue != null) {
                startTimer(timeValue);
            }
        } else {
            cancelNotification();
        }
    }

    private void createTimer() {
        final Disposable d = loadTime().subscribe(triggerTime -> {
            timer = new CountDownTimer(triggerTime, SECOND) {
                @Override
                public void onTick(long millisUntilFinished) {
                    final long time = triggerTime - SystemClock.elapsedRealtime();
                    elapsedTime.setValue(time);
                    if (time < 1) {
                        resetTimer();
                    }
                }

                @Override
                public void onFinish() {
                    resetTimer();
                }
            };
            timer.start();
        });
        disposables.add(d);
    }

    private void startTimer(final int selectedTimerLength) {
        final Boolean isAlarmOn = alarmOn.getValue();
        if (isAlarmOn != null && !isAlarmOn) {
            alarmOn.setValue(true);
            final long selectedInterval;
            if (selectedTimerLength == 0) {
                selectedInterval = SECOND * 10;
            } else {
                selectedInterval = timerLengthOptions[selectedTimerLength] * MINUTE;
            }
            final long triggerTime = SystemClock.elapsedRealtime() + selectedInterval;

            // Clear all notifications before setting alarm
            final NotificationManager manager = ContextCompat.getSystemService(
                    app, NotificationManager.class
            );
            if (manager != null) {
                manager.cancelAll();
            }

            // after we have the time, set the alarm
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager, AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, notifyPendingIntent
            );
            saveTime(triggerTime).subscribe();
        }
        createTimer();
    }

    private Single<Long> loadTime() {
        return Single.fromCallable(() -> prefs.getLong(TRIGGER_TIME, 0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Completable saveTime(final long triggerTime) {
        return Completable.fromAction(() -> prefs.edit().putLong(TRIGGER_TIME, triggerTime).apply())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void cancelNotification() {
        // reset the timer and cancel the alarm
        resetTimer();
        alarmManager.cancel(notifyPendingIntent);
    }

    private void resetTimer() {
        timer.cancel();
        elapsedTime.setValue(0L);
        alarmOn.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // dispose everything
        for (Disposable d : disposables) {
            d.dispose();
        }
    }
}
