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
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import app.zengpu.com.myexercisedemo.Utils.LogUtil;

/**
 * Created by zengp on 2017/11/22.
 */

public class WheelPickerLayoutManager extends LinearLayoutManager {

    private RecyclerView mRecyclerView;
    private int mVerticalOffset = 0; // offset in vertical orientation when scrolling
    private int mItemHeight = 0;
    private int mMaxOverScrollOffset = 0;
    private SparseArray<Rect> mItemAreas; // record all visible child display area

    public WheelPickerLayoutManager(RecyclerView recyclerView) {
        super(recyclerView.getContext());
        this.mRecyclerView = recyclerView;
        setOrientation(VERTICAL);
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(VERTICAL);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (getItemCount() <= 0 || state.isPreLayout()) return;

        // layout algorithm:
        // 1.scrap all attached views
        // 2. record all visible items display area
        // 3. fill views

        detachAndScrapAttachedViews(recycler);

        View first = recycler.getViewForPosition(0);
        measureChildWithMargins(first, 0, 0);
        int childWidth = getDecoratedMeasuredWidth(first);
        int childHeight = getDecoratedMeasuredHeight(first);

        mItemHeight = childHeight;
        mMaxOverScrollOffset = getVerticalSpace() / 2 + childHeight / 2;

        // record all the visible items rect
        mItemAreas = new SparseArray<>();
        // first item layout in center
        int topToCenterOffset = getVerticalSpace() / 2 - childHeight / 2;
        int offsetHeight = getPaddingTop() + topToCenterOffset;
        for (int i = 0; i < getItemCount(); i++) {
            Rect rect = new Rect(getPaddingLeft(), offsetHeight, childWidth, offsetHeight + childHeight);
            mItemAreas.put(i, rect);
            offsetHeight += childHeight;
        }

        // fill views
        fillView(recycler, state);
    }

    private void fillView(RecyclerView.Recycler recycler, RecyclerView.State state) {

        if (getItemCount() <= 0 || state.isPreLayout()) return;

        // the visible area for the RecyclerView
        Rect displayArea = new Rect(0, mVerticalOffset, getHorizontalSpace(),
                getVerticalSpace() + mVerticalOffset);

        // remove invisible child
        Rect rect = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            View item = getChildAt(i);
            rect.set(getDecoratedLeft(item), getDecoratedTop(item),
                    getDecoratedRight(item), getDecoratedBottom(item));
            if (!Rect.intersects(displayArea, rect)) {
                removeAndRecycleView(item, recycler);
            }
        }

        // add visible child
        for (int i = 0; i < getItemCount(); i++) {
            Rect area = mItemAreas.get(i);
            if (Rect.intersects(displayArea, area)) {
                View child = recycler.getViewForPosition(i);
                addView(child);
                measureChildWithMargins(child, 0, 0);
                Rect childRect = new Rect();
                calculateItemDecorationsForChild(child, childRect);
                layoutDecorated(child, area.left, area.top - mVerticalOffset,
                        area.right, area.bottom - mVerticalOffset);

                // rotateX
                int centerY = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2;
                float rotateRadius = 2.5f * centerY / (float) Math.PI;
                int childCenterY = child.getTop() + child.getHeight() / 2;
                float factor = (centerY - childCenterY) * 1f / centerY;
                float rad = (centerY - childCenterY) * 1f / rotateRadius;
                float offsetZ = centerY * (1 - (float) Math.cos(rad));
                float rotateDeg = rad * 180 / (float) Math.PI;
                ViewCompat.setZ(child, -offsetZ);
                child.setRotationX(rotateDeg);
                float currentFactor = 1 - 0.7f * Math.abs(factor);
                child.setAlpha(currentFactor * currentFactor * currentFactor);
            }
        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        // scrap all attached views and re-layout by scrolling distance
        detachAndScrapAttachedViews(recycler);
        // scroll to top bound ; dy < 0; mVerticalOffset < 0
        if (dy < 0 && mVerticalOffset + dy <= -mMaxOverScrollOffset) {
            dy = 5;
            int offset = -(mVerticalOffset + mMaxOverScrollOffset);
            if (getChildCount() == 0) dy += offset;
            LogUtil.d("WheelPickerLayoutManager", "mMaxOverScrollOffset " + mMaxOverScrollOffset
                    + " mVerticalOffset " + mVerticalOffset + " dy " + dy);
        }
        // scroll to bottom bound ; dy > 0; mVerticalOffset > 0
        if (dy > 0) {
            int totalItemCount = mRecyclerView.getAdapter().getItemCount();
            int verticalOffset = mVerticalOffset - (totalItemCount - 1) * mItemHeight;
            if (dy > 0 && verticalOffset + dy >= mMaxOverScrollOffset) {
                dy = -5;
                int offset = -(verticalOffset - mMaxOverScrollOffset);
                if (getChildCount() == 0) dy += offset;
            }
        }
        offsetChildrenVertical(-dy);
        fillView(recycler, state);
        mVerticalOffset += dy;

        return dy;
    }

    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    private int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }


}
