package app.zengpu.com.myexercisedemo.demolist.cardlistview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;

import app.zengpu.com.myexercisedemo.Utils.LogUtil;


/**
 * a helper to handle the drag event of the CardStackView's child views;
 * Created by zengp on 2017/8/22.
 */

class CardDragHelper implements View.OnTouchListener {

    private float mTouchDownX = 0;
    private float mTouchDownY = 0;
    private int mScreenWidth;
    private int mScreenHeight;
    // the dragged border position to differentiate whether the dragged card be drop or reset
    private float mDragThresholdX, mDragThresholdY;
    private int mMinVelocityThreshold = 2000;
    private int mMaxVelocityThreshold = 4500;

    private VelocityTracker mVelocityTracker = null;

    private CardStackView mCardStackView;

    private CardStackView.OnCardDragListener mListener;

    CardDragHelper(CardStackView cardStackView) {
        this.mCardStackView = cardStackView;
        WindowManager wm = (WindowManager) mCardStackView.getContext().getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeight = wm.getDefaultDisplay().getHeight();
        mDragThresholdX = mScreenWidth / 3;
        mDragThresholdY = mScreenHeight / 3;
    }

    void setTargetDragCard() {
        if (mCardStackView.getChildCount() > 0) {
            for (int i = 0; i < mCardStackView.getChildCount(); i++) {
                // only the last child that hold the first data can be dragged through the whole onTouch event
                mCardStackView.getChildAt(i).setOnTouchListener(i == mCardStackView.getChildCount() - 1 ? this : null);
            }
        }
        this.mListener = mCardStackView.mListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        int velocityX = 0;
        int velocityY = 0;
        // only the last child (holding first data) can be touch
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            LogUtil.d("CardDragHelper", "ACTION_DOWN ");
            LogUtil.d("CardDragHelper", "mTouchDownX " + mTouchDownX + " mTouchDownY " + mTouchDownY);
            mTouchDownX = event.getRawX();
            mTouchDownY = event.getRawY();
//            dispatchOnDragEvent(v, false, false, 0, 0);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            LogUtil.d("CardDragHelper", "ACTION_MOVE ");
            LogUtil.d("CardDragHelper", "mTouchDownX " + mTouchDownX + " mTouchDownY " + mTouchDownY);
            if (event.getEventTime() - event.getDownTime() >= 100) {
                dragCard(v, event.getRawX() - mTouchDownX, event.getRawY() - mTouchDownY);
                dispatchOnDragEvent(v, true, false, event.getRawX() - mTouchDownX, event.getRawY() - mTouchDownY);
                return true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mVelocityTracker != null) {
                mVelocityTracker.computeCurrentVelocity(1000);
                velocityX = (int) mVelocityTracker.getXVelocity();
                velocityY = (int) mVelocityTracker.getYVelocity();
            }
            int velocity = (int) Math.sqrt(velocityX * velocityX + velocityY * velocityY);
            if (velocity > mMaxVelocityThreshold && event.getEventTime() - event.getDownTime() < 100) {
                return true;
            } else if (velocity >= mMinVelocityThreshold) {
                releaseCard(v, event.getRawX() - mTouchDownX, event.getRawY() - mTouchDownY, velocity);
                return true;
            } else if (event.getEventTime() - event.getDownTime() >= 100) {
                releaseCard(v, event.getRawX() - mTouchDownX, event.getRawY() - mTouchDownY, velocity);
                return true;
            } else
                return false;
        }
        return false;
    }

    // hold the card just dragging as free as you can
    private void dragCard(View card, float offset_x, float offset_y) {
//        LogUtil.d("CardDragHelper", "offset_x " + offset_x + " offset_y " + offset_y);
        // trans
        card.setTranslationX(offset_x);
        card.setTranslationY(offset_y);
        // factor
        float distance = (float) Math.sqrt(offset_x * offset_x + offset_y * offset_y);
        float maxDistance = (float) Math.sqrt(mScreenWidth * mScreenWidth + mScreenHeight * mScreenHeight);
        float factor = Math.min(1, distance / maxDistance);
        // tansZ
        float ori_elevation = mCardStackView.getVisibleCardCount() * mCardStackView.getCardElevation();
        ViewCompat.setTranslationZ(card, (float) (ori_elevation * (1 + Math.sqrt(factor))));
        //scale
        card.setScaleX(1 - factor);
        card.setScaleY(1 - factor);
        // alpha
        card.setAlpha(1 - factor * factor);
        // rotate
        if (mCardStackView.getAdapter().isEnableRotate()) {
            float rotateDegree = offset_x == 0 ? 0 : (float) Math.atan(offset_y / offset_x);
            rotateDegree = (float) (rotateDegree * 180 / Math.PI);
            // deg factor to make the rotate more smooth
            rotateDegree = rotateDegree * factor;
            card.setRotation(rotateDegree);
        }
        refreshOtherVisibleCardsPosition(offset_x, offset_y);
    }

    private void releaseCard(View card, float offset_x, float offset_y, int velocity) {
        // check card status to decide next action
        if (Math.abs(offset_x) >= mDragThresholdX
                || Math.abs(offset_y) >= mDragThresholdY
                || (velocity >= mMinVelocityThreshold && velocity <= mMaxVelocityThreshold)) {
            dropCard(card);
        } else {
            resetDragCard(card);
        }
    }

    // del the current card or recycle it
    private void dropCard(final View card) {
        final float oriTransX = card.getTranslationX();
        final float oriTransY = card.getTranslationY();
        final float oriRotateDeg = card.getRotation();
        final float oriScaleX = card.getScaleX();
        final float oriScaleY = card.getScaleY();
        final float oriAlpha = card.getAlpha();

        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offset = (float) animation.getAnimatedValue();
                card.setTranslationX(4 * oriTransX - 3 * oriTransX * offset);
                card.setTranslationY(4 * oriTransY - 3 * oriTransY * offset);
                card.setRotation(3 * oriRotateDeg - 2 * oriRotateDeg * offset);
                card.setScaleX(oriScaleX * offset);
                card.setScaleY(oriScaleY * offset);
                card.setAlpha(oriAlpha * offset);
                refreshOtherVisibleCardsPosition(mDragThresholdX + (Math.abs(oriTransX) - mDragThresholdX) * offset,
                        mDragThresholdY + (Math.abs(oriTransY) - mDragThresholdY) * offset);
                dispatchOnDragEvent(card, false, true, oriTransX, oriTransY);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCardStackView.dropView();
            }
        });
        animator.setDuration(350);
        animator.start();
    }

    // let the card go back to its ori position
    private void resetDragCard(final View card) {
        final float oriTransX = card.getTranslationX();
        final float oriTransY = card.getTranslationY();
        final float oriRotateDeg = card.getRotation();
        final float oriScaleX = card.getScaleX();
        final float oriScaleY = card.getScaleY();
        final float oriAlpha = card.getAlpha();

        final float oriTransZ = ViewCompat.getTranslationZ(card);
        final float targetTransZ = mCardStackView.getVisibleCardCount() * mCardStackView.getCardElevation();

        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.setInterpolator(new OvershootInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offset = (float) animation.getAnimatedValue();
                card.setTranslationX(oriTransX * offset);
                card.setTranslationY(oriTransY * offset);
                ViewCompat.setTranslationZ(card, targetTransZ + (oriTransZ - targetTransZ) * offset);
                card.setRotation(oriRotateDeg * offset);
                card.setScaleX((oriScaleX - 1) * offset + 1);
                card.setScaleY((oriScaleY - 1) * offset + 1);
                card.setAlpha((oriAlpha - 1) * offset + 1);
                refreshOtherVisibleCardsPosition(oriTransX * offset, oriTransY * offset);
                dispatchOnDragEvent(card, false, false, oriTransX * offset, oriTransY * offset);
            }
        });
        animator.setDuration(500);
        animator.start();
    }

    // refresh other visible cards' positions when dragging
    private void refreshOtherVisibleCardsPosition(float offset_x, float offset_y) {
        float factor = (float) (Math.sqrt(offset_x * offset_x + offset_y * offset_y)
                / Math.sqrt(mDragThresholdX * mDragThresholdX + mDragThresholdY * mDragThresholdY));
        factor = Math.min(factor, 1);
        if (mCardStackView.getChildCount() > 1) {
            int start = mCardStackView.getChildCount() - 2;
            int totalCount = Math.max(0, mCardStackView.getVisibleCardCount() - 2);
            int cardOffset = mCardStackView.getCardOffset();

            for (int i = start; i >= 0; i--) {

                // we only need to handle the first several cards
                if (i < start - totalCount - 1)
                    break;

                // trans x y. if the visible cards count is three, for example,
                // here we only need to handle translation x and y for the other two visible cards
                if (i >= start - totalCount) {
                    mCardStackView.getChildAt(i).setTranslationX(-cardOffset * factor);
                    mCardStackView.getChildAt(i).setTranslationY(cardOffset * factor);
                }

                // it is different for handling transZ compared to transX Y.
                // we just need to handle more than one card.

                // calculate the current card ori elevation
                int current = mCardStackView.getVisibleCardCount() - 1 - (start - i);
                int oriElevation = mCardStackView.getCardElevation() * current;
                // update
                int currentElevation = (int) (oriElevation + mCardStackView.getCardElevation() * factor);
                ViewCompat.setTranslationZ(mCardStackView.getChildAt(i), currentElevation);
            }
        }
    }

    private void dispatchOnDragEvent(View view, boolean isDragging, boolean isDropped,
                                     float offsetX, float offsetY) {
        if (null != mListener) {
            mListener.onDraggingStateChanged(view, isDragging, isDropped, offsetX, offsetY);
            if (isDragging)
                mListener.onCardDragging(view, offsetX, offsetY);
        }
    }
}
