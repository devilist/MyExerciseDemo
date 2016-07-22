package app.zengpu.com.myexercisedemo.demolist.videoRecord.model;

import java.io.Serializable;

/**
 * 视频信息 路径 类型 文件名称
 * Created by tao on 2016/4/10.
 */
public class VideoInfo implements Serializable{
    /**
     * 视频文件路径
     */
   private String filePath;
    /**
     * 视频文件类型
     */
    private String mimeType;
    /**
     * 视频文件缩略图路径
     */
   private String thumbPath;
    /**
     * 视频文件名称
     */
    private String title;
    /**
     * 视频文件宽度
     */
    private String videoWidth;
    /**
     * 视频文件高度
     */
    private String videoHight;
    /**
     * 视频文件时长
     */
    private String videoDuration;
    /**
     * 视频文件大小
     */
    private long videoSize;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(String videoWidth) {
        this.videoWidth = videoWidth;
    }

    public String getVideoHight() {
        return videoHight;
    }

    public void setVideoHight(String videoHight) {
        this.videoHight = videoHight;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public float getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(long videoSize) {
        this.videoSize = videoSize;
    }
}
