package app.zengpu.com.myexercisedemo.demolist.pull_to_refresh;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 *  具有下拉刷新和上拉加载功能的RecyclerView;
 * Created by tao on 2016/4/22.
 */
public class RefreshAndLoadRecyclerView extends RefreshAndLoadViewBase<RecyclerView> {

    public RefreshAndLoadRecyclerView(Context context) {
        super(context);
    }

    public RefreshAndLoadRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshAndLoadRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void setContentViewScrollListener() {
        mContentView= (RecyclerView) getChildAt(2);
        mContentView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // 用户设置了加载更多监听器，且到了最底部，并且是上拉操作，那么执行加载更多.
                if (mLoadListener != null && isBottom() && mYOffset < -mTouchSlop
                        && mCurrentStatus == STATUS_IDLE) {
                    showFooterView();
                    doLoadMore();
                }
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    @Override
    protected boolean isTop() {
        LinearLayoutManager lm= (LinearLayoutManager) mContentView.getLayoutManager();
        return lm.findFirstCompletelyVisibleItemPosition() == 0
                && getScrollY() <= mHeaderView.getMeasuredHeight();
    }

    @Override
    protected boolean isBottom() {
        LinearLayoutManager lm= (LinearLayoutManager) mContentView.getLayoutManager();
        return mContentView != null && mContentView.getAdapter() != null
                && lm.findLastCompletelyVisibleItemPosition() ==
                mContentView.getAdapter().getItemCount() - 1;
    }
}
