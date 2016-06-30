package app.zengpu.com.myexercisedemo.demolist.galleryfinaldemo.listener;

import android.widget.AbsListView;

import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.lidroid.xutils.task.TaskHandler;

/**
 * Desction:
 * Author:pengjianbo
 * Date:2016/1/9 0009 18:40
 */
public class XUtils2PauseOnScrollListener extends PauseOnScrollListener {
    public XUtils2PauseOnScrollListener(TaskHandler taskHandler, boolean pauseOnScroll, boolean pauseOnFling) {
        super(taskHandler, pauseOnScroll, pauseOnFling);
    }

    public XUtils2PauseOnScrollListener(TaskHandler taskHandler, boolean pauseOnScroll, boolean pauseOnFling, AbsListView.OnScrollListener customListener) {
        super(taskHandler, pauseOnScroll, pauseOnFling, customListener);
    }
}
