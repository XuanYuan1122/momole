package com.moemoe.lalala.galgame.framework;

/**
 * Created by Haru on 2016/7/18 0018.
 */
public class Live2DFramework {
    private static IPlatformManager platformManager;

    public Live2DFramework()
    {

    }

    public static IPlatformManager getPlatformManager() {
        return platformManager;
    }

    public static void setPlatformManager(IPlatformManager platformManager) {
        Live2DFramework.platformManager = platformManager;
    }
}
