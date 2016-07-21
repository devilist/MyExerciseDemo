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
    protected void configHeaderAndFooter() {
        mHeaderView.setBackgroundColor(Color.DKGRAY);
        mHeaderTipsTextView.setTextColor(Color.WHITE);
        mHeaderTipsTextView.setTextSize(16);

        mFooterView.setBackgroundColor(Color.GRAY);
        mFooterTipsTextView.setTextColor(Color.WHITE);
        mFooterTipsTextView.setTextSize(16);
    }


//    /**
//     * 配置header和footer
//     */
//    static class Builder {
//
//        private int headerBgColor;
//        private String headerTipText;
//        private int headerTipTextColor;
//        private int headerTipTextSize;
//        private int headerProgressHeight;
//
//        private int footerBgColor;
//        private String footerTipText;
//        private int footerTipTextColor;
//        private int footerTipTextSize;
//        private int footerProgressHeight;
//
//        public Builder headerBgColor(int headerBgColor) {
//            this.headerBgColor = headerBgColor;
//            return this;
//        }
//        public Builder headerTipText(String headerTipText) {
//            this.headerTipText = headerTipText;
//            return this;
//        }
//        public Builder headerTipTextColor(int headerTipTextColor) {
//            this.headerTipTextColor = headerTipTextColor;
//            return this;
//        }
//        public Builder headerTipTextSize(int headerTipTextSize) {
//            this.headerTipTextSize = headerTipTextSize;
//            return this;
//        }
//        public Builder headerProgressHeight(int headerProgressHeight) {
//            this.headerProgressHeight = headerProgressHeight;
//            return this;
//        }
//
//        public Builder footerBgColor(int footerBgColor) {
//            this.footerBgColor = footerBgColor;
//            return this;
//        }
//        public Builder footerTipText(String footerTipText) {
//            this.footerTipText = footerTipText;
//            return this;
//        }
//        public Builder footerTipTextColor(int footerTipTextColor) {
//            this.footerTipTextColor = footerTipTextColor;
//            return this;
//        }
//        public Builder footerTipTextSize(int footerTipTextSize) {
//            this.footerTipTextSize = footerTipTextSize;
//            return this;
//        }
//        public Builder footerProgressHeight(int footerProgressHeight) {
//            this.footerProgressHeight = footerProgressHeight;
//            return this;
//        }
//
//        public RefreshAndLoadRecyclerView create(Context context) {
//
//            return new RefreshAndLoadRecyclerView(context);
//
//        }
//
//    }
}
