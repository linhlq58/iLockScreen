package com.tetstudio.ilockscreen.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tetstudio.ilockscreen.R;
import com.tetstudio.ilockscreen.dialogs.ChangePassTypeDialog;
import com.tetstudio.ilockscreen.utils.Constant;

/**
 * Created by Linh Lee on 10/12/2016.
 */
public class ChangePasswordActivity extends Activity implements View.OnClickListener {
    private TextView titleText;
    //private TextView passwordText;
    private TextView incorrectPassword;
    private TextView clearText;
    private TextView confirmText;
    private TextView passType;
    private ImageView backgroundImage;
    private LinearLayout mLinearLayout;
    private ImageView[] mDots;
    private Bitmap mySelectedImage;
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
    private TextView no1Text;
    private TextView no2Text;
    private TextView no2Char;
    private TextView no3Text;
    private TextView no3Char;
    private TextView no4Text;
    private TextView no4Char;
    private TextView no5Text;
    private TextView no5Char;
    private TextView no6Text;
    private TextView no6Char;
    private TextView no7Text;
    private TextView no7Char;
    private TextView no8Text;
    private TextView no8Char;
    private TextView no9Text;
    private TextView no9Char;
    private TextView number0;
    private String password = "";
    private String tempNewPass = "";
    private String currentPassword;
    private SharedPreferences sharedPreferences;
    private String imageBase64;
    private boolean is4CharPre;
    private boolean is4Char;
    private int confirmType = 0;
    private Animation shake;
    private Typeface myFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        titleText = (TextView) findViewById(R.id.title_text);
        //passwordText = (TextView) findViewById(R.id.password);
        incorrectPassword = (TextView) findViewById(R.id.incorrect_password);
        clearText = (TextView) findViewById(R.id.clear_text);
        confirmText = (TextView) findViewById(R.id.confirm_text);
        passType = (TextView) findViewById(R.id.pass_type);
        backgroundImage = (ImageView) findViewById(R.id.bg_img);
        mLinearLayout = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        number1 = (RelativeLayout) findViewById(R.id.no_1);
        number2 = (RelativeLayout) findViewById(R.id.no_2);
        number3 = (RelativeLayout) findViewById(R.id.no_3);
        number4 = (RelativeLayout) findViewById(R.id.no_4);
        number5 = (RelativeLayout) findViewById(R.id.no_5);
        number6 = (RelativeLayout) findViewById(R.id.no_6);
        number7 = (RelativeLayout) findViewById(R.id.no_7);
        number8 = (RelativeLayout) findViewById(R.id.no_8);
        number9 = (RelativeLayout) findViewById(R.id.no_9);
        no1Text = (TextView) findViewById(R.id.no_1_text);
        no2Text = (TextView) findViewById(R.id.no_2_text);
        no2Char = (TextView) findViewById(R.id.no_2_char);
        no3Text = (TextView) findViewById(R.id.no_3_text);
        no3Char = (TextView) findViewById(R.id.no_3_char);
        no4Text = (TextView) findViewById(R.id.no_4_text);
        no4Char = (TextView) findViewById(R.id.no_4_char);
        no5Text = (TextView) findViewById(R.id.no_5_text);
        no5Char = (TextView) findViewById(R.id.no_5_char);
        no6Text = (TextView) findViewById(R.id.no_6_text);
        no6Char = (TextView) findViewById(R.id.no_6_char);
        no7Text = (TextView) findViewById(R.id.no_7_text);
        no7Char = (TextView) findViewById(R.id.no_7_char);
        no8Text = (TextView) findViewById(R.id.no_8_text);
        no8Char = (TextView) findViewById(R.id.no_8_char);
        no9Text = (TextView) findViewById(R.id.no_9_text);
        no9Char = (TextView) findViewById(R.id.no_9_char);
        number0 = (TextView) findViewById(R.id.no_0);
        shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        myFont = Constant.setRobotoFont(this);

        titleText.setTypeface(myFont);
        incorrectPassword.setTypeface(myFont);
        clearText.setTypeface(myFont);
        confirmText.setTypeface(myFont);
        passType.setTypeface(myFont);
        no1Text.setTypeface(myFont);
        no2Text.setTypeface(myFont);
        no2Char.setTypeface(myFont);
        no3Text.setTypeface(myFont);
        no3Char.setTypeface(myFont);
        no4Text.setTypeface(myFont);
        no4Char.setTypeface(myFont);
        no5Text.setTypeface(myFont);
        no5Char.setTypeface(myFont);
        no6Text.setTypeface(myFont);
        no6Char.setTypeface(myFont);
        no7Text.setTypeface(myFont);
        no7Char.setTypeface(myFont);
        no8Text.setTypeface(myFont);
        no8Char.setTypeface(myFont);
        no9Text.setTypeface(myFont);
        no9Char.setTypeface(myFont);
        number0.setTypeface(myFont);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentPassword = sharedPreferences.getString("currentPass", "");
        imageBase64 = sharedPreferences.getString("image base64", "");
        is4Char = sharedPreferences.getBoolean("4char password", true);
        is4CharPre = sharedPreferences.getBoolean("4char password", true);
        if (currentPassword.equals("")) {
            titleText.setText(getResources().getString(R.string.enter_new_password));
            confirmText.setText(getResources().getString(R.string.next));
            confirmType = 1;
        } else {
            titleText.setText(getResources().getString(R.string.enter_current_password));
            confirmText.setText(getResources().getString(R.string.next));
            confirmType = 0;
        }
        if (is4Char) {
            dotsQuantity = 4;
        } else {
            dotsQuantity = 6;
        }

        if (confirmType == 0) {
            drawPageSelectionIndicators(currentPassword.length(), password.length());
        } else {
            drawPageSelectionIndicators(dotsQuantity, password.length());
        }

        if (confirmType == 1) {
            passType.setVisibility(View.VISIBLE);
        } else {
            passType.setVisibility(View.GONE);
        }

        setBackgroundImage();

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
        clearText.setOnClickListener(this);
        confirmText.setOnClickListener(this);
        passType.setOnClickListener(this);

        Constant.increaseHitArea(clearText);
        Constant.increaseHitArea(confirmText);
        Constant.increaseHitArea(passType);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mySelectedImage != null) {
            mySelectedImage.recycle();
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
            case R.id.pass_type:
                ChangePassTypeDialog dialog = new ChangePassTypeDialog(this, new ChangePassTypeDialog.ButtonListener() {
                    @Override
                    public void okClicked() {
                        password = "";
                        is4CharPre = sharedPreferences.getBoolean("4char password pre", true);
                        if (is4CharPre) {
                            dotsQuantity = 4;
                        } else {
                            dotsQuantity = 6;
                        }
                        drawPageSelectionIndicators(dotsQuantity, password.length());
                    }
                });
                dialog.show();
                break;
            case R.id.clear_text:
                password = "";
                if (confirmType == 0) {
                    drawPageSelectionIndicators(currentPassword.length(), password.length());
                } else {
                    drawPageSelectionIndicators(dotsQuantity, password.length());
                }
                break;
            case R.id.confirm_text:
                switch (confirmType) {
                    case 0:
                        if (password.equals(currentPassword)) {
                            titleText.setText(getResources().getString(R.string.enter_new_password));
                            confirmText.setText(getResources().getString(R.string.next));
                            password = "";
                            drawPageSelectionIndicators(dotsQuantity, password.length());
                            confirmType = 1;
                            passType.setVisibility(View.VISIBLE);
                        } else {
                            password = "";
                            drawPageSelectionIndicators(currentPassword.length(), password.length());
                            incorrectPassword.setVisibility(View.VISIBLE);
                            incorrectPassword.setText(getResources().getString(R.string.incorrect_pass));
                            mLinearLayout.startAnimation(shake);
                        }
                        break;
                    case 1:
                        if (password.length() == dotsQuantity) {
                            titleText.setText(getResources().getString(R.string.confirm_password));
                            confirmText.setText("OK");
                            tempNewPass = password;
                            password = "";
                            drawPageSelectionIndicators(dotsQuantity, password.length());
                            confirmType = 2;
                            passType.setVisibility(View.GONE);
                        } else {
                            password = "";
                            drawPageSelectionIndicators(dotsQuantity, password.length());
                            incorrectPassword.setVisibility(View.VISIBLE);
                            incorrectPassword.setText(getResources().getString(R.string.please_enter_correct));
                        }
                        break;
                    case 2:
                        if (password.equals(tempNewPass)) {
                            sharedPreferences.edit().putString("currentPass", password).apply();
                            sharedPreferences.edit().putBoolean("4char password", is4CharPre).apply();
                            finish();
                        } else {
                            password = "";
                            drawPageSelectionIndicators(dotsQuantity, password.length());
                            incorrectPassword.setVisibility(View.VISIBLE);
                            incorrectPassword.setText(getResources().getString(R.string.incorrect_pass));
                            mLinearLayout.startAnimation(shake);
                        }
                        break;
                }
                break;
        }
    }

    private void inputPassword(String s) {
        if (incorrectPassword.getVisibility() == View.VISIBLE) {
            incorrectPassword.setVisibility(View.GONE);
        }
        if (confirmType == 0) {
            if (password.length() < currentPassword.length()) {
                password += s;
                drawPageSelectionIndicators(currentPassword.length(), password.length());
            }
        } else {
            if (password.length() < dotsQuantity) {
                password += s;
                drawPageSelectionIndicators(dotsQuantity, password.length());
            }
        }
    }

    private void drawPageSelectionIndicators(int dotsNum, int passLength) {
        int margin = Constant.convertDpIntoPixels(10, this);

        if (mLinearLayout != null) {
            mLinearLayout.removeAllViews();
        }

        mDots = new ImageView[dotsNum];

        //set image with orange circle if mDots[i] == mPosition
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new ImageView(this);
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

    public void setBackgroundImage() {
        if (imageBase64.equals("")) {
            backgroundImage.setImageResource(sharedPreferences.getInt("background img", R.mipmap.wallpager_25));
        } else {
            byte[] imageAsBytes = Base64.decode(imageBase64, Base64.DEFAULT);
            mySelectedImage = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(mySelectedImage , 0, 0, mySelectedImage.getWidth(), mySelectedImage.getHeight(), matrix, true);
            backgroundImage.setImageBitmap(rotatedBitmap);
        }
    }
}
