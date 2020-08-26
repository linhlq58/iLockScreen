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
 * Created by Linh Lee on 10/28/2016.
 */
public class ChangeLanguageDialog extends Dialog {
    private Activity context;
    private LanguageChecked listener;

    private TextView okButton;
    private TextView cancelButton;
    private RadioButton radioEng;
    private RadioButton radioViet;
    private SharedPreferences sharedPreferences;
    private boolean isEnglish;

    public ChangeLanguageDialog(Activity context, LanguageChecked listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_change_language);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        isEnglish = sharedPreferences.getBoolean("english", false);

        okButton = (TextView) findViewById(R.id.ok_button);
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        radioEng = (RadioButton) findViewById(R.id.radio_en);
        radioViet = (RadioButton) findViewById(R.id.radio_vi);

        if (isEnglish) {
            radioEng.setChecked(true);
        } else {
            radioViet.setChecked(true);
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioEng.isChecked()) {
                    sharedPreferences.edit().putBoolean("english", true).apply();
                    listener.onEngSelected();
                } else if (radioViet.isChecked()) {
                    sharedPreferences.edit().putBoolean("english", false).apply();
                    listener.onViSelected();
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

    public interface LanguageChecked {
        void onEngSelected();

        void onViSelected();
    }
}
