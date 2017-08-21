package app.zengpu.com.myexercisedemo.demolist.cardlistview;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import app.zengpu.com.myexercisedemo.Utils.LogUtil;

/**
 * Created by zengp on 2017/8/21.
 */

public class CardListView extends ViewGroup {

    private Context mContext;
    private int mVisibleCardCount = 3; // the count of cards that can be seen at the top
    private int mCardOffset = 5; // the x y offset from a card to its next card

    private ListAdapter mAdapter;
    private AdapterDataObserver mObserver;

    public CardListView(Context context) {
        this(context, null);
    }

    public CardListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setVisibleCardCount(int visibleCardCount) {
        if (visibleCardCount < 3) {
            Log.w("CardListView", "visible Cards Count must be not less than three !");
        } else
            this.mVisibleCardCount = visibleCardCount;
    }

    public void setCardOffset(int cardOffset) {
        if (cardOffset < 0) {
            Log.w("CardListView", "card Offset must be not less than zero !");
        } else
            this.mCardOffset = cardOffset;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() <= 0)
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        else {
            // contain padding that must be subtracted when size of child views is evaluated
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            // final size
            int finalW = widthSize;
            int finalH = heightSize;

            // child size
            int childW, childH;
            // Here, we only measure the first child view size for the reason that
            // we as default think all the child view sizes are the same;
            View child = getChildAt(0);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            childW = params.width;
            childH = params.height;

            // measure width
            if (widthMode == MeasureSpec.AT_MOST) {
                // wrap_content
                finalW = childW + (mVisibleCardCount - 1) * mCardOffset + getPaddingLeft() + getPaddingRight();
            }
            // measure height
            if (heightMode == MeasureSpec.AT_MOST) {
                // wrap_content
                finalH = childH + (mVisibleCardCount - 1) * mCardOffset + getPaddingTop() + getPaddingBottom();
            }
            setMeasuredDimension(finalW, finalH);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() >= 0) {
            View child = getChildAt(0);
            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            // calculate the validate areas that all the visible cards cover.
            // What a flurried moment !!! the follow is the most complicated and soul-stirring algorithm
            // just for myself right now that is no more easy than any others' across the whole world !!!
            // guys who see this code you never know !!!
            int childWidthWithTotalOffset = params.width + mCardOffset * (mVisibleCardCount - 1);
            int childHeightWithTotalOffset = params.height + mCardOffset * (mVisibleCardCount - 1);
            int left = (getMeasuredWidth() - childWidthWithTotalOffset) / 2;
            int top = (getMeasuredHeight() - childHeightWithTotalOffset) / 2;
            if (getChildCount() <= mVisibleCardCount) {
                for (int i = getChildCount() - 1; i <= 0; i--) {
                    View child_i = getChildAt(i);
                    int left_i = left + mCardOffset * i;
                    int top_i = top + mCardOffset * (getChildCount() - 1 - i);
                    child_i.layout(left_i, top_i, left_i + params.width, top_i + params.height);
                }
            } else {
                for (int i = getChildCount() - 1; i <= 0; i--) {
                    View child_i = getChildAt(i);
                    if (i > mVisibleCardCount - 1) {
                        child_i.layout(left + mCardOffset * (mVisibleCardCount - 1),
                                top, left + mCardOffset * (mVisibleCardCount - 1) + params.width,
                                top + params.height);
                    } else {
                        int left_i = left + mCardOffset * i;
                        int top_i = top + mCardOffset * (getChildCount() - 1 - i);
                        child_i.layout(left_i, top_i, left_i + params.width, top_i + params.height);
                    }
                }
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(mContext, attrs);
    }

    public void setAdapter(ListAdapter adapter) {
        if (null != mObserver && null != mAdapter) {
            mAdapter.unregisterDataSetObserver(mObserver);
        }
        removeAllViews();
        mAdapter = adapter;
        if (mAdapter != null) {
            mObserver = new AdapterDataObserver();
            mAdapter.registerDataSetObserver(mObserver);
        }
    }

    private void loadView() {
        removeAllViews();
        if (null != mAdapter && mAdapter.getCount() > 0) {
            LogUtil.d("LabelLayout", "count " + mAdapter.getCount());
            for (int i = 0; i < mAdapter.getCount(); i++) {
                View view = mAdapter.getView(i, null, this);
                MarginLayoutParams params = new MarginLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
                addView(view, params);
                invalidate();
            }
        }
    }

    private class AdapterDataObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            loadView();
        }
    }
}
