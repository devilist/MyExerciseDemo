package app.zengpu.com.myexercisedemo.demolist.recyclerViewPager.snap;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.Utils.LogUtil;
import app.zengpu.com.myexercisedemo.demolist.recyclerViewPager.AppInfo;
import app.zengpu.com.myexercisedemo.demolist.recyclerViewPager.RVPAdapter;
import app.zengpu.com.myexercisedemo.demolist.recyclerViewPager.RefreshRecyclerViewPager;
import app.zengpu.com.myexercisedemo.demolist.recyclerViewPager.snap.layoutmanager.ScaleLinearLayoutManager;

/**
 * Created by zengp on 2017/10/17.
 */

public class SnapHelperActivity extends BaseActivity implements
        RefreshRecyclerViewPager.OnRefreshListener,
        RefreshRecyclerViewPager.OnLoadMoreListener {

    private RefreshRecyclerViewPager rrvp_pager;
    private RecyclerView rv_list;
    private RVPAdapter adapter;
    private List<AppInfo> appInfolist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview_snap);
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

    }

    private void initView() {
        rrvp_pager = (RefreshRecyclerViewPager) findViewById(R.id.rrvp_pager);
        rv_list = (RecyclerView) findViewById(R.id.rv_list);

        rrvp_pager.setCanRefresh(true);
        rrvp_pager.setCanLoad(true);
        rrvp_pager.setOnLoadMoreListener(this);
        rrvp_pager.setOnRefreshListener(this);
        rrvp_pager.setHiddenRefreshProgress(true);
        rrvp_pager.setHiddenRefreshProgress(true);

        adapter = new RVPAdapter(this, appInfolist.subList(0, 10), R.layout.rvp_activity_item_snap);
        rv_list.setAdapter(adapter);
        final LinearLayoutManager manager = new ScaleLinearLayoutManager(this, rv_list);
        rv_list.setLayoutManager(manager);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rv_list);

        rv_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LogUtil.d("onScrollStateChanged", "newState " + newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int first_position = manager.findFirstCompletelyVisibleItemPosition();
                    Toast.makeText(SnapHelperActivity.this, "" + first_position, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        rrvp_pager.refreshComplete();
    }

    @Override
    public void onLoadMore() {
        adapter = new RVPAdapter(this, appInfolist.subList(0, 10), R.layout.rvp_activity_item_snap);
        rv_list.setAdapter(adapter);
        rrvp_pager.loadMoreCompelte();
    }
}
