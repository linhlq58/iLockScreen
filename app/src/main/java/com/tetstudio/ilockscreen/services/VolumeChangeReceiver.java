package com.tetstudio.ilockscreen.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

/**
 * Created by lequy on 1/14/2017.
 */

public class VolumeChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int volumeLevel= am.getStreamVolume(AudioManager.STREAM_MUSIC);

        Intent i = new Intent("volume_changed");
        i.putExtra("volume_level", volumeLevel);
        context.sendBroadcast(i);
    }
}
