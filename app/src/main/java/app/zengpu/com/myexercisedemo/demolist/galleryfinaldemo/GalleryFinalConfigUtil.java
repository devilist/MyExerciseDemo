package app.zengpu.com.myexercisedemo.demolist.galleryfinaldemo;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;
import java.util.List;

import app.zengpu.com.myexercisedemo.R;
import app.zengpu.com.myexercisedemo.demolist.galleryfinaldemo.listener.UILPauseOnScrollListener;
import app.zengpu.com.myexercisedemo.demolist.galleryfinaldemo.loader.UILImageLoader;
import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.PauseOnScrollListener;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by zengpu on 2016/6/30.
 */
public class GalleryFinalConfigUtil {

    private static final int REQUEST_CODE_CAMERA = 1000;
    private static final int REQUEST_CODE_GALLERY = 1001;
    private static final int REQUEST_CODE_CROP = 1002;
    private static final int REQUEST_CODE_EDIT = 1003;


    /**
     * 打开GalleryFinal
     *
     * @param context
     * @param mPhotoList               返回选择的图片
     * @param isSingle                 是否只选一张
     * @param isCrop                   是否裁剪
     * @param maxSelect                最多选几张
     * @param mOnHanlderResultCallback 结果回调接口
     */
    public static void openGalleryFinal(Context context,
                                        List<PhotoInfo> mPhotoList,
                                        boolean isSingle,
                                        boolean isCrop,
                                        int maxSelect,
                                        GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback) {

        // 配置主题
        ThemeConfig themeConfig = initThemeConfig();
        // 选择需要的功能
        final FunctionConfig functionConfig = initFunctionConfig(mPhotoList, isSingle, isCrop, maxSelect);

        // 初始化GalleryFinal
        initGalleryFinal(context, themeConfig, functionConfig);

        String path = "/storage/emulated/0/DCIM/Camera/IMG_20160102_120534.jpg";

        // 打开GalleryFinal
        if (isSingle)
            GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, functionConfig, mOnHanlderResultCallback);
        else
            GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY, functionConfig, mOnHanlderResultCallback);
    }

    /**
     * 打开GalleryFinal take photo
     *
     * @param context
     * @param mPhotoList               返回选择的图片
     * @param isCrop                   是否裁剪
     * @param mOnHanlderResultCallback 结果回调接口
     */
    public static void openGalleryFinal_TakePhoto(Context context,
                                                  List<PhotoInfo> mPhotoList,
                                                  boolean isCrop,
                                                  GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback) {

        // 配置主题
        ThemeConfig themeConfig = initThemeConfig();
        // 选择需要的功能
        final FunctionConfig functionConfig = initFunctionConfig(mPhotoList, true, isCrop, 1);

        // 初始化GalleryFinal
        initGalleryFinal(context, themeConfig, functionConfig);

        // 打开GalleryFinal
        GalleryFinal.openCamera(REQUEST_CODE_CAMERA, functionConfig, mOnHanlderResultCallback);
    }

    /**
     * 打开GalleryFinal剪切图片 crop photo
     *
     * @param context
     * @param mPhotoList               返回选择的图片
     * @param path                     图片路径
     * @param mOnHanlderResultCallback 结果回调接口
     */
    public static void openGalleryFinal_Crop(Context context,
                                             List<PhotoInfo> mPhotoList,
                                             String path,
                                             GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback) {

        // 配置主题
        ThemeConfig themeConfig = initThemeConfig();
        // 选择需要的功能
        final FunctionConfig functionConfig = initFunctionConfig(mPhotoList, true, true, 1);

        // 初始化GalleryFinal
        initGalleryFinal(context, themeConfig, functionConfig);

        // 打开GalleryFinal
        if (new File(path).exists()) {
            GalleryFinal.openCrop(REQUEST_CODE_CROP, functionConfig, path, mOnHanlderResultCallback);
        } else {
            Toast.makeText(context, "图片不存在", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 初始化GarellyFinal
     *
     * @param context
     * @param themeConfig    主题配置
     * @param functionConfig 功能配置
     */
    public static void initGalleryFinal(Context context, ThemeConfig themeConfig, FunctionConfig functionConfig) {

        //配置imageloader
        ImageLoader imageLoader = null;
        PauseOnScrollListener pauseOnScrollListener = null;

        imageLoader = new UILImageLoader();
        pauseOnScrollListener = new UILPauseOnScrollListener(false, true);

//        imageLoader = new XUtils2ImageLoader(GalleryFinalActivity.this);
//        imageLoader = new XUtilsImageLoader();
//        imageLoader = new GlideImageLoader();
//        pauseOnScrollListener = new GlidePauseOnScrollListener(false, true);
//        imageLoader = new FrescoImageLoader(GalleryFinalActivity.this);
//        imageLoader = new PicassoImageLoader();
//        pauseOnScrollListener = new PicassoPauseOnScrollListener(false, true);

        CoreConfig coreConfig = new CoreConfig.Builder(context, imageLoader, themeConfig)
                .setFunctionConfig(functionConfig) //配置全局GalleryFinal功能
                .setPauseOnScrollListener(pauseOnScrollListener) //设置imageloader滑动加载图片优化OnScrollListener,根据选择的ImageLoader来选择PauseOnScrollListener
                .setNoAnimcation(true) // 无动画
                .build();

//        setDebug //debug开关
//        setEditPhotoCacheFolder(File file)//配置编辑（裁剪和旋转）功能产生的cache文件保存目录，不做配置的话默认保存在/sdcard/GalleryFinal/edittemp/
//        setTakePhotoFolder设置拍照保存目录，默认是/sdcard/DICM/GalleryFinal/

        GalleryFinal.init(coreConfig);

        initImageLoader(context);

    }

    /**
     * 配置主题色
     *
     * @return
     */
    private static ThemeConfig initThemeConfig() {

//        setTitleBarTextColor//标题栏文本字体颜色
//        setTitleBarBgColor//标题栏背景颜色
//        setTitleBarIconColor//标题栏icon颜色，如果设置了标题栏icon，设置setTitleBarIconColor将无效
//        setCheckNornalColor//选择框未选颜色
//        setCheckSelectedColor//选择框选中颜色
//        setCropControlColor//设置裁剪控制点和裁剪框颜色
//        setFabNornalColor//设置Floating按钮Nornal状态颜色
//        setFabPressedColor//设置Floating按钮Pressed状态颜色
//        setIconBack//设置返回按钮icon
//        setIconCamera//设置相机icon
//        setIconCrop//设置裁剪icon
//        setIconRotate//设置旋转icon
//        setIconClear//设置清楚选择按钮icon（标题栏清除选择按钮）
//        setIconFolderArrow//设置标题栏文件夹下拉arrow图标
//        setIconDelete//设置多选编辑页删除按钮icon
//        setIconCheck//设置checkbox和文件夹已选icon
//        setIconFab//设置Floating按钮icon
//        setEditPhotoBgTexture//设置图片编辑页面图片margin外背景
//        setIconPreview设置预览按钮icon
//        setPreviewBg设置预览页背景

        //设置主题
        ThemeConfig themeConfig = new ThemeConfig.Builder()
                .setTitleBarBgColor(Color.parseColor("#bb000000"))
                .setBottomBarBgColor(Color.parseColor("#bb000000"))
                .setTitleBarTextColor(Color.WHITE)
                .setTitleBarIconColor(Color.WHITE)
                .setFabNornalColor(Color.BLACK)
                .setFabPressedColor(Color.BLUE)
                .setCheckNornalColor(Color.WHITE)
                .setCheckSelectedColor(Color.BLACK)
//                .setIconBack(R.mipmap.ic_action_previous_item)
                .setIconRotate(R.mipmap.ic_action_repeat)
                .setIconCrop(R.mipmap.ic_action_crop)
                .setIconCamera(R.mipmap.ic_action_camera)
                .build();

        return themeConfig;

    }

    /**
     * 配置所需要的功能
     *
     * @param mPhotoList 返回选择的图片
     * @param isSingle   是否只选一张
     * @param isCrop     是否裁剪
     * @param maxSelect  最多选几张
     * @return
     */
    private static FunctionConfig initFunctionConfig(List<PhotoInfo> mPhotoList, boolean isSingle,
                                                     boolean isCrop, int maxSelect) {
        /*
        * 多选情况下，不可编辑，不可裁剪
        * 单选情况下，可以裁剪也可以不裁剪
        * */

        boolean isEditted = false;
        boolean isCropped = false;

        if (!isCrop || !isSingle) {
            // 不可剪，多选
            isEditted = false;
            isCropped = false;
        } else {
            // 单选可剪
            isEditted = true;
            isCropped = true;
        }

        //配置功能
        final FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setMutiSelectMaxSize(maxSelect)
                .setEnableEdit(isEditted)// 可编辑
                .setEnableRotate(false) // 是否可旋转
                .setRotateReplaceSource(false) // 是否覆盖原图
                .setEnableCrop(isCropped) // 剪切
                .setCropSquare(true) // 剪切为方形
                .setCropWidth(500)
                .setCropHeight(500)
                .setCropReplaceSource(true) // 是否覆盖原图
                .setEnablePreview(true)
                .setEnableCamera(true)
                .setSelected(mPhotoList)
                .setForceCrop(isCropped)//启动强制裁剪功能,一进入编辑页面就开启图片裁剪，不需要用户手动点击裁剪，此功能只针对单选操作
                .setForceCropEdit(false)//在开启强制裁剪功能时是否可以对图片进行编辑（也就是是否显示旋转图标和拍照图标）
                .build();

        return functionConfig;

    }


    /**
     * ImageLoader 初始化
     *
     * @param context
     */
    private static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config.build());
    }

}
