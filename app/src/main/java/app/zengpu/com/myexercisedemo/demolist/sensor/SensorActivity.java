/*
 * Copyright  2018  admin
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

package app.zengpu.com.myexercisedemo.demolist.sensor;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;

/**
 * Created by zengpu on 2018/7/31
 */
public class SensorActivity extends BaseActivity {

    private SensorImageView sensor_image;

    private int currentImgRes = R.mipmap.bg_sensor1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        sensor_image = findViewById(R.id.sensor_image);

        sensor_image.setOnAccListener(new SensorImageView.OnAccListener() {
            @Override
            public void onAccChanged(float acc_x, float acc_y, float acc_z) {
                float acc = (float) Math.sqrt(acc_x * acc_x + acc_y * acc_y + acc_z * acc_z);
                if (acc > 40) {
                    Log.e("SensorActivity", "哎呀，晃得头晕，你赢了！");
                    final int bgres = currentImgRes;
                    if (bgres == R.mipmap.bg_sensor1) {
                        currentImgRes = R.mipmap.bg_sensor2;
                        sensor_image.setImageResource(currentImgRes);
                    } else {
                        currentImgRes = R.mipmap.bg_sensor1;
                        sensor_image.setImageResource(currentImgRes);
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensor_image.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensor_image.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensor_image.onDestroy();
    }
}
