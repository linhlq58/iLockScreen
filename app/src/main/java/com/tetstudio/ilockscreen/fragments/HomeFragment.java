package com.tetstudio.ilockscreen.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.tetstudio.ilockscreen.R;
import com.tetstudio.ilockscreen.adapters.ListNotificationAdapter;
import com.tetstudio.ilockscreen.objects.NotificationObject;
import com.tetstudio.ilockscreen.services.NotificationReceiver;
import com.tetstudio.ilockscreen.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Linh Lee on 10/10/2016.
 */
public class HomeFragment extends Fragment {
    private View shadowView;
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
    private SharedPreferences sharedPreferences;
    private boolean is24h;
    private Typeface myFont;
    private BroadcastReceiver receiver;
    private IntentFilter filter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        is24h = sharedPreferences.getBoolean("24h format", true);

        shadowView = rootView.findViewById(R.id.shadow_view);
        mTimeText = (TextView) rootView.findViewById(R.id.time_text);
        mDateText = (TextView) rootView.findViewById(R.id.date_text);
        mAmPmText = (TextView) rootView.findViewById(R.id.time_type);
        mListView = (ListView) rootView.findViewById(R.id.list_notification);
        mClearAllNoti = (TextView) rootView.findViewById(R.id.clear_all_noti);
        mSlideText = (ShimmerTextView) rootView.findViewById(R.id.slide_text);
        myFont = Constant.setRobotoFont(getActivity());

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

        mAdapter = new ListNotificationAdapter(getActivity(), R.layout.notification_layout, mListNotification);
        mListView.setAdapter(mAdapter);

        filter = new IntentFilter();
        filter.addAction("getListNotification");
        receiver = new NotificationReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                Log.d("testttt", "okkkkkk");
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
                        ArrayList<NotificationObject> listNoti = extras.getParcelableArrayList("list notification");
                        mListNotification.addAll(listNoti);
                        mAdapter.notifyDataSetChanged();

                        sendBroadcastToUnlockScreen();
                    } else if (intent.getStringExtra("type").equals("addNoti")) {
                        NotificationObject notification = extras.getParcelable("noti object");
                        mListNotification.add(notification);
                        mAdapter.notifyDataSetChanged();

                        sendBroadcastToUnlockScreen();
                    }
                }
            }
        };
        getActivity().registerReceiver(receiver, filter);

        sendBroadcastToUnlockScreen();

        Constant.increaseHitArea(mClearAllNoti);
        mClearAllNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("NOTIFICATION_LISTENER_SERVICE");
                intent.putExtra("command", "removeAll");
                getActivity().sendBroadcast(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        is24h = sharedPreferences.getBoolean("24h format", true);
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
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (broadcastReceiver != null) {
            getActivity().unregisterReceiver(broadcastReceiver);
        }
    }

    private void sendBroadcastToUnlockScreen() {
        Intent i = new Intent("check noti");
        if (checkListEmpty()) {
            i.putExtra("flag", true);
        } else {
            i.putExtra("flag", false);
        }
        getActivity().sendBroadcast(i);
    }

    public boolean checkListEmpty() {
        if (mListNotification.size() > 0) {
            mClearAllNoti.setVisibility(View.VISIBLE);
            shadowView.setVisibility(View.VISIBLE);
            return true;
        } else {
            mClearAllNoti.setVisibility(View.GONE);
            shadowView.setVisibility(View.GONE);
            return false;
        }
    }
}
