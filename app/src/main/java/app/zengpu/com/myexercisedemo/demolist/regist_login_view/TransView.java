package app.zengpu.com.myexercisedemo.demolist.regist_login_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by zengpu on 2017/4/19.
 */

public class TransView extends ViewGroup {

    public TransView(Context context) {
        super(context);
    }

    public TransView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TransView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
