package com.tetstudio.ilockscreen.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tetstudio.ilockscreen.R;

/**
 * Created by Linh Lee on 10/21/2016.
 */
public class ChangePassTypeDialog extends Dialog {
    private Activity context;
    private ButtonListener listener;

    private TextView okButton;
    private TextView cancelButton;
    private RadioButton radio4char;
    private RadioButton radio6char;
    private SharedPreferences sharedPreferences;
    private boolean is4Char;

    public ChangePassTypeDialog(Activity context, ButtonListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_change_pass_type);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        is4Char = sharedPreferences.getBoolean("4char password pre", true);

        okButton = (TextView) findViewById(R.id.ok_button);
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        radio4char = (RadioButton) findViewById(R.id.radio_4char);
        radio6char = (RadioButton) findViewById(R.id.radio_6char);

        if (is4Char) {
            radio4char.setChecked(true);
        } else {
            radio6char.setChecked(true);
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radio4char.isChecked()) {
                    sharedPreferences.edit().putBoolean("4char password pre", true).apply();
                } else if (radio6char.isChecked()) {
                    sharedPreferences.edit().putBoolean("4char password pre", false).apply();
                }
                listener.okClicked();
                dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public interface ButtonListener {
        void okClicked();
    }
}
