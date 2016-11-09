package com.moemoe.lalala.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Haru on 2016/4/29 0029.
 */
public class SoftKeyboardUtils {

    static private final String TAG = "SoftKeyboardUtils";

    /**
     * dismiss current soft keyboard
     * @see #dismissSoftKeyboard(android.app.Activity, android.widget.EditText)
     * @param activity
     */
    public static void dismissSoftKeyboard(final Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

    public static void showSoftKeyboard(final Activity activity) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (activity != null) {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    try {
                        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_IMPLICIT);
                    } catch (Exception e) {
                    }
                }
            }
        }, 300);
    }

    public static void showSoftKeyboard(final Context context, final EditText editText){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int i = 0;
                while(editText.getWindowToken() == null){
                    if(i == 10){
                        break;
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                    }
                    i++;
                }
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
            }
        }, 300);
    }

    public static void dismissSoftKeyboard(final Activity activity, final EditText text) {
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            try {
                imm.hideSoftInputFromWindow(text.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
            }
        }
    }
}
