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
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.wheel_picker.dialog.WheelPicker;

/**
 * Created by zengp on 2017/11/22.
 */

public class WheelPickerActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_single, tv_double, tv_triple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel_picker);

        tv_single = findViewById(R.id.tv_single);
        tv_double = findViewById(R.id.tv_double);
        tv_triple = findViewById(R.id.tv_triple);

        tv_single.setOnClickListener(this);
        tv_double.setOnClickListener(this);
        tv_triple.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_triple:
                TripleWheelPicker.instance()
                        .setGravity(Gravity.BOTTOM)
                        .setResource(R.raw.picker_location)
                        .setDefPosition(15, 5)
                        .showAllItem(true)
                        .setPickerListener(new WheelPicker.OnPickerListener() {
                            @Override
                            public void onPickResult(String... result) {
                                Log.d("RecyclerWheelPicker", "result " + result[0] + "-" + result[1] + "-" + result[2]);
                                Toast.makeText(WheelPickerActivity.this,
                                        result[0] + "-" + result[1] + "-" + result[2], Toast.LENGTH_SHORT).show();
                            }
                        }).build().show(getSupportFragmentManager());
                break;
            case R.id.tv_double:
                DoubleWheelPicker.instance()
                        .setGravity(Gravity.BOTTOM)
                        .setResource(R.raw.picker_location)
                        .setDefPosition(15, 0)
                        .setPickerListener(new WheelPicker.OnPickerListener() {
                            @Override
                            public void onPickResult(String... result) {
                                Log.d("RecyclerWheelPicker", "result " + result[0] + "-" + result[1]);
                                Toast.makeText(WheelPickerActivity.this,
                                        result[0] + "-" + result[1], Toast.LENGTH_SHORT).show();
                            }
                        }).build().show(getSupportFragmentManager());
                break;
            case R.id.tv_single:
                SingleWheelPicker.instance()
                        .setGravity(Gravity.BOTTOM)
                        .setResource(R.raw.picker_location)
                        .showAllItem(false)
                        .setDefPosition(15, 5)
                        .setDefValues("浙江", "杭州")
                        .setPickerListener(new WheelPicker.OnPickerListener() {
                            @Override
                            public void onPickResult(String... result) {
                                Log.d("RecyclerWheelPicker", "result " + result[0]);
                                Toast.makeText(WheelPickerActivity.this,
                                        result[0], Toast.LENGTH_SHORT).show();
                            }
                        }).build().show(getSupportFragmentManager());
                break;
        }
    }
}
