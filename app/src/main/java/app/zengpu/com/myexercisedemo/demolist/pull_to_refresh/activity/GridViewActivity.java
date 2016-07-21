package app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.view.RefreshAndLoadGridView;
import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.view.core.RefreshAndLoadViewBase;

/**
 * Created by zengpu on 2016/7/21.
 */
public class GridViewActivity extends AppCompatActivity implements
        RefreshAndLoadViewBase.OnRefreshListener, RefreshAndLoadViewBase.OnLoadListener {

    private RefreshAndLoadGridView refreshAndLoadGridView;
    private GridView gridView;
    private List<String> datas = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_to_refresh_gridview);

        refreshAndLoadGridView = (RefreshAndLoadGridView) findViewById(R.id.rllv_list);
        refreshAndLoadGridView.setOnRefreshListener(this);
        refreshAndLoadGridView.setOnLoadListener(this);

        gridView = (GridView) findViewById(R.id.gv_list);

        datas = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            datas.add("item" + i);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, datas);
        gridView.setAdapter(adapter);

    }

    int textFlag = 0;

    @Override
    public void onRefresh() {
        refreshAndLoadGridView.postDelayed(new Runnable() {

            @Override
            public void run() {

                // 加载失败
                if (textFlag == 0) {
                    refreshAndLoadGridView.refreshAndLoadFailure();
                    textFlag = 1;
                    return;
                }
                // 加载成功
                if (textFlag == 1) {
                    // 更新数据
                    datas.add(new Date().toGMTString());
                    adapter.notifyDataSetChanged();
                    // 更新完后调用该方法结束刷新
                    refreshAndLoadGridView.refreshComplete();
                    textFlag = 2;
                    return;
                }
                // 没有更多数据
                if (textFlag == 2) {
                    refreshAndLoadGridView.refreshAndLoadNoMore();
                    textFlag = 0;
                    return;
                }
            }
        }, 1500);
    }

    @Override
    public void onLoad() {

        refreshAndLoadGridView.postDelayed(new Runnable() {

            @Override
            public void run() {

                // 加载失败
                if (textFlag == 0) {
                    refreshAndLoadGridView.refreshAndLoadFailure();
                    textFlag = 1;
                    return;
                }
                // 加载成功
                if (textFlag == 1) {
                    // 更新数据
                    datas.add(new Date().toGMTString());
                    adapter.notifyDataSetChanged();
                    // 更新完后调用该方法结束刷新
                    refreshAndLoadGridView.loadCompelte();
                    textFlag = 2;
                    return;
                }
                // 没有更多数据
                if (textFlag == 2) {
                    refreshAndLoadGridView.refreshAndLoadNoMore();
                    textFlag = 0;
                    return;
                }
            }
        }, 1500);
    }
}
