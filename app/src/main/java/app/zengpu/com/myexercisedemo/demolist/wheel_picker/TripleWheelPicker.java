/*
 * Copyright  2017  zengp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.zengpu.com.myexercisedemo.demolist.wheel_picker;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.wheel_picker.bean.Data;
import app.zengpu.com.myexercisedemo.demolist.wheel_picker.core.RecyclerWheelPicker;
import app.zengpu.com.myexercisedemo.demolist.wheel_picker.dialog.WheelPicker;

/**
 * Created by zengp on 2017/11/26.
 */

@SuppressLint("ValidFragment")
public class TripleWheelPicker extends WheelPicker {

    private TextView tv_cancel, tv_ok;
    private RecyclerWheelPicker rv_picker1, rv_picker2, rv_picker3;
    private String pickData1 = "", pickData2 = "", pickData3 = "";

    private TripleWheelPicker(Builder builder) {
        super(builder);
    }

    public static Builder instance() {
        return new Builder<TripleWheelPicker>(TripleWheelPicker.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (builder.gravity == Gravity.BOTTOM) window.setGravity(Gravity.BOTTOM);
        View contentView = inflater.inflate(R.layout.dialog_wheel_picker_triple, container, false);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final List<String> list1 = new ArrayList();
        for (int i = 1917; i < 2018; i++)
            list1.add(i + "");
        final List<String> list2 = new ArrayList();
        for (int i = 0; i < 13; i++)
            list2.add(i + "");
        final List<String> list3 = new ArrayList();
        for (int i = 1; i < 32; i++)
            list3.add(i + "");
        tv_ok = (TextView) getView().findViewById(R.id.tv_ok);
        tv_cancel = (TextView) getView().findViewById(R.id.tv_cancel);
        rv_picker1 = (RecyclerWheelPicker) getView().findViewById(R.id.rv_picker1);
        rv_picker2 = (RecyclerWheelPicker) getView().findViewById(R.id.rv_picker2);
        rv_picker3 = (RecyclerWheelPicker) getView().findViewById(R.id.rv_picker3);

        tv_ok.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        rv_picker1.setUnit("年");
        rv_picker2.setUnit("月");
        rv_picker3.setUnit("日");
//        rv_picker1.setData(list1);
//        rv_picker2.setData(list2);
//        rv_picker3.setData(list3);

        rv_picker1.setOnWheelScrollListener(this);
        rv_picker2.setOnWheelScrollListener(this);
        rv_picker3.setOnWheelScrollListener(this);
    }

    @Override
    public void onWheelScrollChanged(RecyclerWheelPicker wheelPicker, boolean isScrolling, int position, Data data) {
        super.onWheelScrollChanged(wheelPicker, isScrolling, position, data);
        if (wheelPicker == rv_picker1) {
            rv_picker2.setScrollEnabled(!isScrolling);
            rv_picker3.setScrollEnabled(!isScrolling);
            if (!isScrolling)
                pickData1 = data.data;
        } else if (wheelPicker == rv_picker2) {
            rv_picker3.setScrollEnabled(!isScrolling);
            if (!isScrolling)
                pickData2 = data.data;
        } else if (wheelPicker == rv_picker3) {
            if (!isScrolling)
                pickData3 = data.data;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.tv_ok) {
            if (!rv_picker1.isScrolling()
                    && !rv_picker2.isScrolling()
                    && !rv_picker3.isScrolling()
                    && null != builder.pickerListener) {
                builder.pickerListener.onPickResult(pickData1, pickData2, pickData3);
            }
        }
        rv_picker1.release();
        rv_picker2.release();
        rv_picker3.release();
        dismiss();
    }
}
