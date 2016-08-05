package app.zengpu.com.myexercisedemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.zengpu.com.myexercisedemo.MainActivity.RecyclerViewAdapter.OnItemClickListener;
import app.zengpu.com.myexercisedemo.Utils.CustomAlertDialog;
import app.zengpu.com.myexercisedemo.demolist.advancepagerslidingtabstrip.activity.ApstActivity;
import app.zengpu.com.myexercisedemo.demolist.galleryfinaldemo.GalleryFinalActivity;
import app.zengpu.com.myexercisedemo.demolist.multi_drawer.MultiDrawerActivity;
import app.zengpu.com.myexercisedemo.demolist.photoloop0.PhotoLoopActivity;
import app.zengpu.com.myexercisedemo.demolist.photoloop1.ImageLoopActivity;
import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.RefreshAndLoadActivity;
import app.zengpu.com.myexercisedemo.demolist.videoRecord.VideoAppendActivity;

/**
 * Created by zengpu on 16/3/30.
 */
public class MainActivity extends AppCompatActivity {

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

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (position == 0) {
                    new CustomAlertDialog(MainActivity.this,
                            R.string.dialog_title,
                            R.string.dialog_message,
                            null, new CustomAlertDialog.AlertDialogClickListener() {
                        @Override
                        public void onResult(boolean confirmed, Bundle bundle) {

                            Toast.makeText(MainActivity.this, String.valueOf(confirmed), Toast.LENGTH_SHORT).show();
                        }
                    }, true, R.style.MyAlertDialog).show();
                } else {
                    try {
                        Class<?> activityClazz = Class.forName(demoList.get(position)[1]);

                        Intent intent = new Intent(getApplicationContext(), activityClazz);

                        startActivity(intent);

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void initData() {
        demoList.add(new String[]{"自定义CustomAlertDialog", CustomAlertDialog.class.getName()});
        demoList.add(new String[]{"多层抽屉 ", MultiDrawerActivity.class.getName()});
        demoList.add(new String[]{"图片轮播:ViewPager+Handler", PhotoLoopActivity.class.getName()});
        demoList.add(new String[]{"图片轮播:ViewPager+定时任务", ImageLoopActivity.class.getName()});
        demoList.add(new String[]{"下拉刷新，上拉加载", RefreshAndLoadActivity.class.getName()});
        demoList.add(new String[]{"GalleryFinal图片查看器", GalleryFinalActivity.class.getName()});
        demoList.add(new String[]{"视频断点拍摄", VideoAppendActivity.class.getName()});
        demoList.add(new String[]{"APST ", ApstActivity.class.getName()});
    }

    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private List<String[]> demoList;
        private OnItemClickListener mOnItemClickListener;


        public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

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
