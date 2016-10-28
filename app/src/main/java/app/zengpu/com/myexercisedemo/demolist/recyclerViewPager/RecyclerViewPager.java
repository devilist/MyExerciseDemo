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

    private float mFlingSlop = 0; // 滑屏触发翻页的最小距离
    private float mVelocitySlop = 0; // 滑屏触发翻页的最小速度
    private int currentPosition = 0; // 当前位置
    private int maxCount = 0; // item个数

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
        mFlingSlop = mScreenWidth * 3 / 5;
        mVelocitySlop = 1000;

        this.setOnTouchListener(this);
        gestureDetector = new GestureDetector(context, this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean is_null = null == event;

//        LogUtil.e("RecyclerViewPager", "onTouch");
//        LogUtil.e("RecyclerViewPager", "MotionEvent null: " + is_null);

        gestureDetector.onTouchEvent(event);

        float x_down = 0;
        float x_move = 0;
        float x_up = 0;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x_down = event.getX();
                LogUtil.e("RecyclerViewPager1", "x_down: " + x_down);
                return false;
            case MotionEvent.ACTION_HOVER_MOVE:
                x_move = event.getX();
                LogUtil.e("RecyclerViewPager1", "x_move: " + x_move);
                return false;
            case MotionEvent.ACTION_UP:
                x_up = x_move - x_down;
                LogUtil.e("RecyclerViewPager1", "x_up: " + x_up);
                return true;

        }
        // 拦截事件
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        LogUtil.e("RecyclerViewPager1", "onDown");
        if (null == linearLayoutManager) {
            linearLayoutManager = (LinearLayoutManager) getLayoutManager();
        }
        if (null == adapter) {
            adapter = getAdapter();
        }
        currentPosition = linearLayoutManager.findFirstVisibleItemPosition();
        maxCount = adapter.getItemCount();

        LogUtil.e("RecyclerViewPager1", "currentPosition : " + currentPosition);
        LogUtil.e("RecyclerViewPager1", "maxCount : " + maxCount);

        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // 识别滑动
        LogUtil.e("RecyclerViewPager", "onFling");
        int fing_position = 0;
        if (null == e1 | null == e2)
            fing_position = currentPosition;

        else {

            float x_fing = e2.getX() - e1.getX();

            LogUtil.e("RecyclerViewPager", "velocityX : " + velocityX);
            LogUtil.e("RecyclerViewPager", "x_fing : " + x_fing);
            LogUtil.e("RecyclerViewPager", "mFlingSlop : " + mFlingSlop);


            if (x_fing >= mFlingSlop | velocityX >= mVelocitySlop) {
                // 左划
                fing_position = currentPosition == 0 ? 0 : currentPosition - 1;

            } else if (x_fing <= -mFlingSlop | velocityX <= -mVelocitySlop) {
                // 右划
                fing_position = currentPosition == maxCount - 1 ? maxCount - 1 : currentPosition + 1;

            } else {
                fing_position = currentPosition;
            }
        }
        LogUtil.e("RecyclerViewPager", "fing_position : " + fing_position);
        smoothScrollToPosition(fing_position);
        currentPosition = fing_position;

        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        LogUtil.e("RecyclerViewPager", "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        LogUtil.e("RecyclerViewPager", "onSingleTapUp");
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        LogUtil.e("RecyclerViewPager", "onScroll");
        LogUtil.e("RecyclerViewPager", "onScroll distanceX: " + distanceX);


        int fing_position = currentPosition;
        if (null == e1 | null == e2)
            return false;

        float x_scroll = e1.getX() + e2.getX();
//        float x_scroll = getScrollX();
        if (x_scroll >= mFlingSlop) {
            //  左划
            fing_position = currentPosition == 0 ? 0 : currentPosition - 1;

        } else if (x_scroll <= -mFlingSlop ) {

            fing_position = currentPosition == maxCount - 1 ? maxCount - 1 : currentPosition + 1;
        }
//        smoothScrollBy((int) x_scroll, 0);

        LogUtil.e("RecyclerViewPager", "onScroll x_scroll: " + x_scroll);
        LogUtil.e("RecyclerViewPager", "onScroll e1.getX(): " + e1.getX());
        LogUtil.e("RecyclerViewPager", "onScroll e2.getX(): " +  e2.getX());
        LogUtil.e("RecyclerViewPager", "onScroll e2.getAction(): " +  e2.getAction());
//        smoothScrollToPosition(fing_position);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        LogUtil.e("RecyclerViewPager", "onLongPress");
    }
}
