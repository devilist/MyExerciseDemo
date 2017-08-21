package app.zengpu.com.myexercisedemo.demolist.cardlistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.recyclerViewPager.AppInfo;

/**
 * Created by zengp on 2017/8/21.
 */

public class CardListAdapter extends BaseAdapter {

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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_card_list, parent, false);
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
        ImageView appIconIv;
        TextView appNameTv;
        TextView appVCodeTv;
        TextView appVNameTv;
        TextView appPnameTv;
    }
}
