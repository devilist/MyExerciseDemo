package app.zengpu.com.myexercisedemo.demolist.galleryfinaldemo;

import android.content.Context;
import android.graphics.Color;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

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

        // 打开GalleryFinal
        if (isSingle)
            GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, functionConfig, mOnHanlderResultCallback);
        else if (!isCrop)
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
            GalleryFinal.openCamera(REQUEST_CODE_GALLERY, functionConfig, mOnHanlderResultCallback);
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
        //设置主题
        ThemeConfig themeConfig = new ThemeConfig.Builder()
                .setTitleBarBgColor(Color.rgb(0xFF, 0x57, 0x22))
                .setTitleBarTextColor(Color.BLACK)
                .setTitleBarIconColor(Color.BLACK)
                .setFabNornalColor(Color.RED)
                .setFabPressedColor(Color.BLUE)
                .setCheckNornalColor(Color.WHITE)
                .setCheckSelectedColor(Color.BLACK)
                .setIconBack(R.mipmap.ic_action_previous_item)
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
