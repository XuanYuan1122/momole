package com.moemoe.lalala.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ToastUtils;

/**
 * Created by yi on 2016/10/12.
 */

public class AppInstallReceiver extends BroadcastReceiver {

    private static final String mPackageName= "com.miHoYo.enterprise.NGHSoD";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {  // install
            String packageName = intent.getDataString().split(":")[1];
            if(packageName.equals(mPackageName)){
                PreferenceUtils.saveHaveGameFuku(context,true);
                ToastUtils.showShortToast(context,"安装成功，快去试试新衣服吧");
            }
        }
    }
}
