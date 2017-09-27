package com.moemoe.lalala.greendao.gen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.moemoe.lalala.model.entity.JuQIngStoryEntity;
import com.moemoe.lalala.model.entity.JuQingTriggerEntity;
import com.moemoe.lalala.utils.JuQingDoneEntity;
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
            MigrationHelper.migrate(db, AuthorInfoDao.class,
                    NetaDbDao.class,
                    AlarmClockEntityDao.class,
                    JuQingDoneEntityDao.class,
                    JuQingTriggerEntityDao.class,
                    JuQIngStoryEntityDao.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
