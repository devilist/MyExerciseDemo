package app.zengpu.com.myexercisedemo.demolist.advancepagerslidingtabstrip.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.zengpu.com.myexercisedemo.R;


/**
 * Created by linhonghong on 2015/8/11.
 */
public class ThirdFragment  extends Fragment {

    public static ThirdFragment instance() {
        ThirdFragment view = new ThirdFragment();
        return view;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, null);
        return view;
    }
}