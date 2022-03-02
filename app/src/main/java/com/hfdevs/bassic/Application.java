package com.hfdevs.bassic;

import androidx.appcompat.app.AppCompatDelegate;

import com.hfdevs.bassic.utils.SharedPrefs;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SharedPrefs.init(this);
        AppCompatDelegate.setDefaultNightMode(SharedPrefs.isNightMode() ?
                AppCompatDelegate.MODE_NIGHT_YES :
                AppCompatDelegate.MODE_NIGHT_NO);
    }
}
