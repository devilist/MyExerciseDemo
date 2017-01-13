package app.zengpu.com.myexercisedemo.demolist.svg_path_anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import app.zengpu.com.myexercisedemo.Utils.LogUtil;

/**
 * Created by zengpu on 2017/1/13.
 */

public class SvgPathView extends View {

    protected int mPaddingLeft, mPaddingTop;

    private Paint mStrokePaint;
    private Paint mFillPaint;
    private int mStrokeColor = 0xff000000;
    private float mStrokeWidth = 1.0f;
    private int mFillColor = 0xffffffff;

    private long mStrokeAnimDuration = 20000; // 路径动画时间
    private long mFillAnimDuration = 2000; // 填充动画时间

    private boolean isNeedFill = false; // 是否需要填充

    private SvgPath mSvgPath; // 路径封装类
    private String mSvgPathString;
    private Path mStrokeAnimPath; // 动画路径
    private AnimatorSet mStrokeAnimSet; // 路径动画序列

    private ValueAnimator mStrokeAnimator;

    public SvgPathView(Context context) {
        this(context, null);
    }

    public SvgPathView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SvgPathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initPaint();
        mStrokeAnimPath = new Path();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        // strokePaint
        mStrokePaint = new Paint();
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStrokeWidth(mStrokeWidth);
        mStrokePaint.setColor(mStrokeColor);
        // fillPaint
        mFillPaint = new Paint();
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setAntiAlias(true);
        mFillPaint.setColor(mFillColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        if (null != mSvgPath && null != mSvgPath.getPath()) {
//            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//            setMeasuredDimension(widthSize, heightSize);
//            return;
//        }
//        int desiredWidth = 0, desiredHeight = 0;
//        final float strokeWidth = mStrokePaint.getStrokeWidth() / 2;
//        desiredWidth += getPaddingLeft() + mSvgPath.getPathBounds().left + mSvgPath.getPathBounds().width() + strokeWidth;
//        desiredHeight += getPaddingTop() + mSvgPath.getPathBounds().top + mSvgPath.getPathBounds().height() + strokeWidth;
//
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(widthMeasureSpec);
//
//        int measuredWidth = widthMode == MeasureSpec.AT_MOST ? desiredWidth : widthSize;
//        int measuredHeight = heightMode == MeasureSpec.AT_MOST ? desiredHeight : heightSize;
//        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        LogUtil.d("SvgPathView", "w is " + w);
        parsePath(w, h);

        startAnim();
    }

    private void parsePath(float measuredWidth, float measuredHeight) {
        SvgPathParser oriPathParser = new SvgPathParser();
        Path oriPath;
        try {
            oriPath = oriPathParser.parsePath(mSvgPathString);
        } catch (ParseException e) {
            oriPath = new Path();
        }
        SvgPath oriSvgPath = new SvgPath(oriPath);
        float oriWidth = oriSvgPath.getPathBounds().width();
        float oriHeight = oriSvgPath.getPathBounds().height();

        if (oriWidth == 0) {
            oriWidth = measuredWidth;
        }
        if (oriHeight == 0) {
            oriHeight = measuredHeight;
        }

        SvgPathParser desirePathParser = new SvgPathParser(measuredWidth / oriWidth, measuredHeight / oriHeight);
        Path desirePath;
        try {
            desirePath = desirePathParser.parsePath(mSvgPathString);
        } catch (ParseException e) {
            desirePath = new Path();
        }
        mSvgPath = new SvgPath(desirePath);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(mPaddingLeft, mPaddingTop);
        canvas.drawPath(mStrokeAnimPath, mStrokePaint);
    }

    /**
     *
     */
    public void startAnim() {
        startStrokeAnim();
    }

    /**
     * 开始路径动画
     * <p>当一个路径文件里包含有多条路径时，需要为每条路径单独设置动画，然后依次播放。
     * PathMeasure.getLength()获得的是路径中某条子路径的长度；
     * PathMeasure.getSegment()获得的是路径中某条子路径的片段；
     * 利用PathMeasure.nextContour()方法可以一次获得每条路径，从而为每条路径设置动画。
     */
    private void startStrokeAnim() {
        // 获取路径的PathMeasure
        final PathMeasure strokePathMeasure = mSvgPath.getPathMeasure();
        // 子路径持续时间
        long subPathDuration = mStrokeAnimDuration / mSvgPath.getCount();
        mStrokeAnimator = ValueAnimator.ofFloat(0, 1f);
        mStrokeAnimator.setDuration(subPathDuration);
        mStrokeAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mStrokeAnimator.setInterpolator(new LinearInterpolator());

        mStrokeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
//                LogUtil.d("SvgPathView", "value is " + value);
                //更新动画路径
                strokePathMeasure.getSegment(0, strokePathMeasure.getLength() * value, mStrokeAnimPath, true);
                SvgPathView.this.invalidate();
            }
        });

        mStrokeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                strokePathMeasure.getSegment(0, strokePathMeasure.getLength(), mStrokeAnimPath, true);
                // 移动到下一条路径
                strokePathMeasure.nextContour();
                if (strokePathMeasure.getLength() == 0) {
                    mStrokeAnimator.end();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });
        // 初始化
        mStrokeAnimPath.reset();
        mStrokeAnimPath.lineTo(0, 0);

        mStrokeAnimator.start();
    }

    private void startStrokeAnimSet() {
        mStrokeAnimPath.reset();
        mStrokeAnimPath.rLineTo(0, 0);
        mStrokeAnimSet = new AnimatorSet();
        // 获取路径的PathMeasure
        final PathMeasure strokePathMeasure = mSvgPath.getPathMeasure();
        List<Animator> animators = new ArrayList<>();
        // 子路径动画持续时间
        long subPathDuration = mStrokeAnimDuration / mSvgPath.getCount();
        // 遍历每条路径
        while (strokePathMeasure.getLength() != 0) {
            ValueAnimator subStrokeAnimator = ValueAnimator.ofFloat(0, 1f);
            subStrokeAnimator.setDuration(subPathDuration);
            subStrokeAnimator.setInterpolator(new LinearInterpolator());
            subStrokeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    //更新动画路径
                    strokePathMeasure.getSegment(0, strokePathMeasure.getLength() * value, mStrokeAnimPath, true);
                    SvgPathView.this.invalidate();
                }
            });
            subStrokeAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    //当前子路径动画完成后，将PathMeasure移动到下一条子路径
//                    SvgPathView.this.postInvalidate();
                    strokePathMeasure.nextContour();
                }
            });
            animators.add(subStrokeAnimator);
            strokePathMeasure.nextContour();
        }
        strokePathMeasure.setPath(mSvgPath.getPath(), false);
        mStrokeAnimSet.playSequentially(animators);
        mStrokeAnimSet.start();
    }


    public SvgPathView setSvgPath(SvgPath svgPath) {
        this.mSvgPath = svgPath;
        return this;
    }

    public SvgPathView setSvgPathString(String svgPathString) {
        this.mSvgPathString = svgPathString;
        return this;
    }

    public SvgPathView setNeedFill(boolean needFill) {
        isNeedFill = needFill;
        return this;
    }

    public SvgPathView setFillAnimDuration(long fillAnimDuration) {
        this.mFillAnimDuration = fillAnimDuration;
        return this;
    }

    public SvgPathView setFillColor(int fillColor) {
        this.mFillColor = fillColor;
        return this;
    }

    public SvgPathView setStrokeAnimDuration(long strokeAnimDuration) {
        this.mStrokeAnimDuration = strokeAnimDuration;
        return this;
    }

    public SvgPathView setStrokeColor(int strokeColor) {
        this.mStrokeColor = strokeColor;
        return this;
    }

    public SvgPathView setStrokeWidth(int strokeWidth) {
        this.mStrokeWidth = strokeWidth;
        return this;
    }

}
