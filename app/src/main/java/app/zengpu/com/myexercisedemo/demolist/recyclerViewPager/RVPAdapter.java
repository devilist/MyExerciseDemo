package app.zengpu.com.myexercisedemo.demolist.recyclerViewPager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.zengpu.com.myexercisedemo.R;

/**
 * Created by zengpu on 2016/10/28.
 */

public class RVPAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<String> appNameList = new ArrayList<>();
    private List<Drawable> appIconlist = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CardView rootCv;
        private TextView appNameTv;
        private ImageView appIconIv;
        private OnItemClickListener mListener;

        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            mListener = listener;

            rootCv = (CardView) itemView.findViewById(R.id.cv_view);
            appNameTv = (TextView) itemView.findViewById(R.id.tv_app_name);
            appIconIv = (ImageView) itemView.findViewById(R.id.iv_app_icon);

            rootCv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(v, getLayoutPosition());
        }
    }

    public RVPAdapter(Context context, List<String> appNameList) {
        this.context = context;
        this.appNameList = appNameList;
    }

    public RVPAdapter(Context context, List<String> appNameList, List<Drawable> appIconlist) {
        this.context = context;
        this.appNameList = appNameList;
        this.appIconlist = appIconlist;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.rvp_activity_item, parent, false);
        return new ViewHolder(v, mOnItemClickListener);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder mViewHolder = (ViewHolder) holder;
        mViewHolder.appNameTv.setText(appNameList.get(position));
        if (null != appIconlist && appIconlist.size() != 0 && null != appIconlist.get(position)) {
            mViewHolder.appIconIv.setImageDrawable(appIconlist.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return appNameList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}

