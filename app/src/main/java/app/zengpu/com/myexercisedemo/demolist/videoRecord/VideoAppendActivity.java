package app.zengpu.com.myexercisedemo.demolist.videoRecord;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.Utils.LogUtil;
import app.zengpu.com.myexercisedemo.demolist.videoRecord.model.VideoInfo;
import app.zengpu.com.myexercisedemo.demolist.videoRecord.util.SupportedSizesReflect;
import app.zengpu.com.myexercisedemo.demolist.videoRecord.util.VideoUtil;

/**
 * 视频认证 拍摄视频页面
 * Created by zengpu on 2016/4/10.
 */
public class VideoAppendActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "VideoAppendActivity";

    /**
     * title中的切换摄像头按钮
     */
    private Button changeCameraButton;
    /**
     * 录制视频View
     */
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    /**
     * 长按拍摄视频
     */
    private Button shiPinPaiSheButton;
    /**
     * 点击取消拍摄视频
     */
    private Button quXiaoButton;
    /**
     * 拍摄视频完后点击确认
     */
    private Button queRenButton;
    /**
     * 视频录制进度条
     */
    private ProgressBar progressBar;
    /**
     * 是否正在录制中
     */
    private boolean isRecording = false;

    private MediaRecorder mediarecorder;

    private Camera camera;
    /**
     * 摄像头参数
     */
    private Camera.Parameters params;

    private int screenW;
    private int screenH;
    /**
     * 视频文件
     */
    private File file;
    /**
     * 视频录制编码速率
     */
    private int BitRate = 5;
    /**
     * 默认选择前置摄像头
     */
    private int cameraPosition = 1;
    /**
     * 当前录制的时间
     */
    private long currentRecordingTime = 0;
    /**
     * 实时显示录制时间
     */
    private TextView time_tv;
    /**
     * 最大录制时间 10000s
     */
    private static final long MAX_RECORDING_TIME = 10000 * 1000;
    /**
     * 记录每一次按拍的时间，小于1s时，重录
     */
    private long touchTime = 0;
    /**
     * 录制视频文件名 vd_name = "VD_" + getCurrentTime() + ".mp4";
     */
    private String vd_name;
    /**
     * 当前录制的最新一段视频的完整路径
     */
    private String currentVideoFilePath = "";
    /**
     * 合并后保存的的视频的完整路径，初始为空
     */
    private String saveVideoPath = "";
    /**
     * progressBar更新
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
//                    if (currentRecordingTime > MAX_RECORDING_TIME ) {
//                        currentRecordingTime = MAX_RECORDING_TIME;
//                        progressBar.setProgress((int) MAX_RECORDING_TIME);
//                        stop();
//                        break;
//                    } else {
//                        progressBar.setProgress(msg.arg1);
//                    }
                    time_tv.setText(timeFormat((int) currentRecordingTime / 1000));
                    LogUtil.d(TAG, "currentRecordingTime is : " + msg.arg1);
                    postDelayed(timeRun, 1000);
                    break;
            }
        }
    };
    /**
     * 开启线程记录录制时间
     */
    private Runnable timeRun = new Runnable() {
        @Override
        public void run() {
            currentRecordingTime = currentRecordingTime + 1000;
            Message message = new Message();
            message.what = 0;
            message.arg1 = (int) currentRecordingTime;
            handler.sendMessage(message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);
        makeDirs();
        initView();
    }

    private void initView() {
        changeCameraButton = (Button) findViewById(R.id.btn_camera);
        quXiaoButton = (Button) findViewById(R.id.btn_video_clear);
        queRenButton = (Button) findViewById(R.id.btn_video_play);
        time_tv = (TextView) findViewById(R.id.time);
        changeCameraButton.setOnClickListener(this);
        quXiaoButton.setOnClickListener(this);
        queRenButton.setOnClickListener(this);

        shiPinPaiSheButton = (Button) findViewById(R.id.btn_video_record);
        progressBar = (ProgressBar) findViewById(R.id.paishe_progressbar);
        progressBar.setMax((int) MAX_RECORDING_TIME);
        surfaceView = (SurfaceView) findViewById(R.id.paishe_surfaceview);

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenW = dm.widthPixels;
        screenH = dm.heightPixels;

        //surfaceHolder和surfaceView初始化
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
        params.width = screenW;
        params.height = screenH;
        surfaceView.setLayoutParams(params);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.setKeepScreenOn(true);
        surfaceView.setFocusable(true);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (null == camera) {
                    //默认后置摄像头
                    int cameraCount = 0;
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    cameraCount = Camera.getNumberOfCameras();
                    for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                        Camera.getCameraInfo(camIdx, cameraInfo);
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            try {
                                camera = Camera.open(camIdx);
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        camera = deal(camera);
                        camera.setPreviewDisplay(surfaceHolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cameraPosition = 1;
                    initCamera();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                //该方法至少会调用一次
                initCamera();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mediarecorder != null) {
                    mediarecorder.setOnErrorListener(null);
                    mediarecorder.setPreviewDisplay(null);
                    mediarecorder.stop();
                    mediarecorder.reset();
                    mediarecorder.release();
                    mediarecorder = null;
                    camera.lock();
                }
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        });

        shiPinPaiSheButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (currentRecordingTime >= MAX_RECORDING_TIME) {
                            stop();
                        } else {
                            isRecording = true;
                            start();
                            touchTime = (new Date()).getTime();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        /* 手指抬起后要处理几件事情：
                            1.单次录制时间过短报错问题
                            2.判断总的录制时间是否小于三秒
                            3.拼接视频
                         */
                        /* 单次录制时间过短时的处理，不然会报空指针错误
                           mediarecorder 的start()和stop()方法之间的时间间隔不能太小
                          */
                        touchTime = (new Date()).getTime() - touchTime;
                        LogUtil.d(TAG, "touchTime is : " + touchTime);
                        if (touchTime < 500) {
                            // 如果单次拍摄时间过短，这里设定为200ms，首先停掉handler，以
                            // 停止记录currentRecordingTime，然后视频继续录制1s，防止报错
                            // 1s后，停止录制，删除该视频。
                            handler.removeCallbacks(timeRun);
                            long beforeDate = (new Date()).getTime();
                            long nowDate = beforeDate;
                            while ((nowDate - beforeDate) < 1000) {
                                nowDate = (new Date()).getTime();
                            }
                            stop();
                            LogUtil.d(TAG, "currentRecordingTime0 is : " + currentRecordingTime);
                            file = new File(currentVideoFilePath);
                            file.delete();
                            break;
                        }

                        stop();
                        isRecording = false;
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.d(TAG, "saveVideoPath is : " + saveVideoPath);
                                LogUtil.d(TAG, "currentVideoFilePath is : " + currentVideoFilePath);
                                if (saveVideoPath.equals("")) {
                                    saveVideoPath = currentVideoFilePath;
                                    currentVideoFilePath = "";
                                } else if (!(currentVideoFilePath.equals(""))) {
                                    try {
                                        VideoUtil.appendVideo(VideoAppendActivity.this, saveVideoPath, currentVideoFilePath);
                                        currentVideoFilePath = "";
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_camera:
                // 切换摄像头
                if (!isRecording) {
                    int cameraCount = 0;
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    //获得摄像头个数
                    cameraCount = Camera.getNumberOfCameras();
                    if (cameraPosition == 1) {
                        //后置切换到前置
                        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                            //获得摄像头信息
                            Camera.getCameraInfo(camIdx, cameraInfo);
                            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                                camera.stopPreview();//停掉原来摄像头的预览
                                camera.release();//释放资源
                                camera = null;//取消原来摄像头
                                camera = Camera.open(camIdx);//打开当前选中的摄像头
                            }
                        }
                        try {
                            camera = deal(camera);
                            camera.setPreviewDisplay(surfaceHolder);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        cameraPosition = 0;
                        initCamera();
                    } else {
                        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                            //获得摄像头信息
                            Camera.getCameraInfo(camIdx, cameraInfo);
                            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                                camera.stopPreview();//停掉原来摄像头的预览
                                camera.release();//释放资源
                                camera = null;//取消原来摄像头
                                camera = Camera.open(camIdx);//打开当前选中的摄像头
                            }
                        }
                        try {
                            camera = deal(camera);
                            camera.setPreviewDisplay(surfaceHolder);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        cameraPosition = 1;
                        initCamera();
                    }
                }
                break;
            case R.id.btn_video_play:
                //拍完视频后，确认逻辑,合并
                if (currentRecordingTime < 3 * 1000) {
                    Toast.makeText(VideoAppendActivity.this, "录制时间太短!", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    cameraPosition = 0;
                    //视频压缩，预览
                    /* 组装视频信息 */
                    VideoInfo videoInfo = setVideoInfo(saveVideoPath, vd_name);

                    LogUtil.d("video", "filePath is : " + videoInfo.getFilePath());
                    LogUtil.d("video", "thumbPath is : " + videoInfo.getThumbPath());
                    LogUtil.d("video", "mimeType is : " + videoInfo.getMimeType());
                    LogUtil.d("video", "title is : " + videoInfo.getTitle());
                    LogUtil.d("video", "Duration is : " + videoInfo.getVideoDuration());
                    LogUtil.d("video", "Width is : " + videoInfo.getVideoWidth());
                    LogUtil.d("video", "Hight is : " + videoInfo.getVideoHight());
                    LogUtil.d("video", "VideoSize is : " + videoInfo.getVideoSize());

                    VideoPreviewActivity.actionStart(this, videoInfo);
                }
                break;
            case R.id.btn_video_clear:
                //取消视频逻辑，删除视频，从新开始
                deleteVideo();
                saveVideoPath = "";
                currentVideoFilePath = "";
                currentRecordingTime = 0;
                time_tv.setText("00:00");
                progressBar.setProgress((int) currentRecordingTime);
                isRecording = false;
                break;
        }
    }

    /**
     * 获得当前时间，视频最后录制完后以此时间命名
     *
     * @return 当前时间字符串
     */
    private String getCurrentTime() {
        long l = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date(l);
        String s = format.format(date);
        return s;
    }

    /**
     * 时间格式化
     *
     * @param timeMs
     * @return
     */
    public String timeFormat(int timeMs) {
        int totalSeconds = timeMs;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.getDefault());
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 开始录制
     */
    protected void start() {
        vd_name = "VD_" + getCurrentTime() + ".mp4";
        file = new File(VideoUtil.getSDPath(this) + vd_name);
        if (file.exists()) {
            // 如果文件存在，删除它，演示代码保证设备上只有一个录音文件
            file.delete();
        }
        camera.stopPreview();
        camera.unlock();
        // 创建mediarecorder对象
        mediarecorder = new MediaRecorder();
        // 设置录制视频源为Camera(相机)
        mediarecorder.setCamera(camera);
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        // 录像旋转90度
        mediarecorder.setOrientationHint(270);
        // 设置录制完成后视频的封装格式为mp4
        mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 设置录制的视频编码h263 h264
        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错  分辨率不能设置成长宽一样
        mediarecorder.setVideoSize(720, 480);
        mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        // 设置高质量录制,改变编码速率
        mediarecorder.setVideoEncodingBitRate(BitRate * 1024 * 512);
        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
        mediarecorder.setVideoFrameRate(30);
        mediarecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
        // 设置视频文件输出的路径
        currentVideoFilePath = VideoUtil.getSDPath(this) + vd_name;
        mediarecorder.setOutputFile(currentVideoFilePath);
        // 设置最大录制时间
//        mediarecorder.setMaxDuration((int) (MAX_RECORDING_TIME - totalRecordingTime));
        try {
            // 准备、开始
            mediarecorder.prepare();
            //开始刻录
            mediarecorder.start();
            camera.startPreview();
            isRecording = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //如果录制过程中发生错误，停止录制
        mediarecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                // 发生错误，停止录制
                mediarecorder.stop();
                mediarecorder.reset();
                mediarecorder.release();
                mediarecorder = null;
                isRecording = false;
                // 删除视频文件
                // 如果文件存在，删除它，演示代码保证设备上只有一个录音文件
                deleteVideo();

            }
        });
        // 开启线程，实时显示录制时间
        handler.post(timeRun);
    }

    /**
     * 停止录制
     */
    protected void stop() {
        queRenButton.setVisibility(View.VISIBLE);
        quXiaoButton.setVisibility(View.VISIBLE);
        if (mediarecorder != null) {
            mediarecorder.setOnErrorListener(null);
            mediarecorder.setPreviewDisplay(null);
            mediarecorder.stop();
            mediarecorder.reset();
            mediarecorder.release();
            mediarecorder = null;
            isRecording = false;
        }
        handler.removeCallbacks(timeRun);
        handler.removeMessages(0);

    }

    /**
     * 摄像头初始化，设置摄像头参数
     */
    private void initCamera() {
        try {
//            camera.stopPreview();
            params = camera.getParameters();
            params.setPreviewSize(720, 480);
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            camera.setParameters(params);
            camera.startPreview();
//             设置方向
//            params.set("orientation", "portrait");
//            if (cameraPosition == 0) camera.setDisplayOrientation(180);
//            else camera.setDisplayOrientation(270);

//            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
//                params.set("orientation","portrait");
//                camera.setDisplayOrientation(0);
//            }else {
//                params.set("orientation","landscape");
//                camera.setDisplayOrientation(0);
//            }

//            camera.startPreview();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Camera deal(Camera camera){
        //设置camera预览的角度，因为默认图片是倾斜90度的
        camera.setDisplayOrientation(90);

        Camera.Size pictureSize=null;
        Camera.Size previewSize=null;
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewFrameRate(5);
        //设置旋转代码
        parameters.setRotation(90);
//			parameters.setPictureFormat(PixelFormat.JPEG);

        List<Camera.Size> supportedPictureSizes
                = SupportedSizesReflect.getSupportedPictureSizes(parameters);
        List<Camera.Size> supportedPreviewSizes
                = SupportedSizesReflect.getSupportedPreviewSizes(parameters);

        if ( supportedPictureSizes != null &&
                supportedPreviewSizes != null &&
                supportedPictureSizes.size() > 0 &&
                supportedPreviewSizes.size() > 0) {

            //2.x
            pictureSize = supportedPictureSizes.get(0);

            int maxSize = 1280;
            if(maxSize > 0){
                for(Camera.Size size : supportedPictureSizes){
                    if(maxSize >= Math.max(size.width,size.height)){
                        pictureSize = size;
                        break;
                    }
                }
            }

            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);

            previewSize = getOptimalPreviewSize(
                    supportedPreviewSizes,
                    display.getWidth(),
                    display.getHeight());

            parameters.setPictureSize(pictureSize.width, pictureSize.height);
            parameters.setPreviewSize(previewSize.width, previewSize.height);

        }
        camera.setParameters(parameters);
        return camera;
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    /**
     * 判断是否有SD卡，并创建文件存放件路径
     */
    public void makeDirs() {
        File sdDir = null;
        // 判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        } else if (!sdCardExist) {
        }
        File dirs_v = new File(sdDir + "/Video");
        Log.d(TAG, "dirs_v is : " + dirs_v);
        if (!dirs_v.exists())
            dirs_v.mkdirs();
    }

    /**
     * 删除视频
     */
    private void deleteVideo() {
        file = new File(saveVideoPath);
        if (file.exists()) {
            file.delete();
            Toast.makeText(this, "视频已清除", Toast.LENGTH_SHORT).show();
        }
        file = new File(currentVideoFilePath);
        if (file.exists()) {
            file.delete();
            Toast.makeText(this, "视频已清除", Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * 视频录制完后，封装视频信息
     *
     * @param filePath 视频路径
     * @param fileName 视屏保存名字
     * @return VideoInfo
     */
    private VideoInfo setVideoInfo(String filePath, String fileName) {
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setFilePath(filePath);
        videoInfo.setMimeType("video/mp4");
        videoInfo.setTitle(fileName);
        // 获得视频大小，MB
        long videoSize = 0;
        try {
            videoSize = VideoUtil.computeFileSize(videoInfo.getFilePath(), 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        videoInfo.setVideoSize(videoSize);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(videoInfo.getFilePath());
        // 获得视频时长，宽，高
        videoInfo.setVideoDuration(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        videoInfo.setVideoWidth(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        videoInfo.setVideoHight(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));

        return videoInfo;
    }
}

