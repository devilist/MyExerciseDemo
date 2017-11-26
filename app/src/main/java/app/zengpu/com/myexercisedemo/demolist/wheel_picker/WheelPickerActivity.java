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

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;

/**
 * Created by zengp on 2017/11/22.
 */

public class WheelPickerActivity extends BaseActivity {

    private RecyclerWheelPicker rv_list1, rv_list2, rv_list3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel_picker);

        initView();
    }

    private void initView() {
        final List<String> list1 = new ArrayList();
        for (int i = 1917; i < 2018; i++)
            list1.add(i + "");
        final List<String> list2 = new ArrayList();
        for (int i = 0; i < 13; i++)
            list2.add(i + "");
        final List<String> list3 = new ArrayList();
        for (int i = 1; i < 32; i++)
            list3.add(i + "");
        rv_list1 = findViewById(R.id.rv_list1);
        rv_list2 = findViewById(R.id.rv_list2);
        rv_list3 = findViewById(R.id.rv_list3);

        rv_list1.setUnit("年");
        rv_list2.setUnit("月");
        rv_list3.setUnit("日");
        rv_list1.setData(list1);
        rv_list2.setData(list2);
        rv_list3.setData(list3);

        rv_list1.setOnWheelScrollListener(new RecyclerWheelPicker.OnWheelScrollListener() {
            @Override
            public void onWheelScrollChanged(boolean isScrolling, int position, String data) {
                Log.d("RecyclerWheelPicker", "isScrolling " + isScrolling + " position " + position + " data " + data);
                rv_list2.setScrollEnabled(!isScrolling);
                rv_list3.setScrollEnabled(!isScrolling);
            }
        });
    }
}
