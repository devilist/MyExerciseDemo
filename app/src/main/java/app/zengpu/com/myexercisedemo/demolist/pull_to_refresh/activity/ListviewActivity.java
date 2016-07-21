package app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.view.RefreshAndLoadListView;
import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.view.core.RefreshAndLoadViewBase;

/**
 * Created by zengpu on 2016/7/4.
 */
public class ListviewActivity extends AppCompatActivity {

    private RefreshAndLoadListView refreshAndLoadListView;
    private ListView listView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_to_refresh_listview);

        refreshAndLoadListView = (RefreshAndLoadListView) findViewById(R.id.rllv_list);
        refreshAndLoadListView.setCanLoad(false);

        listView = (ListView) findViewById(R.id.lv_list);

        final List<String> datas = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            datas.add("item" + i);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1, datas);
        listView.setAdapter(adapter);


        refreshAndLoadListView.setOnRefreshListener(new RefreshAndLoadViewBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAndLoadListView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // 更新数据
                        datas.add(new Date().toGMTString());
                        adapter.notifyDataSetChanged();
                        // 更新完后调用该方法结束刷新
                        refreshAndLoadListView.refreshComplete();
                    }
                }, 1500);
            }
        });

        refreshAndLoadListView.setOnLoadListener(new RefreshAndLoadViewBase.OnLoadListener() {
            @Override
            public void onLoad() {
                refreshAndLoadListView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        datas.add(new Date().toGMTString());
                        adapter.notifyDataSetChanged();
                        refreshAndLoadListView.loadCompelte();
                    }
                }, 1500);
            }
        });
    }
}
