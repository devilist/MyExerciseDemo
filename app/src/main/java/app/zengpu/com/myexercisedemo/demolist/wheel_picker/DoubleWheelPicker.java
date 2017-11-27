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
import app.zengpu.com.myexercisedemo.demolist.wheel_picker.parser.DataParser;

/**
 * Created by zengp on 2017/11/26.
 */

@SuppressLint("ValidFragment")
public class DoubleWheelPicker extends WheelPicker {

    private TextView tv_cancel, tv_ok;
    private RecyclerWheelPicker rv_picker1, rv_picker2;
    private String pickData1 = "", pickData2 = "";

    private List<Data> datas = new ArrayList<>();

    private DoubleWheelPicker(Builder builder) {
        super(builder);
    }

    public static Builder instance() {
        return new Builder<DoubleWheelPicker>(DoubleWheelPicker.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (builder.gravity == Gravity.BOTTOM) window.setGravity(Gravity.BOTTOM);
        View contentView = inflater.inflate(R.layout.dialog_wheel_picker_double, container, false);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        tv_ok = (TextView) getView().findViewById(R.id.tv_ok);
        tv_cancel = (TextView) getView().findViewById(R.id.tv_cancel);
        rv_picker1 = (RecyclerWheelPicker) getView().findViewById(R.id.rv_picker1);
        rv_picker2 = (RecyclerWheelPicker) getView().findViewById(R.id.rv_picker2);
        tv_ok.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        datas = DataParser.parserData(getContext(), builder.resInt, builder.isAll);

        String[] units = builder.units;
        if (null != units) {
            if (units.length > 0)
                rv_picker1.setUnit(units[0]);
            if (units.length > 1)
                rv_picker1.setUnit(units[1]);
        }
        rv_picker1.setData(datas);
        rv_picker2.setData(datas.get(0).items);

        rv_picker1.setOnWheelScrollListener(this);
        rv_picker2.setOnWheelScrollListener(this);
    }

    @Override
    public void onWheelScrollChanged(RecyclerWheelPicker wheelPicker, boolean isScrolling, int position, Data data) {
        super.onWheelScrollChanged(wheelPicker, isScrolling, position, data);
        if (wheelPicker == rv_picker1) {
            if (!isScrolling && null != data) {
                pickData1 = data.data;
                rv_picker2.setData(data.items);
            }
        } else if (wheelPicker == rv_picker2) {
            if (!isScrolling && null != data)
                pickData2 = data.data;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.tv_ok) {
            if (!rv_picker1.isScrolling()
                    && !rv_picker2.isScrolling()
                    && null != builder.pickerListener) {
                builder.pickerListener.onPickResult(pickData1, pickData2, "");
            }
        }
        rv_picker1.release();
        rv_picker2.release();
        dismiss();
    }
}
