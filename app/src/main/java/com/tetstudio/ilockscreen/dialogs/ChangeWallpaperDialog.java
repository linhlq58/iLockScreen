package com.tetstudio.ilockscreen.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.tetstudio.ilockscreen.R;

/**
 * Created by Linh Lee on 10/12/2016.
 */
public class ChangeWallpaperDialog extends Dialog implements View.OnClickListener {
    private Activity context;
    private ButtonClicked buttonClicked;

    private TextView existedButton;
    private TextView galleryButton;

    public ChangeWallpaperDialog(Activity context, ButtonClicked buttonClicked) {
        super(context);
        this.context = context;
        this.buttonClicked = buttonClicked;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_change_wallpaper);

        existedButton = (TextView) findViewById(R.id.existed_button);
        galleryButton = (TextView) findViewById(R.id.gallery_button);

        existedButton.setOnClickListener(this);
        galleryButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.existed_button:
                buttonClicked.existedButtonClicked();
                dismiss();
                break;
            case R.id.gallery_button:
                buttonClicked.galleryButtonClicked();
                dismiss();
                break;
        }
    }

    public interface ButtonClicked {
        void existedButtonClicked();
        void galleryButtonClicked();
    }
}
