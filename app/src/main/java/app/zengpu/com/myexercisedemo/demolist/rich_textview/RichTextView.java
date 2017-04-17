package app.zengpu.com.myexercisedemo.demolist.rich_textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import app.zengpu.com.myexercisedemo.R;

/**
 * Created by tao on 2017/4/14.
 */

public class RichTextView extends TextView {

    private Context mContext;
    private int prefix_count; // 前缀字数
    private float prefix_text_padding;// 前缀文字左右padding
    private float prefix_padding;// 前缀和后面的文字之间的padding
    private int prefix_textSize; // 前缀文字大小
    private int prefix_text_color; // 前缀文字颜色
    private float prefix_bg_radius; // 前缀背景圆角
    private Drawable prefix_bg; // 前缀背景

    private OnPrefixClickListener mOnPrefixListener;
    private View.OnClickListener mOnClickListener;

    public RichTextView(Context context) {
        this(context, null);
    }

    public RichTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.rich_textview);
        prefix_count = mTypedArray.getInteger(R.styleable.rich_textview_prefix_count, 0);
        prefix_bg_radius = mTypedArray.getDimension(R.styleable.rich_textview_prefix_bg_radius, 0);
        prefix_text_padding = mTypedArray.getDimension(R.styleable.rich_textview_prefix_text_padding, 0);
        prefix_padding = mTypedArray.getDimension(R.styleable.rich_textview_prefix_padding, 0);
        prefix_textSize = mTypedArray.getDimensionPixelSize(R.styleable.rich_textview_prefix_textSize, (int) getTextSize());
        prefix_text_color = mTypedArray.getColor(R.styleable.rich_textview_prefix_text_color, getCurrentTextColor());
        prefix_bg = mTypedArray.getDrawable(R.styleable.rich_textview_prefix_bg);
        mTypedArray.recycle();

    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        mOnClickListener = l;
    }

    public void setOnPrefixClickListener(OnPrefixClickListener listener) {
        this.mOnPrefixListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                // 触发点击事件
                if (event.getEventTime() - event.getDownTime() < 300) {
                    float x = event.getX();
                    float y = event.getY();
                    float prefix_w = getPaddingLeft() + prefix_count * prefix_textSize + prefix_text_padding;
                    float prefix_h = getPaddingTop() + Math.max(prefix_textSize, getTextSize());
                    if (x <= prefix_w && y <= prefix_h) {
                        // 前缀区域
                        if (prefix_count == 0 && null != mOnClickListener)
                            mOnClickListener.onClick(this);
                        else if (null != mOnPrefixListener)
                            mOnPrefixListener.onPrefixClick(this, (String) getText().subSequence(0, prefix_count));

                    } else if (null != mOnClickListener) {
                        mOnClickListener.onClick(this);
                    }
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        String text = getText().toString();
        if (TextUtils.isEmpty(text) || prefix_count == 0
                || (TextUtils.isEmpty(text) && text.length() < prefix_count)) {
            super.onDraw(canvas);
        } else
            drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        String text = getText().toString();
        // 行高
        float text_size = getTextSize();
        // 前面四个字画笔
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(prefix_text_color);
        paint.setTextSize(prefix_textSize);
        // 第一行行高
        float first_line_height = Math.max(prefix_textSize, getTextSize());
        // 行XY偏移
        float line_offset_x = getPaddingLeft() + prefix_text_padding;
        float line_offset_y = getPaddingTop() + first_line_height;
        // 前四个字Y偏移
        float prefix_offset_y = getPaddingTop() + first_line_height -
                (getTextSize() - prefix_textSize > 0 ? getOffsetY(getPaint()) : 0);
        // 前面四个字 先画背景，再画文字
        drawPrefixBg(canvas);
        canvas.drawText(text.substring(0, prefix_count),
                line_offset_x, prefix_offset_y, paint);
        // 更新偏移
        line_offset_x = line_offset_x
                + prefix_count * prefix_textSize
                + prefix_text_padding
                + prefix_padding;
        // 剩余文字
        int width = getMeasuredWidth();
        for (int i = prefix_count; i < text.length(); i++) {
            if (line_offset_x > width - getPaddingRight() - getTextSize()) {
                line_offset_x = getPaddingLeft();
                line_offset_y = line_offset_y + getLineSpacingExtra() + text_size;
            }
            canvas.drawText(String.valueOf(text.charAt(i)), line_offset_x, line_offset_y, getPaint());
            // 更新偏移
            line_offset_x += getTextSize();
        }
    }

    // prifix背景
    private void drawPrefixBg(Canvas canvas) {

        if (null != prefix_bg) {
            float ascent = getPaint().getFontMetrics().ascent - getPaint().getFontMetrics().top;
            float prefix_height = Math.max(prefix_textSize, getTextSize());
            float prefix_width = prefix_textSize * prefix_count + prefix_text_padding * 2;
            float left = getPaddingLeft();
            float top = getPaddingTop() + ascent;
            float right = getPaddingLeft() + prefix_width;
            float bottom = getPaddingTop() + prefix_height + ascent;

            if (prefix_bg instanceof ColorDrawable) {
                // 纯色背景
                ColorDrawable bgDrawable = (ColorDrawable) prefix_bg;
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(bgDrawable.getColor());
                RectF rectF = new RectF(left, top, right, bottom);
                canvas.drawRoundRect(rectF, prefix_bg_radius, prefix_bg_radius, paint);
            } else if (prefix_bg instanceof BitmapDrawable) {
                // 图片背景
                BitmapDrawable bgDrawable = (BitmapDrawable) prefix_bg;
                Bitmap bg = scaleBitmap(bgDrawable.getBitmap(), (int) prefix_width, (int) prefix_height);
                // 先生成圆角图片
                Paint prefix_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                prefix_paint.setColor(Color.WHITE);
                RectF prefix_rect = new RectF(0, 0, prefix_width, prefix_height);
                Bitmap target = Bitmap.createBitmap((int) prefix_width, (int) prefix_height, Bitmap.Config.ARGB_8888);
                Canvas prefix_canvas = new Canvas(target);
                prefix_canvas.drawRoundRect(prefix_rect, prefix_bg_radius, prefix_bg_radius, prefix_paint);
                prefix_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                prefix_canvas.drawBitmap(bg, 0, 0, prefix_paint);
                // 绘制生成的圆角图片
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                canvas.drawBitmap(target, null, new RectF(left, top, right, bottom), paint);
            }
        }
    }

    private float getOffsetY(Paint p) {
        Paint.FontMetrics fm = p.getFontMetrics();
        return (getTextSize() - (fm.ascent - fm.top) / 2 - prefix_textSize) / 2;
    }

    private Bitmap scaleBitmap(Bitmap in, float target_w, float target_h) {
        float width = in.getWidth();
        float height = in.getHeight();
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = target_w / width;
        float scaleHeight = target_h / height;
        // 缩放图片动作 矩阵右乘
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(in, 0, 0, (int) width, (int) height, matrix, true);
    }

    public interface OnPrefixClickListener {
        void onPrefixClick(View v, String text);
    }
}
