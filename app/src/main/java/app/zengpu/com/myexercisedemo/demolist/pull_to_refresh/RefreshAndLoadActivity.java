package app.zengpu.com.myexercisedemo.demolist.pull_to_refresh;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.activity.GridViewActivity;
import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.activity.ListviewActivity;
import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.activity.RecyclerviewActivity;
import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.activity.TextViewActivity;

/**
 * Created by zengpu on 2016/7/21.
 */
public class RefreshAndLoadActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private List<String[]> demoList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
    }

    private void initView() {

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_demolist);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new RecyclerViewAdapter(this, demoList);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                try {
                    Class<?> activityClazz = Class.forName(demoList.get(position)[1]);

                    Intent intent = new Intent(getApplicationContext(), activityClazz);

                    startActivity(intent);

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void initData() {
        demoList.add(new String[]{"listview", ListviewActivity.class.getName()});
        demoList.add(new String[]{"gridview", GridViewActivity.class.getName()});
        demoList.add(new String[]{"recyclerview", RecyclerviewActivity.class.getName()});
        demoList.add(new String[]{"textview", TextViewActivity.class.getName()});
    }


    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private List<String[]> demoList;
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

        public RecyclerViewAdapter(Context context, List<String[]> demoList) {
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
            mViewHolder.itemTv.setText(demoList.get(position)[0]);
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


