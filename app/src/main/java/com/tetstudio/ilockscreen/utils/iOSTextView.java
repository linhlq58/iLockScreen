package com.tetstudio.ilockscreen.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Linh Lee on 10/24/2016.
 */
public class iOSTextView extends TextView {
    public iOSTextView(Context context) {
        super(context);
        Typeface typeface = Constant.setRobotoFont(context);
        this.setTypeface(typeface);
    }

    public iOSTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface typeface = Constant.setRobotoFont(context);
        this.setTypeface(typeface);
    }

    public iOSTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Typeface typeface = Constant.setRobotoFont(context);
        this.setTypeface(typeface);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
