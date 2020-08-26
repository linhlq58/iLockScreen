package com.tetstudio.ilockscreen.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.tetstudio.ilockscreen.objects.NotificationObject;
import com.tetstudio.ilockscreen.utils.Constant;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Linh Lee on 10/17/2016.
 */
@SuppressLint("OverrideAbstract")
public class NLService extends NotificationListenerService {
    private NLServiceReceiver nlServiceReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        if (Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
            nlServiceReceiver = new NLServiceReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("NOTIFICATION_LISTENER_SERVICE");
            registerReceiver(nlServiceReceiver, filter);
        }
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d("NLS connected", "connected");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlServiceReceiver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        Bundle extras = NotificationCompat.getExtras(sbn.getNotification());
        String title = "";
        String content;
        String encodedImage;
        Bitmap bmp;

        /*if (extras.getString(Notification.EXTRA_TITLE) == null || extras.getString(Notification.EXTRA_TITLE).equals("")) {
            title = Constant.getAppName(this, sbn.getPackageName());
        } else {
            title = extras.getString(Notification.EXTRA_TITLE);
        }*/
        if (extras.get(Notification.EXTRA_TITLE) instanceof SpannableString) {
            title = ((SpannableString) extras.get(Notification.EXTRA_TITLE)).toString();
        } else if (extras.get(Notification.EXTRA_TITLE) instanceof String){
            title = extras.getString(Notification.EXTRA_TITLE);
        }

        CharSequence chars = extras.getCharSequence(Notification.EXTRA_TEXT);
        if (!TextUtils.isEmpty(chars)) {
            content = chars.toString();
        } else if (!TextUtils.isEmpty((chars =
                extras.getString(Notification.EXTRA_SUMMARY_TEXT)))) {
            content = chars.toString();
        } else {
            content = "";
        }

        Drawable appIcon = null;
        try {
            appIcon = getPackageManager().getApplicationIcon(sbn.getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (appIcon instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) appIcon).getBitmap();
        } else {
            Bitmap bitmap = Bitmap.createBitmap(appIcon.getIntrinsicWidth(), appIcon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            appIcon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            appIcon.draw(canvas);
            bmp = bitmap;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArr = baos.toByteArray();
        encodedImage = Base64.encodeToString(byteArr, Base64.DEFAULT);

        Intent i = new Intent();
        i.setAction("getListNotification");
        i.putExtra("type", "addNoti");
        i.putExtra("noti object", new NotificationObject(sbn.getId(), encodedImage, title, content));
        sendBroadcast(i);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);

        Intent i = new Intent();
        i.setAction("getListNotification");
        i.putExtra("type", "removeNoti");
        i.putExtra("removed id", sbn.getId());
        sendBroadcast(i);
    }

    public class NLServiceReceiver extends BroadcastReceiver {
        private StatusBarNotification[] listSbn;
        private ArrayList<NotificationObject> listNotificationObjects;
        private Bitmap bmp;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("command").equals("getAll")) {
                listSbn = getActiveNotifications();
                listNotificationObjects = new ArrayList<>();
                if (listSbn != null) {
                    if (listSbn.length > 0) {
                        for (int i = 0; i < listSbn.length; i++) {
                            Bundle extras = NotificationCompat.getExtras(listSbn[i].getNotification());
                            extras = listSbn[i].getNotification().extras;
                            String title = "";
                            String content;
                            String encodedImage;

                            /*if (extras.getString(Notification.EXTRA_TITLE) == null || extras.getString(Notification.EXTRA_TITLE).equals("")) {
                                title = Constant.getAppName(context, listSbn[i].getPackageName());
                            } else {
                                title = extras.getString(Notification.EXTRA_TITLE);
                            }*/
                            if (extras.get(Notification.EXTRA_TITLE) instanceof SpannableString) {
                                title = ((SpannableString) extras.get(Notification.EXTRA_TITLE)).toString();
                            } else if (extras.get(Notification.EXTRA_TITLE) instanceof String){
                                title = extras.getString(Notification.EXTRA_TITLE);
                            }

                            CharSequence chars = extras.getCharSequence(Notification.EXTRA_TEXT);
                            if (!TextUtils.isEmpty(chars)) {
                                content = chars.toString();
                            } else if (!TextUtils.isEmpty((chars =
                                    extras.getString(Notification.EXTRA_SUMMARY_TEXT)))) {
                                content = chars.toString();
                            } else {
                                content = "";
                            }

                            Drawable appIcon = null;
                            try {
                                appIcon = getPackageManager().getApplicationIcon(listSbn[i].getPackageName());
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }

                            if (appIcon instanceof BitmapDrawable) {
                                bmp = ((BitmapDrawable) appIcon).getBitmap();
                            } else {
                                Bitmap bitmap = Bitmap.createBitmap(appIcon.getIntrinsicWidth(), appIcon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(bitmap);
                                appIcon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                                appIcon.draw(canvas);
                                bmp = bitmap;
                            }

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] byteArr = baos.toByteArray();
                            encodedImage = Base64.encodeToString(byteArr, Base64.DEFAULT);

                            listNotificationObjects.add(new NotificationObject(listSbn[i].getId(), encodedImage, title, content));
                        }

                        Intent i = new Intent();
                        i.setAction("getListNotification");
                        i.putExtra("type", "getAllNoti");
                        i.putParcelableArrayListExtra("list notification", listNotificationObjects);
                        sendBroadcast(i);
                    }
                }
            } else if (intent.getStringExtra("command").equals("removeAll")) {
                cancelAllNotifications();
            }
        }
    }
}
