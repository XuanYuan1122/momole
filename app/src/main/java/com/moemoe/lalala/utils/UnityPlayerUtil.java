package com.moemoe.lalala.utils;

import com.unity3d.player.UnityPlayer;

/**
 * Created by yi on 2017/10/12.
 */

public class UnityPlayerUtil {

    private static UnityPlayer instance;

    public static UnityPlayer getInstance(){
        return instance;
    }

    public static void setInstance(UnityPlayer player){
        instance = player;
    }
}
