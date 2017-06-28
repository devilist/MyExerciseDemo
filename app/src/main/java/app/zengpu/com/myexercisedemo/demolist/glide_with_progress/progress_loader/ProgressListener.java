package app.zengpu.com.myexercisedemo.demolist.glide_with_progress.progress_loader;

/**
 * Created by zengp on 2017/6/27.
 */

public interface ProgressListener {
    void progress(long bytesRead, long contentLength, boolean done);
}
