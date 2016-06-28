package app.zengpu.com.myexercisedemo.demolist.photoloop1;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import java.util.ArrayList;

import app.zengpu.com.myexercisedemo.R;

/**
 * 图片轮播
 * Created by zengpu on 16/3/30.
 */
public class ImageLoopActivity extends AppCompatActivity {

    private ImageLoopViewPager mImageLoopViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_loop_layout);
        mImageLoopViewPager = (ImageLoopViewPager)findViewById(R.id.image_loop_viewpager);

        LayoutInflater inflater = LayoutInflater.from(this);
        ImageView view1 = (ImageView) inflater.inflate(R.layout.item,null);
        ImageView view2 = (ImageView) inflater.inflate(R.layout.item,null);
        ImageView view3 = (ImageView) inflater.inflate(R.layout.item,null);
        view1.setImageResource(R.drawable.picture4);
        view2.setImageResource(R.drawable.picture5);
        view3.setImageResource(R.drawable.picture6);

        ArrayList<ImageView> views = new ArrayList<>();
        views.add(view1);
        views.add(view2);
        views.add(view3);

        PagerAdapter adapter = new MyImageLoopPagerAdapter(mImageLoopViewPager, views);
        mImageLoopViewPager.setAdapter(adapter);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mImageLoopViewPager.setLifeCycle(ImageLoopViewPager.RESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageLoopViewPager.setLifeCycle(ImageLoopViewPager.PAUSE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageLoopViewPager.setLifeCycle(ImageLoopViewPager.DESTROY);
    }

    private static class MyImageLoopPagerAdapter extends  ImageLoopPagerAdapter {

        private ArrayList<ImageView> viewList;

        public MyImageLoopPagerAdapter(ImageLoopViewPager viewPager, ArrayList<ImageView> viewList) {
            super(viewPager);
            this.viewList = viewList;
        }

        @Override
        public int getRealCount() {
            return viewList != null ? viewList.size() :0;
        }

        @Override
        public Object instantiateRealItem(ViewGroup container, int position) {

            ImageView view = viewList.get(position);

            ViewParent viewParent =view.getParent();
            if (viewParent != null) {
                ViewGroup parent = (ViewGroup)viewParent;
                parent.removeView(view);
            }
            container.addView(view);
            return view;
        }
    }


}
