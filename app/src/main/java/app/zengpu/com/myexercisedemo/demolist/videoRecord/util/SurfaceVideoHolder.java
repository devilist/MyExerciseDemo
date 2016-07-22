package app.zengpu.com.myexercisedemo.demolist.videoRecord.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;

import app.zengpu.com.myexercisedemo.Utils.LogUtil;

/**
 * 视频管理器
 * Created by guodx on 16/5/14.
 */
public class SurfaceVideoHolder implements View.OnClickListener,
        RoundProgressPlayerButton.OnRoundProgressListener, SurfaceHolder.Callback,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {
    private Context mContext;
    private SurfaceView mSurfaceView;
    private ProgressBar mTimeBar;
    private RoundProgressPlayerButton mPlayerBtn;
    private String filepath = null;
    private String filePicPath = null; // 图片地址
    private ImageView filePreviewPicIv; // 显示视频图片

    private MediaPlayer player = null;
    private SurfaceHolder surfaceHolder = null;
    private Handler handler = null;
    private Runnable timeRun = null;
    private boolean isPlayerAlive = true;   // 回收后避免报错

    private boolean isPrepared = false;     // 是否缓存完成

    public SurfaceVideoHolder(Context context, SurfaceView surfaceView,
                              ProgressBar timeBar, RoundProgressPlayerButton playBtn, String filepath, String filePicPath, ImageView filePreviewPicIv) {
        if (null == context || null == timeBar || null == playBtn || TextUtils.isEmpty(filepath)) {
            throw new IllegalArgumentException();
        }
        this.mContext = context;
        this.mSurfaceView = surfaceView;
        this.surfaceHolder = surfaceView.getHolder();
        this.mTimeBar = timeBar;
        this.mTimeBar.setMax(0);
        this.mPlayerBtn = playBtn;
        this.filepath = filepath;
        this.filePicPath = filePicPath;
        this.filePreviewPicIv = filePreviewPicIv;

        // 显示播放进度
        this.handler = new Handler();
        this.timeRun = new Runnable() {
            @Override
            public void run() {
                if (null != player && isPlayerAlive && player.isPlaying()) {
                    mTimeBar.setProgress(player.getCurrentPosition());
                    handler.postDelayed(timeRun, 100);
                }
            }
        };

        // 显示视频图片
        if (!TextUtils.isEmpty(filePicPath) ) {
            if (filePicPath.startsWith("http")) {
                Glide.with(context).load(filePicPath).into(filePreviewPicIv);
            } else {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver()
                            .openInputStream(Uri.fromFile(new File(filePicPath))));
                    filePreviewPicIv.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            filePreviewPicIv.setVisibility(View.VISIBLE);
        }
        // 加载进度条
        this.mPlayerBtn.setMax(100);
        this.mPlayerBtn.setOnRoundProgressListener(this);

        // 视频的点击事件
        this.mSurfaceView.setOnClickListener(this);
        // 视频sufaceview准备工作
        this.surfaceHolder.addCallback(this);
        // Surface类型
        if (filepath.startsWith("http")) {
            // 网络缓存视频
            this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        } else {
            // 本地视频
            this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_HARDWARE);
        }
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>events

    /**
     * 视频缓存完成
     */
    @Override
    public void onLoadFinish() {
        isPrepared = true;
        // 先设置视频时长
//        mTimeBar.setMax(player.getDuration());
        mPlayerBtn.setProgress(mPlayerBtn.getMax());
        // 缓存完成 直接播放
        mSurfaceView.setBackground(null);
//        play();
    }

    /**
     * 没有缓存之前，播放按钮点击事件
     *
     * @param v
     */
    @Override
    public void onUnLoadClick(View v) {
        if (filepath.startsWith("http") && !isPrepared) {
            try {
                LogUtil.d("SurfaceVideoHolder", "player is :" + player.toString());
                player.prepareAsync();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 缓存完成之后，播放按钮点击事件
     *
     * @param v
     */
    @Override
    public void onLoadedClick(View v) {
        // 暂停后，点击播放
        if (filepath.startsWith("http") && !isPrepared) {
//            try {
//                player.prepare();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            try {
                player.prepareAsync();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } else {
            play();
        }
    }

    /**
     * 缓存之前，缓存完成之后，播放按钮点击事件
     *
     * @param v
     */
    @Override
    public void onNormalClick(View v) {

    }

    @Override
    public void onClick(View v) {
        if (mPlayerBtn.getProgress() != mPlayerBtn.getMax()) {
            return;
        }
        if (filepath.startsWith("http") && !isPrepared) {
//            try {
//                player.prepare();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            try {
                player.prepareAsync();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } else {
            if (player.isPlaying()) {
                pause();
            } else {
                play();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setDisplay(surfaceHolder);

        player.setOnBufferingUpdateListener(this);
        player.setOnCompletionListener(this);

        LogUtil.d("SurfaceVideoHolder", "filepath is :" + filepath);

        //设置显示视频显示在SurfaceView上
        try {
            if (null != mPlayerBtn) {
                mPlayerBtn.prepare(false);
                if (!filepath.startsWith("http")) {
                    // 本地视频 直接显示加载完成
                    mPlayerBtn.setProgress(mPlayerBtn.getMax());
                }
            }

            player.setDataSource(filepath);
            if (!filepath.startsWith("http")) {
                player.prepare();
            } else
                player.setOnPreparedListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    /**
     * 缓冲进度处理
     *
     * @param mp
     * @param percent
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        // 刷新进度
        mPlayerBtn.setProgress(percent);
    }

    /**
     * 播放完成处理
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        // 播放按钮
        mPlayerBtn.setVisibility(View.VISIBLE);
        mTimeBar.setProgress(player.getDuration());
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // 放在进度条按钮的onLoadFinish事件里边处理了
        LogUtil.d("SurfaceVideoHolder", "mp.getDuration() is :" + mp.getDuration());
        play();
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>methods

    /**
     * 播放
     */
    private void play() {
        // 先设置视频时长
        mTimeBar.setMax(player.getDuration());

        // 0.隐藏预览图片
        filePreviewPicIv.setVisibility(View.GONE);
        // 1.隐藏播放按钮
        mPlayerBtn.setVisibility(View.GONE);
        // 2.播放视频
        player.start();
        // 3.绘制时间进度
        handler.postDelayed(timeRun, 100);
    }

    /**
     * 暂停播放
     */
    private void pause() {
        // 1.显示播放按钮
        mPlayerBtn.setVisibility(View.VISIBLE);
        // 2.暂停视频
        player.pause();
    }

    /**
     * 释放
     */
    public void release() {
        isPlayerAlive = false;
        isPrepared = false;
        timeRun = null;
        handler = null;

        if (player != null) {
//            if (player.isPlaying()) {
            player.stop();
//            }
            player.release();
            player = null;
        }
//        if (player.isPlaying()) {
//            player.stop();
//        }


        mSurfaceView = null;
        mTimeBar = null;
        mPlayerBtn = null;
        filepath = null;
        surfaceHolder = null;


    }
}
