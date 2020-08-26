package com.tetstudio.ilockscreen.adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tetstudio.ilockscreen.R;
import com.tetstudio.ilockscreen.objects.NotificationObject;
import com.tetstudio.ilockscreen.services.NotificationReceiver;
import com.tetstudio.ilockscreen.utils.Constant;
import com.tetstudio.ilockscreen.utils.iOSTextView;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by Linh Lee on 10/10/2016.
 */
public class MyPagerAdapter extends PagerAdapter implements View.OnClickListener {
    private static int NUM_ITEMS = 2;
    private Context context;

    private SharedPreferences sharedPreferences;
    private Typeface myFont;

    private RelativeLayout unlockLayout;
    private View unlockShadowView;
    private iOSTextView incorrectPassword;
    private iOSTextView deleteText;
    private LinearLayout mLinearLayout;
    private ImageView[] mDots;
    private int dotsQuantity;
    private RelativeLayout number1;
    private RelativeLayout number2;
    private RelativeLayout number3;
    private RelativeLayout number4;
    private RelativeLayout number5;
    private RelativeLayout number6;
    private RelativeLayout number7;
    private RelativeLayout number8;
    private RelativeLayout number9;
    private iOSTextView number0;
    private String password = "";
    private String currentPassword;
    private boolean is4Char;
    private Animation shake;
    private Vibrator vibrator;
    private BroadcastReceiver checkNotiReceiver;
    private IntentFilter checkNotiFilter;

    private View homeShadowView;
    private TextView mTimeText;
    private TextView mDateText;
    private TextView mAmPmText;
    private ListView mListView;
    private TextView mClearAllNoti;
    private ListNotificationAdapter mAdapter;
    private ArrayList<NotificationObject> mListNotification;
    private Shimmer shimmer;
    private ShimmerTextView mSlideText;
    private SimpleDateFormat clockFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat clock12Format = new SimpleDateFormat("hh:mm");
    private SimpleDateFormat amPmFormat = new SimpleDateFormat("a");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd");
    private BroadcastReceiver broadcastReceiver;
    private boolean is24h;
    private BroadcastReceiver receiver;
    private IntentFilter filter;

    public MyPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        LayoutInflater inflater = LayoutInflater.from(context);

        ViewGroup unlockLayout = (ViewGroup) inflater.inflate(R.layout.fragment_unlock, container, false);
        ViewGroup homeLayout = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        View viewArr[] = {unlockLayout, homeLayout};
        if (position == 0) {
            handleUnlockView(unlockLayout);
        } else if (position == 1) {
            handleHomeView(homeLayout);
        }
        container.addView(viewArr[position]);
        return viewArr[position];
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    private View findItemInViewById(View container, int viewId) {
        View result = container.findViewById(viewId);
        if (result != null) {
            return result;
        } else if (container instanceof ViewGroup) {
            int viewCount = ((ViewGroup) container).getChildCount();
            for (int i = 0; i < viewCount; ++i) {
                result = findItemInViewById(((ViewGroup) container).getChildAt(i), viewId);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private void handleUnlockView(ViewGroup view) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        unlockLayout = (RelativeLayout) findItemInViewById(view, R.id.unlock_layout);
        unlockShadowView = findItemInViewById(view, R.id.shadow_view);
        incorrectPassword = (iOSTextView) findItemInViewById(view, R.id.incorrect_password);
        deleteText = (iOSTextView) findItemInViewById(view, R.id.delete_text);
        mLinearLayout = (LinearLayout) findItemInViewById(view, R.id.viewPagerCountDots);
        number1 = (RelativeLayout) findItemInViewById(view, R.id.no_1);
        number2 = (RelativeLayout) findItemInViewById(view, R.id.no_2);
        number3 = (RelativeLayout) findItemInViewById(view, R.id.no_3);
        number4 = (RelativeLayout) findItemInViewById(view, R.id.no_4);
        number5 = (RelativeLayout) findItemInViewById(view, R.id.no_5);
        number6 = (RelativeLayout) findItemInViewById(view, R.id.no_6);
        number7 = (RelativeLayout) findItemInViewById(view, R.id.no_7);
        number8 = (RelativeLayout) findItemInViewById(view, R.id.no_8);
        number9 = (RelativeLayout) findItemInViewById(view, R.id.no_9);
        number0 = (iOSTextView) findItemInViewById(view, R.id.no_0);
        shake = AnimationUtils.loadAnimation(context, R.anim.shake);

        currentPassword = sharedPreferences.getString("currentPass", "");
        is4Char = sharedPreferences.getBoolean("4char password", true);
        if (is4Char) {
            dotsQuantity = 4;
        } else {
            dotsQuantity = 6;
        }

        checkPassEmpty();

        if (sharedPreferences.getBoolean("hasPassChecked", false) == false) {
            unlockLayout.setVisibility(View.GONE);
        }

        drawPageSelectionIndicators(password.length());

        number1.setOnClickListener(this);
        number2.setOnClickListener(this);
        number3.setOnClickListener(this);
        number4.setOnClickListener(this);
        number5.setOnClickListener(this);
        number6.setOnClickListener(this);
        number7.setOnClickListener(this);
        number8.setOnClickListener(this);
        number9.setOnClickListener(this);
        number0.setOnClickListener(this);
        deleteText.setOnClickListener(this);

        Constant.increaseHitArea(deleteText);

        /*checkNotiFilter = new IntentFilter("check noti");
        checkNotiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    boolean flag = extras.getBoolean("flag");
                    if (flag) {
                        unlockShadowView.setVisibility(View.VISIBLE);
                    } else {
                        unlockShadowView.setVisibility(View.GONE);
                    }
                }
            }
        };
        context.registerReceiver(receiver, filter);*/
    }

    private void handleHomeView(ViewGroup view) {
        is24h = sharedPreferences.getBoolean("24h format", true);

        homeShadowView = findItemInViewById(view, R.id.shadow_view);
        mTimeText = (TextView) findItemInViewById(view, R.id.time_text);
        mDateText = (TextView) findItemInViewById(view, R.id.date_text);
        mAmPmText = (TextView) findItemInViewById(view, R.id.time_type);
        mListView = (ListView) findItemInViewById(view, R.id.list_notification);
        mClearAllNoti = (TextView) findItemInViewById(view, R.id.clear_all_noti);
        mSlideText = (ShimmerTextView) findItemInViewById(view, R.id.slide_text);
        myFont = Constant.setRobotoFont(context);
        OverScrollDecoratorHelper.setUpOverScroll(mListView);

        //Setup slide text
        mSlideText.setText(sharedPreferences.getString("slide text", "Slide to unlock"));
        mSlideText.setTypeface(myFont);
        shimmer = new Shimmer();
        shimmer.start(mSlideText);

        if (is24h) {
            mTimeText.setText(clockFormat.format(new Date()));
            mAmPmText.setVisibility(View.GONE);
        } else {
            mTimeText.setText(clock12Format.format(new Date()));
            mAmPmText.setVisibility(View.VISIBLE);
            mAmPmText.setText(amPmFormat.format(new Date()));
        }
        mDateText.setText(dateFormat.format(new Date()));

        mListNotification = new ArrayList<>();

        mAdapter = new ListNotificationAdapter(context, R.layout.notification_layout, mListNotification);
        mListView.setAdapter(mAdapter);

        filter = new IntentFilter();
        filter.addAction("getListNotification");
        receiver = new NotificationReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    if (intent.getStringExtra("type").equals("removeNoti")) {
                        for (int i = 0; i < mListNotification.size(); i++) {
                            if (mListNotification.get(i).getId() == extras.getInt("removed id")) {
                                mListNotification.remove(mListNotification.get(i));
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        sendBroadcastToUnlockScreen();
                    } else if (intent.getStringExtra("type").equals("getAllNoti")) {
                        mListNotification.removeAll(mListNotification);
                        ArrayList<NotificationObject> listNoti = extras.getParcelableArrayList("list notification");
                        mListNotification.addAll(listNoti);
                        mAdapter.notifyDataSetChanged();

                        sendBroadcastToUnlockScreen();
                    } else if (intent.getStringExtra("type").equals("addNoti")) {
                        NotificationObject notification = extras.getParcelable("noti object");
                        if (mListNotification.size() > 0) {
                            boolean flag = true;
                            for (int i = 0; i < mListNotification.size(); i++) {
                                if (notification.getId() == mListNotification.get(i).getId()) {
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
                                mListNotification.add(notification);
                            }
                        } else {
                            mListNotification.add(notification);
                        }
                        mAdapter.notifyDataSetChanged();

                        sendBroadcastToUnlockScreen();
                    }
                }
            }
        };
        context.registerReceiver(receiver, filter);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    if (is24h) {
                        mTimeText.setText(clockFormat.format(new Date()));
                        mAmPmText.setVisibility(View.GONE);
                    } else {
                        mTimeText.setText(clock12Format.format(new Date()));
                        mAmPmText.setVisibility(View.VISIBLE);
                        mAmPmText.setText(amPmFormat.format(new Date()));
                    }
                    mDateText.setText(dateFormat.format(new Date()));
                }
            }
        };
        context.registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

        sendBroadcastToUnlockScreen();

        Constant.increaseHitArea(mClearAllNoti);

        mClearAllNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("NOTIFICATION_LISTENER_SERVICE");
                intent.putExtra("command", "removeAll");
                context.sendBroadcast(intent);
            }
        });
    }

    // Begin unlock view function
    private void checkPassEmpty() {
        if (password.equals("")) {
            deleteText.setText(context.getResources().getString(R.string.cancel));
        } else {
            deleteText.setText(context.getResources().getString(R.string.delete_text));
        }
    }

    private void inputPassword(String s) {
        if (incorrectPassword.getVisibility() == View.VISIBLE) {
            incorrectPassword.setVisibility(View.GONE);
        }
        if (password.length() < dotsQuantity) {
            password += s;
            drawPageSelectionIndicators(password.length());
        }
        if (password.length() == dotsQuantity) {
            if (password.equals(currentPassword)) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent("unlock");
                        context.sendBroadcast(i);
                    }
                }, 200);
            } else {
                password = "";
                drawPageSelectionIndicators(password.length());
                incorrectPassword.setVisibility(View.VISIBLE);
                mLinearLayout.startAnimation(shake);
            }
        }
        vibrator.vibrate(100);

        checkPassEmpty();
    }

    public void clearPass() {
        password = "";
        drawPageSelectionIndicators(password.length());
        checkPassEmpty();
        incorrectPassword.setVisibility(View.GONE);
    }

    private void drawPageSelectionIndicators(int passLength) {
        int margin = Constant.convertDpIntoPixels(10, context);

        if (mLinearLayout != null) {
            mLinearLayout.removeAllViews();
        }

        mDots = new ImageView[dotsQuantity];

        //set image with orange circle if mDots[i] == mPosition
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new ImageView(context);
            if (i < passLength)
                mDots[i].setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_pass));
            else
                mDots[i].setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_pass_empty));

            //set layout for circle indicators
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0);
            mDots[i].setPadding(margin, 0, margin, 0);
            mLinearLayout.addView(mDots[i], params);
        }
    }
    // End unlock view function

    // Begin home view function
    private void sendBroadcastToUnlockScreen() {
        Intent i = new Intent("check noti");
        if (checkListEmpty()) {
            i.putExtra("flag", true);
        } else {
            i.putExtra("flag", false);
        }
        context.sendBroadcast(i);
    }

    public boolean checkListEmpty() {
        if (mListNotification.size() > 0) {
            mClearAllNoti.setVisibility(View.VISIBLE);
            homeShadowView.setVisibility(View.VISIBLE);
            return true;
        } else {
            mClearAllNoti.setVisibility(View.GONE);
            homeShadowView.setVisibility(View.GONE);
            return false;
        }
    }

    // End home view function

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no_1:
                inputPassword("1");
                break;
            case R.id.no_2:
                inputPassword("2");
                break;
            case R.id.no_3:
                inputPassword("3");
                break;
            case R.id.no_4:
                inputPassword("4");
                break;
            case R.id.no_5:
                inputPassword("5");
                break;
            case R.id.no_6:
                inputPassword("6");
                break;
            case R.id.no_7:
                inputPassword("7");
                break;
            case R.id.no_8:
                inputPassword("8");
                break;
            case R.id.no_9:
                inputPassword("9");
                break;
            case R.id.no_0:
                inputPassword("0");
                break;
            case R.id.delete_text:
                if (password.equals("")) {
                    Intent i = new Intent("go_to_home");
                    context.sendBroadcast(i);
                } else {
                    password = "";
                    drawPageSelectionIndicators(password.length());
                    checkPassEmpty();
                }
                break;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}
