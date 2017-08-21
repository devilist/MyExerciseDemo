package app.zengpu.com.myexercisedemo.demolist.cardlistview;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.recyclerViewPager.AppInfo;
import app.zengpu.com.myexercisedemo.demolist.recyclerViewPager.RVPAdapter;

/**
 * Created by zengp on 2017/8/21.
 */

public class CardListAdapter extends BaseAdapter {

    private Context mContext;
    private List<AppInfo> data;

    public CardListAdapter(Context mContext, List<AppInfo> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public AppInfo getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.rvp_activity_item, parent, false);
            holder.rootCv = (CardView) convertView.findViewById(R.id.cv_view);
            holder.appIconIv = (ImageView) convertView.findViewById(R.id.iv_app_icon);
            holder.appNameTv = (TextView) convertView.findViewById(R.id.tv_app_name);
            holder.appVNameTv = (TextView) convertView.findViewById(R.id.tv_app_version_name);
            holder.appPnameTv = (TextView) convertView.findViewById(R.id.tv_app_package_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AppInfo appInfo = data.get(position);
        holder.appNameTv.setText(appInfo.getAppName());
        holder.appIconIv.setImageDrawable(appInfo.getAppIcon());
        holder.appVNameTv.setText("v" + appInfo.getVersionName());
        holder.appPnameTv.setText(appInfo.getPackageName());
        return convertView;
    }

    static class ViewHolder {
        public CardView rootCv;
        public ImageView appIconIv;
        public TextView appNameTv;
        public TextView appVCodeTv;
        private TextView appVNameTv;
        public TextView appPnameTv;
    }
}
