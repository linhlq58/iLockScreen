package com.tetstudio.ilockscreen.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by lequy on 1/13/2017.
 */

public class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return true;
    }
}
