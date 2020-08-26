package com.tetstudio.ilockscreen.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Linh Lee on 10/24/2016.
 */
public class iOSTextViewMedium extends TextView {
    public iOSTextViewMedium(Context context) {
        super(context);
        Typeface typeface = Constant.setRobotoMediumFont(context);
        this.setTypeface(typeface);
    }

    public iOSTextViewMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface typeface = Constant.setRobotoMediumFont(context);
        this.setTypeface(typeface);
    }

    public iOSTextViewMedium(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Typeface typeface = Constant.setRobotoMediumFont(context);
        this.setTypeface(typeface);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
