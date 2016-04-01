package app.zengpu.com.myexercisedemo.pull_refresh_load_0;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.zengpu.com.myexercisedemo.R;

/**
 * Created by zengpu on 16/3/31.
 */
public class RefreshAndLoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refresh_load_layout);

        final List<String> datas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            datas.add("item" + i);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1,datas);
        ListView listView = (ListView) findViewById(R.id.refresh_load_listview);
        listView.setAdapter(adapter);

        final RefreshAndLoadLayout mRefreshAndLoadLayout =
                (RefreshAndLoadLayout) findViewById(R.id.refresh_load_layout);

//        mRefreshAndLoadLayout.setColorScheme(R.color.umeng_comm_text_topic_light_color,
//                R.color.umeng_comm_yellow, R.color.umeng_comm_green,
//                R.color.umeng_comm_linked_text);

        mRefreshAndLoadLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Toast.makeText(RefreshAndLoadActivity.this,"refreshing",Toast.LENGTH_SHORT).show();

                mRefreshAndLoadLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // 更新数据
                        datas.add(new Date().toGMTString());
                        adapter.notifyDataSetChanged();
                        // 更新完后调用该方法结束刷新
                        mRefreshAndLoadLayout.setRefreshing(false);
                    }
                }, 1000);

            }
        });

        mRefreshAndLoadLayout.setmOnLoadListener(new RefreshAndLoadLayout.OnLoadListener() {
            @Override
            public void onLoad() {

                Toast.makeText(RefreshAndLoadActivity.this, "loading", Toast.LENGTH_SHORT).show();
                mRefreshAndLoadLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // 更新数据
                        datas.add(new Date().toGMTString());
                        adapter.notifyDataSetChanged();
                        // 更新完后调用该方法结束刷新
                        mRefreshAndLoadLayout.setLoading(false);
                    }
                }, 1500);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String string = datas.get(position);
                Toast.makeText(RefreshAndLoadActivity.this, string, Toast.LENGTH_SHORT).show();


            }
        });
    }
}
