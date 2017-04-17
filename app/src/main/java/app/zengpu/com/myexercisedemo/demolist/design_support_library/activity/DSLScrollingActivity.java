package app.zengpu.com.myexercisedemo.demolist.design_support_library.activity;

import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.lang.reflect.Field;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;

public class DSLScrollingActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private Drawable drawable;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private Button animCustomBtn, animDefaultBtn, targetCustomBtn, targetDefaultBtn;

    private TextSwitcher textSwitcher;
    private Button textSwitcherChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dsl_activity_scrolling);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        // 滑动删除
        isSwipeBackEnabled(true);
        // 转场动画
        activityTransitionAnim();
        // 布局改变动画
        LayoutTransitions();
        // textswithcer动画
        initTextSwitcherAnim();



    }

    /**
     * activity 转场动画
     */
    private void activityTransitionAnim() {
        ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        drawable = new BitmapDrawable((Bitmap) getIntent().getParcelableExtra("drawable"));
        imageView.setBackground(drawable);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(" " + getIntent().getStringExtra("appName"));
    }

    /**
     * 布局改变动画
     */
    private void LayoutTransitions() {

        animDefaultBtn = (Button) findViewById(R.id.btn_anim_default);
        targetDefaultBtn = (Button) findViewById(R.id.btn_target_default);

        animDefaultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetDefaultBtn.isShown()) {
                    targetDefaultBtn.setText("消失");
                    targetDefaultBtn.setVisibility(View.GONE);
                } else {
                    targetDefaultBtn.setVisibility(View.VISIBLE);
                    targetDefaultBtn.setText("出现");
                }
            }
        });

        animCustomBtn = (Button) findViewById(R.id.btn_anim_diy);
        targetCustomBtn = (Button) findViewById(R.id.btn_target);

        animCustomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetCustomBtn.isShown()) {
                    targetCustomBtn.setText("消失");
                    targetCustomBtn.setVisibility(View.GONE);
                } else {
                    targetCustomBtn.setVisibility(View.VISIBLE);
                    targetCustomBtn.setText("出现");
                }
            }
        });

        LayoutTransition transition = new LayoutTransition();
        LinearLayout group = (LinearLayout) findViewById(R.id.ll_transition_group_custom);
        group.setLayoutTransition(transition);

        //入场动画:view在这个容器中出现时触发的动画
        ObjectAnimator animIn = ObjectAnimator.ofFloat(null, "rotationY", 0f, 120f, 0f);
        transition.setAnimator(LayoutTransition.APPEARING, animIn);

        //出场动画:view显示时的动画
        ObjectAnimator animOut = ObjectAnimator.ofFloat(null, "rotationX", 0f, 120f, 0f);
        transition.setAnimator(LayoutTransition.DISAPPEARING, animOut);

        //LayoutTransition.CHANGE_APPEARING
        PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofInt("left", 0, 100, 0);
        PropertyValuesHolder pvhTop = PropertyValuesHolder.ofInt("top", 1, 1);
        PropertyValuesHolder pvhRight = PropertyValuesHolder.ofInt("right", 0, 1);
        PropertyValuesHolder pvhBottom = PropertyValuesHolder.ofInt("bottom", 0, 1);
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("ScaleX", 1f, 2f, 1f);
        Animator changeAppearAnimator = ObjectAnimator.ofPropertyValuesHolder(group, pvhLeft, pvhBottom, pvhTop, pvhRight);
        transition.setAnimator(LayoutTransition.CHANGE_APPEARING, changeAppearAnimator);

        //LayoutTransition.CHANGE_DISAPPEARING
        Keyframe frame0 = Keyframe.ofFloat(0f, 0);
        Keyframe frame1 = Keyframe.ofFloat(0.1f, -20f);
        Keyframe frame2 = Keyframe.ofFloat(0.2f, 20f);
        Keyframe frame3 = Keyframe.ofFloat(0.3f, -40f);
        Keyframe frame4 = Keyframe.ofFloat(0.4f, 40f);
        Keyframe frame5 = Keyframe.ofFloat(0.5f, -60f);
        Keyframe frame6 = Keyframe.ofFloat(0.6f, 60f);
        Keyframe frame7 = Keyframe.ofFloat(0.7f, -40f);
        Keyframe frame8 = Keyframe.ofFloat(0.8f, 40f);
        Keyframe frame9 = Keyframe.ofFloat(0.9f, -20f);
        Keyframe frame10 = Keyframe.ofFloat(1, 0);
        PropertyValuesHolder mPropertyValuesHolder = PropertyValuesHolder.ofKeyframe("rotation",
                frame0, frame1, frame2, frame3, frame4, frame5, frame6, frame7, frame8, frame9, frame10);

        ObjectAnimator mObjectAnimatorChangeDisAppearing = ObjectAnimator.ofPropertyValuesHolder(group, pvhLeft, pvhBottom, pvhTop, pvhRight, mPropertyValuesHolder);
        transition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, mObjectAnimatorChangeDisAppearing);

        transition.setStagger(LayoutTransition.CHANGE_APPEARING, 300);
        transition.setStagger(LayoutTransition.CHANGE_DISAPPEARING, 300);

    }

    private int textSwitcherStrIndex = 0;

    private void initTextSwitcherAnim() {
        textSwitcher = (TextSwitcher) findViewById(R.id.ts_switcher);
        textSwitcherChange = (Button) findViewById(R.id.btn_change);
        final String[] arrayTexts = {"锄禾日当午", "汗滴禾下土", "谁知盘中餐", "粒粒皆辛苦"};
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(DSLScrollingActivity.this);
                tv.setTextSize(30);
                tv.setTextColor(Color.BLACK);
                tv.setGravity(Gravity.CENTER);
                return tv;
            }
        });
        textSwitcher.setText(arrayTexts[0]);
        textSwitcherChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSwitcherStrIndex++;
                if (textSwitcherStrIndex >= arrayTexts.length) {
                    textSwitcherStrIndex = 0;
                }
                textSwitcher.setText(arrayTexts[textSwitcherStrIndex]);
            }
        });
        // 设置切入动画
        textSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        // 设置切出动画
        textSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
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
