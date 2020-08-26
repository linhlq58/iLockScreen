package com.tetstudio.ilockscreen;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import com.mz.ZAndroidSystemDK;

import java.util.Locale;

/**
 * Created by lequy on 1/16/2017.
 */

public class MyApplication extends Application {
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        ZAndroidSystemDK.initApplication(this, this.getApplicationContext().getPackageName());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean("first_launch", true)) {
            Locale locale = new Locale("vi");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getApplicationContext().getResources().updateConfiguration(config, null);
            sharedPreferences.edit().putBoolean("first_launch", false).apply();
        }
    }
}
