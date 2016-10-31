package app.zengpu.com.myexercisedemo.demolist.recyclerViewPager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
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

    private Context context;
    private int mScreenWidth = 0;
    private int maxVisableCount = 7;

    private IndicatorAdapter adapter;
    private List<Drawable> indicatorIconList = new ArrayList<>();

    private List<AnimatorSet> animatorSetList = new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;

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
        this.context = context;
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
        long time_down = 0;
        long time_move = 0;
        boolean isDoAnimation = false;

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.e("RecyclerIndicator2", "ACTION_DOWN   " + System.currentTimeMillis());
                time_down = System.currentTimeMillis();
                doTouchAnimation(targetPosition, firstVisablePosition);
//                LogUtil.e("RecyclerIndicator1", "time_down : " + time_down);
                break;
            case MotionEvent.ACTION_MOVE:
                time_move = System.currentTimeMillis() - time_down;
//                LogUtil.e("RecyclerIndicator11", "time_zero : " + (time_move - time_down) % 200);
                if (time_move <= 0)
                    isDoAnimation = false;
                else
                    isDoAnimation = time_move % (2 * adapter.getTouchAnimDuration()) <= 20;
                LogUtil.e("RecyclerIndicator2", "ACTION_MOVE " + "isDoAnimation : " + isDoAnimation);

                if (isDoAnimation && isAllAnimatorFinish()) {
                    doTouchAnimation(targetPosition, firstVisablePosition);
                }
                break;
            case MotionEvent.ACTION_UP:
                doSelectAnimation(targetPosition, firstVisablePosition);
                LogUtil.e("RecyclerIndicator2", "ACTION_UP  " + "time_move is : " + time_move);
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

        LogUtil.e("RecyclerIndicator1", "targetPosition : " + targetPosition);

        return targetPosition;
    }

    /**
     * 手指移动时的动画
     */
    private void doTouchAnimation(final int targetPosition, final int firstVisiablePosition) {
        AnimatorSet animatorSet = adapter.doTouchAnimation(targetPosition, firstVisiablePosition);
        if (null != animatorSet)
            animatorSetList.add(animatorSet);
    }

    /**
     * 选中目标后的动画
     */
    private void doSelectAnimation(final int targetPosition, final int firstVisablePosition) {

        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("RecyclerIndicator2", "###### touch animation finish ######");
                AnimatorSet animatorSet = adapter.doSelectAnimation(targetPosition, firstVisablePosition);
                if (null != animatorSet)
                    animatorSetList.add(animatorSet);
                doScrollXAnimation(targetPosition, firstVisablePosition);
            }
        }, adapter.touchAnimDuration);

    }

    public void doSelectAnimation(int selectedPosition) {
        int targetPosition = (selectedPosition + 1) % maxVisableCount;
        if (targetPosition == 0) targetPosition = maxVisableCount;
        int firstVisablePosition = linearLayoutManager.findFirstVisibleItemPosition();
        AnimatorSet animatorSet = adapter.doSelectAnimation(targetPosition, firstVisablePosition);
        if (null != animatorSet)
            animatorSetList.add(animatorSet);
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

        if (offsetX != 0) {

            final int finalOffsetX = offsetX;

            this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtil.e("RecyclerIndicator2", "-------select animation finish--------" + "  finish time " + System.currentTimeMillis());
                    removeInvalideAnim();
                    smoothScrollBy(finalOffsetX, 0);
                }
            }, adapter.selectAnimDuration + 100);
        } else {
            removeInvalideAnim();
            LogUtil.e("RecyclerIndicator2", "-------select animation finish--------" + "  finish time " + System.currentTimeMillis());
        }
    }


    /**
     * 动画是否播放完毕
     *
     * @return
     */
    private boolean isAllAnimatorFinish() {
        removeInvalideAnim();
        LogUtil.e("RecyclerIndicator2", " isAllAnimatorFinish " + (animatorSetList.size() == 0));
        return animatorSetList.size() == 0;
    }

    /**
     * 清除播放完的动画
     */
    private void removeInvalideAnim() {

        if (animatorSetList.size() == 0)
            return;

        List<AnimatorSet> temporaryList = new ArrayList<>();

        for (int i = 0; i < animatorSetList.size(); i++) {
            if (animatorSetList.get(i).isRunning()) {
                temporaryList.add(animatorSetList.get(i));
            }
        }
        animatorSetList.clear();
        animatorSetList.addAll(temporaryList);
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
        private int mIndicatorWidth = 0; // item宽度
        private int maxVisableCount = 7; // 屏幕上最大可见item个数
        private int indicatorOffsetY = 0; // 动画过程中 Y向最大偏移量
        private long touchAnimDuration = 100; // 手指触摸过程中动画持续时间
        private long selectAnimDuration = 200; // 手指抬起后动画持续时间

        private List<Drawable> indicatorIconList = new ArrayList<>();
        private List<ViewHolder> viewHolderList = new ArrayList<>();
        private Map<Integer, Float> offsetList = new HashMap<>(); // 记录每一个item的实时偏移量

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
                int iconPadding = 8;
                int rootWidth = mScreenWidth / maxVisableCount;
                indicatorOffsetY = rootWidth - rootPadding * 2;
                int rootHeight = rootWidth * 11 / 10 + indicatorOffsetY;

                // 根布局
                LinearLayout.MarginLayoutParams rootLayoutParams = (MarginLayoutParams) indicatorRootLl.getLayoutParams();
                rootLayoutParams.width = rootWidth;
                rootLayoutParams.height = rootHeight;
                rootLayoutParams.bottomMargin = -indicatorOffsetY;
                indicatorRootLl.setPadding(rootPadding, 0, rootPadding, 0);

                // 偏移量布局
                LinearLayout.LayoutParams emptyLayoutParams = new LinearLayout.LayoutParams(rootWidth, indicatorOffsetY);
                emptyLl.setLayoutParams(emptyLayoutParams);

                // icon布局
                ll_icon.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);

                // icon
                ViewGroup.LayoutParams iconLayoutparams = appIconIv.getLayoutParams();
                int iconWidth = rootWidth - rootPadding * 2 - iconPadding * 2;
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
            // 初始化时，第一个item偏移量动画
            if (position == 0) {
                Animator animator = tranYAnimation(0, -indicatorOffsetY);
                animator.setDuration(selectAnimDuration);
                animator.start();

            }
        }

        public void setMaxVisableCount(int maxVisableCount) {
            this.maxVisableCount = maxVisableCount;
        }

        // 初始化数据
        public void newData(List<Drawable> data) {
            indicatorIconList.clear();
            offsetList.clear();
            indicatorIconList.addAll(data);
            for (int i = 0; i < indicatorIconList.size(); i++) {
                offsetList.put(i, 0f);
            }
            notifyDataSetChanged();
        }

        // 添加数据
        public void addData(List<Drawable> data) {
            indicatorIconList.addAll(data);
            for (int i = 0; i < indicatorIconList.size(); i++) {
                offsetList.put(i, 0f);
            }
            notifyItemRangeChanged(indicatorIconList.size() - data.size(), data.size());
        }

        @Override
        public int getItemCount() {
            return indicatorIconList.size();
        }

        public int getmIndicatorWidth() {
            return mIndicatorWidth;
        }

        public long getTouchAnimDuration() {
            return touchAnimDuration;
        }

        public long getSelectAnimDuration() {
            return selectAnimDuration;
        }

        /**
         * 手指触摸时item的动画
         *
         * @param targetPosition        手指触摸的目标位置 [1,maxVisableCount]
         * @param firstVisiablePosition 第一个可见的item位置
         */
        public AnimatorSet doTouchAnimation(int targetPosition, int firstVisiablePosition) {
            if (viewHolderList.size() == 0)
                return null;

            float offsetY = 0;
            float maxOffset = -(float) indicatorOffsetY;

            AnimatorSet animatorSet = new AnimatorSet();
            List<Animator> animatorList = new ArrayList<>();

            LogUtil.e("RecyclerIndicator2", "###### touch animation start ######");
            LogUtil.e("RecyclerIndicator2", "targetPosition is:  " + targetPosition
                    + " firstVisiablePosition is:  " + firstVisiablePosition);

            for (int i = 1; i <= maxVisableCount; i++) {
                //边界控制
                if (firstVisiablePosition + i - 1 > indicatorIconList.size() - 1)
                    break;

                LogUtil.e("RecyclerIndicator2", "currentPosition is:  " + (firstVisiablePosition + i - 1)
                        + " offsetY " + i + " is: " + offsetList.get(firstVisiablePosition + i - 1));

                if (i <= targetPosition) {
                    offsetY = (i + maxVisableCount - targetPosition) * maxOffset / maxVisableCount;
                } else {
                    offsetY = (-i + maxVisableCount + targetPosition) * maxOffset / maxVisableCount;
                }
                if (offsetList.get(firstVisiablePosition + i - 1) != offsetY) {
                    Animator animator = tranYAnimation(firstVisiablePosition + i - 1, offsetY);
                    animatorList.add(animator);
//                    doTranYAnimation(firstVisiablePosition + i - 1, offsetY, touchAnimDuration);
                }
            }
            if (animatorList.size() != 0) {
                animatorSet.playTogether(animatorList);
                animatorSet.setDuration(touchAnimDuration);
                animatorSet.start();
            }

//            LogUtil.e("RecyclerIndicator2", "###### touch animation finish ######");

            return animatorSet;
        }

        /**
         * 手指抬起后item的动画
         *
         * @param targetPosition        手指触摸的目标位置 [1,maxVisableCount]
         * @param firstVisiablePosition 第一个可见的item位置
         */
        public AnimatorSet doSelectAnimation(int targetPosition, int firstVisiablePosition) {
            if (viewHolderList.size() == 0)
                return null;

            AnimatorSet animatorSet = new AnimatorSet();
            List<Animator> animatorList = new ArrayList<>();
            LogUtil.e("RecyclerIndicator2", "-------select animation start--------" + "  start time " + System.currentTimeMillis());
            LogUtil.e("RecyclerIndicator2", "targetPosition is:  " + targetPosition
                    + " firstVisiablePosition is:  " + firstVisiablePosition);
            for (int i = 1; i <= maxVisableCount; i++) {

                if (firstVisiablePosition + i - 1 > viewHolderList.size() - 1)
                    break;
                LogUtil.e("RecyclerIndicator2", "currentPosition is:  " + (firstVisiablePosition + i - 1)
                        + " offsetY " + i + " is: " + offsetList.get(firstVisiablePosition + i - 1));

                if (i == targetPosition
//                        && offsetList.get(firstVisiablePosition + i - 1) > -indicatorOffsetY
                        ) {
                    Animator animator = tranYAnimation(firstVisiablePosition + i - 1, -indicatorOffsetY);
                    animatorList.add(animator);
//                    doTranYAnimation(firstVisiablePosition + i - 1, -indicatorOffsetY, selectAnimDuration);

                } else if (i != targetPosition
//                        && offsetList.get(firstVisiablePosition + i - 1) < 0
                        ) {

                    Animator animator = tranYAnimation(firstVisiablePosition + i - 1, 0);
                    animatorList.add(animator);
//                    doTranYAnimation(firstVisiablePosition + i - 1, 0, selectAnimDuration);
                }
            }

            if (animatorList.size() != 0) {
                animatorSet.playTogether(animatorList);
                animatorSet.setDuration(selectAnimDuration);
                animatorSet.start();
            }
//            LogUtil.e("RecyclerIndicator2", "-------select animation finish--------");
            return animatorSet;
        }

        /**
         * Y向偏移属性动画
         *
         * @param position item位置
         * @param end      结束位置
         * @param duration 持续时间
         */
        private void doTranYAnimation(final int position, final float end, long duration) {

            // 找到目标item
            View view = viewHolderList.get(position).ll_icon;
            // 该item上一次动画结束后的偏移量
            float start = offsetList.get(position);
            // 本次动画过程中的实时偏移量
            final float[] current = {start};

            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", start, end);
            animator.setDuration(duration);
            animator.start();

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // 动画结束，记录本次的偏移量
                    offsetList.put(position, current[0]);

                }
            });

            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // 实时更新的偏移量
                    current[0] = (float) animation.getAnimatedValue("translationY");
//                    offsetList.put(position, current[0]);
                }
            });
        }

        /**
         * Y向偏移属性动画
         *
         * @param position item位置
         * @param end      结束位置
         */
        private Animator tranYAnimation(final int position, final float end) {

            // 找到目标item
            View view = viewHolderList.get(position).ll_icon;
            // 该item上一次动画结束后的偏移量
            float start = offsetList.get(position);
            // 本次动画过程中的实时偏移量
            final float[] current = {start};

            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", start, end);
//            animator.setDuration(duration);

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // 动画啊结束，记录本次的偏移量
                    offsetList.put(position, current[0]);
                    LogUtil.e("RecyclerIndicator2", "finish offsetY " + position + " is: " + (int) current[0]
                            + "      end is : " + (int) end + "  end time " + System.currentTimeMillis());
                }
            });

            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // 实时更新的偏移量
                    current[0] = (float) animation.getAnimatedValue("translationY");
//                    offsetList.put(position, current[0]);
                }
            });

            return animator;
        }
    }

}
