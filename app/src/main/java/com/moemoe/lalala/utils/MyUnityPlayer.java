package com.moemoe.lalala.utils;

import android.content.Context;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;

/**
 * Created by yi on 2017/10/12.
 */

public class MyUnityPlayer extends UnityPlayer {
    public MyUnityPlayer(Context context) {
        super(context);
    }

    @Override
    protected void kill() {
        Toast.makeText(getContext(),"unity关闭",Toast.LENGTH_SHORT).show();
    }
}
