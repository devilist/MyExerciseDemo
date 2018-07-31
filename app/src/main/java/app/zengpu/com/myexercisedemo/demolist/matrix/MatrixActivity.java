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

package app.zengpu.com.myexercisedemo.demolist.matrix;

import android.os.Bundle;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.Utils.LogUtil;

/**
 * Created by zengpu on 2018/7/4
 */
public class MatrixActivity extends BaseActivity {

    public static final String TAG = "MyMatrixActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix);

        calcu1();
    }

    private void calcu1() {
        MyMatrix A = new MyMatrix();
//        A.setValues(new float[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        MyMatrix transMartix = new MyMatrix();
        transMartix.setValues(new float[]{1, 0, 6, 0, 1, 7, 0, 0, 1});
        MyMatrix scaleMartix = new MyMatrix();
        scaleMartix.setValues(new float[]{10, 0, 0, 0, 6, 0, 0, 0, 1});

//        A.preTranslate(6, 7);
        A.preTranslate(180, 320);
        A.preScale(2f / 3, 2f / 3);
        A.preScale(1.5f, 1.5f, 540, 960);
        LogUtil.e(TAG, "A  = " + A.toString());

        MyMatrix B = new MyMatrix();
        B.setValues(new float[]{1, 2, 3, 4, 5, 6, 7, 8, 9});

        B = matrix_multi(B, transMartix);
        B = matrix_multi(B, scaleMartix);
        LogUtil.e(TAG, "B  = " + B.toString());
    }

    private void calcu() {
        MyMatrix A = new MyMatrix();
        MyMatrix B = new MyMatrix();
        A.setValues(new float[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        B.setValues(new float[]{3, 2, 6, 4, 0, 4, 1, 8, 2});
        LogUtil.e(TAG, "===============================\n\n");
        LogUtil.e(TAG, "A  = " + A.toString());
        LogUtil.e(TAG, "B  = " + B.toString());
        LogUtil.e(TAG, "A * B = " + matrix_multi(A, B).toString());
        MyMatrix C = new MyMatrix();
        C.setConcat(A, B);
        LogUtil.e(TAG, "A setConcat B = " + C.toString());
        MyMatrix D = A;
        D.preConcat(B);
        LogUtil.e(TAG, "A preConcat B = " + D.toString());

        MyMatrix transMartix = new MyMatrix();
        transMartix.setValues(new float[]{1, 0, 6, 0, 1, 7, 0, 0, 1});
        LogUtil.e(TAG, "transMartix  = " + transMartix.toString());

        MyMatrix E = new MyMatrix();
        E.setScale(40, 50);
        LogUtil.e(TAG, "E.setScale(40, 50) = " + E.toString());
        C.setConcat(transMartix, E);
        LogUtil.e(TAG, "transMartix setConcat E = " + C.toString());
        E.postTranslate(6, 7);
        LogUtil.e(TAG, "E.postTranslate(6, 7) = " + E.toString());
        E.postRotate(45);
        LogUtil.e(TAG, "E.postRotate(45) = " + E.toString());

        MyMatrix F = new MyMatrix();
        F.setScale(40, 50);
        LogUtil.e(TAG, "F.setScale(40, 50) = " + F.toString());
        C.setConcat(F, transMartix);
        LogUtil.e(TAG, "C  setConcat transMartix = " + C.toString());
        F.preTranslate(6, 7);
        LogUtil.e(TAG, "F.preTranslate(6, 7) = " + F.toString());
        F.preRotate(45);
        LogUtil.e(TAG, "F.preRotate(45) = " + F.toString());
    }

    private MyMatrix matrix_multi(MyMatrix A, MyMatrix B) {
        if (null == A || null == B) return null;
        float[] result = new float[9];
        float[] a = new float[9];
        float[] b = new float[9];
        A.getValues(a);
        B.getValues(b);
        for (int i = 0; i < 9; i = i + 3) {
            result[i] = a[i] * b[0] + a[i + 1] * b[3] + a[i + 2] * b[6];
            result[i + 1] = a[i] * b[1] + a[i + 1] * b[4] + a[i + 2] * b[7];
            result[i + 2] = a[i] * b[2] + a[i + 1] * b[5] + a[i + 2] * b[8];
        }
        MyMatrix R = new MyMatrix();
        R.setValues(result);
        return R;
    }
}
