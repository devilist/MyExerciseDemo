package app.zengpu.com.myexercisedemo.demolist.recyclerViewPager;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.Utils.LogUtil;

/**
 * Created by zengpu on 2016/10/28.
 */

public class RecyclerViewPagerActivity extends AppCompatActivity implements
        RecyclerViewPager.OnPageSelectListener,
        RefreshRecyclerViewPager.OnRefreshListener,
        RefreshRecyclerViewPager.OnLoadMoreListener {

    private RefreshRecyclerViewPager refreshRecyclerViewPager;
    private RecyclerViewPager recyclerViewPager;
    private RVPAdapter adapter;
    private List<AppInfo> appInfolist = new ArrayList<>();

//    private RecyclerIndicator indicator;
    private AnimPagerIndicator animPagerIndicator;
    private List<Drawable> indicatorIconlist = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rvp_activity);

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
                indicatorIconlist.add(appIcon);
            }
        }
    }

    private void initView() {

        refreshRecyclerViewPager = (RefreshRecyclerViewPager) findViewById(R.id.rrvp_pager);

        recyclerViewPager = (RecyclerViewPager) findViewById(R.id.rvp_list);
        adapter = new RVPAdapter(this, appInfolist);
        recyclerViewPager.setAdapter(adapter);
        adapter.setOnItemClickListener(new RVPAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(RecyclerViewPagerActivity.this, appInfolist.get(position).getAppName(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerViewPager.setOnPageSelectListener(this);

        refreshRecyclerViewPager.setOnRefreshListener(this);
        refreshRecyclerViewPager.setOnLoadMoreListener(this);

//        indicator = (RecyclerIndicator) findViewById(R.id.ri_indicator);
//        indicator.setIcon(indicatorIconlist);
//        indicator.setMaxVisableCount(7);
        animPagerIndicator = (AnimPagerIndicator) findViewById(R.id.view_indictor);
        animPagerIndicator.setData(indicatorIconlist);

    }

    int textFlag = 0;

    @Override
    public void onRefresh() {
        refreshRecyclerViewPager.postDelayed(new Runnable() {

            @Override
            public void run() {

                // 加载失败
                if (textFlag == 0) {
                    refreshRecyclerViewPager.refreshAndLoadFailure();
                    textFlag = 1;
                    return;
                }
                // 加载成功
                if (textFlag == 1) {
                    // 更新数据
//                    appInfolist.add(0, appInfolist.get(appInfolist.size() - 1));
//                    adapter.notifyDataSetChanged();
                    // 更新完后调用该方法结束刷新
                    refreshRecyclerViewPager.refreshComplete();
                    textFlag = 2;
                    return;
                }
                // 没有更多数据
                if (textFlag == 2) {
                    refreshRecyclerViewPager.refreshAndLoadNoMore();
                    textFlag = 0;
                    return;
                }
            }
        }, 1500);

    }

    @Override
    public void onLoadMore() {

        refreshRecyclerViewPager.postDelayed(new Runnable() {

            @Override
            public void run() {

                if (textFlag == 0) {
                    refreshRecyclerViewPager.refreshAndLoadFailure();
                    textFlag = 1;
                    return;
                }
                if (textFlag == 1) {
                    // 更新数据
                    appInfolist.add(appInfolist.get(0));
                    adapter.notifyDataSetChanged();
                    // 更新完后调用该方法结束刷新
                    refreshRecyclerViewPager.loadMoreCompelte();
                    textFlag = 2;
                    return;
                }
                if (textFlag == 2) {
                    refreshRecyclerViewPager.refreshAndLoadNoMore();
                    textFlag = 0;
                    return;
                }
            }
        }, 1500);

    }


    @Override
    public void onPageScrolled(int position, float positionOffset) {

    }

    @Override
    public void onPageSelected(int position) {
        LogUtil.e("RecyclerViewPagerActivity", "position : " + position);
//        indicator.doSelectAnimation(position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
