package com.moemoe.lalala.app;

import android.app.Application;
import android.support.multidex.BuildConfig;
import android.support.multidex.MultiDex;

import com.moemoe.lalala.di.components.DaggerNetComponent;
import com.moemoe.lalala.di.components.NetComponent;
import com.moemoe.lalala.di.modules.NetModule;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.UnCaughtException;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.greendao.query.QueryBuilder;

import io.rong.imkit.RongIM;

/**
 * Created by yi on 2017/5/11.
 */

public class MoeMoeApplication extends Application {
    /**
     * 当前进程允许的最大内存
     */
    public static long PROCESS_MEMORY_LIMIT;
    private static MoeMoeApplication mInstance = null;
    private NetComponent netComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        MultiDex.install(this);
        RongIM.init(this);
        initLogger();
        MoeMoeAppListener.init(this);
        initNet();
        GreenDaoManager.getInstance();
        UnCaughtException caughtException = UnCaughtException.getInstance();
        caughtException.init(this);
        //初始化Leak内存泄露检测工具
        //LeakCanary.install(this);
    }

    private void initLogger(){
        LogLevel logLevel;
        if(BuildConfig.DEBUG){
            logLevel = LogLevel.FULL;
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
        }else {
            logLevel = LogLevel.NONE;
            QueryBuilder.LOG_SQL = false;
            QueryBuilder.LOG_VALUES = false;
        }
        Logger.init("NetaApp")
                .methodCount(3)
                .logLevel(logLevel);
    }

    private void initNet(){
        netComponent = DaggerNetComponent.builder()
                .netModule(new NetModule(this))
                .build();
    }

    public NetComponent getNetComponent(){return netComponent;}

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static MoeMoeApplication getInstance() {
        if (mInstance == null) {
            mInstance = new MoeMoeApplication();
        }
        return mInstance;
    }
}
