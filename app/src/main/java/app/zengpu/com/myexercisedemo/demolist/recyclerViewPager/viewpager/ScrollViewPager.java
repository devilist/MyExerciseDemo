package app.zengpu.com.myexercisedemo.demolist.recyclerViewPager.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by zengp on 2017/10/17.
 */

public class ScrollViewPager extends ViewPager {

    public ScrollViewPager(Context context) {
        this(context, null);
    }

    public ScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initScroller();
    }

    private void initScroller() {
        Field field = null;
        try {
            field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            MyScroller scroller = new MyScroller(this.getContext(), new AccelerateDecelerateInterpolator());
            field.set(this, scroller);
            scroller.setCustomDuration(500);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private class MyScroller extends Scroller {

        int mDuration = 2000;

        public MyScroller(Context context) {
            super(context);
        }

        public MyScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public MyScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        public void setCustomDuration(int time) {
            mDuration = time;
        }

        public int getCustomDuration() {
            return mDuration;
        }
    }
}
