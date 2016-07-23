package app.zengpu.com.myexercisedemo.demolist.videoRecord.util;

import android.content.Context;
import android.os.Environment;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.TrackBox;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Mp4TrackImpl;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.LinkedList;
import java.util.List;

import app.zengpu.com.myexercisedemo.Utils.LogUtil;

/**
 * 视频拼接工具
 * Created by tao on 2016/4/14.
 */
public final class VideoUtil {

    public static String videoFolderName = "VideoTemp";
    public static String videoTempFolderName = "VideoTemp/temp";

    /**
     * 拼接两段视频。拼接后的视频名字默认为第一段视频的名字。拼接成功后将删除原来的视频
     *
     * @param context
     * @param saveVideoPath        第一段视频名，同时也是合并后保存的视频文件名
     * @param currentVideoFilePath 第二段视频名
     * @throws IOException
     */
    public static void appendVideo(Context context, String saveVideoPath,
                                   String currentVideoFilePath) throws IOException {
        rotateVideo(context, currentVideoFilePath);
        String[] videos = new String[]{saveVideoPath, currentVideoFilePath};
        Movie[] inMovies = new Movie[videos.length];
        //拼接完成后的临时视频文件名
        String appendVideoPath = getSDPath(context, videoFolderName) + "append.mp4";

        //以下开始拼接视频
        int index = 0;
        for (String video : videos) {
            inMovies[index] = MovieCreator.build(video);
            index++;
        }
        List<Track> videoTracks = new LinkedList<Track>();
        List<Track> audioTracks = new LinkedList<Track>();
        index = 0;
        for (Movie m : inMovies) {
            for (Track t : m.getTracks()) {
                if (t.getHandler().equals("soun")) {
                    audioTracks.add(t);
                }
                if (t.getHandler().equals("vide")) {
                    if (index == 1) {
                    }
                    videoTracks.add(t);
                }
            }
        }
        Movie result = new Movie();
        if (audioTracks.size() > 0) {
            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }
        if (videoTracks.size() > 0) {
            result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
        }
        Container out = new DefaultMp4Builder().build(result);
        FileChannel fc = new RandomAccessFile(String.format(appendVideoPath), "rw").getChannel();
        out.writeContainer(fc);
        fc.close();

        //视频拼接完成后的后续处理
        //第一段视频文件
        File firstVideo = new File(saveVideoPath);
        //拼接后的临时文件
        File appendVideo = new File(appendVideoPath);
        //拼接后的临时文件名字改为第一段视频文件的名字，并覆盖
        appendVideo.renameTo(firstVideo);
        if (firstVideo.exists()) {
            //拼接后的临时文件删掉
            appendVideo.delete();
            //第二段视频也删掉，最后只剩下最新拼接后的视频了
            new File(currentVideoFilePath).delete();
        }
    }

    /**
     * 拼接几段视频
     *
     * @param context
     * @param saveVideoPath 拼接完成后保存文件名
     * @param videos        需要拼接的视频集合
     * @throws IOException
     */
    public static void appendVideo(Context context, String saveVideoPath, String[] videos)
            throws IOException {
        Movie[] inMovies = new Movie[videos.length];
        int index = 0;
        for (String video : videos) {
            inMovies[index] = MovieCreator.build(video);
            index++;
        }
        List<Track> videoTracks = new LinkedList<Track>();
        List<Track> audioTracks = new LinkedList<Track>();
        for (Movie m : inMovies) {
            for (Track t : m.getTracks()) {
                if (t.getHandler().equals("soun")) {
                    audioTracks.add(t);
                }
                if (t.getHandler().equals("vide")) {
                    videoTracks.add(t);
                }
            }
        }
        Movie result = new Movie();
        if (audioTracks.size() > 0) {
            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }
        if (videoTracks.size() > 0) {
            result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
        }
        Container out = new DefaultMp4Builder().build(result);
        FileChannel fc = new RandomAccessFile(String.format(saveVideoPath), "rw").getChannel();
        out.writeContainer(fc);
        fc.close();
    }


    public static void rotateVideo(Context context, String currentVideoPath) {
        String rotateVideoPath = getSDPath(context, videoFolderName) + "rotate.mp4";
        Movie result = new Movie();
        try {
            result = MovieCreator.build(currentVideoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Container out = new DefaultMp4Builder().build(result);
        MovieHeaderBox mvhd = (MovieHeaderBox) Path.getPath(out, "moov/mvhd");
        mvhd.setMatrix(Matrix.ROTATE_180);
        try {
            out.writeContainer(new FileOutputStream(rotateVideoPath).getChannel());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //视频旋转完成后的后续处理
        //原始视频文件
        File origVideo = new File(currentVideoPath);
        //旋转后的临时文件
        File rotateVideo = new File(rotateVideoPath);
        //旋转后的临时文件名字改为原始视频文件的名字，并覆盖
        rotateVideo.renameTo(origVideo);
        if (origVideo.exists()) {
            //拼接后的临时文件删掉
            rotateVideo.delete();
        }

//        FileChannel fc = new RandomAccessFile(String.format(rotateVideoPath), "rw").getChannel();
//        out.writeContainer(fc);
//        fc.close();

    }

    public static void rotateVideo(Context context, String currentVideoPath,int rotateDegree) {

        // 1.获取视频文件对应的TrackBoxes；
        IsoFile isoFile = null;
        try {
            isoFile = new IsoFile(currentVideoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<TrackBox> trackBoxes = isoFile.getMovieBox().getBoxes(TrackBox.class);
        LogUtil.e("VideoUtil", "trackBoxes size  is: " + trackBoxes.size());

        // 2.根据TrackBoxes获取旋转后的Movie对象；
        Movie rotatedMovie = new Movie();

        for (int i = 0; i< trackBoxes.size();i++) {
            TrackBox trackBox = trackBoxes.get(i);
            LogUtil.e("VideoUtil", "matrix is: " + trackBox.getTrackHeaderBox().getMatrix());
            Matrix matrix = trackBox.getTrackHeaderBox().getMatrix();

            switch (matrix.toString()) {
                case "Rotate 0°":
                    trackBox.getTrackHeaderBox().setMatrix(Matrix.ROTATE_180);
                    break;
                case "Rotate 90°":
                    trackBox.getTrackHeaderBox().setMatrix(Matrix.ROTATE_270);
                    break;
                case "Rotate 180°":
                    trackBox.getTrackHeaderBox().setMatrix(Matrix.ROTATE_0);
                    break;
                case "Rotate 270°":
                    trackBox.getTrackHeaderBox().setMatrix(Matrix.ROTATE_90);
                    break;
            }

            LogUtil.e("VideoUtil", "matrix after is: " + trackBox.getTrackHeaderBox().getMatrix());

//            trackBox.getTrackHeaderBox().setMatrix(Matrix.ROTATE_180);
            rotatedMovie.addTrack(new Mp4TrackImpl(trackBox));
        }

        // 3.将Movie对象写入文件。
        String rotateVideoPath = getSDPath(context, videoFolderName) + "rotate.mp4";
        Container container = new DefaultMp4Builder().build(rotatedMovie);
        //旋转后的临时文件
        File roratedVideoFile = new File(rotateVideoPath);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(roratedVideoFile);
            WritableByteChannel bb = Channels.newChannel(fos);
            container.writeContainer(bb);
            // 关闭文件流
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //视频旋转完成后的后续处理
        //原始视频文件
        File origVideo = new File(currentVideoPath);
        //旋转后的临时文件名字改为原始视频文件的名字，并覆盖
        roratedVideoFile.renameTo(origVideo);
        if (origVideo.exists()) {
            //拼接后的临时文件删掉
            roratedVideoFile.delete();
        }

    }


    /**
     * 获得SD卡路径
     *
     * @param context
     * @return
     */
    public static String getSDPath(Context context, String filePath) {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        } else if (!sdCardExist) {

            LogUtil.e("Video", "SD卡不存在");

        }
        File eis = new File(sdDir + "/" + filePath);
        try {
            if (!eis.exists()) {
                eis.mkdir();
            }
        } catch (Exception e) {

        }
        return sdDir + "/" + filePath + "/";
    }


    /**
     * 计算视频文件大小
     *
     * @param filePath
     * @param videoUnit 单位，1 KB，2，MB
     * @return
     * @throws Exception
     */
    public static long computeFileSize(String filePath, int videoUnit) throws Exception {

        long s = 0;
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            s = fis.available();
            if (videoUnit == 1) s = s / 1024;
            if (videoUnit == 2) s = s / 1024 / 1024;
            //MB
        } else {
            file.createNewFile();
            LogUtil.e("ShiPinXuanZeActivity", "文件不存在");
            s = -1;
        }
        return s;
    }


    /**
     * 获取mediarecorder.setOrientationHint() 和 camera.setDisplayOrientation() 之间的关系
     *
     * @return int[camera前置，camera后置，mediarecorder前置，mediarecorder后置]
     */
    public static int[] getOritationMap() {

        String brand = android.os.Build.MODEL;

        if (brand.contains("MI"))
            return new int[]{90, 90, 270, 90};
        if (brand.contains("Nexus"))
            return new int[]{90, 270, 270, 270};

        // 默认
        return new int[]{90, 270, 270, 270};

    }

}

