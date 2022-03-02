package com.hfdevs.bassic.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

public class SharedPrefs {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor sharedPreferencesEditor;

    public static void init(Application application) {
        context = application.getApplicationContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
    }

    public static void saveTheme(boolean isNightMode) {
        sharedPreferencesEditor.putBoolean(Constants.KEY_IS_NIGHT, isNightMode)
                .apply();
    }

    public static boolean isNightMode() {
        return sharedPreferences.getBoolean(Constants.KEY_IS_NIGHT, false);
    }

    public static void saveSleepTimerToggle(boolean isSleepTimerEnabled) {
        sharedPreferencesEditor.putBoolean(Constants.KEY_IS_SLEEP_TIMER_ENABLED, isSleepTimerEnabled)
                .apply();
    }

    public static boolean isSleepTimerOn() {
        return sharedPreferences.getBoolean(Constants.KEY_IS_SLEEP_TIMER_ENABLED, false);
    }

    public static void saveSleepTime(long timeInMillis) {
        sharedPreferencesEditor.putLong(Constants.KEY_SLEEP_TIME, timeInMillis)
                .apply();
    }

    public static long getSleepTime() {
        return sharedPreferences.getLong(Constants.KEY_SLEEP_TIME,
                Calendar.getInstance().getTimeInMillis());
    }


}
