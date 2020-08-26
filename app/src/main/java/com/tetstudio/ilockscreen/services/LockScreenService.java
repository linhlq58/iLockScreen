package com.tetstudio.ilockscreen.services;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.KeyguardManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.tetstudio.ilockscreen.R;
import com.tetstudio.ilockscreen.activities.MainActivity;
import com.tetstudio.ilockscreen.adapters.MyPagerAdapter;
import com.tetstudio.ilockscreen.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by Linh Lee on 10/5/2016.
 */
public class LockScreenService extends Service implements View.OnClickListener, View.OnTouchListener, View.OnDragListener {
    private BroadcastReceiver receiver, createViewReceiver, unlockReceiver, haveCallReceiver, panelReceiver;
    private SharedPreferences sharedPreferences;

    private WindowManager mWindowManager;
    private View mainView;
    private ImageView backgroundImage;
    private ViewPager viewPager;
    private MyPagerAdapter adapter;
    private ValueAnimator mColorAnimation;
    private String imageBase64;
    private Bitmap mySelectedImage;

    private View panelView;
    private View menuButton;
    private View shadowMenu;
    private FrameLayout menuButtonView;
    private FrameLayout menuLayout;
    private LinearLayout panelLayout;
    private ImageView wifiButton;
    private ImageView airPlaneButton;
    private ImageView nightButton;
    private ImageView bluetoothButton;
    private ImageView rotationButton;
    private SeekBar lightBar;
    private SeekBar volumeBar;
    private ImageView flashLightButton;
    private ImageView clockButton;
    private ImageView calculatorButton;
    private ImageView cameraButton;
    private WindowManager.LayoutParams mLayoutParams, panelParams;

    private WifiManager wifiManager;
    private AudioManager audioManager;
    private BluetoothAdapter bluetoothAdapter;
    private Camera camera;
    private PackageManager pm;
    private ArrayList<HashMap<String,Object>> items;
    private boolean flashEnable = false;
    private boolean mainIsAdded = false;
    private boolean panelIsAdded = false;
    private Rect rect;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        imageBase64 = sharedPreferences.getString("image base64", "");

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        //LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(LockScreenService.this);
        mainView = inflater.inflate(R.layout.lock_view, null);
        panelView = inflater.inflate(R.layout.panel_layout, null);

        KeyguardManager.KeyguardLock key;
        KeyguardManager km = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);

        //This is deprecated, but it is a simple way to disable the lockscreen in code
        key = km.newKeyguardLock("IN");
        key.disableKeyguard();

        //Start listening for the Screen On, Screen Off, and Boot completed actions
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);

        //Set up a receiver to listen for the Intents in this Service
        receiver = new LockScreenReceiver();
        registerReceiver(receiver, filter);

        createViewReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent notiIntent = new Intent("NOTIFICATION_LISTENER_SERVICE");
                notiIntent.putExtra("command", "getAll");
                sendBroadcast(notiIntent);

                createMainView();
                createPanelView();
            }
        };
        registerReceiver(createViewReceiver, new IntentFilter("create_main_view"));

        unlockReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                unlockScreen();
            }
        };
        registerReceiver(unlockReceiver, new IntentFilter("unlock"));

        haveCallReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                String command = extras.getString("command");
                if (command.equals("remove")) {
                    if (mainIsAdded) {
                        Intent i = new Intent("finish_activity");
                        sendBroadcast(i);
                        mWindowManager.removeView(mainView);
                        mainIsAdded = false;
                    }
                } else if (command.equals("add")) {
                    if (sharedPreferences.getBoolean("onOffchecked", false)) {
                        if (!mainIsAdded) {
                            Intent i = new Intent(context, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(i);

                            Intent notiIntent = new Intent("NOTIFICATION_LISTENER_SERVICE");
                            notiIntent.putExtra("command", "getAll");
                            sendBroadcast(notiIntent);

                            createMainView();
                            createPanelView();
                        }
                    }
                }
            }
        };
        registerReceiver(haveCallReceiver, new IntentFilter("have_a_call"));

        panelReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    boolean isChecked = extras.getBoolean("is_checked");
                    if (isChecked) {
                        createPanelView();
                    } else {
                        if (panelIsAdded) {
                            mWindowManager.removeView(panelView);
                            panelIsAdded = false;
                        }
                    }
                }
            }
        };
        registerReceiver(panelReceiver, new IntentFilter("change_panel"));

        super.onCreate();
    }

    private void createMainView() {
        //Create lock view
        mLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSLUCENT);

        backgroundImage = (ImageView) mainView.findViewById(R.id.bg_img);
        viewPager = (ViewPager) mainView.findViewById(R.id.pager);
        OverScrollDecoratorHelper.setUpOverScroll(viewPager);

        setBackgroundImage();

        mColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), R.color.colorBlackTransparent, android.R.color.transparent);
        mColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                viewPager.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });
        mColorAnimation.setDuration((2 - 1) * 10000000000l);

        if (adapter == null) {
            adapter = new MyPagerAdapter(LockScreenService.this);
        }

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(2);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mColorAnimation.setCurrentPlayTime((long) ((positionOffset + position) * 10000000000l));
            }

            @Override
            public void onPageSelected(int position) {
                if (sharedPreferences.getBoolean("hasPassChecked", false) == false) {
                    if (position == 0) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                unlockScreen();
                            }
                        }, 200);
                    }
                } else {
                    if (position == 0) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                /*Blurry.with(LockScreenService.this)
                                        .radius(25)
                                        .sampling(1)
                                        .async()
                                        .capture(backgroundImage)
                                        .into(backgroundImage);*/
                                return null;
                            }
                        }.execute();
                    } else {
                        setBackgroundImage();
                        adapter.clearPass();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (!mainIsAdded) {
            mWindowManager.addView(mainView, mLayoutParams);
            mainIsAdded = true;
        }

        BroadcastReceiver homeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                changeFragment(1);
            }
        };
        registerReceiver(homeReceiver, new IntentFilter("go_to_home"));

        BroadcastReceiver unlockReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                changeFragment(0);
            }
        };
        registerReceiver(unlockReceiver, new IntentFilter("go_to_unlock"));
    }

    private void createPanelView() {
        // Create Panel View
        panelParams = new WindowManager.LayoutParams();
        panelParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        panelParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        panelParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        panelParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        panelParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        panelParams.format = PixelFormat.TRANSLUCENT;

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        items = new ArrayList<>();

        pm = getPackageManager();
        List<PackageInfo> packs = pm.getInstalledPackages(0);
        for (PackageInfo pi : packs) {
            if( pi.packageName.toString().toLowerCase().contains("calcul")){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("appName", pi.applicationInfo.loadLabel(pm));
                map.put("packageName", pi.packageName);
                items.add(map);
            }
        }

        menuButton = panelView.findViewById(R.id.menu_button);
        shadowMenu = panelView.findViewById(R.id.shadow_menu);
        menuButtonView = (FrameLayout) panelView.findViewById(R.id.menu_button_view);
        menuLayout = (FrameLayout) panelView.findViewById(R.id.menu_layout);
        panelLayout = (LinearLayout) panelView.findViewById(R.id.panel_layout);
        wifiButton = (ImageView) panelView.findViewById(R.id.wifi_button);
        airPlaneButton = (ImageView) panelView.findViewById(R.id.airplane_button);
        nightButton = (ImageView) panelView.findViewById(R.id.night_button);
        bluetoothButton = (ImageView) panelView.findViewById(R.id.bluetooth_button);
        rotationButton = (ImageView) panelView.findViewById(R.id.rotation_button);
        lightBar = (SeekBar) panelView.findViewById(R.id.light_bar);
        volumeBar = (SeekBar) panelView.findViewById(R.id.volume_bar);
        flashLightButton = (ImageView) panelView.findViewById(R.id.flashlight_button);
        clockButton = (ImageView) panelView.findViewById(R.id.clock_button);
        calculatorButton = (ImageView) panelView.findViewById(R.id.calculator_button);
        cameraButton = (ImageView) panelView.findViewById(R.id.camera_button);

        // Set init icon
        if (wifiManager.isWifiEnabled()) {
            wifiButton.setImageResource(R.mipmap.wifi_on);
        } else {
            wifiButton.setImageResource(R.mipmap.wifi_off);
        }

        if (Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1) {
            airPlaneButton.setImageResource(R.mipmap.airplane_on);
        } else {
            airPlaneButton.setImageResource(R.mipmap.airplane_off);
        }

        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                nightButton.setImageResource(R.mipmap.nightmode_off);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                nightButton.setImageResource(R.mipmap.nightmode_off);
                break;
            case AudioManager.RINGER_MODE_SILENT:
                nightButton.setImageResource(R.mipmap.nightmode_on);
                break;
        }

        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                bluetoothButton.setImageResource(R.mipmap.bluetooth_on);
            } else {
                bluetoothButton.setImageResource(R.mipmap.bluetooth_off);
            }
        }

        if (Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
            rotationButton.setImageResource(R.mipmap.rotation_on);
        } else {
            rotationButton.setImageResource(R.mipmap.rotation_off);
        }

        // Setup brightness seekbar
        int bright = Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 0);
        lightBar.setMax(255);
        lightBar.setProgress(bright);
        lightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    Settings.System.putInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS, 1);
                } else {
                    Settings.System.putInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Setup volume seekbar
        volumeBar.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        BroadcastReceiver volumeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int volumeLevel = extras.getInt("volume_level");
                    volumeBar.setProgress(volumeLevel);
                }
            }
        };
        registerReceiver(volumeReceiver, new IntentFilter("volume_changed"));

        Constant.increaseHitArea(menuButton);

        menuButtonView.setOnTouchListener(this);
        menuButtonView.setOnDragListener(this);
        menuButton.setOnClickListener(this);
        menuButton.setOnTouchListener(this);
        menuLayout.setOnDragListener(this);
        shadowMenu.setOnClickListener(this);
        shadowMenu.setOnTouchListener(this);
        wifiButton.setOnClickListener(this);
        airPlaneButton.setOnClickListener(this);
        nightButton.setOnClickListener(this);
        bluetoothButton.setOnClickListener(this);
        rotationButton.setOnClickListener(this);
        flashLightButton.setOnClickListener(this);
        clockButton.setOnClickListener(this);
        calculatorButton.setOnClickListener(this);
        cameraButton.setOnClickListener(this);

        if (!panelIsAdded) {
            mWindowManager.addView(panelView, panelParams);
            panelIsAdded = true;
        } else {
            mWindowManager.removeView(panelView);
            mWindowManager.addView(panelView, panelParams);
            menuButton.setVisibility(View.VISIBLE);
            //mSlideText.setVisibility(View.GONE);
            //shadowMenu.setVisibility(View.VISIBLE);
            menuLayout.setVisibility(View.GONE);
            mWindowManager.updateViewLayout(panelView, panelParams);
        }
    }

    public void changeFragment(int position) {
        viewPager.setCurrentItem(position);
    }

    public void setBackgroundImage() {
        if (imageBase64.equals("")) {
            backgroundImage.setImageResource(sharedPreferences.getInt("background img", R.mipmap.wallpager_25));
        } else {
            byte[] imageAsBytes = Base64.decode(imageBase64, Base64.DEFAULT);
            mySelectedImage = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(mySelectedImage, 0, 0, mySelectedImage.getWidth(), mySelectedImage.getHeight(), matrix, true);
            backgroundImage.setImageBitmap(rotatedBitmap);
        }
    }

    public void unlockScreen() {
        //android.os.Process.killProcess(android.os.Process.myPid());
        Intent i = new Intent("finish_activity");
        sendBroadcast(i);

        if (mainIsAdded) {
            mWindowManager.removeView(mainView);
            mainIsAdded = false;
        }

        if (!sharedPreferences.getBoolean("panelChecked", false)) {
            if (panelIsAdded) {
                mWindowManager.removeView(panelView);
                panelIsAdded = false;
            }
        }
    }

    private void gotoUnlock() {
        Intent gotoUnlock = new Intent("go_to_unlock");
        sendBroadcast(gotoUnlock);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                if (v.getId() == R.id.shadow_menu) {
                    if (Constant.getScreenHeight() - event.getY() <= Constant.convertDpIntoPixels(400, LockScreenService.this)) {
                        ClipData data = ClipData.newPlainText("", "");
                        View transparentView  = new FrameLayout(LockScreenService.this);
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(transparentView);
                        panelLayout.startDrag(data, shadowBuilder, panelLayout, 0);
                    }
                } else {
                    if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
                        menuButton.setVisibility(View.GONE);
                        menuLayout.setVisibility(View.VISIBLE);

                        ClipData data = ClipData.newPlainText("", "");
                        View transparentView  = new FrameLayout(LockScreenService.this);
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(transparentView);
                        panelLayout.startDrag(data, shadowBuilder, panelLayout, 0);
                    }
                }
                break;
        }

        return false;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) panelLayout.getLayoutParams();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                if (Constant.getScreenHeight() - event.getY() >= Constant.convertDpIntoPixels(300, LockScreenService.this)) {
                    layoutParams.bottomMargin = 0;
                } else {
                    layoutParams.bottomMargin = - ((int) event.getY() - (Constant.getScreenHeight() - Constant.convertDpIntoPixels(300, LockScreenService.this)));
                }
                panelLayout.setLayoutParams(layoutParams);
                break;
            case DragEvent.ACTION_DROP:
                if (Constant.getScreenHeight() - event.getY() <= Constant.convertDpIntoPixels(150, LockScreenService.this)) {
                    layoutParams.bottomMargin = - Constant.convertDpIntoPixels(300, LockScreenService.this);
                    menuButton.setVisibility(View.VISIBLE);
                    menuLayout.setVisibility(View.GONE);
                } else {
                    layoutParams.bottomMargin = 0;
                    panelLayout.setLayoutParams(layoutParams);
                }
                Log.d("y axis", (Constant.getScreenHeight() - event.getY()) + "");
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_button:
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) panelLayout.getLayoutParams();
                menuButton.setVisibility(View.GONE);
                //mSlideText.setVisibility(View.GONE);
                //shadowMenu.setVisibility(View.VISIBLE);
                menuLayout.setVisibility(View.VISIBLE);

                layoutParams.bottomMargin = 0;
                panelLayout.setLayoutParams(layoutParams);

                mWindowManager.updateViewLayout(panelView, panelParams);
                break;
            case R.id.shadow_menu:
                menuButton.setVisibility(View.VISIBLE);
                //mSlideText.setVisibility(View.VISIBLE);
                //shadowMenu.setVisibility(View.GONE);
                menuLayout.setVisibility(View.GONE);
                mWindowManager.updateViewLayout(panelView, panelParams);
                break;
            case R.id.wifi_button:
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                    wifiButton.setImageResource(R.mipmap.wifi_off);
                } else {
                    wifiManager.setWifiEnabled(true);
                    wifiButton.setImageResource(R.mipmap.wifi_on);
                }
                mWindowManager.updateViewLayout(panelView, panelParams);
                break;
            case R.id.airplane_button:
                gotoUnlock();

                Intent settingsIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(settingsIntent);
                break;
            case R.id.night_button:
                if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    nightButton.setImageResource(R.mipmap.nightmode_off);
                } else {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    nightButton.setImageResource(R.mipmap.nightmode_on);
                }
                mWindowManager.updateViewLayout(panelView, panelParams);
                break;
            case R.id.bluetooth_button:
                if (bluetoothAdapter != null) {
                    if (bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.disable();
                        bluetoothButton.setImageResource(R.mipmap.bluetooth_off);
                    } else {
                        bluetoothAdapter.enable();
                        bluetoothButton.setImageResource(R.mipmap.bluetooth_on  );
                    }
                }
                mWindowManager.updateViewLayout(panelView, panelParams);
                break;
            case R.id.rotation_button:
                if (Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
                    Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
                    rotationButton.setImageResource(R.mipmap.rotation_off);
                } else {
                    Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
                    rotationButton.setImageResource(R.mipmap.rotation_on);
                }
                mWindowManager.updateViewLayout(panelView, panelParams);
                break;
            case R.id.flashlight_button:
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    if (flashEnable) {
                        if (camera != null) {
                            camera.stopPreview();
                            camera.release();
                        }

                        flashLightButton.setImageResource(R.mipmap.flashlight_off);
                        flashEnable = false;
                    } else {
                        camera = Camera.open();
                        Camera.Parameters p = camera.getParameters();
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(p);
                        camera.startPreview();

                        flashLightButton.setImageResource(R.mipmap.flashlight_on);
                        flashEnable = true;
                    }
                }
                mWindowManager.updateViewLayout(panelView, panelParams);
                break;
            case R.id.clock_button:
                gotoUnlock();

                Intent openClockIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
                openClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(openClockIntent);

                shadowMenu.performClick();
                break;
            case R.id.calculator_button:
                gotoUnlock();

                if(items.size() >= 1) {
                    String packageName = (String) items.get(0).get("packageName");
                    Intent i = pm.getLaunchIntentForPackage(packageName);
                    if (i != null) {
                        startActivity(i);
                    } else {
                        Toast.makeText(this, "Your device has no calculator app", Toast.LENGTH_SHORT).show();
                    }
                }

                shadowMenu.performClick();
                break;
            case R.id.camera_button:
                gotoUnlock();

                Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    PackageManager pm = getPackageManager();

                    final ResolveInfo mInfo = pm.resolveActivity(i, 0);

                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(mInfo.activityInfo.packageName, mInfo.activityInfo.name));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(intent);
                } catch (Exception e){
                    Log.i("error", "Unable to launch camera" + e);
                }

                shadowMenu.performClick();
                break;
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        unregisterReceiver(createViewReceiver);
        unregisterReceiver(unlockReceiver);
        unregisterReceiver(haveCallReceiver);
        unregisterReceiver(panelReceiver);
        super.onDestroy();
    }
}
