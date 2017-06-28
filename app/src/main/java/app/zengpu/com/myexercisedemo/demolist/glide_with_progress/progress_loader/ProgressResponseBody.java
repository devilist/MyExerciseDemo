package app.zengpu.com.myexercisedemo.demolist.glide_with_progress.progress_loader;

import java.io.IOException;

import app.zengpu.com.myexercisedemo.Utils.LogUtil;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by zengp on 2017/6/27.
 */

public class ProgressResponseBody extends ResponseBody {

    private ResponseBody responseBody;
    private ProgressListener progressListener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (null == bufferedSource) {
            bufferedSource = Okio.buffer(getSource(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source getSource(Source source) {

        return new ForwardingSource(source) {
            long totalBytesRead = 0;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long byteRead = super.read(sink, byteCount);
                totalBytesRead += byteRead != -1 ? byteRead : 0;
                if (null != progressListener)
                    progressListener.progress(totalBytesRead, responseBody.contentLength(), byteRead == -1);
                return byteRead;
            }
        };
    }
}
