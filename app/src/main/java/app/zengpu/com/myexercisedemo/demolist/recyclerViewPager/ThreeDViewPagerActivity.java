package app.zengpu.com.myexercisedemo.demolist.recyclerViewPager;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.zengpu.com.myexercisedemo.R;

/**
 * Created by zengpu on 2016/11/28.
 */

public class ThreeDViewPagerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ThreeDVPAdapter adapter;
    private List<AppInfo> appInfolist = new ArrayList<>();
    private ViewPager viewPager;


    private List<View> pagerItemList = new ArrayList<>();

    private List<String> bgColorList = new ArrayList<>();

    private RelativeLayout rootRl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rvp_activity_3d);

        initData();
        initView();
    }

    private void initData() {

        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packages.size(); i++) {

            PackageInfo packageInfo = packages.get(i);
            String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            Drawable appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            String packageName = packageInfo.packageName;

            AppInfo appInfo = new AppInfo();
            appInfo.setAppName(appName);
            appInfo.setAppIcon(appIcon);
            appInfo.setVersionCode(versionCode);
            appInfo.setVersionName(versionName);
            appInfo.setPackageName(packageName);

            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appInfolist.add(appInfo);
            }
        }

        bgColorList.add("#448aff");
        bgColorList.add("#00bcd4");
        bgColorList.add("#009688");
        bgColorList.add("#4caf50");
        bgColorList.add("#8bc34a");
        bgColorList.add("#cddc39");
        bgColorList.add("#ffeb3b");
        bgColorList.add("#ff9800");
        bgColorList.add("#ff5722");
        bgColorList.add("#9e9e9e");

    }

    private void initView() {

        for (int i = 0; i < appInfolist.size(); i++) {
            LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.rvp_activity_item_3d, viewPager, false);
            ImageView imageView = (ImageView) layout.findViewById(R.id.iv_app_icon);
            TextView textView = (TextView) layout.findViewById(R.id.tv_app_name);
            TextView textView1 = (TextView) layout.findViewById(R.id.tv_app_version_name);
            imageView.setBackgroundDrawable(appInfolist.get(i).getAppIcon());
            textView.setText(appInfolist.get(i).getAppName());
            textView1.setText(appInfolist.get(i).getVersionName());

            pagerItemList.add(layout);
        }

        rootRl = (RelativeLayout) findViewById(R.id.rl_root);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        adapter = new ThreeDVPAdapter(pagerItemList);

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

        viewPager.setPageMargin(-70);

        rootRl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return viewPager.dispatchTouchEvent(motionEvent);
            }
        });

        viewPager.setPageTransformer(true, new MyTransformation());
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        rootRl.setBackgroundColor(Color.parseColor(bgColorList.get(position % 10)));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
