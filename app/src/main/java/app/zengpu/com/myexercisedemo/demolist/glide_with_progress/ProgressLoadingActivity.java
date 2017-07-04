package app.zengpu.com.myexercisedemo.demolist.glide_with_progress;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import app.zengpu.com.myexercisedemo.BaseActivity;
import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.Utils.LogUtil;
import app.zengpu.com.myexercisedemo.demolist.glide_with_progress.progress_loader.ProgressListener;
import app.zengpu.com.myexercisedemo.demolist.glide_with_progress.progress_loader.ProgressModelLoader;
import app.zengpu.com.myexercisedemo.demolist.glide_with_progress.widget.ProgressView;
import okhttp3.OkHttpClient;

/**
 * 加载图片 带进度条
 * Created by zengp on 2017/6/27.
 */

public class ProgressLoadingActivity extends BaseActivity {

    private ImageView iv_image;
    private Button btn_load, btn_load1;
    private ProgressView progress_view, progress_view1, progress_view2, progress_view3, progress_view33;
    private ProgressView progress_view4, progress_view5, progress_view6, progress_view66;
    private ProgressView progress_view7, progress_view8, progress_view9, progress_view99;
    private ProgressView progress_view10, progress_view11, progress_view12, progress_view1212;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_loading);
        initView();
    }

    private void initView() {
        btn_load = (Button) findViewById(R.id.btn_load);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        progress_view = (ProgressView) findViewById(R.id.progress_view);
        btn_load1 = (Button) findViewById(R.id.btn_load1);
        progress_view1 = (ProgressView) findViewById(R.id.progress_view1);
        progress_view2 = (ProgressView) findViewById(R.id.progress_view2);
        progress_view3 = (ProgressView) findViewById(R.id.progress_view3);
        progress_view33 = (ProgressView) findViewById(R.id.progress_view33);
        progress_view4 = (ProgressView) findViewById(R.id.progress_view4);
        progress_view5 = (ProgressView) findViewById(R.id.progress_view5);
        progress_view6 = (ProgressView) findViewById(R.id.progress_view6);
        progress_view66 = (ProgressView) findViewById(R.id.progress_view66);
        progress_view7 = (ProgressView) findViewById(R.id.progress_view7);
        progress_view8 = (ProgressView) findViewById(R.id.progress_view8);
        progress_view9 = (ProgressView) findViewById(R.id.progress_view9);
        progress_view99 = (ProgressView) findViewById(R.id.progress_view99);
        progress_view10 = (ProgressView) findViewById(R.id.progress_view10);
        progress_view11 = (ProgressView) findViewById(R.id.progress_view11);
        progress_view12 = (ProgressView) findViewById(R.id.progress_view12);
        progress_view1212 = (ProgressView) findViewById(R.id.progress_view1212);

        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPic();
            }
        });
        btn_load1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadProgress();
            }
        });

    }

    private void loadPic() {
        LogUtil.d("ProgressLoadingActivity", "loadPic");
        String url = "http://o9xuvf3m3.bkt.clouddn.com/new_york.jpg";
        ProgressModelLoader loader = new ProgressModelLoader(new ProgressListener() {
            @Override
            public void progress(long bytesRead, long contentLength, boolean done) {
                LogUtil.d("ProgressLoadingActivity", "bytesRead " + bytesRead
                        + " contentLength  " + contentLength + " progress " + (bytesRead * 100 / contentLength) + " done " + done);
                final float progress = (bytesRead * 100f / contentLength);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress_view.setCurrentProgress(progress);
                    }
                });
            }
        });
        Glide.with(this).using(loader).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).into(iv_image);
    }

    private void loadProgress() {

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1.01f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float offset = (float) animation.getAnimatedValue() * 100;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress_view1.setCurrentProgress(offset);
                        progress_view2.setCurrentProgress(offset);
                        progress_view3.setCurrentProgress(offset);
                        progress_view33.setCurrentProgress(offset);
                        progress_view4.setCurrentProgress(offset);
                        progress_view5.setCurrentProgress(offset);
                        progress_view6.setCurrentProgress(offset);
                        progress_view66.setCurrentProgress(offset);
                        progress_view7.setCurrentProgress(offset);
                        progress_view8.setCurrentProgress(offset);
                        progress_view9.setCurrentProgress(offset);
                        progress_view99.setCurrentProgress(offset);
                        progress_view10.setCurrentProgress(offset);
                        progress_view11.setCurrentProgress(offset);
                        progress_view12.setCurrentProgress(offset);
                        progress_view1212.setCurrentProgress(offset);
                    }
                });
            }
        });
        animator.setDuration(3000);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();

    }
}
