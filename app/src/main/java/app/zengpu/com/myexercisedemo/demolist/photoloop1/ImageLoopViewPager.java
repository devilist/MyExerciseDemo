package app.zengpu.com.myexercisedemo.demolist.photoloop1;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 具有自动轮播效果的ViewPager
 * 当ViewPager被attach到windows时，会触发自动轮播定时任务
 * 当ViewPager被dettach到windows时，会触发关闭自动轮播定时任务
 * Created by zengpu on 16/3/30.
 */
public class ImageLoopViewPager extends ViewPager {

    /**
     * 触发自动轮播任务
     */
    public static final int RESUME = 0;

    /**
     * 暂停自动轮播任务
     */
    public static final int PAUSE = 1;

    /**
     * 销毁自动轮播任务
     */
    public static final int DESTROY = 2;

    /**
     * 生命周期状态，保证轮播定时器 mLoopTimer 在各生命周期选择执行策略
     */
    private int mLifeCycle = RESUME;

    /**
     * 是否处于触摸状态，用于防止触摸滑动和自动轮播冲突
     */
    private boolean isTouching = false;

    /**
     * 轮播定时器
     */
    private ScheduledExecutorService mLoopTimer;

    public ImageLoopViewPager(Context context) {
        super(context);
    }

    public ImageLoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 管理定时器的生命周期。
     * 定时器基于生命周期恢复、暂停、销毁，
     * 而生命周期是在外部调用setLifeCycle()实现
     * @param lifeCycle
     */
    public void setLifeCycle (int lifeCycle) {
        mLifeCycle = lifeCycle;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isTouching = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isTouching = false;
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        shutdownLoopTimer();
        mLoopTimer = Executors.newSingleThreadScheduledExecutor();
        mLoopTimer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                switch (mLifeCycle) {
                    case RESUME:
                        if (!isTouching && getAdapter() != null && getAdapter().getCount() >1) {
                            //子线程中操作UI
                            post(new Runnable() {
                                @Override
                                public void run() {
                                   setCurrentItem(getCurrentItem()+1);
                                }
                            });
                        }
                        break;
                    case PAUSE:
                        break;
                    case  DESTROY:
                        shutdownLoopTimer();
                        break;
                }
            }
        },3000,3000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        shutdownLoopTimer();
    }

    private void  shutdownLoopTimer(){
        if (mLoopTimer != null && mLoopTimer.isShutdown() == false) {
            mLoopTimer.shutdown();
        }
        mLoopTimer = null;
    }
}
