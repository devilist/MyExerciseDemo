package app.zengpu.com.myexercisedemo.demolist.design_support_library.fragment;

import android.content.Intent;
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
import app.zengpu.com.myexercisedemo.Utils.CustomAlertDialog;
import app.zengpu.com.myexercisedemo.demolist.advancepagerslidingtabstrip.activity.ApstActivity;
import app.zengpu.com.myexercisedemo.demolist.design_support_library.adapter.OneRecyclerViewAdapter;
import app.zengpu.com.myexercisedemo.demolist.galleryfinaldemo.GalleryFinalActivity;
import app.zengpu.com.myexercisedemo.demolist.multi_drawer.MultiDrawerActivity;
import app.zengpu.com.myexercisedemo.demolist.photoloop0.PhotoLoopActivity;
import app.zengpu.com.myexercisedemo.demolist.photoloop1.ImageLoopActivity;
import app.zengpu.com.myexercisedemo.demolist.pull_to_refresh.RefreshAndLoadActivity;
import app.zengpu.com.myexercisedemo.demolist.videoRecord.VideoAppendActivity;


/**
 * Created by linhonghong on 2015/8/11.
 */
public class OneFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private OneRecyclerViewAdapter mAdapter;
    private List<String[]> list = new ArrayList<>();

    public static OneFragment instance() {
        OneFragment view = new OneFragment();
		return view;
	}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgment_one, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initView();
    }

    private void initView(){

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new OneRecyclerViewAdapter(getContext(), list);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OneRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (position == 0) {
                    new CustomAlertDialog(getContext(),
                            R.string.dialog_title,
                            R.string.dialog_message,
                            null, new CustomAlertDialog.AlertDialogClickListener() {
                        @Override
                        public void onResult(boolean confirmed, Bundle bundle) {

                            Toast.makeText(getContext(), String.valueOf(confirmed), Toast.LENGTH_SHORT).show();
                        }
                    }, true, R.style.MyAlertDialog).show();
                } else {
                    try {
                        Class<?> activityClazz = Class.forName(list.get(position)[1]);
                        Intent intent = new Intent(getContext(), activityClazz);
                        startActivity(intent);

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initData() {
        list.add(new String[]{"CustomAlertDialog", CustomAlertDialog.class.getName()});
        list.add(new String[]{"muti drawer ", MultiDrawerActivity.class.getName()});
        list.add(new String[]{"image loop:ViewPager+Handler", PhotoLoopActivity.class.getName()});
        list.add(new String[]{"image loop:ViewPager+timer", ImageLoopActivity.class.getName()});
        list.add(new String[]{"refresh and load ", RefreshAndLoadActivity.class.getName()});
        list.add(new String[]{"GalleryFinal", GalleryFinalActivity.class.getName()});
        list.add(new String[]{"video record", VideoAppendActivity.class.getName()});
        list.add(new String[]{"APST", ApstActivity.class.getName()});
    }
}
