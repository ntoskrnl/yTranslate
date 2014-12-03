package com.cardiomood.ytranslate.ui;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Anton Danshin on 01/12/14.
 */
public abstract class TouchableSpan extends ClickableSpan  {

    /**
     * Performs the touch action associated with this span.
     * @return
     */
    public abstract boolean onTouch(View widget, MotionEvent m);

    /**
     * Could make the text underlined or change link color.
     */
    @Override
    public abstract void updateDrawState(TextPaint ds);

}
