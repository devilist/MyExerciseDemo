package app.zengpu.com.myexercisedemo.demolist.videoRecord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.videoRecord.model.VideoInfo;
import app.zengpu.com.myexercisedemo.demolist.videoRecord.util.RoundProgressPlayerButton;
import app.zengpu.com.myexercisedemo.demolist.videoRecord.util.SurfaceVideoHolder;


/**
 * Created by zengpu on 2016/5/18.
 */
public class VideoPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 视频surfaceView
     */
    private SurfaceView surfaceView;

    /**
     * 视频图片
     */
    private ImageView videoPicIv;

    private ProgressBar progressBar;
    /**
     * 视频播放工具类
     */
    private SurfaceVideoHolder surfaceVideoHolder;

    /**
     * 视频播放加载按钮
     */
    private RoundProgressPlayerButton roundProgressPlayerButton;


    /* * 视频信息 */
    private String vd_File_Path;  // 视频存储路径
    private String pic_File_Path; // 视频图片存储路径
    private int vd_size;          // 视频大小
    private String vd_pic_size;   // 视频图片大小

    private VideoInfo videoInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);

        videoInfo = (VideoInfo) getIntent().getSerializableExtra("videoInfo");

        initView();
    }

    private void initView() {

        progressBar = (ProgressBar) findViewById(R.id.video_progressbar);
        surfaceView = (SurfaceView) findViewById(R.id.shipin_surfaceview);
        videoPicIv = (ImageView) findViewById(R.id.iv_video_pic);
        roundProgressPlayerButton = (RoundProgressPlayerButton) findViewById(R.id.rppb_progress);

        surfaceVideoHolder = new SurfaceVideoHolder(this,
                surfaceView,
                progressBar,
                roundProgressPlayerButton,
                videoInfo.getFilePath(),
                videoInfo.getThumbPath(),
                videoPicIv);
    }


    public static void actionStart(Context context, VideoInfo videoInfo) {
        Intent intent = new Intent(context, VideoPreviewActivity.class);
        intent.putExtra("videoInfo", videoInfo);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onStop() {
//        surfaceVideoHolder.release();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        surfaceVideoHolder.release();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        surfaceVideoHolder.release();
        super.onDestroy();
    }


    /**
     * 重置
     */
    private void resetVideoPreview() {
        roundProgressPlayerButton.setVisibility(View.VISIBLE);
        surfaceVideoHolder = null;
        surfaceVideoHolder = new SurfaceVideoHolder(this, surfaceView, progressBar, roundProgressPlayerButton, vd_File_Path, pic_File_Path, videoPicIv);
    }

}
