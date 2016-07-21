package app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.view.RefreshAndLoadRecyclerView;
import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.view.core.RefreshAndLoadViewBase;

/**
 * Created by zengpu on 2016/7/20.
 */
public class RecyclerviewActivity extends AppCompatActivity implements
        RefreshAndLoadViewBase.OnRefreshListener, RefreshAndLoadViewBase.OnLoadListener {

    private RefreshAndLoadRecyclerView refreshAndLoadRecyclerView;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;
    private List<String> datas = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_to_refresh_recyclerview);


        refreshAndLoadRecyclerView = (RefreshAndLoadRecyclerView) findViewById(R.id.rllv_list);

        refreshAndLoadRecyclerView.setOnRefreshListener(this);
        refreshAndLoadRecyclerView.setOnLoadListener(this);
//        refreshAndLoadRecyclerView.setCanLoad(false); // 是否需要上拉加载

        //第一次进入刷新
        refreshAndLoadRecyclerView.refreshing();

        recyclerView = (RecyclerView) findViewById(R.id.lv_list);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        for (int i = 0; i < 6; i++) {
            datas.add("<<<<<<<测试>>>>> " + i);
        }

        mAdapter = new RecyclerViewAdapter(this, datas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (!refreshAndLoadRecyclerView.isLoading() && !refreshAndLoadRecyclerView.isRefreshing())

                    Toast.makeText(RecyclerviewActivity.this, datas.get(position), Toast.LENGTH_SHORT).show();

            }
        });

    }

    int textFlag = 0;

    @Override
    public void onRefresh() {
        refreshAndLoadRecyclerView.postDelayed(new Runnable() {

            @Override
            public void run() {

                // 加载失败
                if (textFlag == 0) {
                    refreshAndLoadRecyclerView.refreshAndLoadFailure();
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
                    mAdapter.notifyDataSetChanged();
                    // 更新完后调用该方法结束刷新
                    refreshAndLoadRecyclerView.refreshComplete();
                    textFlag = 2;
                    return;
                }
                // 没有更多数据
                if (textFlag == 2) {
                    refreshAndLoadRecyclerView.refreshAndLoadNoMore();
//                    datas.add("下拉刷新：数据已经最新 "+ new Date().toLocaleString());
                    textFlag = 0;
                    return;
                }
            }
        }, 1500);
    }

    @Override
    public void onLoad() {
        refreshAndLoadRecyclerView.postDelayed(new Runnable() {

            @Override
            public void run() {

                if (textFlag == 0) {
                    refreshAndLoadRecyclerView.refreshAndLoadFailure();
//                    datas.add("上拉加载：加载失败 "+ new Date().toLocaleString());
                    textFlag = 1;
                    return;
                }
                if (textFlag == 1) {
                    // 更新数据
                    datas.add("上拉加载：加载成功1 "+ new Date().toLocaleString());
                    datas.add("上拉加载：加载成功2 "+ new Date().toLocaleString());
                    datas.add("上拉加载：加载成功3 "+ new Date().toLocaleString());
                    mAdapter.notifyDataSetChanged();
                    // 更新完后调用该方法结束刷新
                    refreshAndLoadRecyclerView.loadCompelte();
                    textFlag = 2;
                    return;
                }
                if (textFlag == 2) {
                    refreshAndLoadRecyclerView.refreshAndLoadNoMore();
//                    datas.add("上拉加载：没有更多数据 "+ new Date().toLocaleString());
                    textFlag = 0;
                    return;
                }
//
            }
        }, 2000);
    }


    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private List<String> demoList;
        private OnItemClickListener mOnItemClickListener;


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView itemTv;
            private OnItemClickListener mListener;

            public ViewHolder(View itemView, OnItemClickListener listener) {
                super(itemView);
                mListener = listener;

                itemTv = (TextView) itemView.findViewById(R.id.tv_item);

                itemTv.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                mListener.onItemClick(v, getLayoutPosition());
            }
        }

        public RecyclerViewAdapter(Context context, List<String> demoList) {
            this.context = context;
            this.demoList = demoList;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_mainactivity_recyclerview, parent, false);
            return new ViewHolder(v, mOnItemClickListener);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder mViewHolder = (ViewHolder) holder;
            mViewHolder.itemTv.setText(demoList.get(position));
        }

        @Override
        public int getItemCount() {
            return demoList.size();
        }

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
        }
    }
}

