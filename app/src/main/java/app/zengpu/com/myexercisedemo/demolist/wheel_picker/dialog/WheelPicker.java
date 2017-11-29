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

package app.zengpu.com.myexercisedemo.demolist.wheel_picker.dialog;


import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;

import app.zengpu.com.myexercisedemo.demolist.wheel_picker.bean.Data;
import app.zengpu.com.myexercisedemo.demolist.wheel_picker.widget.RecyclerWheelPicker;


/**
 * dialogFragment with custom enter and exit animation(or animator)
 * <p>
 * Created by zengp on 2017/9/5.
 */
@SuppressLint("ValidFragment")
public class WheelPicker extends DialogFragment implements Runnable,
        View.OnClickListener, RecyclerWheelPicker.OnWheelScrollListener {

    private long mEnterAnimDuration = 400, mExitAnimDuration = 300;

    private boolean isEnterAnimFinish = false;
    private boolean isEXitAnimFinish = false;
    protected int width, height;

    protected Builder builder;

    protected OnDismissListener mListener;

    public interface OnDismissListener {
        void onDismiss();
    }

    public interface OnPickerListener {
        void onPickResult(String... result);
    }

    public WheelPicker(Builder builder) {
        this.builder = builder;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Point realPoint = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getActivity().getWindowManager().getDefaultDisplay().getRealSize(realPoint);
        } else {
            getActivity().getWindowManager().getDefaultDisplay().getSize(realPoint);
        }
        width = realPoint.x;
        height = realPoint.y;

    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.mListener = listener;
    }

    protected void parseData() {
    }

    protected void pickerClose() {
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onWheelScrollChanged(RecyclerWheelPicker wheelPicker,
                                     boolean isScrolling, int position, Data data) {
    }


    public void setEnterAnimDuration(long duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Anim duration cannot be negative");
        }
        this.mEnterAnimDuration = duration;
    }

    public void setExitAnimDuration(long duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Anim duration cannot be negative");
        }
        this.mExitAnimDuration = duration;
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (builder.gravity == Gravity.BOTTOM)
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void doEnterAnim(final View contentView, long animDuration) {
        ValueAnimator enterAnimator = ValueAnimator.ofFloat(contentView.getHeight(), 0);
        enterAnimator.setDuration(animDuration);
        enterAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                contentView.setTranslationY(value);
            }
        });
        enterAnimator.start();
    }

    public void doExitAnim(final View contentView, long animDuration) {
        ValueAnimator exitAnimator = ValueAnimator.ofFloat(0, contentView.getHeight());
        exitAnimator.setDuration(animDuration);
        exitAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                contentView.setTranslationY(value);
            }
        });
        exitAnimator.start();
    }

    @Override
    final public void dismiss() {
        if (!isEXitAnimFinish) {
            doExitAnim(getView(), mExitAnimDuration);
            getView().postDelayed(this, mExitAnimDuration);
            isEXitAnimFinish = true;
        }
    }

    public void show(FragmentManager manager) {
        super.show(manager, builder.clazz.getName());
    }

    @NonNull
    @Override
    final public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new InnerAnimDialog(getActivity(), getTheme());
    }

    @Override
    final public void run() {
        super.dismiss();
        this.pickerClose();
        if (null != mListener)
            mListener.onDismiss();
    }

    public static class Builder<T extends WheelPicker> {
        protected Class clazz;
        public int gravity = Gravity.BOTTOM;
        @RawRes
        public int resInt = 0;
        public boolean isAll = false;
        public String[] units, defValues;
        public int[] defPosition;
        public OnPickerListener pickerListener;

        public Builder(Class clazz) {
            this.clazz = clazz;
        }

        public Builder setPickerListener(OnPickerListener pickerListener) {
            this.pickerListener = pickerListener;
            return this;
        }

        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setResource(@RawRes int resInt) {
            this.resInt = resInt;
            return this;
        }

        public Builder showAllItem(boolean all) {
            isAll = all;
            return this;
        }

        public Builder setUnits(String... units) {
            this.units = units;
            return this;
        }

        public Builder setDefValues(String... values) {
            this.defValues = values;
            return this;
        }

        public Builder setDefPosition(int... defPosition) {
            this.defPosition = defPosition;
            return this;
        }

        public T build() {
            try {
                Constructor constructor = clazz.getDeclaredConstructor(Builder.class);
                constructor.setAccessible(true);
                return (T) constructor.newInstance(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class InnerAnimDialog extends Dialog implements Runnable {

        InnerAnimDialog(@NonNull Context context, @StyleRes int themeResId) {
            super(context, themeResId);
        }

        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            super.onWindowFocusChanged(hasFocus);
            if (!isEnterAnimFinish) {
                doEnterAnim(getView(), mEnterAnimDuration);
                isEnterAnimFinish = true;
            }
        }

        @Override
        public void cancel() {
            if (!isCancelable())
                return;
            if (!isEXitAnimFinish) {
                doExitAnim(getView(), mExitAnimDuration);
                getView().postDelayed(this, mExitAnimDuration);
                isEXitAnimFinish = true;
            }
        }

        @Override
        public void run() {
            super.cancel();
            pickerClose();
            if (null != mListener)
                mListener.onDismiss();
        }
    }
}
