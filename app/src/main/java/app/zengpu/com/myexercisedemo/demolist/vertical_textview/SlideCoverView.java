package app.zengpu.com.myexercisedemo.demolist.vertical_textview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.Scroller;

import app.zengpu.com.myexercisedemo.Utils.LogUtil;

/**
 * Created by zengpu on 2017/1/27.
 */

public class SlideCoverView extends ViewGroup {


    private Context mContext;
    private int mScreenWidth;      // 屏幕宽度

    protected Scroller mScroller;

    private HorizontalScrollView mScrollView;

    private int mScrollViewChildTotalWidth = 0; // scrollview子view宽度总和


    private boolean isLeftToRight = true; // 阅读方向

    protected int mLastX = 0; // 最后一次触摸事件的X轴坐标
    protected int mXOffset; // 本次触摸滑动x坐标上的偏移量

    protected boolean isScrollToRight = false; // scrollview内容是否滚到了最右边
    protected boolean isScrollToLeft = false; // scrollview内容是否滚到了最左边

    private VelocityTracker mVelocityTracker = null;


    public SlideCoverView(Context context) {
        this(context, null);
    }

    public SlideCoverView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideCoverView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        init();
    }

    private void init() {

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();

        // 初始化Scroller对象
        mScroller = new Scroller(mContext);

        isScrollToLeft = isLeftToRight ? true : false;
        isScrollToRight = isLeftToRight ? false : true;

    }

    public void setLeftToRight(boolean leftToRight) {
        isLeftToRight = leftToRight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int childCount = getChildCount();
        int finalWidth = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            // pathMeasure
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 该view所需要的总宽度
            finalWidth += child.getMeasuredWidth();
        }
        setMeasuredDimension(finalWidth, height);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int top = getPaddingTop();
        int left = getPaddingLeft();

        View child0 = getChildAt(0); // scrollview
        View child1 = getChildAt(1); // coverview

        int initScrollX;

        if (isLeftToRight) {
            child1.layout(left, top, left + child1.getMeasuredWidth(), child1.getMeasuredHeight() + top);
            left += child1.getMeasuredWidth();
            child0.layout(left, top, left + child0.getMeasuredWidth(), child0.getMeasuredHeight() + top);
            // 计算初始化滑动的x轴距离
            initScrollX = 0;
        } else {
            child0.layout(left, top, left + child0.getMeasuredWidth(), child0.getMeasuredHeight() + top);
            left += child0.getMeasuredWidth();
            child1.layout(left, top, left + child1.getMeasuredWidth(), child1.getMeasuredHeight() + top);
            // 计算初始化滑动的x轴距离
            initScrollX = child0.getMeasuredWidth() + getPaddingLeft();
        }

        mScrollView = (HorizontalScrollView) child0;
        mScrollViewChildTotalWidth = getScrollViewWidth(mScrollView);

        // 要移动view到坐标点（100，100），那么偏移量就是(0，0)-(100，100）=（-100 ，-100）,
        // 就要执行view.scrollTo(-100,-100),达到这个效果。
        scrollTo(initScrollX, 0);

    }

    private int getScrollViewWidth(HorizontalScrollView scrollView) {
        int number = scrollView.getChildCount();
        int width = 0;
        for (int i = 0; i < number; i++) {
            View view = scrollView.getChildAt(i);
            width += view.getMeasuredWidth();
        }

        return width;
    }

    /**
     * scrollview内容是否滚动到最左边
     *
     * @return
     */
    protected boolean isLeft() {
        if (null != mScrollView)
            return mScrollView.getScrollX() <= 0;
        else
            return false;
    }

    /**
     * scrollview内容是否滚动到最右边
     *
     * @return
     */
    protected boolean isRight() {
        if (null != mScrollView)
            return mScrollView.getScrollX() + mScreenWidth >= getScrollViewWidth(mScrollView);
        else
            return false;
    }

    /**
     * 与Scroller合作,实现平滑滚动。在该方法中调用Scroller的computeScrollOffset来判断滚动是否结束。
     * 如果没有结束，那么滚动到相应的位置，并且调用postInvalidate方法重绘界面，
     * 从而再次进入到这个computeScroll流程，直到滚动结束。
     * view重绘时会调用此方法
     */

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                mXOffset = (int) ev.getRawX() - mLastX;
                // 阅读方向 从左向右；
                // 如果滑到了最左边，并且是右滑，拦截事件
                if (isLeftToRight && isLeft() && mXOffset > 0) {
                    isScrollToLeft = true;
                    isScrollToRight = false;
                    return true;
                }

                // 阅读方向 从右向左；
                // 如果滑到了最右边，并且是左滑，拦截事件
                if (!isLeftToRight && isRight() && mXOffset < 0) {
                    isScrollToLeft = false;
                    isScrollToRight = true;
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        int velocity = 0;

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int currentX = (int) event.getRawX();
                mXOffset = currentX - mLastX;
                changeScrollX((int) (mXOffset * 1.0f));
                mLastX = currentX;
                break;
            case MotionEvent.ACTION_UP:
                LogUtil.d("SlideView", "ACTION_UP ");
                LogUtil.d("SlideView", "mXOffset " + mXOffset);
                if (mVelocityTracker != null) {
                    mVelocityTracker.computeCurrentVelocity(1000);
                    velocity = (int) mVelocityTracker.getXVelocity();
                }
                LogUtil.d("SlideView", "velocity " + velocity);
                doScrollX(velocity);
                break;
        }
        // 拦截touch事件不让子view消费
        return true;
    }


    /**
     * 根据右滑刷新或左滑加载的距离改变view状态
     *
     * @param distance
     * @return
     */
    protected void changeScrollX(int distance) {

        // 阅读方向 从左向右
        if (isScrollToLeft && isLeftToRight) {
            int curX = getScrollX();
            // 右滑过程边界处理
            if (distance > 0 && curX - distance > getPaddingLeft()) {
                scrollBy(-distance, 0);
            } else if (distance < 0 && curX - distance <= getChildAt(0).getMeasuredWidth() + getPaddingLeft()) {
                // 左滑过程边界处理
                scrollBy(-distance, 0);
            }
        }

        // 阅读方向，从右向左
        if (isScrollToRight && !isLeftToRight) {
            int curX = getScrollX();
            // 右滑过程边界处理
            if (distance > 0 && curX - distance > getPaddingLeft()) {
                scrollBy(-distance, 0);
            } else if (distance < 0 && curX - distance <= getPaddingLeft() + getChildAt(0).getMeasuredWidth()) {
                // 左滑过程边界处理
                scrollBy(-distance, 0);
            }
        }
    }

    /**
     * 手指抬起后根据状态显示或隐藏cover
     * 先根据速度判断，再根据位移判断
     */
    private void doScrollX(int velocity) {
        int curScrollX = getScrollX();
        int velocitySlop = 4000;
        int scrollDuration = 1500;


        LogUtil.d("SlideView", "isScrollToLeft " + isScrollToLeft);
        LogUtil.d("SlideView", "isScrollToRight " + isScrollToRight);

        if (isScrollToLeft && isLeftToRight) {
            int coverViewWidth = getChildAt(0).getMeasuredWidth() + getPaddingLeft();
            if (velocity > velocitySlop) {
                mScroller.startScroll(curScrollX, getScrollY(), -curScrollX, 0, scrollDuration);
            } else if (velocity < -velocitySlop) {
                mScroller.startScroll(curScrollX, getScrollY(), coverViewWidth - curScrollX, 0, scrollDuration);
            } else if (curScrollX <= coverViewWidth / 2) {
                // 超过1/2,则显示cover
                mScroller.startScroll(curScrollX, getScrollY(), -curScrollX, 0, scrollDuration);
            } else if (curScrollX > coverViewWidth / 2) {
                mScroller.startScroll(curScrollX, getScrollY(), coverViewWidth - curScrollX, 0, scrollDuration);
            }
        }

        if (isScrollToRight && !isLeftToRight) {
            int scrollViewWidth = getChildAt(0).getMeasuredWidth() + getPaddingLeft();
            if (velocity > velocitySlop) {
                mScroller.startScroll(curScrollX, getScrollY(), -curScrollX, 0, scrollDuration);
            } else if (velocity < -velocitySlop) {
                mScroller.startScroll(curScrollX, getScrollY(), scrollViewWidth - curScrollX, 0, scrollDuration);
            } else if (curScrollX >= scrollViewWidth / 2) {
                // 显示cover
                mScroller.startScroll(curScrollX, getScrollY(), scrollViewWidth - curScrollX, 0, scrollDuration);
            } else if (curScrollX < scrollViewWidth / 2) {
                mScroller.startScroll(curScrollX, getScrollY(), -curScrollX, 0, scrollDuration);
            }
        }
        invalidate();
    }
}
