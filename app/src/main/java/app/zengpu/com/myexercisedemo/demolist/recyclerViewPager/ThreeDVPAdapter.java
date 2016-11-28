package app.zengpu.com.myexercisedemo.demolist.recyclerViewPager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by zengpu on 2016/11/28.
 */

public class ThreeDVPAdapter extends PagerAdapter {

    private List<View> viewList;

    public ThreeDVPAdapter(List<View> viewList) {
        this.viewList = viewList;
    }

    @Override
    public int getCount() {
        return viewList == null ? 0 : viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return (viewList.get(position));
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        object=null;
    }
}
