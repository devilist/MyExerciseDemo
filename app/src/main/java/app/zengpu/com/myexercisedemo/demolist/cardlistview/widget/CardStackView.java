package app.zengpu.com.myexercisedemo.demolist.cardlistview.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewGroupCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


/**
 * Created by zengp on 2017/8/21.
 */

public class CardStackView extends ViewGroup {

    private Context mContext;
    private CardAdapter mAdapter;
    private AdapterDataObserver mObserver;

    private CardDragHelper mCardDragHelper;

    OnCardDragListener mListener;

    public CardStackView(Context context) {
        this(context, null);
    }

    public CardStackView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardStackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mCardDragHelper = new CardDragHelper(this);
    }

    int getCardOffset() {
        if (mAdapter.getCardOffset() <= 0)
            throw new IllegalArgumentException("cardOffset must be over zero !!!");
        return mAdapter.getCardOffset();
    }

    int getCardElevation() {
        if (mAdapter.getCardElevation() < 0)
            throw new IllegalArgumentException("cardElevation must be over zero !!!");
        return mAdapter.getCardOffset();
    }

    int getVisibleCardCount() {
        int visibleCardCount = mAdapter.getVisibleCardCount();
        if (visibleCardCount <= 0)
            throw new IllegalArgumentException("visibleCardCount must be over zero !!!");
        return mAdapter.getVisibleCardCount();
    }

    public void setOnCardDragListener(OnCardDragListener listener) {
        this.mListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() <= 0)
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            // contain padding that must be subtracted when size of child views is evaluated
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            // final size
            int finalW = widthSize;
            int finalH = heightSize;

            // you must measure all the children
            for (int i = 0; i < getChildCount(); i++) {
                measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
            }

            // child size
            int childW, childH;
            // Here, we only measure the first child view size for the reason that
            // we as default think all the child view sizes are the same;
            View child = getChildAt(0);
            childW = child.getLayoutParams().width;
            childH = child.getLayoutParams().height;

            // measure width
            if (widthMode == MeasureSpec.AT_MOST) {
                // wrap_content
                finalW = childW + (getVisibleCardCount() - 1) * getCardOffset() + getPaddingLeft() + getPaddingRight();
            }
            // measure height
            if (heightMode == MeasureSpec.AT_MOST) {
                // wrap_content
                finalH = childH + (getVisibleCardCount() - 1) * getCardOffset() + getPaddingTop() + getPaddingBottom();
            }
            setMeasuredDimension(finalW, finalH);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            LayoutParams params = child.getLayoutParams();
            // calculate the validate areas that all the visible cards cover.
            // What a flurried moment !!! the follow is the most complicated and soul-stirring algorithm
            // just for myself right now that is no more easy than any others' across the whole world !!!
            // guys who see this code you never know !!!
            int childWidthWithTotalOffset = params.width + getCardOffset() * (getVisibleCardCount() - 1);
            int childHeightWithTotalOffset = params.height + getCardOffset() * (getVisibleCardCount() - 1);
            int left = (getMeasuredWidth() - childWidthWithTotalOffset) / 2;
            int top = (getMeasuredHeight() - childHeightWithTotalOffset) / 2;

            for (int i = getChildCount() - 1; i >= 0; i--) {
                View child_i = getChildAt(i);
                int left_i, top_i;
                // what the fuck!!!
                if (getChildCount() - 1 - i > getVisibleCardCount() - 1) {
                    left_i = left + getCardOffset() * (getVisibleCardCount() - 1);
                    top_i = top;
                } else {
                    left_i = left + getCardOffset() * (getChildCount() - 1 - i);
                    top_i = top + getCardOffset() * (getVisibleCardCount() - 1 - (getChildCount() - 1 - i));
                    // set elevations for all the visible children
                    ViewCompat.setTranslationZ(child_i, getCardElevation() * (getVisibleCardCount() - (getChildCount() - 1 - i)));
                }
                child_i.layout(left_i, top_i, left_i + params.width, top_i + params.height);
            }
            mCardDragHelper.setTargetDragCard();
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(mContext, attrs);
    }

    public CardAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(CardAdapter adapter) {
        if (null != mObserver && null != mAdapter) {
            mAdapter.unregisterDataSetObserver(mObserver);
        }
        removeAllViews();
        mAdapter = adapter;
        if (mAdapter != null) {
            mObserver = new AdapterDataObserver();
            mAdapter.registerDataSetObserver(mObserver);
            mObserver.onChanged();
        }
    }

    private void loadView() {
        removeAllViews();
        if (null != mAdapter && mAdapter.getCount() > 0) {
            // view added at last will be show at top
            for (int i = mAdapter.getCount() - 1; i >= 0; i--) {
                View view = mAdapter.getView(i, null, this);
                addView(view);
            }
        }
    }

    protected void dropView() {
        if (mAdapter.isEnableDataRecycle())
            mAdapter.recycleData();
        else
            mAdapter.delItem(0);
    }

    private class AdapterDataObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            loadView();
        }
    }

    public static abstract class CardAdapter extends BaseAdapter {

        protected abstract void delItem(int position);

        protected abstract void recycleData();

        // the count of cards that can be seen at the top
        public int getVisibleCardCount() {
            return 3;
        }

        // the x y offset from a card to its next card
        public int getCardOffset() {
            return 30;
        }

        // the x y offset from a card to its next card
        public int getCardElevation() {
            return 20;
        }

        // whether card can rotate when dragged
        public boolean isEnableRotate() {
            return true;
        }

        // whether data can be recycled or del
        public boolean isEnableDataRecycle() {
            return true;
        }
    }

    public interface OnCardDragListener {

        void onDraggingStateChanged(View view, boolean isDragging, boolean isDropped, float offsetX, float offsetY);

        void onCardDragging(View view, float offsetX, float offsetY);
    }
}
