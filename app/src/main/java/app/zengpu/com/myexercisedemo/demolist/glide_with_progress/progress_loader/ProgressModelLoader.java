package app.zengpu.com.myexercisedemo.demolist.glide_with_progress.progress_loader;

import android.os.Handler;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.stream.StreamModelLoader;

import java.io.InputStream;

import app.zengpu.com.myexercisedemo.Utils.LogUtil;

/**
 * Created by zengp on 2017/6/27.
 */

public class ProgressModelLoader implements StreamModelLoader<String> {

    private ProgressListener progressListener;

    public ProgressModelLoader(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(String model, int width, int height) {
        // 该方法只会进入一次
        LogUtil.d("DataFetcher", "DataFetcher");
        return new ProgressDataFetcher(model, progressListener);
    }

}
