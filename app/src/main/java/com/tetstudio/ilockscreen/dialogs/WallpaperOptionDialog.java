package com.tetstudio.ilockscreen.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.tetstudio.ilockscreen.R;

/**
 * Created by lequy on 1/17/2017.
 */

public class WallpaperOptionDialog extends Dialog implements View.OnClickListener {
    private Activity context;
    private ButtonClicked buttonClicked;

    private TextView homeButton;
    private TextView lockButton;
    private TextView homeLockButton;

    public WallpaperOptionDialog(Activity context, ButtonClicked buttonClicked) {
        super(context);
        this.context = context;
        this.buttonClicked = buttonClicked;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_wallpaper_option);

        homeButton = (TextView) findViewById(R.id.home_button);
        lockButton = (TextView) findViewById(R.id.lock_button);
        homeLockButton = (TextView) findViewById(R.id.home_and_lock);

        homeButton.setOnClickListener(this);
        lockButton.setOnClickListener(this);
        homeLockButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_button:
                buttonClicked.homeButtonClicked();
                dismiss();
                break;
            case R.id.lock_button:
                buttonClicked.lockButtonClicked();
                dismiss();
                break;
            case R.id.home_and_lock:
                buttonClicked.homeLockButtonClicked();
                dismiss();
                break;
        }
    }

    public interface ButtonClicked {
        void homeButtonClicked();
        void lockButtonClicked();
        void homeLockButtonClicked();
    }
}
