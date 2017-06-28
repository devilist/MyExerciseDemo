package app.zengpu.com.myexercisedemo.demolist.glide_with_progress.progress_loader;


import java.io.IOException;

import app.zengpu.com.myexercisedemo.Utils.LogUtil;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by zengp on 2017/6/27.
 */

public class ProgressInterceptor implements Interceptor {

    private ProgressListener progressListener;

    public ProgressInterceptor(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder().body(new ProgressResponseBody(response.body(), progressListener)).build();
    }
}
