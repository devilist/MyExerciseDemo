package app.zengpu.com.myexercisedemo.demolist.rich_textview;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by zengp on 2017/7/4.
 */

public class RichTextView extends TextView {

    private int mHyperTextColor = 0;
    private OnHyperTextClickListener listener;

    public RichTextView(Context context) {
        super(context);
    }

    public RichTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RichTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnHyperTextClickListener(OnHyperTextClickListener listener) {
        this.listener = listener;
        parseSpannableText(getText(), mHyperTextColor);
    }

    public void setHyperTextColor(@ColorRes int color) {
        mHyperTextColor = getResources().getColor(color);
        parseSpannableText(getText(), mHyperTextColor);
    }


    private void parseSpannableText(CharSequence text, final int hyperTextColor) {
        if (TextUtils.isEmpty(text))
            return;

        setText(text);

        if (!(text instanceof Spanned))
            text = Html.fromHtml(text.toString());

        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        URLSpan[] urls = builder.getSpans(0, text.length(), URLSpan.class);

        if (null == urls || (null != urls && urls.length == 0))
            return;

        for (final URLSpan span : urls) {
            final int start = builder.getSpanStart(span);
            final int end = builder.getSpanEnd(span);
            final String link = span.getURL();
            final String des = text.subSequence(start, end).toString();

            builder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    widget.postInvalidate();
                    if (null != listener) {
                        listener.HyperTextClick(widget, link, des);
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    if (hyperTextColor != 0)
                        ds.setColor(hyperTextColor);
                    ds.setUnderlineText(false);
                }
            }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.removeSpan(span);
        }

        setLinksClickable(true);
        setMovementMethod(LinkMovementMethod.getInstance());
        setText(builder);
    }

    public interface OnHyperTextClickListener {
        void HyperTextClick(View v, String url, String description);
    }

}
