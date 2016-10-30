package app.zengpu.com.myexercisedemo.demolist.recyclerViewPager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.Utils.LogUtil;

/**
 * 翻页指示器
 * Created by zengpu on 16/10/30.
 */

public class RecyclerIndicator extends RecyclerView {

    private int mScreenWidth = 0;
    private int maxVisableCount = 7;

    private IndicatorAdapter adapter;
    private List<Drawable> indicatorIconList = new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;

    private Handler animHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 0) {

            }
        }
    };


    public RecyclerIndicator(Context context) {
        super(context);
        init(context);
    }

    public RecyclerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecyclerIndicator(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;

        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(linearLayoutManager);

        adapter = new IndicatorAdapter(context);
        setAdapter(adapter);

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        float x_offset = e.getX();
        int targetPosition = cumputeTargetPositionFromOffsetX(x_offset);
        int firstVisablePosition = linearLayoutManager.findFirstVisibleItemPosition();
        LogUtil.e("RecyclerIndicator", "x_offset : " + x_offset);
        int time_down = 0;
        int time_move = 0;

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                doTouchAnimation(targetPosition, firstVisablePosition);
                time_down = (int) e.getDownTime();
                LogUtil.e("RecyclerIndicator1", "time_down : " + time_down);
                break;
            case MotionEvent.ACTION_MOVE:
                time_move = (int) e.getEventTime();
                LogUtil.e("RecyclerIndicator1", "time_move : " + time_move);
                LogUtil.e("RecyclerIndicator11", "%%%%% : " + (time_move - time_down) % 100);
                if ((time_move - time_down) % 200 <= 20)
                    doTouchAnimation(targetPosition, firstVisablePosition);
                break;
            case MotionEvent.ACTION_UP:
                LogUtil.e("RecyclerIndicator", "ACTION_UP");
                doSelectAnimation(targetPosition, firstVisablePosition);
                break;
        }

        return true;
    }


    /**
     * 通过手指在屏幕的位置寻找目标item
     *
     * @param x_offset
     * @return [1 , maxVisableCount]
     */
    private int cumputeTargetPositionFromOffsetX(float x_offset) {

        // 目标位置
        int targetPosition = 1;

        // 单个indicator的宽度
        int indicatorWidth = mScreenWidth / maxVisableCount;

        targetPosition = (int) x_offset / indicatorWidth + 1;
        if (targetPosition > maxVisableCount)
            targetPosition = maxVisableCount;

        LogUtil.e("RecyclerIndicator", "targetPosition : " + targetPosition);

        return targetPosition;
    }

    /**
     * 手指移动时的动画
     */
    private void doTouchAnimation(int targetPosition, int firstVisiablePosition) {
        adapter.doTouchAnimation(targetPosition, firstVisiablePosition);
    }

    /**
     * 选中目标后的动画
     */
    private void doSelectAnimation(int targetPosition, int firstVisiablePosition) {
        adapter.doSelectAnimation(targetPosition, firstVisiablePosition);
        this.doScrollXAnimation(targetPosition, firstVisiablePosition);

    }

    public void doSelectAnimation(int selectedPosition) {
        int targetPosition = (selectedPosition + 1) % maxVisableCount;
        if (targetPosition == 0) targetPosition = maxVisableCount;
        int firstVisablePosition = linearLayoutManager.findFirstVisibleItemPosition();
        this.doSelectAnimation(targetPosition, firstVisablePosition);
        this.doScrollXAnimation(targetPosition, firstVisablePosition);

    }

    private void doScrollXAnimation(int targetPosition, int firstVisiablePosition) {

        int itemCount = adapter.getItemCount();
        int midPosition = maxVisableCount / 2 + 1;
        int offsetX = 0;

        int lastVisablePosition = linearLayoutManager.findLastVisibleItemPosition();

        if (itemCount <= targetPosition)
            return;
        if (targetPosition < midPosition && firstVisiablePosition != 0) {
            // 往右滚动
            int scrollCount = Math.min(midPosition - targetPosition, firstVisiablePosition);
            offsetX = -scrollCount * adapter.getmIndicatorWidth();
        }
        if (targetPosition > midPosition && lastVisablePosition != itemCount - 1) {
            // 往左滑动
            int scrollCount = Math.min(targetPosition - midPosition, itemCount - lastVisablePosition - 1);
            offsetX = scrollCount * adapter.getmIndicatorWidth();
        }
        if (offsetX != 0)
            smoothScrollBy(offsetX, 0);

    }


    public void setMaxVisableCount(int maxVisableCount) {
        this.maxVisableCount = maxVisableCount;
        adapter.setMaxVisableCount(maxVisableCount);
    }

    public void setIcon(List<Drawable> indicatorIconList) {
        this.indicatorIconList.clear();
        this.indicatorIconList = indicatorIconList;
        adapter.newData(indicatorIconList);

    }

    public void addIcon(List<Drawable> indicatorIconList) {
        this.indicatorIconList.addAll(indicatorIconList);
        adapter.addData(indicatorIconList);
    }

    private static class IndicatorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private int mScreenWidth = 0;
        private int mIndicatorWidth = 0;
        private int maxVisableCount = 7;
        private int indicatorOffsetY = 0;
        private List<Drawable> indicatorIconList = new ArrayList<>();
        private List<ViewHolder> viewHolderList = new ArrayList<>();
        private Map<Integer, Float> offsetList = new HashMap<>();

        public class ViewHolder extends RecyclerView.ViewHolder {

            private LinearLayout indicatorRootLl;
            private LinearLayout ll_icon;
            private ImageView appIconIv;
            private LinearLayout emptyLl;

            public ViewHolder(View itemView) {
                super(itemView);
                indicatorRootLl = (LinearLayout) itemView.findViewById(R.id.ll_indicator_item_root);
                ll_icon = (LinearLayout) itemView.findViewById(R.id.ll_icon);
                appIconIv = (ImageView) itemView.findViewById(R.id.iv_app_icon);
                emptyLl = (LinearLayout) itemView.findViewById(R.id.ll_empty);

                int rootPadding = 6;
                int rootWidth = mScreenWidth / maxVisableCount;
                indicatorOffsetY = rootWidth - rootPadding * 2;
                int rootHeight = rootWidth * 11 / 10 + indicatorOffsetY;

                LinearLayout.MarginLayoutParams rootLayoutParams = (MarginLayoutParams) indicatorRootLl.getLayoutParams();
                rootLayoutParams.width = rootWidth;
                rootLayoutParams.height = rootHeight;
                rootLayoutParams.bottomMargin = -indicatorOffsetY;
                indicatorRootLl.setPadding(rootPadding, 0, rootPadding, 0);

                LinearLayout.LayoutParams emptyLayoutParams = new LinearLayout.LayoutParams(rootWidth, indicatorOffsetY);
                emptyLl.setLayoutParams(emptyLayoutParams);

                ViewGroup.LayoutParams iconLayoutparams = appIconIv.getLayoutParams();
                int iconWidth = rootWidth - rootPadding * 2;
                iconLayoutparams.width = iconWidth;
                iconLayoutparams.height = iconWidth;
                appIconIv.setLayoutParams(iconLayoutparams);

                mIndicatorWidth = rootWidth;

            }
        }

        public IndicatorAdapter(Context context) {
            this.context = context;
            mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        }

        public IndicatorAdapter(Context context, List<Drawable> indicatorIconList) {
            this.context = context;
            this.indicatorIconList = indicatorIconList;
        }

        public IndicatorAdapter(Context context, List<Drawable> indicatorIconList, int maxVisableCount) {
            this.context = context;
            this.indicatorIconList = indicatorIconList;
            this.maxVisableCount = maxVisableCount;
        }

        @Override

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.rvp_indicator_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolderList.add(viewHolder);
            viewHolder.appIconIv.setImageDrawable(indicatorIconList.get(position));
            if (position == 0)
                doTranYAnimation(0, -indicatorOffsetY, 600);

        }


        public void setMaxVisableCount(int maxVisableCount) {
            this.maxVisableCount = maxVisableCount;
        }

        public void newData(List<Drawable> data) {
            indicatorIconList.clear();
            offsetList.clear();
            indicatorIconList.addAll(data);
            for (int i = 0; i < indicatorIconList.size(); i++) {
                offsetList.put(i, 0f);
            }

            notifyDataSetChanged();
        }

        public void addData(List<Drawable> data) {
            indicatorIconList.addAll(data);

            for (int i = 0; i < indicatorIconList.size(); i++) {
                offsetList.put(i, 0f);
            }

            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return indicatorIconList.size();
        }

        public int getmIndicatorWidth() {
            return mIndicatorWidth;
        }

        public void doTouchAnimation(int targetPosition, int firstVisiablePosition) {
            if (viewHolderList.size() == 0)
                return;

            float offsetY = 0;
            float maxOffset = -(float)indicatorOffsetY;

            for (int i = 1; i <= maxVisableCount; i++) {
                //边界控制
                if (firstVisiablePosition + i - 1 > indicatorIconList.size() - 1)
                    return;

                if (i <= targetPosition) {
                    offsetY = (i + maxVisableCount - targetPosition) * maxOffset / maxVisableCount;
                } else {
                    offsetY = (-i + maxVisableCount + targetPosition) * maxOffset / maxVisableCount;
                }
                if (offsetList.get(firstVisiablePosition + i - 1) != offsetY)
                    doTranYAnimation(firstVisiablePosition + i - 1, offsetY, 100);
            }
        }


        /**
         * @param targetPosition
         * @param firstVisiablePosition
         */
        public void doSelectAnimation(int targetPosition, int firstVisiablePosition) {
            if (viewHolderList.size() == 0)
                return;



            for (int i = 1; i <= maxVisableCount; i++) {

                if (firstVisiablePosition + i - 1 > indicatorIconList.size() - 1)
                    return;

                if (i == targetPosition
                        && offsetList.get(firstVisiablePosition + i - 1) > -indicatorOffsetY) {
                    doTranYAnimation(firstVisiablePosition + i - 1, -indicatorOffsetY, 500);

                } else if (i != targetPosition && offsetList.get(firstVisiablePosition + i - 1) < 0) {
                    doTranYAnimation(firstVisiablePosition + i - 1, 0, 500);
                }
            }

        }

        private void doTranYAnimation(final int position, final float end, long duration) {

            View view = viewHolderList.get(position).ll_icon;
            float start = offsetList.get(position);

            final float[] current = {start};

            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", start, end);
            animator.setDuration(duration);
            animator.start();

            animator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    offsetList.put(position, current[0]);
                }
            });

            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    current[0] = (float) animation.getAnimatedValue("translationY");
                    offsetList.put(position, current[0]);

                }
            });
        }
    }

}
