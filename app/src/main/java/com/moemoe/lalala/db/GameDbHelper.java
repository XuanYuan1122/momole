package com.moemoe.lalala.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.support.ConnectionSource;
import com.moemoe.lalala.data.NetaDownloadInfo;


import java.util.List;

/**
 * GameDbHelper
 *
 * @author wlf(Andy)
 * @datetime 2015-12-12 09:23 GMT+8
 * @email 411086563@qq.com
 */
public class GameDbHelper extends BaseOrmLiteSQLiteHelper {

    private static final String DB_NAME = "course.db";
    private static final int DB_VERSION = 1;

    private static GameDbHelper sInstance;

    public GameDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * get GameDbHelper single instance,if the instance is null,will init the instance and open the database
     * <br/>
     * 获取单一实例，如果实例不存在将新创建，并且同时打开当前管理的数据库
     *
     * @param context
     * @return single instance
     */
    public static GameDbHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (GameDbHelper.class) {
                if (sInstance == null || !sInstance.isOpen()) {
                    sInstance = new GameDbHelper(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    @Override
    protected void onConfigTables(List<Class<?>> supportTables) {
        if (supportTables == null) {
            return;
        }
        // add table
        supportTables.add(NetaDownloadInfo.class);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        // nothing to do 
    }

    @Override
    public void close() {
        super.close();
        sInstance = null;
    }
}
