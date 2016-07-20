package app.zengpu.com.myexercisedemo.demolist.pull_refresh_load_1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zengpu on 16/4/2.
 */
public class GeneralRefreshLoadActivity  extends AppCompatActivity {

    private GeneralRefreshAndLoadLayout refreshAndLoadLayout ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshAndLoadLayout = new GeneralRefreshAndLoadLayout(this);
        setContentView(refreshAndLoadLayout);

        final List<String> datas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            datas.add("item" + i);
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1,datas);
        refreshAndLoadLayout.setAdapter(adapter);

        refreshAndLoadLayout.setOnRefreshListener(new GeneralRefreshAndLoadLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

//                Toast.makeText(getApplicationContext(), "refreshing", Toast.LENGTH_SHORT)
//                        .show();

                refreshAndLoadLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // 更新数据
                        datas.add(new Date().toGMTString());
                        adapter.notifyDataSetChanged();
                        // 更新完后调用该方法结束刷新
                        refreshAndLoadLayout.refreshComplete();
                    }
                }, 1500);
            }
        });

        refreshAndLoadLayout.setOnLoadListener(new GeneralRefreshAndLoadLayout.OnLoadListener() {
            @Override
            public void onLoad() {

//                Toast.makeText(getApplicationContext(), "loading", Toast.LENGTH_SHORT)
//                        .show();

                refreshAndLoadLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        datas.add(new Date().toGMTString());
                        adapter.notifyDataSetChanged();
                        refreshAndLoadLayout.loadCompelte();
                    }
                }, 2000);

            }
        });

        //listview添加点击事件
        refreshAndLoadLayout.getContentView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String string = datas.get(position);
                Toast.makeText(GeneralRefreshLoadActivity.this, string, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
