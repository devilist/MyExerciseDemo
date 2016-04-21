package app.zengpu.com.myexercisedemo.multi_drawer;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zengpu on 16/4/2.
 */
public class MultiDrawerLayout extends ViewGroup {

    /**
     * 判定抽屉open或 close 之前的最小速度  dp
     */
    private static final  int MIN_FLING_VELOCITY = 100;

    /**
     * ChildAt(0)
     */
    private View mUpView;

    /**
     * ChildAt(1)  上部抽屉
     */
    private View mUpDrawer;

    /**
     * ChildAt(2)
     */
    private View mDownView;

    /**
     * ChildAt(3) 下部抽屉
     */
    private View mDownDrawer;

    /**
     * ChildAt(4) 双向抽屉
     */
    private View mUpDownDrawer;

    /**
     * upDownDrawer 距离父容器上边或下边的最小外边距,单位 px
     */
    private int mMinUpDownDrawerMargin;

    /**
     * upDrawer显示出来的占自身的百分比
     */
    private float mUpDrawerOnscreen;
    /**
     * downDrawer显示出来的占自身的百分比
     */
    private float mDownDrawerOnscreen;
    /**
     * upDownDrawer显示出来的占自身的百分比
     */
    private float mUpDownDrawerOnscreen;

    /**
     * upDownDrawer偏移因数
     */
    private static final float OFFSET_FACTOR = 0.45f;

    /**
     * * upDrawer 和 downDrawer 偏移补偿; 防止关闭时被upDownDrawer 挡住; dx
     */
    private int upDrawerAndDownDrawerOffsetMargin;

    /**
     * upDrawer 和 downDrawer 偏移补偿因数; 防止关闭时被upDownDrawer 挡住; dp
     */
    private static final float OFFSET_COMPENSATION_FACTOR = 10;


    /**
     * upDownDrawer.getTop() < 0 ? true : false
     */
    private boolean isUpDownDrawerTopBelowZero = true;

    /**
     * 屏幕高度
     */
    private int screenHeight;

    private ViewDragHelper mViewDragHelper;


    public MultiDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 初始化,所有抽屉都打开
        mUpDownDrawerOnscreen = 1.0f;
        mUpDrawerOnscreen = 1.0f;
        mDownDrawerOnscreen = 0.55f;

        DisplayMetrics dm =getResources().getDisplayMetrics();
        // 获得屏幕密度
        final float density = dm.density;
        // px = dp * density;
        final float minVelocity = MIN_FLING_VELOCITY * density;
        upDrawerAndDownDrawerOffsetMargin = (int) (OFFSET_COMPENSATION_FACTOR * density);
        //屏幕高度   px
        screenHeight = dm.heightPixels;
//        screenHeight = getHeight();

//        Context.getWindow().findViewById(Window.ID_ANDROID_CONTENT)

        mMinUpDownDrawerMargin = (int) (screenHeight * OFFSET_FACTOR);

        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {

                int newUp = top;

                if (child == mUpDownDrawer) {
                    newUp =  Math.max(-mMinUpDownDrawerMargin-2 * upDrawerAndDownDrawerOffsetMargin,
                            Math.min(top, mMinUpDownDrawerMargin + 2 * upDrawerAndDownDrawerOffsetMargin));
                }
                if (child == mUpDrawer) {
                    newUp =  Math.max(0, Math.min(top, mMinUpDownDrawerMargin -
                            upDrawerAndDownDrawerOffsetMargin));
                }
                if (child == mDownDrawer) {
                    newUp =  Math.max(screenHeight - 2* mMinUpDownDrawerMargin +
                                    upDrawerAndDownDrawerOffsetMargin,
                            Math.min(top, screenHeight - mMinUpDownDrawerMargin));
                }

                return newUp;
            }

            @Override
            public boolean tryCaptureView(View child, int pointerId) {

                boolean flag = isUpDrawerOpened();
                Log.i("TAG", "isUpDrawerOpened is : " + flag);
                flag = isUpDownDrawerToDownClosed();
                Log.i("TAG","isUpDownDrawerToDownClosed is : " +  flag);

                if (isUpDownDrawerOpened()) {
                    return child == mUpDownDrawer;
                } else {

                    if (isUpDownDrawerToDownClosed()) {
                        if (isUpDrawerOpened()) {
                            return child == mUpDrawer || child ==  mUpDownDrawer;
                        } else {
                            return child == mUpDrawer;
                        }
                    } else {
                        if (isDownDrawerOpened()) {
                            return child == mUpDownDrawer || child == mDownDrawer;
                        } else {
                            return child == mDownDrawer;
                        }
                    }
                }
            }

            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                mViewDragHelper.captureChildView(mUpDownDrawer, pointerId);
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                final int childHeight = releasedChild.getHeight();
                int top = releasedChild.getTop();
                //抽屉开关的阈值
                float threshold;
                // 抽屉的偏移量
                float offset;
                if (releasedChild == mUpDownDrawer) {
                    threshold = 1 - OFFSET_FACTOR/2;
                    offset = (childHeight - Math.abs(top)) * 1.0f / childHeight;
                    if (top < 0) {
                        mViewDragHelper.settleCapturedViewAt(releasedChild.getLeft(),
                                yvel > minVelocity || yvel == 0 && offset > threshold ?
                                        0 : -mMinUpDownDrawerMargin);
                    } else {
                        mViewDragHelper.settleCapturedViewAt(releasedChild.getLeft(),
                                yvel > minVelocity || yvel == 0 && offset < threshold ?
                                        mMinUpDownDrawerMargin : 0);
                    }
//                    Log.i("TAG", "yvel is :" + yvel);
//                    Log.i("TAG", "offset is :" + offset);
//                    Log.i("TAG", "threshold is :" + threshold);
//                    Log.i("TAG", "top is :" + top);
                }

                if (releasedChild == mUpDrawer) {
                    threshold = 0.5f;
                    offset = (childHeight - top) * 1.0f / childHeight;
                    mViewDragHelper.settleCapturedViewAt(releasedChild.getLeft(),
                            yvel > 0 || yvel == 0 && offset < threshold ?
                                    childHeight - upDrawerAndDownDrawerOffsetMargin : 0);
                }

                if (releasedChild == mDownDrawer) {
                    offset = (screenHeight - top) * 1.0f/ screenHeight;
                    threshold = (1.5f * mMinUpDownDrawerMargin +
                            upDrawerAndDownDrawerOffsetMargin)/screenHeight;
                    mViewDragHelper.settleCapturedViewAt(releasedChild.getLeft(),
                            yvel > 0 || yvel == 0 && offset < threshold ?
                                    screenHeight - mMinUpDownDrawerMargin :
                                    screenHeight - 2* mMinUpDownDrawerMargin +
                                            upDrawerAndDownDrawerOffsetMargin);
                }
                invalidate();
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                final int childHeight = changedView.getHeight();
                float offset;
                if (changedView == mUpDownDrawer) {
                    offset = (float) (childHeight - Math.abs(top))/childHeight;
                    mUpDownDrawerOnscreen = offset;
                    isUpDownDrawerTopBelowZero = ( top <= 0 );
                }

                if (changedView == mUpDrawer) {
                    offset = (childHeight - top) * 1.0f / childHeight;
                    mUpDrawerOnscreen = offset;
//                    mDownDrawer.setVisibility(mDownDrawerOnscreen >0.7f ? INVISIBLE : VISIBLE);
                    isUpDownDrawerTopBelowZero = false;
                }

                if (changedView == mDownView) {
                    offset = (screenHeight - top) * 1.0f/ screenHeight;
                    mDownDrawerOnscreen = offset;
//                    mUpDrawer.setVisibility(mUpDrawerOnscreen < 0.5f ? INVISIBLE : VISIBLE );
                    isUpDownDrawerTopBelowZero = true;
                }

                invalidate();
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return mUpDownDrawer == child ? 2 * mMinUpDownDrawerMargin
                        : (mUpDrawer == child ||  mDownDrawer == child ?
                        mMinUpDownDrawerMargin -upDrawerAndDownDrawerOffsetMargin
                        : 0);
            }
        });

        //设置 edge_top track
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_TOP);
        //设置 minVelocity
        mViewDragHelper.setMinVelocity(minVelocity);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//        Log.i("TAG", "heightSize is :" + heightSize);

        setMeasuredDimension(widthSize, heightSize);

        //测量所有布局
        View upDownDrawer = getChildAt(4);
        MarginLayoutParams lp = (MarginLayoutParams) upDownDrawer.getLayoutParams();
        final int upDownDrawerWidthSpec = getChildMeasureSpec(widthMeasureSpec,
                lp.leftMargin + lp.rightMargin,
                lp.width);

        final int upDownDrawerHeightSpec = getChildMeasureSpec(heightMeasureSpec,
                lp.topMargin + lp.bottomMargin,
                lp.height);
        upDownDrawer.measure(upDownDrawerWidthSpec,upDownDrawerHeightSpec);

        //上抽屉和下抽屉的偏移量
        int offsetMargin  = (int) (screenHeight * (1-OFFSET_FACTOR));
        View downDrawer = getChildAt(3);
        lp = (MarginLayoutParams) downDrawer.getLayoutParams();
        final int downDrawerWidthSpec = getChildMeasureSpec(widthMeasureSpec,
                lp.leftMargin + lp.rightMargin ,
                lp.width);
        final int downDrawerHeightSpec = getChildMeasureSpec(heightMeasureSpec,
                lp.topMargin + lp.bottomMargin + offsetMargin ,
                lp.height);
        downDrawer.measure(downDrawerWidthSpec,downDrawerHeightSpec);

        View upDrawer = getChildAt(1);
        lp = (MarginLayoutParams) downDrawer.getLayoutParams();
        final int upDrawerWidthSpec = getChildMeasureSpec(widthMeasureSpec,
                lp.leftMargin + lp.rightMargin ,
                lp.width);
        final int upDrawerHeightSpec = getChildMeasureSpec(heightMeasureSpec,
                lp.topMargin + lp.bottomMargin + offsetMargin,
                lp.height);
        upDrawer.measure(upDrawerWidthSpec,upDrawerHeightSpec);

        View downView = getChildAt(2);
        //定义downView高度
        int downViewHeight = (int)(screenHeight * OFFSET_FACTOR);
        lp = (MarginLayoutParams) downView.getLayoutParams();
        final int downViewWidthSpec = MeasureSpec.makeMeasureSpec(
                widthSize - lp.leftMargin - lp.rightMargin, MeasureSpec.EXACTLY);
        final int downViewHeightSpec = MeasureSpec.makeMeasureSpec(
                downViewHeight - lp.topMargin - lp.bottomMargin, MeasureSpec.EXACTLY);
        downView.measure(downViewWidthSpec, downViewHeightSpec);

        View upView = getChildAt(0);
        int upViewHeight = (int)(screenHeight * OFFSET_FACTOR);
        lp = (MarginLayoutParams) upView.getLayoutParams();
        final int upViewWidthSpec = MeasureSpec.makeMeasureSpec(
                widthSize - lp.leftMargin - lp.rightMargin,MeasureSpec.EXACTLY);
        final int upViewHeightSpec = MeasureSpec.makeMeasureSpec(
                upViewHeight - lp.topMargin - lp.bottomMargin, MeasureSpec.EXACTLY);
        upView.measure(upViewWidthSpec, upViewHeightSpec);

        mUpDownDrawer = upDownDrawer;
        mDownDrawer = downDrawer;
        mUpDrawer = upDrawer;
        mUpView = upView;
        mDownView = downView;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        View upDownDrawer = mUpDownDrawer;
        View downDrawer = mDownDrawer;
        View upDrawer = mUpDrawer;
        View upView = mUpView;
        View downView = mDownView;

        int offsetMargin  = (int) (screenHeight * (1-OFFSET_FACTOR));

        //upView 布局
        MarginLayoutParams lp = (MarginLayoutParams) upView.getLayoutParams();
        upView.layout(lp.leftMargin , lp.topMargin,
                lp.leftMargin + upView.getMeasuredWidth(),
                lp.topMargin + upView.getMeasuredHeight());

        //downView 布局
        lp = (MarginLayoutParams) downView.getLayoutParams();
        downView.layout(lp.leftMargin , lp.topMargin + offsetMargin,
                lp.leftMargin + downView.getMeasuredWidth(),
                lp.topMargin + offsetMargin + downView.getMeasuredHeight());

        //downDrawer布局
        lp = (MarginLayoutParams) downDrawer.getLayoutParams();
        final int downDrawerHeight = downDrawer.getMeasuredHeight();
        int downDrawerTop = (int) (screenHeight * mDownDrawerOnscreen);
        downDrawer.layout(lp.leftMargin, downDrawerTop ,
                lp.leftMargin + downDrawer.getMeasuredWidth(),
                downDrawerTop + downDrawerHeight);

        //upDrawer布局
        lp = (MarginLayoutParams) upDrawer.getLayoutParams();
        final int upDrawerHeight = upDrawer.getMeasuredHeight();
        int upDrawerTop = upDrawerHeight - (int) (upDrawerHeight * mUpDrawerOnscreen);
        upDrawer.layout(lp.leftMargin,upDrawerTop,
                lp.leftMargin + upDrawer.getMeasuredWidth(),
                upDrawerTop + upDrawerHeight);

        //upDownDrawer布局
        lp = (MarginLayoutParams) upDownDrawer.getLayoutParams();
        final int upDownDrawerHeight = upDownDrawer.getMeasuredHeight();

        int upDownDrawerTop = isUpDownDrawerTopBelowZero ?
                -upDownDrawerHeight + (int) (upDownDrawerHeight * mUpDownDrawerOnscreen)
                :
                upDownDrawerHeight - (int) (upDownDrawerHeight * mUpDownDrawerOnscreen);

        upDownDrawer.layout(lp.leftMargin, upDownDrawerTop,
                lp.leftMargin + upDownDrawer.getMeasuredWidth(),
                upDownDrawerTop + upDownDrawerHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean shouldInterceptTouchEvent = mViewDragHelper.shouldInterceptTouchEvent(ev);
        return shouldInterceptTouchEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    /**
     * 打开双向抽屉
     */
    public void openUpDownDrawer () {
        View upDownDrawer = mUpDownDrawer;
        mUpDownDrawerOnscreen = 1.0f;
        mViewDragHelper.smoothSlideViewTo(upDownDrawer, upDownDrawer.getLeft(), 0);
    }

    /**
     * 判断双向抽屉是否打开
     * @return
     */
    public boolean isUpDownDrawerOpened() {
        return mUpDownDrawerOnscreen >= 1.0f;
    }

    /**
     * 向上关闭双向抽屉
     */
    public void closeUpDownDrawerToUp () {
        View upDownDrawer = mUpDownDrawer;
        isUpDownDrawerTopBelowZero = true;
        mUpDownDrawerOnscreen = OFFSET_FACTOR;
        mViewDragHelper.smoothSlideViewTo(upDownDrawer, upDownDrawer.getLeft(), -mMinUpDownDrawerMargin);
    }

    /**
     * 判断双向抽屉是否向上关闭
     * @return
     */
    public boolean isUpDownDrawerToUpClosed() {
        return  mUpDownDrawerOnscreen <= OFFSET_FACTOR && mUpDownDrawer.getTop()<0 ;
    }

    /**
     * 向下关闭双向抽屉
     */
    public void closeUpDownDrawerToDown () {
        View upDownDrawer = mUpDownDrawer;
        isUpDownDrawerTopBelowZero = false;
        mUpDownDrawerOnscreen = OFFSET_FACTOR;
        mViewDragHelper.smoothSlideViewTo(upDownDrawer , upDownDrawer.getLeft(), mMinUpDownDrawerMargin);
    }

    /**
     * 判断双向抽屉是否向下关闭
     * @return
     */
    public boolean isUpDownDrawerToDownClosed() {
        return  mUpDownDrawerOnscreen >= OFFSET_FACTOR && mUpDownDrawer.getTop() > 0 ;
    }

    /**
     * 判断上抽屉是否打开
     * @return
     */
    public boolean isUpDrawerOpened() {
        return mUpDrawer.getTop() <= 0;
    }

    /**
     * 判断上抽屉是否关闭
     * @return
     */
    public boolean isUpDrawerClosed() {
        return mUpDrawer.getTop() >= mMinUpDownDrawerMargin -upDrawerAndDownDrawerOffsetMargin;
    }

    /**
     * 判断下抽屉是否打开
     * @return
     */
    public boolean isDownDrawerOpened() {
        return mDownDrawer.getTop() >= screenHeight - mMinUpDownDrawerMargin;
    }

    /**
     * 判断下抽屉是否关闭
     * @return
     */
    public boolean isDownDrawerClosed() {
        return mUpDrawer.getTop() <= screenHeight - 2* mMinUpDownDrawerMargin +
                upDrawerAndDownDrawerOffsetMargin;
    }

    /**
     * 以下几个方法必须重写,还不明白为什么
     * @return
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams()
    {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new MarginLayoutParams(getContext(), attrs);
    }

    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p)
    {
        return new MarginLayoutParams(p);
    }

}
