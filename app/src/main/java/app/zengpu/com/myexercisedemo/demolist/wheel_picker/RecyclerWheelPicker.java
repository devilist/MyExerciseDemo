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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.zengpu.com.myexercisedemo.R;

/**
 * Created by zengp on 2017/11/22.
 */

public class RecyclerWheelPicker extends RecyclerView {

    private boolean mScrollEnabled = true;
    private int mTextColor, mUnitColor, mDecorationColor;
    private float mTextSize, mUnitSize, mDecorationSize;
    private String mUnitText = "";

    private Paint mDecorationPaint;
    private TextPaint mUnitTextPaint;
    private Rect mDecorationRect;

    private WheelAdapter mAdapter;
    private WheelPickerLayoutManager layoutManager;
    private OnWheelScrollListener mListener;

    public interface OnWheelScrollListener {
        void onWheelScrollChanged(boolean isScrolling, int position, String data);
    }

    public RecyclerWheelPicker(Context context) {
        this(context, null);
    }

    public RecyclerWheelPicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerWheelPicker(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        float density = context.getResources().getDisplayMetrics().density;
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RecyclerWheelPicker);
        mDecorationSize = mTypedArray.getDimension(R.styleable.RecyclerWheelPicker_rwp_decorationSize, 35 * density);
        mDecorationColor = mTypedArray.getColor(R.styleable.RecyclerWheelPicker_rwp_decorationColor, 0xff333333);
        mTextColor = mTypedArray.getColor(R.styleable.RecyclerWheelPicker_rwp_textColor, Color.BLACK);
        mTextSize = mTypedArray.getDimension(R.styleable.RecyclerWheelPicker_rwp_textSize, 22 * scaledDensity);
        mUnitColor = mTypedArray.getColor(R.styleable.RecyclerWheelPicker_rwp_textColor, mTextColor);
        mUnitSize = mTypedArray.getDimension(R.styleable.RecyclerWheelPicker_rwp_textSize, mTextSize);
        mTypedArray.recycle();

        init(context);
    }

    private void init(Context context) {
        setOverScrollMode(OVER_SCROLL_NEVER);

        mDecorationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnitTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mDecorationRect = new Rect();

        mAdapter = new WheelAdapter(context);
        super.setAdapter(mAdapter);
        layoutManager = new WheelPickerLayoutManager(this);
        super.setLayoutManager(layoutManager);
        new LinearSnapHelper().attachToRecyclerView(this);

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (null != mListener) {
                    if (newState == SCROLL_STATE_IDLE) {
                        int centerPosition = layoutManager.findCenterItemPosition();
                        if (centerPosition == NO_POSITION)
                            mListener.onWheelScrollChanged(true, NO_POSITION, "");
                        else
                            mListener.onWheelScrollChanged(false, centerPosition, mAdapter.getData(centerPosition));
                    } else
                        mListener.onWheelScrollChanged(true, NO_POSITION, "");
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (null != mListener) {
                    if (dx == 0 && dy == 0)
                        mListener.onWheelScrollChanged(false, 0, mAdapter.getData(0));
                    else
                        mListener.onWheelScrollChanged(true, NO_POSITION, "");
                }
            }
        });
    }

    @Override
    public void setAdapter(Adapter adapter) {
    }

    public void setOnWheelScrollListener(OnWheelScrollListener listener) {
        this.mListener = listener;
    }

    public void setUnit(String unitText) {
        this.mUnitText = unitText;
    }

    public void setData(List<String> data) {
        mAdapter.setData(data, mTextColor, mTextSize);
    }

    public void setScrollEnabled(boolean scrollEnabled) {
        this.mScrollEnabled = scrollEnabled;
    }

    public boolean isScrollEnabled() {
        return mScrollEnabled;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return mScrollEnabled;
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        drawDecoration(c);
        drawUnitText(c);
    }

    private void drawDecoration(Canvas c) {
        int decorationTop = (int) (getVerticalSpace() / 2 - mDecorationSize / 2);
        int decorationBottom = (int) (getVerticalSpace() / 2 + mDecorationSize / 2);
        mDecorationRect.set(-1, decorationTop, getWidth() + 1, decorationBottom);
        mDecorationPaint.setColor(mDecorationColor);
        mDecorationPaint.setStyle(Paint.Style.STROKE);
        mDecorationPaint.setStrokeWidth(0.25f);
        c.drawRect(mDecorationRect, mDecorationPaint);
    }

    private void drawUnitText(Canvas c) {
        if (null != mUnitText && !TextUtils.isEmpty(mUnitText)) {
            mUnitTextPaint.setColor(mUnitColor);
            mUnitTextPaint.setTextSize(mUnitSize);
            float startX = getHorizontalSpace() - StaticLayout.getDesiredWidth(mUnitText, 0, mUnitText.length(), mUnitTextPaint);
            Paint.FontMetrics fontMetrics = mUnitTextPaint.getFontMetrics();
            float startY = getVerticalSpace() / 2 + mUnitSize / 2 - fontMetrics.descent / 2;
            c.drawText(mUnitText, startX, startY, mUnitTextPaint);
        }
    }

    @Override
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        // rotateX
        int centerY = getVerticalSpace() / 2;
        int childCenterY = child.getTop() + child.getHeight() / 2;
        float factor = (centerY - childCenterY) * 1f / centerY;
        float alphaFactor = 1 - 0.7f * Math.abs(factor);
        child.setAlpha(alphaFactor * alphaFactor * alphaFactor);
        float scaleFactor = 1 - 0.3f * Math.abs(factor);
        child.setScaleX(scaleFactor);
        child.setScaleY(scaleFactor);

        float rotateRadius = 2.0f * centerY / (float) Math.PI;
        float rad = (centerY - childCenterY) * 1f / rotateRadius;
        float offsetZ = rotateRadius * (1 - (float) Math.cos(rad));
        float rotateDeg = rad * 180 / (float) Math.PI;
        ViewCompat.setZ(child, -offsetZ);
        child.setRotationX(rotateDeg);

        float offsetY = centerY - childCenterY - rotateRadius * (float) Math.sin(rad) * 1.3f;
        child.setTranslationY(offsetY);

        return super.drawChild(canvas, child, drawingTime);

//        // parent centerY ,item centerY
//        int centerY = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2;
//        int childCenterY = child.getTop() + child.getHeight() / 2;
//        // alpha
//        float factor = (centerY - childCenterY) * 1f / centerY;
//        float currentFactor = 1 - 0.7f * Math.abs(factor);
//        child.setAlpha(currentFactor * currentFactor * currentFactor);
//
//        // rotate radius
//        float rotateRadius = 2.5f * centerY / (float) Math.PI;
//        // deg
//        float rad = (centerY - childCenterY) * 1f / rotateRadius;
//        float rotateDeg = rad * 180 / (float) Math.PI;
//        // for camera
//        float offsetZ = rotateRadius * (1 - (float) Math.cos(rad));
//        canvas.save();
//        // offset Y for item rotate
//        float offsetY = centerY - childCenterY - rotateRadius * (float) Math.sin(rad);
//        canvas.translate(0, offsetY);
//        mCamera.save();
//        mCamera.translate(0, 0, offsetZ);
//        mCamera.rotateX(rotateDeg);
//        mCamera.getMatrix(mMatrix);
//        mCamera.restore();
//        mMatrix.preTranslate(-child.getWidth() / 2, -childCenterY);
//        mMatrix.postTranslate(child.getWidth() / 2, childCenterY);
//        canvas.concat(mMatrix);
//        super.drawChild(canvas, child, drawingTime);
//        canvas.restore();
//        return true;
    }

    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    private int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }


    private class WheelAdapter extends Adapter<ViewHolder> {

        Context context;
        List<String> data;
        int textColor;
        float textSize;

        WheelAdapter(Context context) {
            this.context = context;
        }

        void setData(List<String> data, int textColor, float textSize) {
            this.data = data;
            this.textColor = textColor;
            this.textSize = textSize;
            notifyDataSetChanged();
        }

        @Override
        public WheelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(context);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) (textSize * 1.3f));
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.CENTER);
            return new WheelHolder(textView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (null != data) {
                TextView textView = (TextView) holder.itemView;
                textView.setText(data.get(position));
                textView.setTextColor(textColor);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                textView.setGravity(Gravity.CENTER);
            }
        }

        String getData(int position) {
            return null == data || position > data.size() - 1 ? "" : data.get(position);
        }

        @Override
        public int getItemCount() {
            return null == data ? 0 : data.size();
        }

        class WheelHolder extends ViewHolder {
            WheelHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
