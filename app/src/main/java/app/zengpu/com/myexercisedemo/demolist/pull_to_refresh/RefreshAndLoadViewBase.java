package app.zengpu.com.myexercisedemo.demolist.pull_to_refresh;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.Utils.LogUtil;

/**
 * 下拉刷新，上拉加载更多抽象基类。
 * 子类继承时需要实现三个抽象方法：
 * setContentViewScrollListener();   为ListView / GridView / RecyclerView等 添加滚动监听
 * isTop();   是否滚动到了头部
 * isBottom();   是否滚动到了底部
 * <p>
 * Created by zengpu on 2016/4/22.
 */
public abstract class RefreshAndLoadViewBase<T extends View> extends ViewGroup implements
        AbsListView.OnScrollListener {
    /**
     * 帮助View滚动的辅助类Scroller
     */
    protected Scroller mScroller;

    /**
     * 下拉刷新时显示的headerView
     */
    protected View mHeaderView;

    /**
     * 上拉加载更多时显示的footerView
     */
    protected View mFooterView;

    /**
     * 本次触摸滑动y坐标上的偏移量
     */
    protected int mYOffset;

    /**
     * 触发上拉加载操作的最小距离
     */
    protected int mTouchSlop;

    /**
     * 内容视图,
     * 即用户触摸导致下拉刷新、上拉加载的主视图. 比如ListView, GridView等
     */
    protected T mContentView;

    /**
     * 最初的滚动位置.第一次布局时滚动header的高度的距离
     */
    protected int mInitScrollY = 0;
    /**
     * 最后一次触摸事件的y轴坐标
     */
    protected int mLastY = 0;

    /**
     * 空闲状态
     */
    public static final int STATUS_IDLE = 0;

    /**
     * 下拉状态, 还没有到达可刷新的状态
     */
    public static final int STATUS_PULL_TO_REFRESH = 1;

    /**
     * 下拉状态，达到可刷新状态
     */
    public static final int STATUS_RELEASE_TO_REFRESH = 2;
    /**
     * 刷新中
     */
    public static final int STATUS_REFRESHING = 3;

    /**
     * 上拉状态, 还没有到达可加载更多的状态
     */
    public static final int STATUS_PULL_TO_LOAD = 4;

    /**
     * 上拉状态，达到可加载更多状态
     */
    public static final int STATUS_RELEASE_TO_LOAD = 5;

    /**
     * 加载更多中
     */
    public static final int STATUS_LOADING = 6;

    /**
     * 当前状态
     */
    protected int mCurrentStatus = STATUS_IDLE;

    /**
     * 是否滚到了底部。滚到底部后执行上拉加载
     */
    protected boolean isScrollToBottom = false;
    /**
     * 是否滚到了顶部。滚到底部后执行下拉刷新
     */
    protected boolean isScrollToTop = false;


    /**
     * header中的箭头图标
     */
    private ImageView mArrowImageView;
    /**
     * 箭头是否向上
     */
    private boolean isArrowUp;
    /**
     * header 中的文本标签
     */
    private TextView mTipsTextView;
    /**
     * header中的时间标签
     */
    private TextView mTimeTextView;
    /**
     * header中的进度条
     */
    private ProgressBar mProgressBar;
    /**
     * 屏幕高度
     */
    private int mScreenHeight;
    /**
     * Header 高度
     */
    private int mHeaderHeight;
    /**
     * Footer 高度
     */
    private int mFooterHeight;
    /**
     * 下拉刷新监听器
     */
    protected OnRefreshListener mOnRefreshListener;
    /**
     * 加载更多监听器
     */
    protected OnLoadListener mLoadListener;

    protected Context context;

    protected int color;


    public RefreshAndLoadViewBase(Context context) {
        this(context, null);
        this.context = context;
    }

    public RefreshAndLoadViewBase(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshAndLoadViewBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        // 初始化Scroller对象
        mScroller = new Scroller(context);
        // 获取屏幕高度
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        // header 的高度为屏幕高度的 1/4
        mHeaderHeight = mScreenHeight / 4;
        mFooterHeight = mScreenHeight / 4;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

//        setHeaderBackgroundColor();
        // 初始化整个布局
        initLayout(context);
    }

    /**
     * 初始化整个布局
     *
     * @param context
     */
    private final void initLayout(Context context) {
        /* 往布局里添加 headerView,mHeaderView = getChildAt(0)*/
        mHeaderView = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, this, false);
        mHeaderView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mHeaderHeight));
        mHeaderView.setPadding(0, mHeaderHeight / 2, 0, 0);
        addView(mHeaderView);
        // HEADER VIEWS
        mArrowImageView = (ImageView) mHeaderView.findViewById(R.id.pull_to_arrow_image);
        mTipsTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_text);
        mTimeTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_updated_at);
        mProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.pull_to_refresh_progress);
        /* 初始化footerView，添加到布局里,mFooterView = getChildAt(1); */
        mFooterView = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_footer, this, false);
        mFooterView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mFooterHeight));
        mFooterView.setPadding(0, 0, 0, mFooterHeight / 2);
        mFooterView.setVisibility(VISIBLE);
        addView(mFooterView);
    }

    /**
     * 为contentView添加滚动监听
     */
    protected abstract void setContentViewScrollListener();

    /**
     * 丈量视图的宽、高。宽度为用户设置的宽度，高度则为header, contentView这两个子控件的高度和。
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int childCount = getChildCount();
        int finalHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            // measure
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 该view所需要的总高度
            finalHeight += child.getMeasuredHeight();
        }
        setMeasuredDimension(width, finalHeight);
    }

    /**
     * 布局函数，将header, contentView,两个view从上到下布局。布局完成后通过Scroller滚动到header的底部，
     * 即滚动距离为header的高度 +本视图的paddingTop，从而达到隐藏header的效果.
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        /* headview = getChildAt(0);
           footview = getChildAt(1);
           mContentView = getChildAt(2);
           布局的时候要把 mContentView 放中间; */
        int top = getPaddingTop();
        int left = getPaddingLeft();
        View child0 = getChildAt(0);
        View child1 = getChildAt(1);
        View child2 = getChildAt(2);
        child0.layout(left, top, left + child0.getMeasuredWidth(), child0.getMeasuredHeight() + top);
        top += child0.getMeasuredHeight();
        child2.layout(left, top, left + child2.getMeasuredWidth(), child2.getMeasuredHeight() + top);
        top += child2.getMeasuredHeight();
        child1.layout(left, top, left + child1.getMeasuredWidth(), child1.getMeasuredHeight() + top);
        // 为mContentView添加滚动监听
        setContentViewScrollListener();
        // 计算初始化滑动的y轴距离
        mInitScrollY = mHeaderView.getMeasuredHeight() + getPaddingTop();
        // 滑动到headerView高度的位置, 从而达到隐藏headerView的效果
        LogUtil.d("RefreshAndLoadViewBase", "mInitScrollY is :" + mInitScrollY);
        // 要移动view到坐标点（100，100），那么偏移量就是(0，0)-(100，100）=（-100 ，-100）,
        // 就要执行view.scrollTo(-100,-100),达到这个效果。
        scrollTo(0, mInitScrollY);
    }

    /**
     * 是否已经到了最顶部,子类需覆写该方法,使得mContentView滑动到最顶端时返回true,
     * 如果到达最顶端用户继续下拉则拦截事件;
     *
     * @return
     */
    protected abstract boolean isTop();

    /**
     * 是否已经到了最底部,子类需覆写该方法,使得mContentView滑动到最底端时返回true;
     * 从而触发自动加载更多的操作
     *
     * @return
     */
    protected abstract boolean isBottom();

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

    /**
     * 在适当的时候拦截触摸事件，两种情况：
     * <p>
     * 1.当mContentView滑动到顶部，并且是下拉时拦截触摸事件，
     * 2.当mContentView滑动到底部，并且是上拉时拦截触摸事件，
     * 其它情况不拦截，交给其childview 来处理。
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */
        final int action = MotionEventCompat.getActionMasked(ev);
        // Always handle the case of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Do not intercept touch event, let the child handle it
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtil.d("RefreshAndLoadViewBase", "ACTION_MOVE getScrollY is: " + getScrollY());
//                LogUtil.d("RefreshAndLoadViewBase", "ACTION_MOVE header height is: " + mHeaderView.getMeasuredHeight());
//                LogUtil.d("RefreshAndLoadViewBase", "ACTION_MOVE list height is: " + mContentView.getHeight());
//                LogUtil.d("RefreshAndLoadViewBase", "ACTION_MOVE mScreen height is: " + mScreenHeight);
                // int yDistance = (int) ev.getRawY() - mYDown;
                mYOffset = (int) ev.getRawY() - mLastY;
                // 如果拉到了顶部, 并且是下拉,则拦截触摸事件,从而转到onTouchEvent来处理下拉刷新事件
                if (isTop() && mYOffset > 0) {
                    isScrollToTop = true;
                    isScrollToBottom = false;
                    return true;
                }
                // 如果拉到了底部, 并且是下上拉,则拦截触摸事件,从而转到onTouchEvent来处理下拉刷新事件
                if (isBottom() && mYOffset < 0) {
                    isScrollToTop = false;
                    isScrollToBottom = true;
                    return true;
                }
                break;
        }
        // Do not intercept touch event, let the child handle it
        return false;
    }

    /**
     * 在这里处理触摸事件以达到下拉刷新或者上拉自动加载的问题
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.d(VIEW_LOG_TAG, "@@@ onTouchEvent : action = " + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int currentY = (int) event.getRawY();
                mYOffset = currentY - mLastY;

                changeScrollY(mYOffset);

                if (isScrollToTop && mCurrentStatus != STATUS_LOADING) {
                    changeTips();
                    rotateHeaderArrow();
                }

                mLastY = currentY;

                break;
            case MotionEvent.ACTION_UP:
                //
                if (isScrollToTop && mCurrentStatus != STATUS_LOADING)
                    doRefresh();
                if (isScrollToBottom && mCurrentStatus != STATUS_REFRESHING)
                    doLoadMore();
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 根据下拉刷新或上拉加载的距离改变header或footer状态
     *
     * @param distance
     * @return
     */
    protected void changeScrollY(int distance) {
        // 最大值为 scrollY(header 隐藏), 最小值为0 ( header 完全显示).
        //curY是当前Y的偏移量，在下拉过程中curY从最大值mInitScrollY逐渐变为0.

        // 下拉刷新过程
        if (isScrollToTop && mCurrentStatus != STATUS_LOADING) {
            int curY = getScrollY();
            LogUtil.d("RefreshAndLoadViewBase", "下拉刷新 curY is: " + curY);
            LogUtil.d("RefreshAndLoadViewBase", "下拉刷新 distance is: " + distance);
            // 下拉过程边界处理
            if (distance > 0 && curY - distance > getPaddingTop()) {
                scrollBy(0, -distance);
            } else if (distance < 0 && curY - distance <= mInitScrollY) {
                // 上拉过程边界处理
                scrollBy(0, -distance);
            }
            curY = getScrollY();
            int slop = mInitScrollY / 2;
            // curY是当前Y的偏移量，在下拉过程中curY从最大值mInitScrollY逐渐变为0.
            if (curY > 0 && curY < slop) {
                mCurrentStatus = STATUS_RELEASE_TO_REFRESH;
            } else if (curY > 0 && curY > slop) {
                mCurrentStatus = STATUS_PULL_TO_REFRESH;
            }
        }

        // 上拉加载过程
        if (isScrollToBottom && mCurrentStatus != STATUS_REFRESHING) {
            int curY = getScrollY() - mHeaderHeight;
            LogUtil.d("RefreshAndLoadViewBase", "上拉加载 curY is: " + curY);
            LogUtil.d("RefreshAndLoadViewBase", "上拉加载 distance is: " + distance);
            // 下拉过程边界处理
            if (distance > 0 && curY - distance > 0) {
                scrollBy(0, -distance);
            } else if (distance < 0 && curY - distance <= mFooterHeight) {
                // 上拉过程边界处理
                scrollBy(0, -distance);
            }
            curY = getScrollY() - mHeaderHeight;
            int slop = mInitScrollY / 2;

            if (curY > 0 && curY < slop) {
                mCurrentStatus = STATUS_PULL_TO_LOAD;
            } else if (curY > 0 && curY > slop) {
                mCurrentStatus = STATUS_RELEASE_TO_LOAD;
            }
        }


    }

    /**
     * 下拉刷新
     * 改变旋转箭头图标
     */
    protected void rotateHeaderArrow() {

        if (mCurrentStatus == STATUS_REFRESHING) {
            return;
        } else if (mCurrentStatus == STATUS_PULL_TO_REFRESH && !isArrowUp) {
            return;
        } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH && isArrowUp) {
            return;
        }
        mProgressBar.setVisibility(View.GONE);
        mArrowImageView.setVisibility(View.VISIBLE);
        float pivotX = mArrowImageView.getWidth() / 2f;
        float pivotY = mArrowImageView.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (mCurrentStatus == STATUS_PULL_TO_REFRESH) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            fromDegrees = 0f;
            toDegrees = 180f;
        }

        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        mArrowImageView.startAnimation(animation);

        if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            isArrowUp = true;
        } else {
            isArrowUp = false;
        }
    }

    /**
     * 下拉刷新根据当前状态修改header view中的文本标签
     */
    protected void changeTips() {
        if (mCurrentStatus == STATUS_PULL_TO_REFRESH) {
            mTipsTextView.setText("下拉刷新");
        } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            mTipsTextView.setText("松开可刷新");
        }
    }

    /**
     * 下拉刷新
     * 手指抬起时,根据用户下拉的高度来判断是否是有效的下拉刷新操作。如果下拉的距离超过header view的
     * 1/2那么则认为是有效的下拉刷新操作，否则恢复原来的视图状态.
     */
    private void changeHeaderViewStaus() {
        int curScrollY = getScrollY();
        // 超过1/2则认为是有效的下拉刷新, 否则还原
        if (curScrollY < mInitScrollY / 2) {
            mScroller.startScroll(getScrollX(), curScrollY, 0, mHeaderView.getPaddingTop()
                    - curScrollY);
            mCurrentStatus = STATUS_REFRESHING;
            mTipsTextView.setText("加载中...");
            mArrowImageView.clearAnimation();
            mArrowImageView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mScroller.startScroll(getScrollX(), curScrollY, 0, mInitScrollY - curScrollY);
            mCurrentStatus = STATUS_IDLE;
        }
        invalidate();
    }

    /**
     * 上拉加载
     * 手指抬起时,根据用户上拉的高度来判断是否是有效的下拉刷新操作。如果上拉的距离超过footer view的
     * 1/2那么则认为是有效的上拉加载操作，否则恢复原来的视图状态.
     */
    private void changeFooterViewStatus() {
        int curScrollY = getScrollY();
        // 超过1/2则认为是有效的下拉刷新, 否则还原
        if (curScrollY > mFooterHeight / 2) {
            mScroller.startScroll(getScrollX(), curScrollY, 0, mHeaderHeight + mFooterView.getPaddingBottom()
                    - curScrollY);
            mCurrentStatus = STATUS_LOADING;

        } else {
            mScroller.startScroll(getScrollX(), curScrollY, 0, mHeaderHeight - curScrollY);
            mCurrentStatus = STATUS_IDLE;
        }
        invalidate();
    }

    /**
     * 刷新结束，恢复状态
     */
    public void refreshComplete() {
        mScroller.startScroll(getScrollX(), getScrollY(), 0, mInitScrollY - getScrollY());
//        hiddenFooterView();
        mCurrentStatus = STATUS_IDLE;
        invalidate();
        updateHeaderTimeStamp();
        // 200毫秒后处理arrow和progressbar,免得太突兀
        this.postDelayed(new Runnable() {

            @Override
            public void run() {
                mArrowImageView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        }, 100);
    }

    /**
     * 加载结束，恢复状态
     */
    public void loadCompelte() {
        // 隐藏footer
        mScroller.startScroll(getScrollX(), getScrollY(), 0, mInitScrollY - getScrollY());
        invalidate();
        mCurrentStatus = STATUS_IDLE;
    }

    /**
     * 显示footer view
     */
    protected void showFooterView() {
        mScroller.startScroll(getScrollX(), getScrollY(), 0, mFooterView.getMeasuredHeight());
        invalidate();
        mCurrentStatus = STATUS_LOADING;
    }


    /**
     * 执行下拉刷新
     */
    protected void doRefresh() {
        changeHeaderViewStaus();
        // 执行刷新操作
        if (mCurrentStatus == STATUS_REFRESHING && mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }

    /**
     * 执行下拉(自动)加载更多的操作
     */
    protected void doLoadMore() {
        changeFooterViewStatus();
        if (mCurrentStatus == STATUS_LOADING && mLoadListener != null) {
            mLoadListener.onLoad();
        }
    }

    /**
     * 修改header上的最近更新时间
     */
    private void updateHeaderTimeStamp() {
        // 设置更新时间
        mTimeTextView.setText("上次更新时间 ");
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getInstance();
        sdf.applyPattern("MM-dd HH:mm");
        mTimeTextView.append(sdf.format(new Date()));
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    /**
     * 滚动监听，当滚动到最底部，且用户设置了加载更多的监听器时触发加载更多操作.
     *
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // 用户设置了加载更多监听器，且到了最底部，并且是上拉操作，那么执行加载更多.
        if (mLoadListener != null && isBottom() && mYOffset < -mTouchSlop
                && mCurrentStatus == STATUS_IDLE) {
//            showFooterView();
//            doLoadMore();
        }
    }


    /**
     * @return
     */
    public View getHeaderView() {
        return mHeaderView;
    }

    /**
     * @return
     */
    public View getFooterView() {
        return mFooterView;
    }

    /**
     * 设置下拉刷新监听器
     *
     * @param listener
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    /**
     * 设置滑动到底部时自动加载更多的监听器
     *
     * @param listener
     */
    public void setOnLoadListener(OnLoadListener listener) {
        mLoadListener = listener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadListener {
        void onLoad();
    }
}
