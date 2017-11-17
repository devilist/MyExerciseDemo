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

package app.zengpu.com.myexercisedemo.demolist.imageview_crop;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.Utils.LogUtil;

/**
 * Created by zengp on 2017/11/15.
 */

public class CropImageViewActivity extends BaseActivity implements CropImageView.ImageCropListener {
    private CropImageView imageView;
    private Button btn_crop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview_crop);

        btn_crop = findViewById(R.id.btn_crop);
        imageView = findViewById(R.id.iv_crop);

        String url = "http://img1.guiquan.miaotu.net/2016-07-07/c5aa826edc5d35d8c231f6bd074371ca.jpg";
        Glide.with(this).load(url).asBitmap().into(imageView);
        String externalPath = Environment.getExternalStorageDirectory().getPath() + "/" + getPackageName() + "/";
        imageView.cropOutSize(1000, 1000)
                .cropSaveDir(externalPath)
                .addCropListener(this);


        btn_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.cropToFile();
            }
        });
    }

    @Override
    public void onCropResult(String filePath, String message, boolean isCropSuccess) {
        LogUtil.d("CropImageView", "isCropSuccess " + isCropSuccess + " message " + message + " filepath " + filePath);
    }
}
