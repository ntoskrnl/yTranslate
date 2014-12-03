package com.cardiomood.ytranslate.ui;

import android.graphics.Color;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Pattern;

/**
 * Created by Anton Danshin on 01/12/14.
 */
public class ClickableWordsHelper {

    private static final MovementMethod MOVEMENT_METHOD = new LinkTouchMovementMethod();
    private static final String DEFAULT_DELIMITER = "[\\s;|\\)\\(!?*_+=><.,:\\[\\]\"]";
    private static final String SPLIT_PATTERN_FORMAT = "((?<=%1$s)|(?=%1$s))";

    private final TextView mTextView;
    private final Pattern mPattern = Pattern.compile("(" + DEFAULT_DELIMITER + ")+");

    private Callback callback;


    public ClickableWordsHelper(TextView textView) {
        this.mTextView = textView;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setText(String text) {
        String html = linkifyWords(text);
        setTextViewHTML(mTextView, html);
    }

    private String linkifyWords(String line) {
        String[] words = line.split(String.format(SPLIT_PATTERN_FORMAT, DEFAULT_DELIMITER));
        StringBuilder sb = new StringBuilder();
        for (String word: words) {
            if (!mPattern.matcher(word).matches()) {
                sb.append("<a href=\"").append(word).append("\">");
                sb.append(word);
                sb.append("</a>");
            } else {
                sb.append(word);
            }
        }
        return sb.toString();
    }

    private void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new MySpan(span.getURL());
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    private void setTextViewHTML(TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for(URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }

        text.setText(strBuilder);
        text.setLinksClickable(true);
        text.setMovementMethod(new LinkTouchMovementMethod());
    }


    private class MySpan extends TouchableSpan {

        String word;
        volatile boolean hovered = false;


        private MySpan(String word) {
            this.word = word;
        }

        @Override
        public void onClick(View widget) {
            if (callback != null) {
                callback.onWordClicked(widget, word);
            }
        }

        @Override
        public boolean onTouch(View widget, MotionEvent m) {
            int action = m.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                hovered = true;
            }
            if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_CANCEL) {
                hovered = false;
            }
            if (callback != null) {
                callback.onWordTouchEvent(widget, word, m);
            }
            return true;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
            ds.setColor(Color.BLACK);
        }
    }

    public interface Callback {

        void onWordClicked(View widget, String word);

        void onWordTouchEvent(View widget, String word, MotionEvent event);

    }

}
