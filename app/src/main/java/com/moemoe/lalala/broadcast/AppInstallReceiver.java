package com.moemoe.lalala.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.ToastUtil;

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
                PreferenceManager.getInstance(context).saveHaveGameFuku(true);
                ToastUtil.showCenterToast(context,"安装成功，快去试试新衣服吧", Toast.LENGTH_SHORT);
            }
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) { // uninstall
        }
        if(Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())){
        }
    }
}
