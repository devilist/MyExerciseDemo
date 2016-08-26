package app.zengpu.com.myexercisedemo.demolist.design_support_library.listener;

import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.Collections;
import java.util.List;

/**
 * Created by tao on 2016/8/26.
 */
public class RecyclerViewItemTouchCallback extends ItemTouchHelper.Callback {

    private RecyclerView.ViewHolder viewHolder;
    private List<?> dataList;
    private RecyclerView.Adapter adapter;

    public RecyclerViewItemTouchCallback(List<?> dataList, RecyclerView.Adapter adapter) {
        this.dataList = dataList;
        this.adapter = adapter;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        // Item是否可以滑动
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        // Item是否可以长按
        // 如果返回false，可以自定义长按事件，调用helper.startDrag方法自定义拖拽

        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // 获取移动标志
        // 拖拽的标记，这里允许上下左右四个方向
        int dragFlags;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager
                | recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {

            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN
                    | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        } else
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;

        // 滑动的标记，这里允许左右滑动
        int swipeFlags = ItemTouchHelper.END;

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /*
     *  这个方法会在某个Item被拖动和移动的时候回调，
     *  这里我们用来播放动画，当viewHolder不为空时为选中状态否则为释放状态
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        if (viewHolder != null) {
            this.viewHolder = viewHolder;
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            pickUpAnimation(viewHolder.itemView);
        } else {
            if (this.viewHolder != null)
                putDownAnimation(this.viewHolder.itemView);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(Color.WHITE);
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        if (dataList != null) {
            int fromPosition = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position
            int toPosition = target.getAdapterPosition();//得到目标ViewHolder的position
            if (fromPosition < toPosition) {
                //分别把中间所有的item的位置重新交换
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(dataList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(dataList, i, i - 1);
                }
            }
//            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            //返回true表示执行拖动
            return true;
        } else
            return false;
    }

    /*
     * 当onMove返回true时调用
     */
    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
//        // 移动完成后刷新列表
        if (adapter != null) {
            adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        dataList.remove(viewHolder.getAdapterPosition());
        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        //滑动时改变Item的透明度
        int spanCount = 1;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager)
            spanCount = 1;
        else if (recyclerView.getLayoutManager() instanceof GridLayoutManager)
            spanCount = ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount();
        else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager)
            spanCount = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).getSpanCount();

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth() / spanCount;
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        }
    }

    private void pickUpAnimation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationZ", 1f, 10f);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(300);
        animator.start();
    }

    private void putDownAnimation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationZ", 10f, 1f);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(300);
        animator.start();
    }
}
