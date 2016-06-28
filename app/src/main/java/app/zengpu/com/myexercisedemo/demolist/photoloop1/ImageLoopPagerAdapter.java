package app.zengpu.com.myexercisedemo.demolist.photoloop1;

import android.support.annotation.IntRange;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * 自定义抽象PagerAdapter，用以实现图片轮播
 * 两个自定义的抽象方法getRealCount 和 instantiateRealItem 须在具体实现中重写
 * Created by zengpu on 16/3/30.
 */
public abstract class ImageLoopPagerAdapter  extends PagerAdapter {

    /**
     * ViewPager页数系数
     * COEFFICIENT须大于1，尽量小
     */
    private static final int COEFFICIENT = 10;

    /**
     * 绑定的ViewPager
     */
    private ViewPager viewPager;

    public ImageLoopPagerAdapter(ViewPager viewPager) {

        this.viewPager = viewPager;
    }

    /**
     * 自定义抽象方法，用以获得实际VIewPager的页数
     * @return 实际ViewPager的页数
     */
    @IntRange(from = 0)
    public abstract int getRealCount();


    @Override
    public int getCount() {

        long realCount = getRealCount();
        // 当getRealDataCount()返回1（即肉眼可见有1屏）的时候，那么getCount()就返回1;
        // 当getRealDataCount()返回大于1，假设为3（即肉眼可见3屏）的时候，
        // 那么getCount()就返回30，也就是对于程序来说实际有30屏。
        // 当我们不到达页面边界（第1屏或第30屏），循环的效果是有的，
        // 当到达边界，第1屏没法左滑，第30屏没法右滑，此时要实现循环效果，
        // 需要在finishUpdate方法里进行相应操作
        if (realCount > 1) {
            realCount = getRealCount() * COEFFICIENT;
            realCount = realCount > Integer.MAX_VALUE ? Integer.MAX_VALUE : realCount;
        }
        return (int) realCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position = position % getRealCount();
        return this.instantiateRealItem(container , position);
    }

    /**
     * 自定义抽象方法
     * @param container
     * @param position
     * @return
     */
    public abstract Object instantiateRealItem (ViewGroup container, int position);

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView((View) object);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        //Called when the a change in the shown pages has been completed.
        // At this point you must ensure that all of the pages have actually
        // been added or removed from the container as appropriate.

        //当我们的getCount()（程序页面数量）小于等于1时，不轮播
        //当我们的getCount()（程序页面数量）大于1时，还是假设为30。获取ViewPager当前的页面位置，
        // 然后判断程序边界。当前程序页面为第1页（对于肉眼来说为第1页），则直接替换为程序页面的第4页
        //当前程序页面为最后一页（对于肉眼来说为第3页），则直接替换为程序页面的第3页
        if (getCount() <= 1) {
            return;
        }
        int position = viewPager.getCurrentItem();
        if (position == 0) {
            position = getRealCount();
            viewPager.setCurrentItem(position,false);
        } else if (position == getCount() -1 ) {
            position = getRealCount()-1;
            viewPager.setCurrentItem(position,false);
        }
    }
}
