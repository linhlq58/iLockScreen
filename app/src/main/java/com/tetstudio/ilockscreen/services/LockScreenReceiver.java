package com.tetstudio.ilockscreen.services;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tetstudio.ilockscreen.activities.MainActivity;
import com.tetstudio.ilockscreen.adapters.MyPagerAdapter;

/**
 * Created by Linh Lee on 10/5/2016.
 */
public class LockScreenReceiver extends BroadcastReceiver {
    private SharedPreferences sharedPreferences;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        //If the screen was just turned on or it just booted up, start your Lock Activity
        if (sharedPreferences.getBoolean("onOffchecked", false)) {
            if (action.equals(Intent.ACTION_SCREEN_OFF) || action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(i);

                Intent mainIntent = new Intent("create_main_view");
                context.sendBroadcast(mainIntent);
            }
        }
    }
}
