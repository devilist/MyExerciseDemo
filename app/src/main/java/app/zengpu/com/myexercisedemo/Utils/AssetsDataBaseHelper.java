package app.zengpu.com.myexercisedemo.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 本地数据库操作帮助类
 * Created by zengpu on 2016/11/21.
 */
public class AssetsDataBaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME_GHY_DICT = "ghydict.db"; // 古汉语字典

    private String mDbName; // 数据库名字
    private SQLiteDatabase mDataBase;
    private final Context mContext;

    public AssetsDataBaseHelper(Context context, String db_name) {

        super(context, db_name, null, 1);
        this.mContext = context;
        this.mDbName = db_name;
    }

    /**
     * 在应用包路径下创建数据库
     */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            //已存在
        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database：" + e.toString());
            }
        }
    }

    /**
     * 检查数据库是否已经存在应用包名路径下，避免每次打开app时重复从assets文件夹里copy到应用目录
     *
     * @return
     */
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = getDbPath() + mDbName;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {
            //不存在
        }

        if (checkDB != null) {
            checkDB.close();
        }

//        File dbFile = mContext.getDatabasePath(mDbName);
//        return dbFile.exists();

        return checkDB != null ? true : false;
    }

    /**
     * 将assets目录下的本地数据库copy到应用目录下
     */
    private void copyDataBase() throws IOException {
        // 打开assets目录下的本地数据库
        InputStream myInput = mContext.getAssets().open(mDbName);
        // 新建一个和本地数据库同名的数据库，存放路径（app包路径下）
        String outFileName = getDbPath() + mDbName;


        // 打开新建的数据库
        OutputStream myOutput = new FileOutputStream(outFileName);
        //转换为字节流，复制
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //关闭流
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    /**
     * 打开数据库
     *
     * @throws SQLException
     */
    public void openDataBase() throws SQLException {
        String mPath = getDbPath() + mDbName;
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    /**
     * 数据库存放路径 "/data/user/0/ [PACKAGE_NAME] /files/databases/"
     *
     * @return
     */
    private String getDbPath() {
        String dir = mContext.getFilesDir().getAbsolutePath() + "/databases/";
        File dir_file = new File(dir);
        if (!dir_file.exists())
            dir_file.mkdir();
        return dir;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }


    /**
     * 查字典 根据汉字查释义
     *
     * @return
     */
    public Map<String, String> queryHanzi(String hanzi) {
        if (!mDbName.equals(DB_NAME_GHY_DICT))
            return null;
//        String[] columns = new String[]{"ID","hanzi","yinjie","bushou","bushoubihuashu","zongbihuashu","bishun","shiyi"};
        String[] columns = new String[]{"hanzi", "shiyi"};
        Cursor cursor = mDataBase.query("GuHanZi", columns, "hanzi = ?", new String[]{hanzi}, null, null, null);
        if (null != cursor) {
            Map<String, String> result = new HashMap<>();
            while (cursor.moveToNext()) {
                result.put(cursor.getString(cursor.getColumnIndex("hanzi")), cursor.getString(cursor.getColumnIndex("shiyi")));
            }
            return result;
        } else
            return null;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
