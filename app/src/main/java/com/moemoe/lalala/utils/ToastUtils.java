package com.moemoe.lalala.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.moemoe.lalala.R;

/**
 * Created by yi on 2016/11/27.
 */

public class ToastUtils {
    private static Toast toast;
    private static View view;

    private ToastUtils() {
    }

    private static void getToast(Context context) {
        if (toast == null) {
            toast = new Toast(context);
        }
        if (view == null) {
            view = Toast.makeText(context, "", Toast.LENGTH_SHORT).getView();
        }
        toast.setView(view);
    }

    public static void showShortToast(Context context, CharSequence msg) {
        showToast(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
    }

    public static void showShortToast(Context context, int resId) {
        showToast(context.getApplicationContext(), resId, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, CharSequence msg) {
        showToast(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, int resId) {
        showToast(context.getApplicationContext(), resId, Toast.LENGTH_LONG);
    }

    public static void showNotVipToast(Context context, CharSequence msg){
        if(toast != null) toast.cancel();
        toast = new Toast(context);
        TextView tv = new TextView(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, context.getResources().getDimensionPixelSize(R.dimen.y60));
        tv.setLayoutParams(lp);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.x30));
        tv.setTextColor(Color.WHITE);
        tv.setText(msg);
        tv.setPadding(context.getResources().getDimensionPixelSize(R.dimen.x32),0,context.getResources().getDimensionPixelSize(R.dimen.x32),0);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundResource(R.drawable.shape_yellow_background_y30);
        toast.setView(tv);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private static void showToast(Context context, CharSequence msg, int duration) {
        try {
            getToast(context);
            toast.setText(msg);
            toast.setDuration(duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {

        }
    }

    private static void showToast(Context context, int resId, int duration) {
        try {
            if (resId == 0) {
                return;
            }
            getToast(context);
            toast.setText(resId);
            toast.setDuration(duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {

        }
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
