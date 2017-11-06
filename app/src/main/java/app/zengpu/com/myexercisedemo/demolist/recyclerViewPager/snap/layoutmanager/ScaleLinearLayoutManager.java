package app.zengpu.com.myexercisedemo.demolist.recyclerViewPager.snap.layoutmanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;


/**
 * Created by zengp on 2017/10/17.
 */

public class ScaleLinearLayoutManager extends LinearLayoutManager {

    private RecyclerView mRecyclerView;

    private float mMinScale = 0.85f;

    public ScaleLinearLayoutManager(Context context, RecyclerView recyclerView) {
        super(context);
        setOrientation(HORIZONTAL);
        mRecyclerView = recyclerView;
    }

    public ScaleLinearLayoutManager(Context context, int orientation, RecyclerView recyclerView) {
        super(context);
        setOrientation(orientation);
        mRecyclerView = recyclerView;
    }

    public void setOriginalScale(float oriScale) {
        this.mMinScale = oriScale;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);

        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }

        final View child_0 = recycler.getViewForPosition(0);
        measureChildWithMargins(child_0, 0, 0);
        int childWidth = getDecoratedMeasuredWidth(child_0);
        int childHeight = getDecoratedMeasuredHeight(child_0);

        if (null != mRecyclerView) {
            final int childSizeWithMargin = getOrientation() == HORIZONTAL ? childWidth : childHeight;
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    final int recyclerViewSize = getOrientation() == HORIZONTAL ?
                            mRecyclerView.getMeasuredWidth() : mRecyclerView.getMeasuredHeight();

                    if (recyclerViewSize <= childSizeWithMargin)
                        return;

                    for (int i = 0; i < recyclerView.getChildCount(); i++) {
                        // scale child for only visible child
                        View child_i = recyclerView.getChildAt(i);
                        int offset_i = child_i.getRight();
                        int dividePoint = (childSizeWithMargin + recyclerViewSize) / 2;
                        // scale factor, nonlinear interceptor (para curve)
                        float scale_i = -(1 - mMinScale) * 1f * (float) (Math.pow((offset_i - dividePoint) * 1f / dividePoint, 2)) + 1;
                        // trans distance
                        float trans_i = 0;
                        if (offset_i <= dividePoint) {
                            // scale_i = (1 - mMinScale) * 1f * offset_i / dividePoint + mMinScale;
                            trans_i = childSizeWithMargin * (1 - scale_i) / 2;
                        } else {
                            // scale_i = -(1 - mMinScale) * 1f * offset_i / dividePoint + 2 - mMinScale;
                            trans_i = -childSizeWithMargin * (1 - scale_i) / 2;
                        }

                        // if there are only less two visible child,
                        // an extra offset is needed to make the first or last child stay in the center
                        String property = getOrientation() == HORIZONTAL ? "translationX" : "translationY";
                        float extraTrans = (recyclerViewSize - childSizeWithMargin) * 1f / 2;
                        if (findFirstCompletelyVisibleItemPosition() == 0) {
                            doTransAnimator(child_i, property, trans_i, trans_i + extraTrans);
                        } else if (findFirstCompletelyVisibleItemPosition() == recyclerView.getAdapter().getItemCount() - 1) {
                            doTransAnimator(child_i, property, trans_i, trans_i - extraTrans);
                        }

                        child_i.setScaleX(scale_i);
                        child_i.setScaleY(scale_i);
                        if (getOrientation() == HORIZONTAL)
                            child_i.setTranslationX(trans_i);
                        else
                            child_i.setTranslationY(trans_i);
                    }
                }
            });
        }
    }

    private void doTransAnimator(View target, String propertyName, float from, float end) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, propertyName, from, end);
        animator.setDuration(250);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        animator.start();
    }
}
