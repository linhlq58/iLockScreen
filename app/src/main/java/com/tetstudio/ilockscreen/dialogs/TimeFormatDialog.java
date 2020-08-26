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
 * Created by Linh Lee on 10/20/2016.
 */
public class TimeFormatDialog extends Dialog {
    private Activity context;

    private TextView okButton;
    private TextView cancelButton;
    private RadioButton radio24h;
    private RadioButton radio12h;
    private SharedPreferences sharedPreferences;
    private boolean is24h;

    public TimeFormatDialog(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_time_format);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        is24h = sharedPreferences.getBoolean("24h format", true);

        okButton = (TextView) findViewById(R.id.ok_button);
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        radio24h = (RadioButton) findViewById(R.id.radio_24h);
        radio12h = (RadioButton) findViewById(R.id.radio_12h);

        if (is24h) {
            radio24h.setChecked(true);
        } else {
            radio12h.setChecked(true);
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radio24h.isChecked()) {
                    sharedPreferences.edit().putBoolean("24h format", true).apply();
                } else if (radio12h.isChecked()) {
                    sharedPreferences.edit().putBoolean("24h format", false).apply();
                }
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
}
