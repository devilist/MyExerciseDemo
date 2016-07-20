package app.zengpu.com.myexercisedemo.demolist.pull_to_refresh;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.zengpu.com.myexercisedemo.R;

/**
 * Created by zengpu on 2016/7/20.
 */
public class PullToRefreshRecyclerviewActivity extends AppCompatActivity {

    private RefreshAndLoadRecyclerView refreshAndLoadRecyclerView;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_to_refresh_recyclerview);

        refreshAndLoadRecyclerView = (RefreshAndLoadRecyclerView) findViewById(R.id.rllv_list);

        recyclerView = (RecyclerView) findViewById(R.id.lv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final List<String> datas = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            datas.add("item" + i);
        }

        mAdapter = new RecyclerViewAdapter(this, datas);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });


        refreshAndLoadRecyclerView.setOnRefreshListener(new RefreshAndLoadViewBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAndLoadRecyclerView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // 更新数据
                        datas.add(new Date().toGMTString());
                        mAdapter.notifyDataSetChanged();
                        // 更新完后调用该方法结束刷新
                        refreshAndLoadRecyclerView.refreshComplete();
                    }
                }, 1500);
            }
        });

        refreshAndLoadRecyclerView.setOnLoadListener(new RefreshAndLoadViewBase.OnLoadListener() {
            @Override
            public void onLoad() {
                refreshAndLoadRecyclerView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        datas.add(new Date().toGMTString());
                        mAdapter.notifyDataSetChanged();
                        refreshAndLoadRecyclerView.loadCompelte();
                    }
                }, 2000);
            }
        });

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

