package app.zengpu.com.myexercisedemo.demolist.cardlistview;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.Utils.LogUtil;
import app.zengpu.com.myexercisedemo.demolist.recyclerViewPager.AppInfo;

/**
 * Created by zengp on 2017/8/21.
 */

public class CardListViewActivity extends BaseActivity {

    private List<AppInfo> appInfolist = new ArrayList<>();

    private CardListView clv_list;
    private CardListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list_view);
        initData();
        initView();
    }

    private void initView() {
        clv_list = (CardListView) findViewById(R.id.clv_list);
        adapter = new CardListAdapter(this);
        clv_list.setAdapter(adapter);
        adapter.addData(appInfolist);
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

            LogUtil.d("CardListViewActivity", "appName " + appName);

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
    }
}
