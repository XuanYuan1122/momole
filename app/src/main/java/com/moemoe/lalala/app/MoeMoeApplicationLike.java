package com.moemoe.lalala.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.multidex.BuildConfig;
import android.support.multidex.MultiDex;

import com.moemoe.lalala.di.components.DaggerNetComponent;
import com.moemoe.lalala.di.components.NetComponent;
import com.moemoe.lalala.di.modules.NetModule;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.MyLogImp;
import com.moemoe.lalala.utils.SampleApplicationContext;
import com.moemoe.lalala.utils.TinkerManager;
import com.moemoe.lalala.utils.UnCaughtException;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created by yi on 2016/11/27.
 */
@DefaultLifeCycle(application = "com.moemoe.lalala.app.MoeMoeApplication",
                  flags = ShareConstants.TINKER_ENABLE_ALL,
                  loadVerifyFlag = false)
public class MoeMoeApplicationLike extends DefaultApplicationLike {

    private static MoeMoeApplicationLike instance;
    private NetComponent netComponent;

    public MoeMoeApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent, Resources[] resources, ClassLoader[] classLoader, AssetManager[] assetManager) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent, resources, classLoader, assetManager);
    }

    public static MoeMoeApplicationLike getInstance(){
        return instance;
    }

    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        MultiDex.install(base);
        instance = this;
        SampleApplicationContext.application = getApplication();
        SampleApplicationContext.context = getApplication();
        TinkerManager.setTinkerApplicationLike(this);
        TinkerManager.initFastCrashProtect();
        //should set before tinker is installed
        TinkerManager.setUpgradeRetryEnable(true);

        //optional set logIml, or you can use default debug log
        TinkerInstaller.setLogIml(new MyLogImp());

        //installTinker after load multiDex
        //or you can put com.tencent.tinker.** to main dex
        TinkerManager.installTinker(this);
        Tinker tinker = Tinker.with(getApplication());
        initLogger();
        initNet();
        GreenDaoManager.getInstance();
        UnCaughtException caughtException = UnCaughtException.getInstance();
        caughtException.init(base);

    }

    private void initLogger(){
        LogLevel logLevel;
        if(BuildConfig.DEBUG){
            logLevel = LogLevel.FULL;
        }else {
            logLevel = LogLevel.NONE;
        }
        Logger.init("NetaApp")
                .methodCount(3)
                .logLevel(logLevel);
    }

    private void initNet(){
        netComponent = DaggerNetComponent.builder()
                .netModule(new NetModule(getApplication()))
                .build();
    }

    public NetComponent getNetComponent(){return netComponent;}

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
    }
}
