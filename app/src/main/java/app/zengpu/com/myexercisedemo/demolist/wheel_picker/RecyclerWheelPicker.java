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

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by zengp on 2017/11/22.
 */

public class RecyclerWheelPicker extends RecyclerView {

    private int mChildHeight = 0;
    private Paint mDecorationPaint;
    private Rect mDecorationRect;

    public RecyclerWheelPicker(Context context) {
        this(context, null);
    }

    public RecyclerWheelPicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerWheelPicker(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOverScrollMode(OVER_SCROLL_NEVER);
        mDecorationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDecorationRect = new Rect();

        WheelPickerLayoutManager layoutManager = new WheelPickerLayoutManager(this);
        setLayoutManager(layoutManager);

        new LinearSnapHelper().attachToRecyclerView(this);

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("RecyclerWheelPicker ", " onScrollStateChanged");
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                int centerY = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2;
//                float radius = 2.5f * centerY / (float) Math.PI;
//                for (int i = 0; i < recyclerView.getChildCount(); i++) {
//                    View child = recyclerView.getChildAt(i);
//                    if (null != child) {
//                        int childCenterY = child.getTop() + child.getHeight() / 2;
//                        float factor = (centerY - childCenterY) * 1f / centerY;
//                        float rad = (centerY - childCenterY) * 1f / radius;
//                        float offsetZ = centerY * (1 - (float) Math.cos(rad));
//                        float rotateDeg = rad * 180 / (float) Math.PI;
//                        ViewCompat.setZ(child, -offsetZ);
//                        child.setRotationX(rotateDeg);
//                        float currentFactor = 1 - 0.7f * Math.abs(factor);
//                        child.setAlpha(currentFactor * currentFactor * currentFactor);
//                    }
//                }
            }
        });
    }



    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        if (getChildCount() > 0)
            mChildHeight = getChildAt(0).getHeight();
        if (mChildHeight == 0) return;
        int topLineStartY = getVerticalSpace() / 2 - mChildHeight / 2;
        int bottomLineStartY = getVerticalSpace() / 2 + mChildHeight / 2;

        mDecorationRect.set(-1, topLineStartY, getWidth() + 1, bottomLineStartY);
        mDecorationPaint.setColor(0xff333333);
        mDecorationPaint.setStyle(Paint.Style.STROKE);
        mDecorationPaint.setStrokeWidth(0.5f);
        c.drawRect(mDecorationRect, mDecorationPaint);
    }

    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }
}
