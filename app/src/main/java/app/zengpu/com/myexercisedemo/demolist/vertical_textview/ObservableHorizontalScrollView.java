package app.zengpu.com.myexercisedemo.demolist.vertical_textview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Created by zengpu on 2017/1/27.
 */

public class ObservableHorizontalScrollView extends HorizontalScrollView {

    private OnScrollListener mOnScrollListener;

    public ObservableHorizontalScrollView(Context context) {
        super(context);
    }

    public ObservableHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (null != mOnScrollListener)
            mOnScrollListener.onScrollChanged(l, t, oldl, oldt);

    }

    public void setOnScrollLisenter(OnScrollListener lisenter) {
        this.mOnScrollListener = lisenter;
    }

    public interface OnScrollListener {

        void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}
