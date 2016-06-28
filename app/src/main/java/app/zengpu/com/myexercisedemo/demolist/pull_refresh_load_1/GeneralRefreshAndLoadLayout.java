package app.zengpu.com.myexercisedemo.demolist.pull_refresh_load_1;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.zengpu.com.myexercisedemo.R;

/**
 * 下拉刷新和上拉加载更多。
 * 下拉刷新采用View.scrollTo() 配合辅助类Scroller.startScroll()方法完成
 * 上拉加载更多采用view.addFooterView()方法
 * Created by zengpu on 16/4/2.
 */
public class GeneralRefreshAndLoadLayout extends ViewGroup implements
                                                         AbsListView.OnScrollListener{
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
    private int mTouchSlop;

    /**
     * 内容视图,
     * 即用户触摸导致下拉刷新、上拉加载的主视图. 比如ListView, GridView等,此处为ListView
     */
    protected ListView mContentView;

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
     * 下拉或者上拉状态, 还没有到达可刷新的状态
     */
    public static final int STATUS_PULL_TO_REFRESH = 1;

    /**
     * 下拉或者上拉状态，达到可刷新状态
     */
    public static final int STATUS_RELEASE_TO_REFRESH = 2;
    /**
     * 刷新中
     */
    public static final int STATUS_REFRESHING = 3;

    /**
     * 加载更多中
     */
    public static final int STATUS_LOADING = 4;

    /**
     * 当前状态
     */
    protected int mCurrentStatus = STATUS_IDLE;

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


    public GeneralRefreshAndLoadLayout(Context context) {
        this(context, null);
    }

    public GeneralRefreshAndLoadLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GeneralRefreshAndLoadLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        // 初始化Scroller对象
        mScroller = new Scroller(context);
        // 获取屏幕高度
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        // header 的高度为屏幕高度的 1/4
        mHeaderHeight = mScreenHeight / 4;
        mFooterHeight = mScreenHeight / 12;

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        // 初始化整个布局
        initLayout(context);
    }

    /**
     * 初始化整个布局
     * @param context
     */
    private final void initLayout(Context context) {

        // 往布局里添加 headerView
        mHeaderView = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, this, false);
        mHeaderView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mHeaderHeight));
        mHeaderView.setPadding(0, mHeaderHeight/2, 0, 0);
        addView(mHeaderView);
        // HEADER VIEWS
        mArrowImageView = (ImageView) mHeaderView.findViewById(R.id.pull_to_arrow_image);
        mTipsTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_text);
        mTimeTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_updated_at);
        mProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.pull_to_refresh_progress);

        // 往布局里添加内容视图 可为listview，gridview texview 等；此处为ListView
        mContentView = new ListView(context);
        // 为ListView设置滚动监听器
        mContentView.setOnScrollListener(this);
        // 设置ListView布局参数
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(params);
        // 添加mContentView
        addView(mContentView);
        // 初始化footerView，不用添加到布局里
        mFooterView = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_footer, this, false);
        mFooterView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mFooterHeight));
    }

    /**
     * 丈量视图的宽、高。宽度为用户设置的宽度，高度则为header, contentView这两个子控件的高度和。
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

        int childCount = getChildCount();
        int top = getPaddingTop();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.layout(0, top, child.getMeasuredWidth(), child.getMeasuredHeight() + top);
            top += child.getMeasuredHeight();
        }
        // 计算初始化滑动的y轴距离
        mInitScrollY = mHeaderView.getMeasuredHeight() + getPaddingTop();
        // 滑动到header view高度的位置, 从而达到隐藏header view的效果
        Log.d("general","mInitScrollY is :"+ mInitScrollY);
        // 要移动view到坐标点（100，100），那么偏移量就是(0，0)-(100，100）=（-100 ，-100）,
        // 就要执行view.scrollTo(-100,-100),达到这个效果。
        scrollTo(0, mInitScrollY);
    }

    /**
     * 设置onitemclicklistener的adapter
     * @param adapter
     */
    public void setAdapter(ListAdapter adapter) {
        mContentView.setAdapter(adapter);
    }

    public ListAdapter getAdapter() {
        return mContentView.getAdapter();
    }

    /**
     * 是否已经到了最顶部,如果到达最顶端用户继续下拉则拦截事件;
     * @return
     */
    protected boolean isTop() {
        Log.d("general", "getScrollY  is :" + String.valueOf(getScrollY()));
        return mContentView.getFirstVisiblePosition() == 0
                && getScrollY() <= mHeaderView.getMeasuredHeight();
    }

    /**
     * 最后一个item是否完全显现出来
     * 是否已经到了最底部,子类需覆写该方法,使得mContentView滑动到最底端时返回true;
     * 从而触发自动加载更多的操作
     * @return
     */
    protected boolean isBottom() {
        return mContentView != null && mContentView.getAdapter() != null
                && mContentView.getLastVisiblePosition() ==
                mContentView.getAdapter().getCount() - 1;
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

    /**
     * 在适当的时候拦截触摸事件，这里指的适当的时候是当mContentView滑动到顶部，
     * 并且是下拉时拦截触摸事件，否则不拦截，交给其childview 来处理。
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
                // int yDistance = (int) ev.getRawY() - mYDown;
                mYOffset = (int) ev.getRawY() - mLastY;
                // 如果拉到了顶部, 并且是下拉,则拦截触摸事件,从而转到onTouchEvent来处理下拉刷新事件
                if (isTop() && mYOffset > 0) {
                    return true;
                }
                break;
        }
        // Do not intercept touch event, let the child handle it
        return false;
    }

    /**
     * 在这里处理触摸事件以达到下拉刷新或者上拉自动加载的问题
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(VIEW_LOG_TAG, "@@@ onTouchEvent : action = " + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int currentY = (int) event.getRawY();
                mYOffset = currentY - mLastY;
                if (mCurrentStatus != STATUS_LOADING) {
                    changeScrollY(mYOffset);
                }

                rotateHeaderArrow();
                changeTips();
                mLastY = currentY;
                break;
            case MotionEvent.ACTION_UP:
                // 下拉刷新的具体操作
                doRefresh();
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * @param distance
     * @return
     */
    protected void changeScrollY(int distance) {
        // 最大值为 scrollY(header 隐藏), 最小值为0 ( header 完全显示).
        //curY是当前Y的偏移量，在下拉过程中curY从最大值mInitScrollY逐渐变为0.
        int curY = getScrollY();
        // 下拉
        if (distance > 0 && curY - distance > getPaddingTop()) {
            scrollBy(0, -distance);
        } else if (distance < 0 && curY - distance <= mInitScrollY) {
            // 上拉过程
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

    /**
     * 旋转箭头图标
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
     * 根据当前状态修改header view中的文本标签
     */
    protected void changeTips() {
        if (mCurrentStatus == STATUS_PULL_TO_REFRESH) {
            mTipsTextView.setText("下拉刷新");
        } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            mTipsTextView.setText("松开可刷新");
        }
    }

    /**
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
     * 刷新结束，恢复状态
     */
    public void refreshComplete() {
        mScroller.startScroll(getScrollX(), getScrollY(), 0, mInitScrollY - getScrollY());
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
//        mScroller.startScroll(getScrollX(), getScrollY(), 0, mInitScrollY - getScrollY());
//        invalidate();
        mContentView.removeFooterView(mFooterView);
        mCurrentStatus = STATUS_IDLE;
    }

    /**
     * 显示footer view
     */
    private void showFooterView() {
        mContentView.addFooterView(mFooterView);
//        mScroller.startScroll(getScrollX(), getScrollY(), 0, mFooterView.getMeasuredHeight());
//        invalidate();
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
            showFooterView();
            doLoadMore();
        }
    }

    /**
     * 执行下拉(自动)加载更多的操作
     */
    protected void doLoadMore() {
        if (mLoadListener != null) {
            mLoadListener.onLoad();
        }
    }

    /**
     * 返回Content View
     *
     * @return
     */
    public ListView getContentView() {
        return mContentView;
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
