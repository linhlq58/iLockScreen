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
import android.widget.EditText;
import android.widget.TextView;

import com.tetstudio.ilockscreen.R;

/**
 * Created by Linh Lee on 10/17/2016.
 */
public class SetSlideTextDialog extends Dialog {
    private Activity context;

    private EditText editSlideText;
    private TextView okButton;
    private TextView cancelButton;
    private SharedPreferences sharedPreferences;

    public SetSlideTextDialog(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_set_slide_text);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        editSlideText = (EditText) findViewById(R.id.edit_slide_text);
        okButton = (TextView) findViewById(R.id.ok_button);
        cancelButton = (TextView) findViewById(R.id.cancel_button);

        editSlideText.setText(sharedPreferences.getString("slide text", "Slide to unlock"));
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().putString("slide text", editSlideText.getText().toString()).apply();
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
