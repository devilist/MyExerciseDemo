package app.zengpu.com.myexercisedemo.demolist.cardlistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.cardlistview.widget.CardStackView;
import app.zengpu.com.myexercisedemo.demolist.recyclerViewPager.AppInfo;

/**
 * Created by zengp on 2017/8/21.
 */

public class CardListAdapter extends CardStackView.CardAdapter {

    private Context mContext;
    private List<AppInfo> data;

    public CardListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void addData(List<AppInfo> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return null == data ? 0 : data.size();
    }

    @Override
    public AppInfo getItem(int position) {
        return null == data ? null : data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected void delItem(int position) {
        if (null != data && data.size() > 0) {
            data.remove(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getVisibleCardCount() {
        return 5;
    }

    @Override
    public int getCardOffset() {
        return 30;
    }

    @Override
    public int getCardElevation() {
        return 40;
    }

    @Override
    public boolean isEnableRotate() {
        return true;
    }

    @Override
    public boolean isEnableDataRecycle() {
        return true;
    }

    @Override
    protected void recycleData() {
        if (isEnableDataRecycle() && data.size() > 1) {
            AppInfo first = data.get(0);
            List<AppInfo> temp = data.subList(1, data.size());
            temp.add(first);
            data = temp;
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_card_list, parent, false);
            holder.root = (LinearLayout) convertView.findViewById(R.id.root);
            holder.appIconIv = (ImageView) convertView.findViewById(R.id.iv_app_icon);
            holder.appNameTv = (TextView) convertView.findViewById(R.id.tv_app_name);
            holder.appVNameTv = (TextView) convertView.findViewById(R.id.tv_app_version_name);
            holder.appPnameTv = (TextView) convertView.findViewById(R.id.tv_app_package_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AppInfo appInfo = data.get(position);
        holder.appNameTv.setText(appInfo.getAppName());
        holder.appIconIv.setImageDrawable(appInfo.getAppIcon());
        holder.appVNameTv.setText("v" + appInfo.getVersionName());
        holder.appPnameTv.setText(appInfo.getPackageName());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, appInfo.getAppName(), Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    static class ViewHolder {
        LinearLayout root;
        ImageView appIconIv;
        TextView appNameTv;
        TextView appVCodeTv;
        TextView appVNameTv;
        TextView appPnameTv;
    }
}
