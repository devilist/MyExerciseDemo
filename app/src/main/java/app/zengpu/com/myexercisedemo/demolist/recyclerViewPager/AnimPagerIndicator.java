package app.zengpu.com.myexercisedemo.demolist.recyclerViewPager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
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
 * Created by zengpu on 2016/11/3.
 */

public class AnimPagerIndicator extends LinearLayout
//        implements
{

    private Context context;
    private int mScreenWidth = 0;
    private int mVisableCount = 7;
    private int mItemWidth = mScreenWidth / mVisableCount; // item宽度
    private int mItemOffsetY = 0;

    private long mTouchAnimDuration = 100; // 手指触摸过程中动画持续时间
    private long mSelectAnimDuration = 200; // 手指抬起后Y向动画持续时间

    private List<View> mItemViewList = new ArrayList<>();
    private List<AnimatorSet> mAnimatorSetList = new ArrayList<>();
    private Map<Integer, Float> mItemOffsetList = new HashMap<>(); // 记录每一个item的实时偏移量
    private float indicatorOffsetX = 0f;

    private RecyclerView viewPager;


    public AnimPagerIndicator(Context context) {
        this(context, null);
    }

    public AnimPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mVisableCount = 7;
        mItemWidth = mScreenWidth / mVisableCount;
        setOrientation(LinearLayout.HORIZONTAL);
//        setOnTouchListener(this);
    }

    public void setData(List<Drawable> indicatorIconList) {
        if (this.getChildCount() != 0) {
            this.removeAllViews();
            mItemViewList.clear();
            mItemOffsetList.clear();
        }

        for (int i = 0; i < indicatorIconList.size(); i++) {
            LinearLayout itemView = createIndicatorItem(indicatorIconList.get(i));
            this.addView(itemView);
            mItemViewList.add(itemView.getChildAt(1));
            mItemOffsetList.put(i, 0f);
        }
        LinearLayout.MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;
        params.bottomMargin = -mItemOffsetY;

        this.invalidate();

//        setItemClickEvent();
        Animator animator = tranYAnimation(0, -mItemOffsetY, mSelectAnimDuration);
        animator.start();
    }

    public void addData(List<Drawable> indicatorIconList) {

        for (int i = 0; i < indicatorIconList.size(); i++) {
            LinearLayout itemView = createIndicatorItem(indicatorIconList.get(i));
            this.addView(itemView);
            mItemViewList.add(itemView.getChildAt(1));
            mItemOffsetList.put(i, 0f);
        }
        this.invalidate();

    }

    public void setReyclerViewPager(final RecyclerView viewPager) {
        this.viewPager = viewPager;

//        this.viewPager.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE && isIndicatorScroll) {
//                    LinearLayoutManager layoutManager = (LinearLayoutManager) viewPager.getLayoutManager();
//                    doSelectAnimation(layoutManager.findFirstVisibleItemPosition());
//                }
//            }
//        });
    }

    /**
     * 创建indicator item
     *
     * @param iconDrawable
     * @return
     */
    private LinearLayout createIndicatorItem(Drawable iconDrawable) {
        // 根布局
        LinearLayout ll_item = new LinearLayout(context);
        ll_item.setOrientation(LinearLayout.VERTICAL);
//        ll_item.setClickable(true);
        // 子布局，Y偏移量
        LinearLayout ll_sub_offset = new LinearLayout(context);
        ll_sub_offset.setOrientation(LinearLayout.VERTICAL);
        // 子布局，icon
        LinearLayout ll_sub_icon = new LinearLayout(context);
        ll_sub_icon.setOrientation(LinearLayout.VERTICAL);
        ll_sub_icon.setGravity(Gravity.CENTER_HORIZONTAL);
        ll_sub_icon.setBackground(context.getResources().getDrawable(R.drawable.shape_rvp_indicator_bg));

        ImageView iv_icon = new ImageView(context);

        ll_item.addView(ll_sub_offset);
        ll_item.addView(ll_sub_icon);
        ll_sub_icon.addView(iv_icon);

        int rootPadding = 6;
        int iconPadding = 8;
        mItemOffsetY = mItemWidth - rootPadding * 2 - iconPadding;
        int mItemHeight = mItemWidth * 11 / 10 + mItemOffsetY;

        // 根布局
        LinearLayout.LayoutParams rootLayoutParams = new LinearLayout.LayoutParams(mItemWidth, mItemHeight);
        ll_item.setLayoutParams(rootLayoutParams);
        ll_item.setPadding(rootPadding, 0, rootPadding, 0);

        // 偏移量布局
        LinearLayout.LayoutParams offsetLayoutParams = new LinearLayout.LayoutParams(mItemWidth - rootPadding * 2, mItemOffsetY);
        ll_sub_offset.setLayoutParams(offsetLayoutParams);

        // icon布局
        LinearLayout.LayoutParams iconLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ll_sub_icon.setLayoutParams(iconLayoutParams);
        ll_sub_icon.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);

        // icon
        ViewGroup.LayoutParams iconLayoutparams = iv_icon.getLayoutParams();
        int iconWidth = mItemWidth - rootPadding * 2 - iconPadding * 2;
        iconLayoutparams.width = iconWidth;
        iconLayoutparams.height = iconWidth;
        iv_icon.setLayoutParams(iconLayoutparams);
        iv_icon.setImageDrawable(iconDrawable);

        return ll_item;
    }

    private void setItemClickEvent() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final int targetPosition = i;
            getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    doSelectAnimation(targetPosition);
                }
            });

        }
    }

    private long last_action_event_time = 0;

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        float x_offset = e.getX();
        int targetPosition = cumputeTargetPositionFromOffsetX(x_offset);
        long action_time = 0;
        boolean isDoAnimation = false;

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isIndicatorScroll = false;
                long second_action_event_time = System.currentTimeMillis();
                if (last_action_event_time > 0) {
                    if (second_action_event_time - last_action_event_time < 500) {
                        last_action_event_time = second_action_event_time;
                        return false;
                    } else {
                        doTouchAnimation(targetPosition);
                    }
                } else {
                    doTouchAnimation(targetPosition);
                }

                LogUtil.e("AnimPagerIndicator2", "ACTION_DOWN   " + System.currentTimeMillis());
                break;
            case MotionEvent.ACTION_MOVE:
                action_time = e.getEventTime() - e.getDownTime();
                LogUtil.e("AnimPagerIndicator2", "time_move : " + action_time);
                if (action_time < mTouchAnimDuration)
                    isDoAnimation = false;
                else
                    isDoAnimation = action_time % (2 * mTouchAnimDuration) <= 20;
                LogUtil.e("AnimPagerIndicator2", "ACTION_MOVE " + "isDoAnimation : " + isDoAnimation);

                if (isDoAnimation && isAllAnimatorFinish()) {
                    doTouchAnimation(targetPosition);
                }
                break;
            case MotionEvent.ACTION_UP:
                last_action_event_time = System.currentTimeMillis();

                doSelectAnimationWithDelay(targetPosition);
                LogUtil.e("AnimPagerIndicator2", "ACTION_UP  " + "time_up is : " + action_time);
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
        targetPosition = (int) x_offset / mItemWidth + 1;
        if (targetPosition > mVisableCount)
            targetPosition = mVisableCount;

        LogUtil.e("AnimPagerIndicator1", "targetPosition : " + targetPosition);

        return targetPosition;
    }

    /**
     * 获得第一个可见的item位置
     *
     * @return
     */
    private int findFirstVisableItemPosition() {
        LogUtil.e("AnimPagerIndicator0", " getScrollX  " + getScrollX());
        return Math.abs((int) getScrollX() / mItemWidth);
    }

    /**
     * 手指移动时的动画
     */
    private void doTouchAnimation(final int targetPosition) {
        int firstVisablePosition = findFirstVisableItemPosition();
        AnimatorSet animatorSet = touchAnimation(targetPosition, firstVisablePosition);
        if (null != animatorSet)
            mAnimatorSetList.add(animatorSet);
    }

    /**
     * 选中目标后的动画
     */
    private void doSelectAnimationWithDelay(final int targetPosition) {

        final int firstVisablePosition = findFirstVisableItemPosition();
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("AnimPagerIndicator2", "###### touch animation finish ######");
                AnimatorSet selectAnimatorSet = selectAnimation(targetPosition, firstVisablePosition);
                if (null != selectAnimatorSet)
                    mAnimatorSetList.add(selectAnimatorSet);
                doScrollXAnimation(targetPosition);
            }
        }, mTouchAnimDuration);

    }

    public void doSelectAnimation(int selectedPosition) {
        int targetPosition = (selectedPosition + 1) % mVisableCount;
        if (targetPosition == 0) targetPosition = mVisableCount;
        int firstVisablePosition = findFirstVisableItemPosition();
        AnimatorSet selectAnimatorSet = selectAnimation(targetPosition, firstVisablePosition);
        if (null != selectAnimatorSet)
            mAnimatorSetList.add(selectAnimatorSet);
        this.doScrollXAnimation(targetPosition);

    }

    private boolean isIndicatorScroll = false;

    private void doScrollXAnimation(final int targetPosition) {


        final int itemCount = this.getChildCount();
        int midPosition = mVisableCount / 2 + 1;
        int offsetX = 0;
        int scrollCount = 0;
        final int firstVisablePosition = findFirstVisableItemPosition();
        int lastVisablePosition = firstVisablePosition + mVisableCount - 1;

        if (itemCount <= targetPosition)
            return;
        if (targetPosition < midPosition && firstVisablePosition != 0) {
            // 往右滚动
            scrollCount = Math.min(midPosition - targetPosition, firstVisablePosition);
            offsetX = -scrollCount * mItemWidth;
        }
        if (targetPosition > midPosition && lastVisablePosition != itemCount - 1) {
            // 往左滑动
            scrollCount = Math.min(targetPosition - midPosition, itemCount - lastVisablePosition - 1);
            offsetX = scrollCount * mItemWidth;
        }

        if (offsetX != 0) {

            final int finalOffsetX = offsetX;
            this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    removeInvalideAnim();
                    scrollBy(finalOffsetX, 0);
                    invalidate();
                    isIndicatorScroll = true;
                    viewPager.smoothScrollToPosition(firstVisablePosition + targetPosition - 1);
                    LogUtil.e("AnimPagerIndicator2", "firstVisiablePosition is:  " + firstVisablePosition);
                    LogUtil.e("AnimPagerIndicator2", "-------select animation finish--------" + "  finish time " + System.currentTimeMillis());
                }
            }, mSelectAnimDuration + 100);
        } else {
            removeInvalideAnim();
            isIndicatorScroll = true;
            viewPager.smoothScrollToPosition(firstVisablePosition + targetPosition - 1);
            LogUtil.e("AnimPagerIndicator2", "-------select animation finish--------" + "  finish time " + System.currentTimeMillis());
        }
    }

    /**
     * 动画是否播放完毕
     *
     * @return
     */
    private boolean isAllAnimatorFinish() {
        removeInvalideAnim();
        LogUtil.e("AnimPagerIndicator2", " isAllAnimatorFinish " + (mAnimatorSetList.size() == 0));
        return mAnimatorSetList.size() == 0;
    }


    /**
     * 清除播放完的动画
     */
    private void removeInvalideAnim() {

        if (mAnimatorSetList.size() == 0)
            return;

        List<AnimatorSet> temporaryList = new ArrayList<>();

        for (int i = 0; i < mAnimatorSetList.size(); i++) {
            if (mAnimatorSetList.get(i).isRunning()) {
                temporaryList.add(mAnimatorSetList.get(i));
            }
        }
        mAnimatorSetList.clear();
        mAnimatorSetList.addAll(temporaryList);
    }


    /**
     * 手指触摸时item的动画
     *
     * @param targetPosition        手指触摸的目标位置 [1,maxVisableCount]
     * @param firstVisiablePosition 第一个可见的item位置
     */
    public AnimatorSet touchAnimation(int targetPosition, final int firstVisiablePosition) {
        if (mItemViewList.size() == 0)
            return null;

        float offsetY = 0;
        float maxOffset = -(float) mItemOffsetY;

        AnimatorSet animatorSet = new AnimatorSet();
        List<Animator> animatorList = new ArrayList<>();

        LogUtil.e("AnimPagerIndicator2", "###### touch animation start ######");
        LogUtil.e("AnimPagerIndicator2", "targetPosition is:  " + targetPosition
                + " firstVisiablePosition is:  " + firstVisiablePosition);

        for (int i = 1; i <= mVisableCount; i++) {
            //边界控制
            if (firstVisiablePosition + i - 1 > mItemViewList.size() - 1)
                break;

            LogUtil.e("AnimPagerIndicator2", "currentPosition is:  " + (firstVisiablePosition + i - 1)
                    + " offsetY " + i + " is: " + mItemOffsetList.get(firstVisiablePosition + i - 1));

            if (i <= targetPosition) {
                offsetY = (i + mVisableCount - targetPosition) * maxOffset / mVisableCount;
            } else {
                offsetY = (-i + mVisableCount + targetPosition) * maxOffset / mVisableCount;
            }
            if (mItemOffsetList.get(firstVisiablePosition + i - 1) != offsetY) {
                Animator animator = tranYAnimation(firstVisiablePosition + i - 1, offsetY, mTouchAnimDuration);
                animatorList.add(animator);
            }
        }
        if (animatorList.size() != 0) {
            animatorSet.playTogether(animatorList);
            animatorSet.setDuration(mTouchAnimDuration);
            animatorSet.start();
        }
        return animatorSet;
    }

    /**
     * 手指抬起后item的动画
     *
     * @param targetPosition        手指触摸的目标位置 [1,maxVisableCount]
     * @param firstVisiablePosition 第一个可见的item位置
     */
    public AnimatorSet selectAnimation(int targetPosition, final int firstVisiablePosition) {
        if (mItemViewList.size() == 0)
            return null;

        AnimatorSet animatorSet = new AnimatorSet();
        List<Animator> animatorList = new ArrayList<>();
        LogUtil.e("AnimPagerIndicator2", "-------select animation start--------" + "  start time " + System.currentTimeMillis());
        LogUtil.e("AnimPagerIndicator2", "targetPosition is:  " + targetPosition
                + " firstVisiablePosition is:  " + firstVisiablePosition);
        for (int i = 1; i <= mVisableCount; i++) {

            if (firstVisiablePosition + i - 1 > mItemViewList.size() - 1)
                break;
            LogUtil.e("AnimPagerIndicator2", "currentPosition is:  " + (firstVisiablePosition + i - 1)
                    + " offsetY " + i + " is: " + mItemOffsetList.get(firstVisiablePosition + i - 1));

            if (i == targetPosition && mItemOffsetList.get(firstVisiablePosition + i - 1) > -mItemOffsetY) {
                Animator animator = tranYAnimation(firstVisiablePosition + i - 1, -mItemOffsetY, mSelectAnimDuration);
                animatorList.add(animator);

            } else if (i != targetPosition && mItemOffsetList.get(firstVisiablePosition + i - 1) < 0) {

                Animator animator = tranYAnimation(firstVisiablePosition + i - 1, 0, mSelectAnimDuration);
                animatorList.add(animator);
            }
        }

        if (animatorList.size() != 0) {
            animatorSet.playTogether(animatorList);
            animatorSet.setDuration(mSelectAnimDuration);
            animatorSet.start();


        }
        return animatorSet;
    }


    /**
     * Y向偏移属性动画
     *
     * @param position item位置
     * @param end      结束位置
     */
    private Animator tranYAnimation(final int position, final float end, long duration) {

        // 找到目标item
        final View view = mItemViewList.get(position);
        // 该item上一次动画结束后的偏移量
        float start = mItemOffsetList.get(position);
        // 本次动画过程中的实时偏移量
        final float[] current = {start};

        final ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", start, end);
        animator.setDuration(duration);

        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画啊结束，记录本次的偏移量
                mItemOffsetList.put(position, current[0]);
                LogUtil.e("AnimPagerIndicator2", "finish offsetY " + position + " is: " + (int) current[0]
                        + "  end is : " + (int) end
                        + "  view transY is: " + view.getTranslationY()
                        + "  view Y is: " + view.getY()
                        + "  end time " + System.currentTimeMillis());
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 实时更新的偏移量
                current[0] = (float) animation.getAnimatedValue("translationY");
            }
        });

        return animator;
    }

    /**
     * Y向偏移属性动画
     *
     * @param end 结束位置
     */
    private Animator tranXAnimation(final View view, final float end, long duration) {

        // 找到目标item
        // 该item上一次动画结束后的偏移量
        // 本次动画过程中的实时偏移量
        final float[] current = {indicatorOffsetX};

        final ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", indicatorOffsetX, end);
        animator.setDuration(duration);

        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画啊结束，记录本次的偏移量
                indicatorOffsetX = current[0];
                LogUtil.e("AnimPagerIndicator3", "end time " + System.currentTimeMillis());
                LogUtil.e("AnimPagerIndicator3", "indicatorOffsetX " + indicatorOffsetX);
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 实时更新的偏移量
                current[0] = (float) animation.getAnimatedValue("translationX");
            }
        });
        animator.start();

        return animator;
    }


}
