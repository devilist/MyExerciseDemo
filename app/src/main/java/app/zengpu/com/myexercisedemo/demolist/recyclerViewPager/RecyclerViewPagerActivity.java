package app.zengpu.com.myexercisedemo.demolist.recyclerViewPager;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.zengpu.com.myexercisedemo.R;

/**
 * Created by tao on 2016/10/28.
 */

public class RecyclerViewPagerActivity extends AppCompatActivity {

    private RecyclerViewPager recyclerViewPager;
    private RVPAdapter adapter;
    private List<String> appNameList = new ArrayList<>();
    private List<Drawable> appIconlist = new ArrayList<>();


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

            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appNameList.add(appName);
                appIconlist.add(appIcon);
            }
        }
    }

    private void initView() {
        recyclerViewPager = (RecyclerViewPager) findViewById(R.id.rvp_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewPager.setLayoutManager(linearLayoutManager);
        adapter = new RVPAdapter(this, appNameList, appIconlist);
        recyclerViewPager.setAdapter(adapter);
        adapter.setOnItemClickListener(new RVPAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(RecyclerViewPagerActivity.this, appNameList.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
