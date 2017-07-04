package app.zengpu.com.myexercisedemo.demolist.advancepagerslidingtabstrip.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.rich_textview.RichTextView0;


/**
 * Created by linhonghong on 2015/8/11.
 */
public class ThirdFragment extends Fragment {
    RichTextView0 rtv_0;

    public static ThirdFragment instance() {
        ThirdFragment view = new ThirdFragment();
        return view;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRichTextview();
    }


    private void initRichTextview() {
        rtv_0 = (RichTextView0) getView().findViewById(R.id.rtv_0);
        rtv_0.setOnPrefixClickListener(new RichTextView0.OnPrefixClickListener() {
            @Override
            public void onPrefixClick(View v, String text) {
                Toast.makeText(getContext(), "点击了前缀区域", Toast.LENGTH_SHORT).show();
            }

//            @Override
//            public void onTextClick() {
//                Toast.makeText(getContext(), "点击了文字区域", Toast.LENGTH_SHORT).show();
//            }
        });
        rtv_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "点击了文字区域", Toast.LENGTH_SHORT).show();
            }
        });

        getView().findViewById(R.id.tv_tip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "richtextview", Toast.LENGTH_SHORT).show();
            }
        });
    }
}