package app.zengpu.com.myexercisedemo.demolist.design_support_library.activity;

import android.os.Bundle;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by tao on 2016/8/26.
 */
public class BaseActivity extends SwipeBackActivity {

    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    protected void isSwipeBackEnabled(boolean isSwipeBackEnabled) {
        if (isSwipeBackEnabled) {
            mSwipeBackLayout = getSwipeBackLayout();
            mSwipeBackLayout.setEdgeSize(200);
            mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);
        }
    }
}
