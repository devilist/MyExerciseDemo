package app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.view.RefreshAndLoadListView;
import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.view.core.RefreshAndLoadViewBase;

/**
 * Created by zengpu on 2016/7/4.
 */
public class ListviewActivity extends BaseActivity implements
        RefreshAndLoadViewBase.OnRefreshListener, RefreshAndLoadViewBase.OnLoadListener {

    private RefreshAndLoadListView refreshAndLoadListView;
    private ListView listView;
    private List<String> datas = new ArrayList<>();
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_to_refresh_listview);

        isSwipeBackEnabled(true);
        refreshAndLoadListView = (RefreshAndLoadListView) findViewById(R.id.rllv_list);
        refreshAndLoadListView.setOnRefreshListener(this);
        refreshAndLoadListView.setOnLoadListener(this);
//        refreshAndLoadListView.setCanLoad(false);

        listView = (ListView) findViewById(R.id.lv_list);

        for (int i = 0; i < 5; i++) {
            datas.add("<<<<<<<测试>>>>>" + i);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, datas);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!refreshAndLoadListView.isLoading() && !refreshAndLoadListView.isRefreshing())

                    Toast.makeText(ListviewActivity.this, datas.get(position), Toast.LENGTH_SHORT).show();

            }
        });


    }

    int textFlag = 0;

    @Override
    public void onRefresh() {
        refreshAndLoadListView.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 加载失败
                if (textFlag == 0) {
                    refreshAndLoadListView.refreshAndLoadFailure();
//                    datas.add("下拉刷新：加载失败 "+ new Date().toLocaleString());
                    textFlag = 1;
                    return;
                }
                // 加载成功
                if (textFlag == 1) {
                    // 更新数据
                    datas.add("下拉刷新：加载成功1 "+ new Date().toLocaleString());
                    datas.add("下拉刷新：加载成功2 "+ new Date().toLocaleString());
                    datas.add("下拉刷新：加载成功3 "+ new Date().toLocaleString());
                    adapter.notifyDataSetChanged();
                    // 更新完后调用该方法结束刷新
                    refreshAndLoadListView.refreshComplete();
                    textFlag = 2;
                    return;
                }
                // 没有更多数据
                if (textFlag == 2) {
                    refreshAndLoadListView.refreshAndLoadNoMore();
//                    datas.add("下拉刷新：数据已经最新 "+ new Date().toLocaleString());
                    textFlag = 0;
                    return;
                }
            }
        }, 1500);

    }

    @Override
    public void onLoad() {
        refreshAndLoadListView.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (textFlag == 0) {
                    refreshAndLoadListView.refreshAndLoadFailure();
//                    datas.add("上拉加载：加载失败 "+ new Date().toLocaleString());
                    textFlag = 1;
                    return;
                }
                if (textFlag == 1) {
                    // 更新数据
                    datas.add("上拉加载：加载成功1 "+ new Date().toLocaleString());
                    datas.add("上拉加载：加载成功2 "+ new Date().toLocaleString());
                    datas.add("上拉加载：加载成功3 "+ new Date().toLocaleString());
                    adapter.notifyDataSetChanged();
                    // 更新完后调用该方法结束刷新
                    refreshAndLoadListView.loadCompelte();
                    textFlag = 2;
                    return;
                }
                if (textFlag == 2) {
                    refreshAndLoadListView.refreshAndLoadNoMore();
//                    datas.add("上拉加载：没有更多数据 "+ new Date().toLocaleString());
                    textFlag = 0;
                    return;
                }
            }
        }, 1500);
    }
}
