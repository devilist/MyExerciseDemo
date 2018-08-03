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


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;

/**
 * Created by zengpu on 2018/7/31
 */
public class SensorImageView extends AppCompatImageView implements SensorEventListener,
        ViewTreeObserver.OnGlobalLayoutListener {

    public SensorImageView(Context context) {
        this(context, null);
    }

    public SensorImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SensorImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private final static String TAG = "SensorImageView";

    private Matrix mMatrix;

    private SensorManager mSensorManager;
    private Sensor mSensor_orien;
    private Sensor mSensor_acc;

    private float mScaleFactor = 1.5f;
    private float mTransPerPixel = 5f;

    private OnAccListener mOnAccListener;

    public interface OnTranslateListener {
        void onTranslate(float x, float y);
    }

    public interface OnAccListener {
        void onAccChanged(float acc_x, float acc_y, float acc_z);
    }

    public void setOnAccListener(OnAccListener onAccListener) {
        this.mOnAccListener = onAccListener;
    }

    private void init(Context context) {
        setScaleType(ScaleType.MATRIX);
        mMatrix = new Matrix();
        reset();

        // 初始化传感器
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (null != mSensorManager) {
            mSensor_orien = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            mSensor_acc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

    }

    public void onResume() {
        if (getDrawable() == null || getDrawable().getMinimumHeight() == 0
                || getDrawable().getIntrinsicWidth() == 0) {
            if (null != mSensorManager) {
                mSensorManager.unregisterListener(this);
            }
        } else if (null != mSensorManager) {
            if (null != mSensor_orien)
                mSensorManager.registerListener(this, mSensor_orien, SensorManager.SENSOR_DELAY_UI);
            if (null != mSensor_acc)
                mSensorManager.registerListener(this, mSensor_acc, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void onPause() {
        if (null != mSensorManager) {
            mSensorManager.unregisterListener(this);
        }
    }

    public void onDestroy() {
        if (null != mSensorManager)
            mSensorManager = null;
        if (null != mSensor_orien)
            mSensor_orien = null;
        if (null != mSensor_acc)
            mSensor_acc = null;
    }

    private void reset() {
        if (null != mMatrix) mMatrix.reset();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (getDrawable() == null || getWidth() == 0 || getHeight() == 0) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }

        initScale();
    }

    private void initScale() {
        int width = getWidth();
        int height = getHeight();
        int intrinsicWidth = getDrawable().getIntrinsicWidth();
        int intrinsicHeight = getDrawable().getIntrinsicHeight();
        float scale;
        float ratio = width * 1.0f / height;
        float ratio_ori = intrinsicWidth * 1.0f / intrinsicHeight;
        if (ratio >= ratio_ori) {
            scale = width * 1f / intrinsicWidth;
        } else {
            scale = height * 1f / intrinsicHeight;
        }
        scale = scale * mScaleFactor;
        // trans to center
        int transX = getMeasuredWidth() / 2 - intrinsicWidth / 2;
        int transY = getMeasuredHeight() / 2 - intrinsicHeight / 2;
        mMatrix.reset();
        mMatrix.postTranslate(transX, transY);
        mMatrix.postScale(scale, scale, width / 2, height / 2);
        setScaleType(ScaleType.MATRIX);
        setImageMatrix(mMatrix);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            // orientation
            float rotate_Z = event.values[0];
            float rotate_X = event.values[1];  // 绕x轴转，改变的是Y向
            float rotate_Y = event.values[2];
            Log.e(TAG, "rotate_Z " + rotate_Z + " rotate_X " + rotate_X + " rotate_Y " + rotate_Y);

            // 计算平移距离。方向反一下
            float transX = -mTransPerPixel * (float) Math.sin(Math.toRadians(rotate_Y));
            float transY = -mTransPerPixel * (float) Math.sin(Math.toRadians(rotate_X));
            checkBound();
            mMatrix.postTranslate(transX, transY);
            setImageMatrix(mMatrix);
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // acc
            float acc_X = event.values[0];
            float acc_Y = event.values[1];
            float acc_Z = event.values[2];
            Log.e(TAG, "acc_X " + acc_X + " acc_Y " + acc_Y + " acc_Z " + acc_Z);
            if (null != mOnAccListener)
                mOnAccListener.onAccChanged(acc_X, acc_Y, acc_Z);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setScaleFactor(float scaleFactor) {
        if (scaleFactor < 1 && scaleFactor >= 5)
            this.mScaleFactor = 1f;
        else
            this.mScaleFactor = scaleFactor;

        reset();
    }

    public void setTransPerPixel(float transPerPixel) {
        if (Math.abs(transPerPixel) > 30f)
            mTransPerPixel = 5f;
        else
            this.mTransPerPixel = transPerPixel;
    }

    private void checkBound() {
        RectF currentBound = getCurrentBoundRectF();
        float transX = 0, transY = 0;
        if (currentBound.width() >= getWidth()) {
            if (currentBound.left > 0)
                transX = -currentBound.left;
            if (currentBound.right < getWidth())
                transX = getWidth() - currentBound.right;
        }
        if (currentBound.width() < getWidth()) {
            transX = currentBound.width() * 0.5f + getWidth() * 0.5f - currentBound.right;
        }
        if (currentBound.height() >= getHeight()) {
            if (currentBound.top > 0)
                transY = -currentBound.top;
            if (currentBound.bottom < getHeight())
                transY = getHeight() - currentBound.bottom;
        }
        if (currentBound.height() < getHeight()) {
            transY = currentBound.height() * 0.5f + getHeight() * 0.5f - currentBound.bottom;
        }
        mMatrix.postTranslate(transX, transY);
        setImageMatrix(mMatrix);
    }

    private RectF getCurrentBoundRectF() {
        RectF rectF = new RectF();
        if (getDrawable() != null) {
            rectF.set(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
            mMatrix.mapRect(rectF);
        }
        return rectF;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        reset();
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageResource(int resId) {
        reset();
        super.setImageResource(resId);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        reset();
        super.setImageDrawable(drawable);
    }
}
