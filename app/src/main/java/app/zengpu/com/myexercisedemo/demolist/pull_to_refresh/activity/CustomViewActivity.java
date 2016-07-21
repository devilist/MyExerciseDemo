package app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.view.RefreshAndLoadCustomView;
import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.view.core.RefreshAndLoadViewBase;

/**
 * Created by zengpu on 16/7/21.
 */
public class CustomViewActivity extends AppCompatActivity implements
        RefreshAndLoadViewBase.OnRefreshListener, RefreshAndLoadViewBase.OnLoadListener {

    private RefreshAndLoadCustomView refreshAndLoadCustomView;
    private TextView textView;
    private ImageView imageView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_to_refresh_customview);
        refreshAndLoadCustomView = (RefreshAndLoadCustomView) findViewById(R.id.rllv_list);
        refreshAndLoadCustomView.setOnRefreshListener(this);
        refreshAndLoadCustomView.setOnLoadListener(this);

        textView = (TextView) findViewById(R.id.tv_text);
        textView.setText("<<<<<<<测试>>>>>");

        imageView = (ImageView) findViewById(R.id.iv_imageview);
    }

    int textFlag = 0;

    @Override
    public void onRefresh() {
        refreshAndLoadCustomView.postDelayed(new Runnable() {

            @Override
            public void run() {

                // 加载失败
                if (textFlag == 0) {
                    refreshAndLoadCustomView.refreshAndLoadFailure();
//                    datas.add("下拉刷新：加载失败 "+ new Date().toLocaleString());
                    textFlag = 1;
                    return;
                }
                // 加载成功
                if (textFlag == 1) {
                    // 更新数据
                    textView.setText("下拉刷新：加载成功 "+ new Date().toLocaleString());
                    imageView.setBackgroundResource(R.drawable.picture5);
                    // 更新完后调用该方法结束刷新
                    refreshAndLoadCustomView.refreshComplete();
                    textFlag = 2;
                    return;
                }
                // 没有更多数据
                if (textFlag == 2) {
                    refreshAndLoadCustomView.refreshAndLoadNoMore();
//                    datas.add("下拉刷新：数据已经最新 "+ new Date().toLocaleString());
                    textFlag = 0;
                    return;
                }
            }
        }, 1500);

    }

    @Override
    public void onLoad() {
        refreshAndLoadCustomView.postDelayed(new Runnable() {

            @Override
            public void run() {

                // 加载失败
                if (textFlag == 0) {
                    refreshAndLoadCustomView.refreshAndLoadFailure();
//                    datas.add("下拉刷新：加载失败 "+ new Date().toLocaleString());
                    textFlag = 1;
                    return;
                }
                // 加载成功
                if (textFlag == 1) {
                    // 更新数据
                    textView.setText("上拉加载：加载成功 "+ new Date().toLocaleString());
                    imageView.setBackgroundResource(R.drawable.picture4);
                    // 更新完后调用该方法结束刷新
                    refreshAndLoadCustomView.refreshComplete();
                    textFlag = 2;
                    return;
                }
                // 没有更多数据
                if (textFlag == 2) {
                    refreshAndLoadCustomView.refreshAndLoadNoMore();
//                    datas.add("下拉刷新：数据已经最新 "+ new Date().toLocaleString());
                    textFlag = 0;
                    return;
                }
            }
        }, 1500);

    }
}
