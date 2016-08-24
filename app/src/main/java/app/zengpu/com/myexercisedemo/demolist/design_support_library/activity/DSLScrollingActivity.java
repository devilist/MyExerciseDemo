package app.zengpu.com.myexercisedemo.demolist.design_support_library.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import java.lang.reflect.Field;

import app.zengpu.com.myexercisedemo.R;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class DSLScrollingActivity extends SwipeBackActivity implements AppBarLayout.OnOffsetChangedListener {

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private Drawable drawable;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dsl_activity_scrolling);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        ImageView imageView = (ImageView) findViewById(R.id.backdrop);

        drawable = new BitmapDrawable((Bitmap) getIntent().getParcelableExtra("drawable"));

        imageView.setBackground(drawable);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(" " + getIntent().getStringExtra("appName"));

        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeSize(200);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);


    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, DSLScrollingActivity.class);
        context.startActivity(intent);
    }

    public static void actionStart(AppCompatActivity activity, View transitionView, Bitmap drawable, String appName) {

        String transitionName = activity.getString(R.string.transition_string);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionView, transitionName);

        Intent intent = new Intent(activity, DSLScrollingActivity.class);
        intent.putExtra("drawable", drawable);
        intent.putExtra("appName", appName);

        ActivityCompat.startActivity(activity, intent, options.toBundle());

    }

    @Override
    protected void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset <= -appBarLayout.getMeasuredHeight() + toolbar.getMeasuredHeight() + getStatusBarHeight(this))
            toolbar.setLogo(drawable);
        else toolbar.setLogo(null);
    }

    private int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {
            sbar = 0;
            e1.printStackTrace();
        }
        return sbar;
    }
}
