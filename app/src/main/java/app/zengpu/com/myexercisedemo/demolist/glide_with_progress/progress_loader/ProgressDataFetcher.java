package app.zengpu.com.myexercisedemo.demolist.glide_with_progress.progress_loader;


import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import app.zengpu.com.myexercisedemo.Utils.LogUtil;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zengp on 2017/6/27.
 */

public class ProgressDataFetcher implements DataFetcher<InputStream> {

    private String url;
    private Call progressCall;
    private InputStream stream;
    private boolean isCancelled = false;
    private ProgressListener progressListener;

    public ProgressDataFetcher(String url, ProgressListener progressListener) {
        this.url = url;
        this.progressListener = progressListener;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        // 用okhttp3
        Request request = new Request.Builder().url(url).build();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new ProgressInterceptor(progressListener))
                .build();
        progressCall = client.newCall(request);
        Response response = progressCall.execute();
        if (isCancelled)
            return null;

        if (!response.isSuccessful())
            throw new IOException("failure " + response);

        stream = response.body().byteStream();
        return stream;
    }

    @Override
    public void cleanup() {
        if (null != stream) {
            try {
                stream.close();
                stream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != progressCall)
            progressCall.cancel();

    }

    @Override
    public String getId() {
        return url;
    }

    @Override
    public void cancel() {
        isCancelled = true;

    }
}
