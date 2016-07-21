package app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.view.core.RefreshAndLoadViewBase;

/**
 * Created by zengpu on 16/7/21.
 */
public class RefreshAndLoadCustomView extends RefreshAndLoadViewBase<ViewGroup>{


    public RefreshAndLoadCustomView(Context context) {
        super(context);
    }

    public RefreshAndLoadCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshAndLoadCustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void setContentViewScrollListener() {
        mContentView = (ViewGroup) getChildAt(2);
        mContentView.setClickable(true);

    }

    @Override
    protected boolean isTop() {
        return true;
    }

    @Override
    protected boolean isBottom() {
        return true;
    }

    @Override
    protected boolean iscontentViewCompletelyShow() {
        return false;
    }
}
