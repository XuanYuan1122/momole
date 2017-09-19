package com.moemoe.lalala.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import com.moemoe.lalala.view.activity.BaseAppCompatActivity;

/**
 * Created by yi on 2016/11/27.
 */

public class DensityUtil {
    private static float density = -1F;
    private static int widthPixels = -1;
    private static int heightPixels = -1;

    private DensityUtil() {
    }

    public static float getDensity(Context context) {
        if (density <= 0F) {
            density = context.getResources().getDisplayMetrics().density;
        }
        return density;
    }

    public static int dip2px(Context context,float dpValue) {
        return (int) (dpValue * getDensity(context) + 0.5F);
    }

    public static int px2dip(Context context,float pxValue) {
        return (int) (pxValue / getDensity(context) + 0.5F);
    }

    public static int getScreenWidth(Context context) {
        if (widthPixels <= 0) {
            widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        }
        return widthPixels;
    }

    public static int getScreenRWidth(Context context) {
        DisplayMetrics metrics =new DisplayMetrics();
        ((BaseAppCompatActivity)context).getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        return metrics.widthPixels;
    }


    public static int getScreenHeight(Context context) {
        if (heightPixels <= 0) {
            heightPixels = context.getResources().getDisplayMetrics().heightPixels;
        }
        return heightPixels;
    }

    public static int getScreenRHeight(Context context) {
        DisplayMetrics metrics =new DisplayMetrics();
        ((BaseAppCompatActivity)context).getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        return metrics.heightPixels;
    }
}
