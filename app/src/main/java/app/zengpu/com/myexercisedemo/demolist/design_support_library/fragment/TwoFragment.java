package app.zengpu.com.myexercisedemo.demolist.design_support_library.fragment;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.design_support_library.activity.DSLScrollingActivity;
import app.zengpu.com.myexercisedemo.demolist.design_support_library.adapter.OneRecyclerViewAdapter;


/**
 * Created by linhonghong on 2015/8/11.
 */
public class TwoFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private OneRecyclerViewAdapter mAdapter;
    private List<String> list = new ArrayList<>();
    private List<Drawable> drawablelist = new ArrayList<>();

    public static TwoFragment instance() {
        TwoFragment view = new TwoFragment();
        return view;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initView();
    }

    private void initView() {

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new OneRecyclerViewAdapter(getContext(), list,drawablelist);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OneRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                switch (view.getId()) {
                    case R.id.iv_icon:
                        Toast.makeText(getContext(), "kvkvvvvvv", Toast.LENGTH_SHORT).show();
                        DSLScrollingActivity.actionStart(getContext());
                        break;
                }
            }

        });
    }

    private void initData() {
        List<PackageInfo> packages = getContext().getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packages.size(); i++) {

            PackageInfo packageInfo = packages.get(i);
            String appName = packageInfo.applicationInfo.loadLabel(getContext().getPackageManager()).toString();
            Drawable appIcon = packageInfo.applicationInfo.loadIcon(getContext().getPackageManager());

            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                list.add(appName);
                drawablelist.add(appIcon);
            }

        }
    }
}
