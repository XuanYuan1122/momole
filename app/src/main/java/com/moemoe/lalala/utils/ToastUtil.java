package com.moemoe.lalala.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Haru on 2016/4/22 0022.
 */
public class ToastUtil {
    /**
     * 显示一条屏幕中间的toast
     * @param context
     * @param msgResId message resource id of toast
     * @param duration
     */
    public static void showCenterToast(Context context, int msgResId, int duration){
        showCenterToast(context, context.getString(msgResId), duration);
    }

    /**
     * 显示一条屏幕中间的toast
     * @param context
     * @param msg message of toast
     * @param duration
     */
    public static void showCenterToast(Context context, String msg, int duration){
        if(context != null){
            Toast t = Toast.makeText(context, msg, duration);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
        }
    }

    public static void showCenterToast(Context context, int msg){
        if(context != null) showCenterToast(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, int resId) {
        if (context != null) {
            Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static void showToast(Context context, String msg) {
        if (context != null) {
            Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static void showTopToast(Context context, int resId) {
        if (context != null) {
            Toast t = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP, 0, 0);
            t.show();
        }
    }
}
