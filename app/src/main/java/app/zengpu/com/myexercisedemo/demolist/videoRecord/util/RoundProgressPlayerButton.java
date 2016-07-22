package app.zengpu.com.myexercisedemo.demolist.videoRecord.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import app.zengpu.com.myexercisedemo.R;

/**
 * 仿iphone带进度的进度条，线程安全的View，可直接在线程中更新进度
 *
 * @author xiaanming
 */
public class RoundProgressPlayerButton extends View {
    /**
     * 画笔对象的引用
     */
    private Paint paint;

    /**
     * 圆环的颜色
     */
    private int roundButtonColor;

    /**
     * 圆环进度的颜色
     */
    private int roundProgressColor;

    /**
     * 三角形颜色
     */
    private int triangleColor;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;

    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;

    /**
     * 圆环的宽度
     */
    private float roundButtonWidth;

    /**
     * 进度条的宽度，空心的时候起效
     */
    private float roundProgressWidth;

    /**
     * 播放按钮 三角形的线宽度
     */
    private float triangleWidth;

    /**
     * 圆环 跟 进度条 之间的 间隙
     */
    private float gap;

    /**
     * 最大进度
     */
    private int max;

    /**
     * 当前进度
     */
    private int progress;
    /**
     * 是否显示中间的进度
     */
    private boolean textIsDisplayable;

    /**
     * 进度的风格，实心或者空心
     */
    private int style;

    public static final int STROKE = 0;
    public static final int FILL = 1;

    // guodx自创
//    private float dotX1, dotY1, dotX2, dotY2, dotX3, dotY3, dotX4, dotY4;
    private float dotX2, dotY2, dotX3, dotY3, dotX4, dotY4;
    private int triangleStart = -1;     // 画三角辅助参数
    private boolean isLine1Ok = false;  // 3条线是否画好
    private boolean isLine2Ok = false;
    private boolean isLine3Ok = false;
    private float speed1 = 12;          // 速度
    private float speed2 = 16;
    private float speed3 = 30;

    public RoundProgressPlayerButton(Context context) {
        this(context, null);
    }

    public RoundProgressPlayerButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressPlayerButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        paint = new Paint();


        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressPlayerButton);

        //获取自定义属性和默认值
        roundButtonColor = mTypedArray.getColor(R.styleable.RoundProgressPlayerButton_roundButtonColor, Color.RED);
        roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressPlayerButton_roundProgressColor, Color.GREEN);
        triangleColor = mTypedArray.getColor(R.styleable.RoundProgressPlayerButton_triangleColor, Color.BLACK);
        textColor = mTypedArray.getColor(R.styleable.RoundProgressPlayerButton_textColor, Color.GREEN);
        textSize = mTypedArray.getDimension(R.styleable.RoundProgressPlayerButton_textSize, 15);
        roundButtonWidth = mTypedArray.getDimension(R.styleable.RoundProgressPlayerButton_roundButtonWidth, 5);
        roundProgressWidth = mTypedArray.getDimension(R.styleable.RoundProgressPlayerButton_roundProgressWidth, roundButtonWidth);
        triangleWidth = mTypedArray.getDimension(R.styleable.RoundProgressPlayerButton_triangleWidth, roundProgressWidth);
        max = mTypedArray.getInteger(R.styleable.RoundProgressPlayerButton_max, 100);
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressPlayerButton_textIsDisplayable, true);
        style = mTypedArray.getInt(R.styleable.RoundProgressPlayerButton_style, 0);
        gap = mTypedArray.getDimension(R.styleable.RoundProgressPlayerButton_gap, 0);

        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 1.画点击效果
        drawClick(canvas, paint);

        // 2.画最外层的大圆环
        int center = getWidth() / 2; //获取圆心的x坐标
        int radius = (int) (center - roundButtonWidth / 2); //圆环的半径
        paint.setColor(roundButtonColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(roundButtonWidth); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(center, center, radius, paint); //画出圆环

        if (status == STATUS_PROGRESS_LOADING) {
            // 3.画进度百分比
            if (textIsDisplayable && style == STROKE) {
                // 需要显示进度百分比
                int percent = (int) (((float) progress / (float) max) * 100);  //中间的进度百分比，先转换成float在进行除法运算，不然都为0
                if (percent != 0) {
                    paint.setStrokeWidth(0);
                    paint.setColor(textColor);
                    paint.setTextSize(textSize);
                    paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
                    float textWidth = paint.measureText(percent + "%");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
                    canvas.drawText(percent + "%", center - textWidth / 2, center + textSize / 2, paint); //画出进度百分比
                }
            }
        }
        if (status == STATUS_PROGRESS_LOADING || status == STATUS_PROGRESS_FINISH) {
            // 4.画圆弧 ，画圆环的进度
            //设置进度是实心还是空心
            paint.setStrokeWidth(roundProgressWidth); //设置圆环的宽度
            paint.setColor(roundProgressColor);  //设置进度的颜色
            RectF oval = new RectF(center - radius + gap, center - radius + gap, center
                    + radius - gap, center + radius - gap);  //用于定义的圆弧的形状和大小的界限

            switch (style) {
                case STROKE: {
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawArc(oval, 0, 360 * progress / max, false, paint);  //根据进度画圆弧
                    break;
                }
                case FILL: {
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    if (progress != 0)
                        canvas.drawArc(oval, 0, 360 * progress / max, true, paint);  //根据进度画圆弧
                    break;
                }
            }
        }

        // 5.画三角
        if (status == STATUS_BUTTON_DRAW || status == STATUS_BUTTON_SHOW || status == STATUS_PROGRESS_FINISH) {
            // 绘制按钮
            paint.setStrokeWidth(triangleWidth);    //设置三角形线的宽度
            paint.setColor(triangleColor);          //设置进度的颜色
            drawTriangle(canvas, paint);
        }


    }

    /**
     * 绘制三角形，播放按钮
     *
     * @param canvas
     * @param paint
     */
    private void drawTriangle(Canvas canvas, Paint paint) {
        // 1.初始化4个点
        if (dotX2 == 0) {
//            dotX1 = (float) getWidth() * 5 / 12;
//            dotY1 = (float) getWidth() * 9 / 24;

            dotX2 = (float) getWidth() * 3 / 4;
            dotY2 = (float) getWidth() / 2;

            dotX3 = dotX4 = (float) getWidth() / 3;
            dotY3 = (float) getWidth() * 3 / 4;
            dotY4 = (float) getWidth() / 4;
        }
        Path path = new Path();
        path.moveTo(dotX4, dotY4);

        // 直线1
        if (!isLine1Ok) {
            // 线1 没画完
            float x = dotX4 + speed1 * (++triangleStart);
            if (x > dotX2) {
                x = dotX2;
                isLine1Ok = true;   // 画完了
                triangleStart = 0;
            }
            float y = returnNextDot1(dotX4, dotY4, dotX2, dotY2, x);
            path.lineTo(x, y);
        } else {
            // 线1 画完了
            path.lineTo(dotX2, dotY2);

            if (!isLine2Ok) {
                // 线2 没画完
                float x = dotX2 - speed2 * (++triangleStart);
                if (x < dotX3) {
                    x = dotX3;
                    isLine2Ok = true;   // 画完了
                    triangleStart = 0;
                }
                float y = returnNextDot2(dotX2, dotY2, dotX3, dotY3, x);
                path.lineTo(x, y);
            } else {
                // 线2 画完了
                path.lineTo(dotX3, dotY3);

                if (!isLine3Ok) {
                    // 线3 没画完
                    float y = dotY3 - speed3 * (++triangleStart);
                    if (y < dotY4) {
                        y = dotY4;
                        isLine3Ok = true;   // 画完了
                        status = STATUS_BUTTON_SHOW;
                    }
                    path.lineTo(dotX3, y);
                } else {
                    // 线3 画完了
                    path.lineTo(dotX4, dotY4);
                    path.lineTo(dotX2, dotY2);
                }
            }
        }
        canvas.drawPath(path, paint);
        if (status == STATUS_BUTTON_DRAW) {
            postInvalidateDelayed(100);
        }
    }

    // 线1计算
    private float returnNextDot1(float x1, float y1, float x2, float y2, float x) {
        return (y2 - y1) * (x - x1) / (x2 - x1) + y1;
    }

    // 线2计算
    private float returnNextDot2(float x2, float y2, float x3, float y3, float x) {
        return y3 - (y3 - y2) * (x - x3) / (x2 - x3);
    }

    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }

        // 处理状态值
        if (progress >= max) {
            // 加载完成
            if (status == STATUS_PROGRESS_LOADING || status == STATUS_BUTTON_SHOW || status == STATUS_BUTTON_DRAW) {
                status = STATUS_PROGRESS_FINISH;
                if (null != listener) {
                    listener.onLoadFinish();
                }
            }

        } else if (progress <= 0) {
            // 显示按钮
            status = STATUS_BUTTON_SHOW;
        } else {
            // 加载中
            status = STATUS_PROGRESS_LOADING;
        }

    }


    public int getCricleColor() {
        return roundButtonColor;
    }

    public void setCricleColor(int cricleColor) {
        this.roundButtonColor = cricleColor;
    }

    public int getCricleProgressColor() {
        return roundProgressColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.roundProgressColor = cricleProgressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRoundButtonWidth() {
        return roundButtonWidth;
    }

    public void setRoundButtonWidth(float roundButtonWidth) {
        this.roundButtonWidth = roundButtonWidth;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            if (status != STATUS_PROGRESS_LOADING && status != STATUS_BUTTON_DRAW) {
                // 没有开始进度条 可点击
                addDrop(new Drop(getWidth() / 2, getWidth() / 2));
                handler.post(run);
                if (null != listener) {
                    // 点击震动
                    this.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public interface OnRoundProgressListener {

        /**
         * 加载完成
         */
        void onLoadFinish();

        /**
         * 没加载之前的点击事件，处理加载
         *
         * @param v
         */
        void onUnLoadClick(View v);

        /**
         * 加载完成后的点击，处理显示结果
         *
         * @param v
         */
        void onLoadedClick(View v);

        /**
         * 非加载中的点击处理
         *
         * @param v
         */
        void onNormalClick(View v);
    }

    private OnRoundProgressListener listener = null;
    // 是否第一次显示完成，放置onresume
    private int status = STATUS_BUTTON_DRAW;
    public static final int STATUS_BUTTON_DRAW = 0;         // 按钮绘制状态
    public static final int STATUS_BUTTON_SHOW = 1;         // 按钮绘制完成状态
    public static final int STATUS_PROGRESS_LOADING = 2;    // 进度条加载中状态
    public static final int STATUS_PROGRESS_FINISH = 3;     // 进度条加载完成状态


    public void setOnRoundProgressListener(final OnRoundProgressListener listener) {
        this.listener = listener;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener && status != STATUS_BUTTON_DRAW && status != STATUS_PROGRESS_LOADING) {
                    listener.onNormalClick(v);
                    if (status != STATUS_PROGRESS_FINISH) {
                        // 未加载点击
                        listener.onUnLoadClick(v);
                    } else {
                        // 加载完成点击
                        listener.onLoadedClick(v);
                    }
                }
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    List<Drop> dropList = new ArrayList<Drop>();

    private void drawClick(Canvas canvas, Paint paint) {
        Iterator<Drop> it = dropList.iterator();
        while (it.hasNext()) {
            Drop drop = it.next();
            drop.drawSelf(canvas, paint);
            if (drop.r >= drop.r_max) {
                it.remove();
            }
        }
    }

    private void addDrop(Drop drop) {
        this.dropList.add(drop);
    }

    class Drop {

        public float x = 100, y = 100;
        public float r = 10;
        public int alpha = 255;
        public float r_max;

        public Drop() {
        }

        public Drop(float x, float y) {
            this.x = x;
            this.y = y;
            Random r = new Random();
            r_max = x * 2 / 3 + r.nextInt((int) x / 2);
        }

        public void drawSelf(Canvas canvas, Paint paint) {
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(221, 221, 221));
            paint.setAlpha(alpha);
            canvas.drawCircle(x, y, r, paint);
            r += 10;
            r = r >= r_max ? r_max : r;
            alpha -= 10;
            alpha = alpha < 0 ? 0 : alpha;
        }
    }

    /**
     * 绘制点击效果
     */
    private Handler handler = new Handler();
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            postInvalidate();
            if (dropList.size() > 0) {
                handler.postDelayed(run, 50);
            } else {
                if (visibility != Integer.MAX_VALUE) {
                    RoundProgressPlayerButton.super.setVisibility(visibility == View.GONE ? View.GONE : View.INVISIBLE);
                    visibility = Integer.MAX_VALUE;
                }
            }
        }
    };

    /**
     * 准备工作
     */
    public void prepare(boolean isShowAnim) {
//        status = STATUS_BUTTON_SHOW;
        progress = 0;
        if (isShowAnim) {
            status = STATUS_BUTTON_DRAW;
            isLine1Ok = false;
            isLine2Ok = false;
            isLine3Ok = false;
        } else {
            status = STATUS_BUTTON_SHOW;
            isLine1Ok = true;
            isLine2Ok = true;
            isLine3Ok = true;
        }
    }

    /**
     * 是否显示完成了
     *
     * @return
     */
    public boolean isLoaded() {
        return status == STATUS_PROGRESS_FINISH;
    }

    private int visibility = Integer.MAX_VALUE;

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            if (getVisibility() != View.VISIBLE) {
                this.dropList.clear();
            }
            super.setVisibility(visibility);
        } else {
            // 等动画效果结束在显示结果
            if (dropList.size() > 0) {
                this.visibility = visibility;
            } else {
                super.setVisibility(visibility);
            }
        }
    }


}
