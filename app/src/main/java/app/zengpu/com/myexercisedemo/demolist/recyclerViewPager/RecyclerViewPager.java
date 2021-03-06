package app.zengpu.com.myexercisedemo.demolist.recyclerViewPager;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import app.zengpu.com.myexercisedemo.Utils.LogUtil;

/**
 * 具有viewpager效果的recyclerview
 * Created by zengpu on 2016/10/28.
 */

public class RecyclerViewPager extends RecyclerView implements
        View.OnTouchListener,
        GestureDetector.OnGestureListener {

    /**
     * The RecyclerViewPager is not currently scrolling.
     */
    public static final int SCROLL_STATE_IDLE = 0;

    /**
     * The RecyclerViewPager is currently being dragged by outside input such as user touch input.
     */
    public static final int SCROLL_STATE_DRAGGING = 1;

    /**
     * The RecyclerViewPager is currently animating to a final position while not under
     * outside control.
     */
    public static final int SCROLL_STATE_SETTLING = 2;

    /**
     * 触发翻页动作的最小滑动距离
     */
    private float mFlingSlop = 0;

    /**
     * 触发翻页动作的最小滑动距离的比例因子
     */
    private float mFlingFactor = 0.5f;

    /**
     * 触发翻页动作的最小滑动速度
     */
    private float mVelocitySlop = 0;

    /**
     * 当前recyclerView第一个可见的item的位置
     */
    private int currentPosition = 0;

    /**
     * 滑动事件结束后，选中的item的位置
     */
    private int mSelectedPosition = 0;

    /**
     * recyclerView的item个数
     */
    private int mItemCount = 0;

    /**
     * touch操作按下的位置 x
     */
    private float mTouchDownX = 0;

    /**
     * touch操作抬起的位置 x
     */
    private float mTouchUpX = 0;

    /**
     * 滑动过程中是否触发了onFling事件
     */
    private boolean is_trigger_onFling = false;


    private OnPageSelectListener mOnPageSelectListener;

    private RecyclerView.Adapter adapter;

    private GestureDetector gestureDetector;

    private LinearLayoutManager linearLayoutManager;

    public RecyclerViewPager(Context context) {
        super(context);
        init(context);
    }

    public RecyclerViewPager(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecyclerViewPager(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        int mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mFlingFactor = 0.55f;
        mFlingSlop = mScreenWidth * mFlingFactor;
        mVelocitySlop = 2000;

        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(linearLayoutManager);

        gestureDetector = new GestureDetector(context, this);

        this.setOnTouchListener(this);

        this.addOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    /*
                     * 页面停止滚动后，偶尔会出现目标页面没有完全滚入屏幕的情况，
                     * 通过获得当前item的偏移量来判断是否完全滚入
                     * 如果偏移量不为0，则需要用 smoothScrollBy()方法完成页面滚动
                     */
                    int mSelectedPageOffsetX = getScollOffsetX(mSelectedPosition);
                    if (mSelectedPageOffsetX != 0) {
                        smoothScrollBy(mSelectedPageOffsetX, 0);
                    }
                }

                if (null != mOnPageSelectListener) {
                    mOnPageSelectListener.onPageScrollStateChanged(newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        mOnPageSelectListener.onPageSelected(mSelectedPosition);
                    }

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LogUtil.e("RecyclerViewPager4", "dx : " + dx);
                if (null != mOnPageSelectListener) {
                    mOnPageSelectListener.onPageScrolled(mSelectedPosition, getScollOffsetX(mSelectedPosition));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {

        // 如果recyclerview的item里添加了onClick事件，则触摸事件会被onClick事件消费掉，
        // OnTouchLisenter监听就获取不到ACTION_DOWN时的触摸位置，因此在这里记录mTouchDownX
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownX = e.getX();
                is_trigger_onFling = false;
                LogUtil.e("RecyclerViewPager2", "mTouchDownX : " + mTouchDownX);
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        if (null == linearLayoutManager) {
            linearLayoutManager = (LinearLayoutManager) getLayoutManager();
        }
        if (null == adapter) {
            adapter = getAdapter();
        }
        currentPosition = linearLayoutManager.findFirstVisibleItemPosition();
        mSelectedPosition = currentPosition;
        mItemCount = adapter.getItemCount();

        // 将触摸事件传递给GestureDetector
        gestureDetector.onTouchEvent(event);

        /*
         * 手指滑动时，页面有两种翻页效果：
         * 1.如果不需要页面跟着一起滑动，只在手指抬起后进行翻页，则只需要处理onFling事件即可；在onTouch方法里直接返回true
         * 2.如果滑动时需要页面跟着一起滑动(像ViewPager一样)，则需要同时处理onFling和onScroll事件；
         *   onFling事件在OnGestureListener里处理；
         *   onScroll事件最终需要在onTouch里 ACTION_UP 触发后进行后续判断页面的滚动
         *
         * OnGestureListener监听里事件执行顺序有两种：
         *  onFling事件流： onDown —— onScroll —— onScroll... —— onFling
         *  onScroll事件流：onDown —— onScroll —— onScroll —— onScroll...
         * 当滑动速度比较快时，会进入第一种情况，最后执行onFling；
         * 当滑动速度比较快时，会进入第二种情况，这种情况不会进入到onFling里，最终会进入onTouch的ACTION_UP里
         */
        switch (event.getAction()) {

            // ACTION_DOWN 事件在onInterceptTouchEvent方法里记录
            case MotionEvent.ACTION_UP:

                if (!is_trigger_onFling) {
                    LogUtil.e("RecyclerViewPager2", "onTouch: ACTION_UP ");
                    LogUtil.e("RecyclerViewPager2", "is_trigger_onFling : " + is_trigger_onFling);

                    mTouchUpX = event.getX() - mTouchDownX;

                    LogUtil.e("RecyclerViewPager2", "mTouchUpX : " + mTouchUpX);

                    if (mTouchUpX >= mFlingSlop) {
                        // 往右滑，position减小
                        mSelectedPosition = currentPosition == 0 ? 0 : currentPosition;

                    } else if (mTouchUpX <= -mFlingSlop) {
                        // 往左滑动，position增大
                        mSelectedPosition = currentPosition == mItemCount - 1 ? mItemCount - 1 : currentPosition + 1;

                    } else if (mTouchUpX < mFlingSlop && mTouchUpX > 0) {
                        // 往右滑动，但未达到阈值
                        if (currentPosition == 0 && getScollOffsetX(0) >= 0)
                            // 边界控制，如果当前已经停留在第一页
                            mSelectedPosition = 0;
                        else
                            mSelectedPosition = currentPosition + 1;

                    } else {
                        mSelectedPosition = currentPosition;
                    }

                    smoothScrollToPosition(mSelectedPosition);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        LogUtil.e("RecyclerViewPager0", "onDown");

        is_trigger_onFling = false;

        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        LogUtil.e("RecyclerViewPager0", "onFling");

        is_trigger_onFling = true;

        if (null == e1 | null == e2) {

            if (velocityX >= mVelocitySlop) {
                // 往右滑动，position减少
                mSelectedPosition = currentPosition == 0 ? 0 : currentPosition;

            } else if (velocityX <= -mVelocitySlop) {
                // 往左滑动，position增大
                mSelectedPosition = currentPosition == mItemCount - 1 ? mItemCount - 1 : currentPosition + 1;

            } else if (velocityX < mVelocitySlop && velocityX >= 0) {
                // 往右滑动，未达到速度阈值
                if (currentPosition == 0 && getScollOffsetX(0) >= 0)
                    // 边界控制，如果当前已经停留在第一页
                    mSelectedPosition = 0;
                else
                    mSelectedPosition = currentPosition + 1;

            } else
                mSelectedPosition = currentPosition;

        } else {

            float x_fling = e2.getX() - e1.getX();

            LogUtil.e("RecyclerViewPager0", "velocityX : " + velocityX);
            LogUtil.e("RecyclerViewPager0", "x_fling : " + x_fling);
            LogUtil.e("RecyclerViewPager0", "mFlingSlop : " + mFlingSlop);


            if (x_fling >= mFlingSlop | velocityX >= mVelocitySlop) {
                // 往右滑动，position减少
                mSelectedPosition = currentPosition == 0 ? 0 : currentPosition;

            } else if (x_fling <= -mFlingSlop | velocityX <= -mVelocitySlop) {
                // 往左滑动，position增大
                mSelectedPosition = currentPosition == mItemCount - 1 ? mItemCount - 1 : currentPosition + 1;

            } else {
                if (x_fling < mFlingSlop && x_fling > 0) {
                    // 往右滑动，未达到阈值
                    if (currentPosition == 0 && getScollOffsetX(0) >= 0)
                        // 边界控制，如果当前已经停留在第一页
                        mSelectedPosition = 0;
                    else
                        mSelectedPosition = currentPosition + 1;
                } else
                    mSelectedPosition = currentPosition;
            }
        }
        smoothScrollToPosition(mSelectedPosition);
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        LogUtil.e("RecyclerViewPager00", "onShowPress");
        is_trigger_onFling = false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        LogUtil.e("RecyclerViewPager00", "onSingleTapUp");
        is_trigger_onFling = false;
        return true;
    }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        LogUtil.e("RecyclerViewPager0", "onScroll");
        is_trigger_onFling = false;
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        LogUtil.e("RecyclerViewPager0", "onLongPress");
    }

    /**
     * 获得当前页面相对于屏幕左侧边缘的偏移量
     *
     * @param position 当前页面位置
     * @return 偏移量
     */
    private int getScollOffsetX(int position) {

        if (null == linearLayoutManager) {
            linearLayoutManager = (LinearLayoutManager) getLayoutManager();
        }

        View childView = linearLayoutManager.findViewByPosition(position);
        if (null == childView) {
            return 0;
        }
        return childView.getLeft();
    }

    public void setOnPageSelectListener(OnPageSelectListener mOnPageSelectListener) {
        this.mOnPageSelectListener = mOnPageSelectListener;
    }

    /**
     * recyclerPager页面滚动监听
     */
    public interface OnPageSelectListener {

        /**
         * 滚动的过程中被调用
         *
         * @param position
         * @param positionOffset 当前第一个可见的item的左侧距离屏幕左边缘的距离
         */
        void onPageScrolled(int position, float positionOffset);

        /**
         * 滚动事件结束后被调用
         *
         * @param position
         */
        void onPageSelected(int position);

        /**
         * 滚动状态变化时被调用
         *
         * @param state
         */
        void onPageScrollStateChanged(int state);

    }
}
