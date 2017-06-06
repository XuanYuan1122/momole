package com.moemoe.lalala.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

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


//    public static final long DEFAULT_DURATION = 1000L;
//
//    public static final int LENGTH_LONG = android.widget.Toast.LENGTH_LONG;
//    public static final int LENGTH_SHORT = android.widget.Toast.LENGTH_SHORT;
//
//    private static android.widget.Toast normalToast;
//    private static android.widget.Toast gravityToast;
//    private static Handler handler;
//
//
//    static {
//        if (!(Looper.myLooper() == Looper.getMainLooper())) {
//            Looper.prepare();
//        }
//        handler = new Handler();
//    }
//
//
//    private static Runnable run = new Runnable() {
//        public void run() {
//            if (normalToast != null) normalToast.cancel();
//            if (gravityToast != null) gravityToast.cancel();
//        }
//    };
//
//    private static void toast(Context context, CharSequence text, int duration,int gravity,int textColor,float textSie,int w,int h,int bgColor) {
//        if (context == null) return;
//        handler.removeCallbacks(run);
//        long delayMillis;
//        switch (duration) {
//            case LENGTH_LONG:
//                delayMillis = 3000L;
//                break;
//            case LENGTH_SHORT:
//            default:
//                delayMillis = DEFAULT_DURATION;
//                break;
//        }
//        if (normalToast == null) {
//            normalToast = android.widget.Toast.makeText(context, text, duration);
//        } else {
//            normalToast.setText(text);
//        }
//        TextView tv = new TextView(context);
//        tv.setText(text);
//        tv.setTextColor(textColor);
//        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,textSie);
//        tv.setBackgroundColor(bgColor);
//        tv.setGravity(Gravity.CENTER);
//        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(w,h);
//        tv.setLayoutParams(lp);
//        normalToast.setView(tv);
//        normalToast.setGravity(gravity,0,0);
//        handler.postDelayed(run, delayMillis);
//        normalToast.show();
//    }
//
//    private static void toast(Context context, CharSequence text, int duration) {
//        if (context == null) return;
//        handler.removeCallbacks(run);
//        long delayMillis;
//        switch (duration) {
//            case LENGTH_LONG:
//                delayMillis = 3000L;
//                break;
//            case LENGTH_SHORT:
//            default:
//                delayMillis = DEFAULT_DURATION;
//                break;
//        }
//        if (normalToast == null) {
//            normalToast = android.widget.Toast.makeText(context, text, duration);
//        } else {
//            normalToast.setText(text);
//        }
//        handler.postDelayed(run, delayMillis);
//        normalToast.show();
//    }
//
//    private static void toast(Context context, CharSequence text, int duration, int gravity, int xOffset, int yOffset) {
//        if (context == null) return;
//        handler.removeCallbacks(run);
//        long delayMillis;
//        switch (duration) {
//            case LENGTH_LONG:
//                delayMillis = 3000L;
//                break;
//            case LENGTH_SHORT:
//            default:
//                delayMillis = DEFAULT_DURATION;
//                break;
//        }
//        if (gravityToast == null) {
//            gravityToast = android.widget.Toast.makeText(context, text, duration);
//        } else {
//            gravityToast.setText(text);
//        }
//        gravityToast.setGravity(gravity, xOffset, yOffset);
//        handler.postDelayed(run, delayMillis);
//        gravityToast.show();
//    }
//
//
//    /**
//     * 弹出Toast
//     *
//     * @param context 弹出Toast的上下文
//     * @param text 弹出Toast的内容
//     * @param duration 弹出Toast的持续时间
//     */
//    public static void show(Context context, CharSequence text, int duration) {
//        if (duration > 0) {
//            duration = LENGTH_SHORT;
//        }
//        toast(context, text, duration);
//    }
//
//
//    /**
//     * 中间弹出Toast
//     *
//     * @param context 弹出Toast的上下文
//     * @param text 弹出Toast的内容
//     */
//    public static void showCenter(Context context, CharSequence text) {
//        toast(context, text, LENGTH_SHORT, Gravity.CENTER, 0, 0);
//    }
//
//    /**
//     * 中弹出Toast
//     *
//     * @param context 弹出Toast的上下文
//     * @param text 弹出Toast的内容
//     * @param gravity 弹出Toast的gravity
//     * @param xOffset 弹出Toast的x间距
//     * @param yOffset 弹出Toast的y间距
//     */
//    public static void showGravity(Context context, CharSequence text, int gravity, int xOffset, int yOffset) {
//        toast(context, text, LENGTH_SHORT, gravity, xOffset, yOffset);
//    }
//
//    public static void showTopTv(Context context,CharSequence text){
//        //toast(Context context, CharSequence text, int duration,int gravity,int textColor,float textSie,int w,int h,int bgColor) {
//        toast(context,text,LENGTH_SHORT,
//                Gravity.TOP,
//                Color.WHITE,
//                18,
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                DensityUtil.dip2px(context,64),
//                ContextCompat.getColor(context, R.color.main_cyan_90));
//    }
//
//
//    /**
//     * 弹出Toast
//     *
//     * @param context 弹出Toast的上下文
//     * @param text 弹出Toast的内容
//     * @param duration 弹出Toast的持续时间
//     * @param gravity 弹出Toast的gravity
//     * @param xOffset 弹出Toast的x间距
//     * @param yOffset 弹出Toast的y间距
//     */
//    public static void showGravity(Context context, CharSequence text, int duration, int gravity, int xOffset, int yOffset) {
//        toast(context, text, duration, gravity, xOffset, yOffset);
//    }
//
//
//    /**
//     * 弹出Toast
//     *
//     * @param context 弹出Toast的上下文
//     * @param resId 弹出Toast的内容的资源ID
//     * @param duration 弹出Toast的持续时间
//     */
//    public static void show(Context context, int resId, int duration) throws NullPointerException {
//        if (null == context) throw new NullPointerException("The context is null!");
//        duration = duration > 0 ? LENGTH_LONG : LENGTH_SHORT;
//        toast(context, context.getResources().getString(resId), duration);
//    }
}
