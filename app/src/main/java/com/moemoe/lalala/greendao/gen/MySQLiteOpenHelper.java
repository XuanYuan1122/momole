package com.moemoe.lalala.greendao.gen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.moemoe.lalala.utils.MigrationHelper;


/**
 * Created by yi on 2017/1/22.
 */

public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            MigrationHelper.migrate(db, AuthorInfoDao.class,PrivateMessageItemEntityDao.class,ChatContentDbEntityDao.class,ChatUserEntityDao.class,GroupUserEntityDao.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
