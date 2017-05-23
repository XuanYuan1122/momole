package com.moemoe.lalala.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.moemoe.lalala.R;

/**
 * Created by yi on 2016/11/28.
 */

public class NetworkUtils {
    public static boolean isNetworkAvailable(Context context){
        boolean isAvailable = false;
        if (context == null){
            return isAvailable;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null){
            return isAvailable;
        }
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null){
            return isAvailable;
        }
        isAvailable = ni.isAvailable();
        //LogUtils.LOGI(TAG, "NetState = " + isAvailable);
        return isAvailable;
    }

    public static boolean checkNetworkAndShowError(final Context context){
        if(isNetworkAvailable(context)){
            return true;
        }else{
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showShortToast(context, context.getString(R.string.msg_connection));
                }
            });
            return false;
        }
    }

    /**
     * 当前是否wifi网络下
     * @return
     */
    public static boolean isWifi(Context context){
        boolean res = false;
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null) {
                if (info.isConnected()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                        res = true;
                    } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        res = false;
                    }
                }
            }
        }
        return res;
    }
}
