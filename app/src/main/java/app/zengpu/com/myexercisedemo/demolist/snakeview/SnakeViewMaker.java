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

package app.zengpu.com.myexercisedemo.demolist.snakeview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * a view wrapper that makes the wrapped view move like a snake when touched and dragged
 * Created by zengp on 2017/11/6.
 */

public class SnakeViewMaker implements View.OnTouchListener {

    private Context mContext;
    private View mTargetView;
    private ViewGroup mAttachViewGroup;

    private int mChildCount = 5;
    private List<ImageView> mChildren = new ArrayList<>();

    private int mTargetWidth = 0;
    private int mTargetHeight = 0;
    private Bitmap mTargetBitmap = null;
    private int[] mTargetLocation = new int[]{0, 0};
    private int mContentTopInWindow = 0; // statusbar height + titlebar height


    private final int mDragDelay = 100;
    private final float mMaxVelocity = 1000;
    private VelocityTracker mVelocityTracker = null;


    public SnakeViewMaker(Context context) {
        this.mContext = context;
    }

    public SnakeViewMaker addTargetView(View target) {
        this.mTargetView = target;
        return this;
    }

    public void attachToRootLayout(ViewGroup attach) {
        if (attach instanceof LinearLayout) {
            Log.e("SnakeViewMaker", "view parent can not be LinearLayout!");
        } else {
            this.mAttachViewGroup = attach;
            updateTargetViewCache();
            if (mTargetHeight == 0 || mTargetWidth == 0 || null == mTargetBitmap) {
                // if it is the first time to attach, the target view may be not finishing the drawing process,
                // so a listener is needed to observe the view draw finish event
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    mTargetView.getViewTreeObserver().addOnWindowFocusChangeListener(
                            new ViewTreeObserver.OnWindowFocusChangeListener() {
                                @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
                                @Override
                                public void onWindowFocusChanged(boolean hasFocus) {
                                    Log.d("SnakeViewMaker", "hasFocus " + hasFocus);
                                    if (hasFocus) {
                                        mTargetView.getViewTreeObserver().removeOnWindowFocusChangeListener(this);
                                        // width height bitmap cache
                                        updateTargetViewCache();
                                        // attach child as soon as the target view drawing finished
                                        attachToRootLayoutInternal();
                                    }
                                }
                            });
                } else {
                    mTargetView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateTargetViewCache();
                            attachToRootLayoutInternal();
                        }
                    }, 3000);
                }
            } else {
                attachToRootLayoutInternal();
            }
        }
    }

    private void updateTargetViewCache() {
        mTargetHeight = mTargetView.getHeight();
        mTargetWidth = mTargetView.getWidth();
        mTargetView.setDrawingCacheEnabled(true);
        mTargetView.buildDrawingCache();
        Bitmap bitmap = mTargetView.getDrawingCache();
        if (null != bitmap) {
            mTargetBitmap = Bitmap.createBitmap(bitmap);
        }
        mTargetView.setDrawingCacheEnabled(false);

    }

    private void attachToRootLayoutInternal() {
        if (!mChildren.isEmpty()) {
            for (View child : mChildren)
                mAttachViewGroup.removeView(child);
            mChildren.clear();
        }
        updateTargetViewLocation();
        // add child
        for (int i = 0; i < mChildCount; i++) {
            ImageView child_i = new ImageView(mTargetView.getContext());
            mAttachViewGroup.addView(child_i);
            child_i.getLayoutParams().width = mTargetWidth;
            child_i.getLayoutParams().height = mTargetHeight;
            child_i.setScaleType(ImageView.ScaleType.CENTER_CROP);
            child_i.setImageBitmap(mTargetBitmap);
            child_i.setTranslationX(mTargetLocation[0]);
            child_i.setTranslationY(mTargetLocation[1]);
            float alpha = i == mChildCount - 1 ? 1f : 0.5f / mChildCount * (i + 1);
            child_i.setAlpha(alpha);
            ViewCompat.setTranslationZ(child_i, 20 * i);
            mChildren.add(child_i);
            if (i == mChildCount - 1) {
                child_i.setOnTouchListener(this);
                // onClick event
                child_i.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTargetView.performClick();
                    }
                });
            }
        }
        mTargetView.setVisibility(View.INVISIBLE);
    }

    public void detachSnake() {
        mTargetView.setVisibility(View.VISIBLE);
        if (null != mAttachViewGroup) {
            for (View child : mChildren)
                mAttachViewGroup.removeView(child);
            mChildren.clear();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        int velocityX = 0;
        int velocityY = 0;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mChildren.get(mChildCount - 1).setFocusable(true);
            mChildren.get(mChildCount - 1).setFocusableInTouchMode(true);
            mChildren.get(mChildCount - 1).requestFocus();
            mChildren.get(mChildCount - 1).requestFocusFromTouch();
            updateTargetViewLocation();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (event.getEventTime() - event.getDownTime() >= 100) {
                float accX = 0;
                float accY = 0;
                if (mVelocityTracker != null) {
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    velocityX = (int) mVelocityTracker.getXVelocity();
                    velocityY = (int) mVelocityTracker.getYVelocity();
                    accX = velocityX / (mMaxVelocity);
                    accY = velocityY / (mMaxVelocity);
                }
                dragView(event.getRawX() - mTargetWidth / 2,
                        event.getRawY() - mTargetHeight / 2 - mContentTopInWindow,
                        velocityX, velocityY, accX, accY);
                mChildren.get(mChildCount - 1).setClickable(false);
                return true;
            }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mChildren.get(mChildCount - 1).setClickable(true);
            releaseView();
            if (event.getEventTime() - event.getDownTime() < 100) {
                return false;
            } else
                return true;
        }
        return false;
    }

    private void dragView(final float currentX, final float currentY,
                          float velocityX, float velocityY, float accX, float accY) {
//        Log.d("SnakeViewMaker", "vx " + velocityX + " vy " + velocityY
//                + " accX " + accX + " accY " + accY
//                + " x " + currentX + " y " + currentY);
        for (int i = 0; i < mChildCount; i++) {
            final View child_i = mChildren.get(i);
            final float transX = currentX;
            final float transY = currentY;
            final int delay = mDragDelay * (mChildCount - 1 - i);
            child_i.postDelayed(new Runnable() {
                @Override
                public void run() {
                    child_i.setTranslationX(transX);
                    child_i.setTranslationY(transY);
                    child_i.requestLayout();
                }
            }, delay);
        }
    }

    private void releaseView() {
        final Interpolator interpolator = new OvershootInterpolator();
        final int duration = 700;
        for (int i = 0; i < mChildCount; i++) {
            final View child_i = mChildren.get(i);
            final int delay = mDragDelay * (mChildCount - 1 - i);
            child_i.postDelayed(new Runnable() {
                @Override
                public void run() {
                    child_i.animate()
                            .translationX(mTargetLocation[0])
                            .translationY(mTargetLocation[1])
                            .setDuration(duration)
                            .setInterpolator(interpolator)
                            .start();
                }
            }, delay);
        }
    }

    private void updateTargetViewLocation() {
        mTargetView.getLocationOnScreen(mTargetLocation);
        int statusBarHeight = getStatusBarHeight(mContext);
        int titleBarHeight = 0;
        if (mContext instanceof Activity) {
            // actionbar height
            Activity activity = (Activity) mContext;
            ViewGroup content = activity.findViewById(android.R.id.content);
            titleBarHeight = content.getTop();
            // statusbar height
            // compat
            boolean isTranslucentStatus = false;
            boolean isFitSystemWindows = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WindowManager.LayoutParams params = activity.getWindow().getAttributes();
                isTranslucentStatus = (params.flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) != 0;
                if (null != content.getChildAt(0))
                    isFitSystemWindows = content.getChildAt(0).getFitsSystemWindows();

                if (isTranslucentStatus && !isFitSystemWindows)
                    statusBarHeight = 0;
            }
            Log.d("SnakeViewMaker", "isTranslucentStatus " + isTranslucentStatus);
            Log.d("SnakeViewMaker", "isFitSystemWindows " + isFitSystemWindows);
        }
        Log.d("SnakeViewMaker", "statusBarHeight " + statusBarHeight);
        Log.d("SnakeViewMaker", "titleBarHeight " + titleBarHeight);
        mContentTopInWindow = titleBarHeight + statusBarHeight;
        int top = mTargetLocation[1] - mContentTopInWindow;
        mTargetLocation[1] = top;
    }

    private float getOvershootInterpolation(float tension, float t) {
        t -= 1.0f;
        return t * t * ((tension + 1) * t + tension) + 1.0f;
    }

    private int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
}
