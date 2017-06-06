package com.moemoe.lalala.utils;

import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.greendao.gen.DaoMaster;
import com.moemoe.lalala.greendao.gen.DaoSession;
import com.moemoe.lalala.greendao.gen.MySQLiteOpenHelper;

/**
 * Created by yi on 2016/11/28.
 */

public class GreenDaoManager {
    private static GreenDaoManager mInstance; //单例
    private DaoMaster mDaoMaster; //以一定的模式管理Dao类的数据库对象
    private DaoSession mDaoSession; //管理制定模式下的所有可用Dao对象

    private GreenDaoManager(){
        if (mInstance == null){
//            DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(SampleApplicationContext.context,
//                    "netaInfo_v2.0",null);
            MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(MoeMoeApplication.getInstance(),"netaInfo_v2.0",null);
            mDaoMaster = new DaoMaster(mySQLiteOpenHelper.getWritableDatabase());
            mDaoSession = mDaoMaster.newSession();
        }
    }

    public static GreenDaoManager getInstance(){
        if(mInstance == null){
            synchronized (GreenDaoManager.class){
                if(mInstance == null){
                    mInstance = new GreenDaoManager();
                }
            }
        }
        return mInstance;
    }

    public DaoMaster getMaster(){return mDaoMaster;}

    public DaoSession getSession(){return mDaoSession;}

    public DaoSession getNewSession(){
        mDaoSession = mDaoMaster.newSession();
        return mDaoSession;
    }
}
