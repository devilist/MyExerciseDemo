package app.zengpu.com.myexercisedemo;

import android.database.SQLException;
import android.os.Bundle;

import java.io.IOException;

import app.zengpu.com.myexercisedemo.Utils.AssetsDataBaseHelper;
import app.zengpu.com.myexercisedemo.Utils.LogUtil;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by tao on 2016/8/26.
 */
public class BaseActivity extends SwipeBackActivity {

    private SwipeBackLayout mSwipeBackLayout;

    protected AssetsDataBaseHelper assetsDataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    protected void isSwipeBackEnabled(boolean isSwipeBackEnabled) {
        if (isSwipeBackEnabled) {
            mSwipeBackLayout = getSwipeBackLayout();
            mSwipeBackLayout.setEdgeSize(200);
            mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);
        }
    }

    /**
     * 打开数据库
     *
     * @return 是否打开成功
     */
    protected boolean openDictDataBase() {

        assetsDataBaseHelper = new AssetsDataBaseHelper(this, AssetsDataBaseHelper.DB_NAME_GHY_DICT);

        boolean isOpenSuccess = false;

        try {
            assetsDataBaseHelper.createDataBase();
        } catch (IOException e) {
            LogUtil.e("AssetsDataBaseHelper", "Unable to create database: " + e.toString());
        }
        try {
            assetsDataBaseHelper.openDataBase();
            isOpenSuccess = true;
        } catch (SQLException e) {
            LogUtil.e("AssetsDataBaseHelper", "Unable to open database: " + e.toString());
        }
        return isOpenSuccess;
    }

    /**
     * 关闭数据库
     */
    protected void closeDictDataBase() {
        assetsDataBaseHelper.close();
    }
}
