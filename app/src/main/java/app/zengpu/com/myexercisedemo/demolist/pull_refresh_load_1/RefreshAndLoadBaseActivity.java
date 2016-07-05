package app.zengpu.com.myexercisedemo.demolist.pull_refresh_load_1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengpu on 16/3/31.
 */
public class RefreshAndLoadBaseActivity extends AppCompatActivity {

    private RefreshAndLoadLayoutBase refreshAndLoadLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshAndLoadLayout = new RefreshAndLoadLayoutBase(this);

        final List<String> datas = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            datas.add("item" + i);
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1,datas);
        refreshAndLoadLayout.setAdapter(adapter);

        refreshAndLoadLayout.setOnRefreshListener(new RefreshAndLoadLayoutBase.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Toast.makeText(getApplicationContext(), "refreshing", Toast.LENGTH_SHORT)
                        .show();

                refreshAndLoadLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refreshAndLoadLayout.refreshComplete();
                    }
                }, 1500);
            }
        });

        refreshAndLoadLayout.setOnLoadListener(new RefreshAndLoadLayoutBase.OnLoadListener() {
            @Override
            public void onLoad() {

                Toast.makeText(getApplicationContext(), "loading", Toast.LENGTH_SHORT)
                        .show();

                refreshAndLoadLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refreshAndLoadLayout.loadCompelte();
                    }
                }, 1500);

            }
        });

        refreshAndLoadLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String string = datas.get(position);
                Toast.makeText(RefreshAndLoadBaseActivity.this, string, Toast.LENGTH_SHORT).show();

            }
        });

        setContentView(refreshAndLoadLayout);
    }
}
