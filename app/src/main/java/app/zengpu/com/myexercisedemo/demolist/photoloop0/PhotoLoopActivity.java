package app.zengpu.com.myexercisedemo.demolist.photoloop0;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import app.zengpu.com.myexercisedemo.R;

public class PhotoLoopActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ImageHandler handler = new ImageHandler(new WeakReference<>(this));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tupianlunbo_layout);

        viewPager = (ViewPager)findViewById(R.id.tupianlunbo_viewpager);
        LayoutInflater inflater = LayoutInflater.from(this);
        ImageView view1 = (ImageView) inflater.inflate(R.layout.item,null);
        ImageView view2 = (ImageView) inflater.inflate(R.layout.item,null);
        ImageView view3 = (ImageView) inflater.inflate(R.layout.item,null);
        view1.setImageResource(R.drawable.picture4);
        view2.setImageResource(R.drawable.picture5);
        view3.setImageResource(R.drawable.picture6);
//        view1.setBackground(getResources().getDrawable(R.drawable.picture4));
//        view2.setBackground(getResources().getDrawable(R.drawable.picture5));
//        view3.setBackground(getResources().getDrawable(R.drawable.picture6));

        ArrayList<ImageView> views = new ArrayList<>();
        views.add(view1);
        views.add(view2);
        views.add(view3);

        viewPager.setAdapter(new ImageAdapter(views));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            // 配合Adapter的currentItem字段进行设置。当播放到新的页面后，页码发生变化，
            // 通过sendMessage发送MSG_PAGE_CHANGED消息和最新的currentItem（即arg1字段），
            // handler接收到消息后更新最新的currentItem = msg.arg1;
            @Override
            public void onPageSelected(int position) {

                Log.d("tupianlunbo", "position is :" + position);
                // public static Message obtain(Handler h, int what, int arg1, int arg2) {
                //    Message m = obtain();
                //    m.target = h;
                //    m.what = what;
                //    m.arg1 = arg1;   arg1 = position = currentItem;
                //    m.arg2 = arg2;
                //    return m;
                // }
                handler.sendMessage(Message.obtain(handler, ImageHandler.MSG_PAGE_CHANGED,
                        position, 0));
            }

            //覆写该方法实现轮播效果的暂停和恢复。
            // Called when the scroll state changes. Useful for discovering when the user
            // begins dragging, when the pager is automatically settling to the current page,
            // or when it is fully stopped/idle.
            @Override
            public void onPageScrollStateChanged(int state) {

                switch (state) {
                    //Indicates that the pager is currently being dragged by the user.
                    //手动播放时，发送消息，请求暂停自动轮播
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        handler.sendEmptyMessage(ImageHandler.MSG_KEEP_SILENT);
                        break;
                    //Indicates that the pager is in an idle, settled state.
                    //The current page is fully in view and no animation is in progress.
                    //停止手动播放后，当页面进入idle状态时，发送消息请求恢复轮播
                    case ViewPager.SCROLL_STATE_IDLE:
//                        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE,
//                                ImageHandler.MSG_DELAY);
                        handler.sendEmptyMessage(ImageHandler.MSG_BREAK_SILENT);
                        break;
                    default:
                        break;
                }
            }
        });
        //将currentItem初始状态设置到中间位置
        viewPager.setCurrentItem(Integer.MAX_VALUE / 2);
        Log.d("tupianlunbo","position at fist is :"+ viewPager.getCurrentItem());

        //bug
        handler.sendEmptyMessageDelayed(ImageHandler.MSG_KEEP_SILENT,
                ImageHandler.MSG_DELAY);

        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE,
                ImageHandler.MSG_DELAY);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    /**
     * viewPager的adapter
     */
    private class ImageAdapter extends PagerAdapter {

        private ArrayList<ImageView> viewList;

        public ImageAdapter(ArrayList<ImageView> viewList) {
            this.viewList = viewList;
        }

        @Override
        public int getCount() {
            // 设置成最大，使用户看不到边界，
            // 这个值直接关系到ViewPager的“边界”，因此当我们把它设置为Integer.MAX_VALUE之后，
            // 用户基本就看不到这个边界了，通常情况下设置为100倍实际内容个数也是可以

            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            // 不要在这里调用removeView；在instantiateItem()方法中已经处理了remove的逻辑
            // super.destroyItem(container, position, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //对ViewPager页号求余，取出View列表中要显示的项。
            //java里 -1%5=-1
            // 考虑用户向左滑的情形，则position可能会出现负值。
            position %= viewList.size();
            if (position < 0) {
                position = viewList.size() + position;
            }

            ImageView view = viewList.get(position);

            //如果View已经在之前添加到了父组件(container)，则必须先remove，
            // 否则会抛出IllegalStateException。
            // 假设一共有三个view，则当用户滑到第四个的时候就会触发这个异常，
            // 原因是我们试图把一个有父组件的View添加到另一个组件
            ViewParent viewParent =view.getParent();
            if (viewParent != null) {
                ViewGroup parent = (ViewGroup)viewParent;
                parent.removeView(view);
            }

            container.addView(view);
            return  view;
        }
    }

    /**
     * 在handler中通过发送带延迟的消息实现处理图片的自动轮播。
     */
    private  static class ImageHandler extends Handler {

        /**
         *  请求更新下一张要显示的View
         */
        protected static final int MSG_UPDATE_IMAGE = 1;

        /**
         * 请求暂停轮播
         */
        protected static final int MSG_KEEP_SILENT = 2;

        /**
         * 请求恢复轮播
         */
        protected static final int MSG_BREAK_SILENT = 3;

        /**
         * 记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。
         * 例如当前如果在第一页，本来准备播放的是第二页，而这时候用户滑动到了末页，
         * 则应该播放的是第一页，如果继续按照原来的第二页播放，则逻辑上有问题。
         */
        protected static final int MSG_PAGE_CHANGED = 4;

        /**
         * 轮播的时间间隔
         */
        protected static final long MSG_DELAY = 3000;

        /**
         * 使用弱引用避免Handler泄露.这里的泛型参数可以是Activity，Fragment等
         */
        private WeakReference<PhotoLoopActivity> weakReference;

        private int currentItem = 0;

        public ImageHandler(WeakReference<PhotoLoopActivity> wk) {

            weakReference =wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            PhotoLoopActivity activity = weakReference.get();
            if (activity == null) {
                // 如果Activity已经回收，无需再处理UI
                return;
            }

            //检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题。
            //这段会把第一次的自动轮播事件吃掉,所以需要加个条件,Position!=Max/2的时候才清除事件.
            //因为第一次Position一定等于Max/2
            Log.d("tupianlunbo","hasMessages :" + activity.handler.hasMessages(MSG_UPDATE_IMAGE));

            if (activity.handler.hasMessages(MSG_UPDATE_IMAGE)) {

                if(msg.arg1!=Integer.MAX_VALUE/2) {
                    activity.handler.removeMessages(MSG_UPDATE_IMAGE);
                }
            }

            switch (msg.what) {

                case MSG_PAGE_CHANGED:
                    //记录当前的页号，避免播放时页面显示不正确
                    currentItem = msg.arg1;
                    break;

                case MSG_UPDATE_IMAGE:
                    currentItem++;
                    activity.viewPager.setCurrentItem(currentItem);
                    //准备下次播放
                    activity.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE,MSG_DELAY);
                    break;

                case MSG_KEEP_SILENT:
                    //暂停轮播，只要不发送消息就暂停
                    break;

                case MSG_BREAK_SILENT:
                    //恢复轮播
                    activity.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE,MSG_DELAY);
                    break;

                default:
                    break;
            }
        }
    }
}
