package com.cardiomood.ytranslate.ui;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Enables {@link TouchableSpan} to receive touch events.
 * <br/>
 * From: http://stackoverflow.com/a/7292485/1199452
 *
 * Created by Anton Danshin on 01/12/14.
 */
public class LinkTouchMovementMethod extends LinkMovementMethod {


    /**
     * {@inheritDoc}
     *
     * The code copied from {@link LinkMovementMethod#onTouchEvent(TextView, Spannable, MotionEvent)}.
     */
    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {

        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_CANCEL) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            TouchableSpan[] link = buffer.getSpans(off, off, TouchableSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onTouch(widget, event);
                    link[0].onClick(widget);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    link[0].onTouch(widget, event);
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                }

                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }
}
