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
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.wheel_picker.adapter.WheelPickerAdapter;

/**
 * Created by zengp on 2017/11/22.
 */

public class WheelPickerActivity extends BaseActivity {

    private RecyclerView rv_list1, rv_list2, rv_list3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel_picker);

        initView();
    }

    private void initView() {
        final List<String> list1 = new ArrayList();
        list1.add("咦放到");
        list1.add("发地方");
        list1.add("呃发");
        list1.add("有规划");
        list1.add("去");
        list1.add("不覆盖");
        list1.add("你");
        list1.add("吗覆盖");
        list1.add("咦身高");
        list1.add("发是的");
        list1.add("呃呃");
        list1.add("有呃");
        list1.add("去地方");
        list1.add("不");
        list1.add("咦放到");
        list1.add("发地方");
        list1.add("呃发");
        list1.add("有规划");
        list1.add("去");
        list1.add("不覆盖");
        list1.add("你");
        list1.add("吗覆盖");
        list1.add("咦身高");
        list1.add("发是的");
        list1.add("呃呃");
        list1.add("有呃");
        list1.add("去地方");
        list1.add("不");


        final List<String> list2 = new ArrayList();
        for (int i = 0; i < 50; i++)
            list2.add(i * 123 + "");
        rv_list1 = findViewById(R.id.rv_list1);
        rv_list2 = findViewById(R.id.rv_list2);
        rv_list3 = findViewById(R.id.rv_list3);

        final List<String> list3 = new ArrayList();
        list3.add("同仁堂");
        list3.add("不对方的");
        list3.add("狗肉馆");
        list3.add("归属感");
        list3.add("第三个");
        list3.add("衣原体");
        list3.add("还没换过");
        list3.add("额热腾腾有人");
        list3.add("符合规定和");
        list3.add("啊师傅");
        list3.add("对方的");
        list3.add("公费");
        list3.add("同仁堂");
        list3.add("不对方的");
        list3.add("狗肉馆");
        list3.add("归属感");
        list3.add("第三个");
        list3.add("衣原体");
        list3.add("还没换过");
        list3.add("额热腾腾有人");
        list3.add("符合规定和");
        list3.add("啊师傅");
        list3.add("对方的");
        list3.add("公费");
        list3.add("同仁堂");
        list3.add("不对方的");
        list3.add("狗肉馆");
        list3.add("归属感");
        list3.add("第三个");
        list3.add("衣原体");
        list3.add("还没换过");
        list3.add("额热腾腾有人");
        list3.add("符合规定和");
        list3.add("啊师傅");
        list3.add("对方的");
        list3.add("公费");
        rv_list1.setAdapter(new WheelPickerAdapter(this, list1));
        rv_list2.setAdapter(new WheelPickerAdapter(this, list2));
        rv_list3.setAdapter(new WheelPickerAdapter(this, list3));

    }
}
