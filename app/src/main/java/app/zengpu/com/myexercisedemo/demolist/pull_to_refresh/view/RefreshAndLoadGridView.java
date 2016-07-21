package app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.view.core.RefreshAndLoadViewBase;

/**
 * Created by zengpu on 2016/7/21.
 */
public class RefreshAndLoadGridView extends RefreshAndLoadViewBase<GridView>{

    public RefreshAndLoadGridView(Context context) {
        super(context);
    }

    public RefreshAndLoadGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshAndLoadGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void setContentViewScrollListener() {
        mContentView = (GridView) getChildAt(2);

        mContentView.setNumColumns(3);
        mContentView.setHorizontalSpacing(8);
        mContentView.setVerticalSpacing(8);
        // 设置滚动监听器
        mContentView.setOnScrollListener(this);

    }

    @Override
    protected boolean isTop() {
        return mContentView.getFirstVisiblePosition() == 0
                && getScrollY() <= mHeaderView.getMeasuredHeight();
    }

    @Override
    protected boolean isBottom() {
        return mContentView != null && mContentView.getAdapter() != null
                && mContentView.getLastVisiblePosition() ==
                mContentView.getAdapter().getCount() - 1;
    }

    @Override
    protected boolean iscontentViewCompletelyShow() {
        return mContentView != null && mContentView.getAdapter() != null
                && mContentView.getFirstVisiblePosition() == 0
                && mContentView.getLastVisiblePosition() == mContentView.getAdapter().getCount() - 1;
    }
}
