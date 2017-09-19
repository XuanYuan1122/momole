package com.moemoe.lalala.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.moemoe.lalala.service.DaemonService;

/**
 * 接收手机启动广播
 * Created by yi on 2017/9/12.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, DaemonService.class));
    }
}
