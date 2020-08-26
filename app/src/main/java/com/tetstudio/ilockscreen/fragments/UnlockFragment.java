package com.tetstudio.ilockscreen.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tetstudio.ilockscreen.R;
import com.tetstudio.ilockscreen.utils.Constant;
import com.tetstudio.ilockscreen.utils.iOSTextView;

/**
 * Created by Linh Lee on 10/10/2016.
 */
public class UnlockFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout unlockLayout;
    private View shadowView;
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
    private SharedPreferences sharedPreferences;
    private boolean is4Char;
    private Animation shake;
    private Vibrator vibrator;
    private BroadcastReceiver receiver;
    private IntentFilter filter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_unlock, container, false);

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        unlockLayout = (RelativeLayout) rootView.findViewById(R.id.unlock_layout);
        shadowView = rootView.findViewById(R.id.shadow_view);
        incorrectPassword = (iOSTextView) rootView.findViewById(R.id.incorrect_password);
        deleteText = (iOSTextView) rootView.findViewById(R.id.delete_text);
        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.viewPagerCountDots);
        number1 = (RelativeLayout) rootView.findViewById(R.id.no_1);
        number2 = (RelativeLayout) rootView.findViewById(R.id.no_2);
        number3 = (RelativeLayout) rootView.findViewById(R.id.no_3);
        number4 = (RelativeLayout) rootView.findViewById(R.id.no_4);
        number5 = (RelativeLayout) rootView.findViewById(R.id.no_5);
        number6 = (RelativeLayout) rootView.findViewById(R.id.no_6);
        number7 = (RelativeLayout) rootView.findViewById(R.id.no_7);
        number8 = (RelativeLayout) rootView.findViewById(R.id.no_8);
        number9 = (RelativeLayout) rootView.findViewById(R.id.no_9);
        number0 = (iOSTextView) rootView.findViewById(R.id.no_0);
        shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
        //passwordText.setText(password);

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

        filter = new IntentFilter("check noti");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    boolean flag = extras.getBoolean("flag");
                    if (flag) {
                        shadowView.setVisibility(View.VISIBLE);
                    } else {
                        shadowView.setVisibility(View.GONE);
                    }
                }
            }
        };
        getActivity().registerReceiver(receiver, filter);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
    }

    private void checkPassEmpty() {
        if (password.equals("")) {
            deleteText.setText(getActivity().getResources().getString(R.string.cancel));
        } else {
            deleteText.setText(getActivity().getResources().getString(R.string.delete_text));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
                    //((MainActivity) getActivity()).changeFragment();
                } else {
                    password = "";
                    //passwordText.setText(password);
                    drawPageSelectionIndicators(password.length());
                    checkPassEmpty();
                }
                break;
        }
    }

    public void unlockScreen() {
        //android.os.Process.killProcess(android.os.Process.myPid());
        getActivity().finish();
    }

    private void inputPassword(String s) {
        if (incorrectPassword.getVisibility() == View.VISIBLE) {
            incorrectPassword.setVisibility(View.GONE);
        }
        if (password.length() < dotsQuantity) {
            password += s;
            //passwordText.setText(password);
            drawPageSelectionIndicators(password.length());
        }
        if (password.length() == dotsQuantity) {
            if (password.equals(currentPassword)) {
                unlockScreen();
            } else {
                password = "";
                //passwordText.setText(password);
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
        //passwordText.setText(password);
        drawPageSelectionIndicators(password.length());
        checkPassEmpty();
        incorrectPassword.setVisibility(View.GONE);
    }

    private void drawPageSelectionIndicators(int passLength) {
        int margin = Constant.convertDpIntoPixels(10, getActivity());

        if (mLinearLayout != null) {
            mLinearLayout.removeAllViews();
        }

        mDots = new ImageView[dotsQuantity];

        //set image with orange circle if mDots[i] == mPosition
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new ImageView(getActivity());
            if (i < passLength)
                mDots[i].setImageDrawable(getResources().getDrawable(R.mipmap.ic_pass));
            else
                mDots[i].setImageDrawable(getResources().getDrawable(R.mipmap.ic_pass_empty));

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
}
