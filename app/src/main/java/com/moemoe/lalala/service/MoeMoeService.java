package com.moemoe.lalala.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

/**
 * Created by Haru on 2016/6/6 0006.
 */
public class MoeMoeService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
