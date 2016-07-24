package app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.view.core.RefreshAndLoadViewBase;

/**
 * 具有下拉刷新和上拉加载功能的RecyclerView;
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
        mContentView = (RecyclerView) getChildAt(2);
        mContentView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    @Override
    protected boolean isTop() {
//        LinearLayoutManager lm = (LinearLayoutManager) mContentView.getLayoutManager();
        GridLayoutManager lm = (GridLayoutManager) mContentView.getLayoutManager();
        return lm.findFirstCompletelyVisibleItemPosition() == 0
                && getScrollY() <= mHeaderView.getMeasuredHeight();
    }

    @Override
    protected boolean isBottom() {
//        LinearLayoutManager lm = (LinearLayoutManager) mContentView.getLayoutManager();
        GridLayoutManager lm = (GridLayoutManager) mContentView.getLayoutManager();
        return mContentView != null && mContentView.getAdapter() != null
                && lm.findLastCompletelyVisibleItemPosition() ==
                mContentView.getAdapter().getItemCount() - 1;
    }

    @Override
    protected boolean iscontentViewCompletelyShow() {
//        LinearLayoutManager lm = (LinearLayoutManager) mContentView.getLayoutManager();
        GridLayoutManager lm = (GridLayoutManager) mContentView.getLayoutManager();
        return mContentView != null && mContentView.getAdapter() != null
                && lm.findFirstCompletelyVisibleItemPosition() == 0
                && lm.findLastCompletelyVisibleItemPosition() == mContentView.getAdapter().getItemCount() - 1;
    }

    /**
     * 配置头和尾
     */
    @Override
    public Builder initHeaderAndFooter() {

        return new Builder()
                .headerBgColor(Color.DKGRAY)                        // header背景色
                .headerTipTextColor(Color.WHITE)                    // 刷新提示文字颜色
                .headerTipTextSize(16)                              // 刷新提示文字大小
                .isShowArrow(false)                                 // 刷新箭头显示
                .footerBgColor(Color.DKGRAY)                        // footer背景色
                .footerTipTextColor(Color.WHITE)                    // 加载提示文字颜色
                .footerTipTextSize(16)                              // 加载提示文字大小
                .pullToRefreshTip("下拉刷新")                        // 下拉刷新未达到可刷新状态提示
                .releaseToRefreshTip("松开可刷新")                   // 下拉刷新达到可刷新状态提示
                .refreshingTip("正在刷新...")                         // 正在刷新提示
                .refreshFailureTip("刷新失败，点击重新刷新")          // 刷新失败提示
                .refreshNoDataTip("数据已最新")                     // 刷新没有更多提示
                .pullToLoadTip("上拉加载")                          // 上拉加载未达到可刷新状态提示
                .releaseToLoadTip("松开可加载")                     // 上拉加载达到可刷新状态提示
                .loadingTip("正在加载...")                           // 正在上拉加载提示
                .loadFailureTip("加载失败，点击重新加载")            // 上拉加载失败提示
                .loadNoDataTip("没有更多了");                      // 上拉加载没有更多提示
    }
}
