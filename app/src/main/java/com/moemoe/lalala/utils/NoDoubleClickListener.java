package com.moemoe.lalala.utils;

import android.view.View;

/**
 * Created by yi on 2016/11/28.
 */

public abstract class NoDoubleClickListener implements View.OnClickListener{

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        long timeD = currentTime - lastClickTime;
        lastClickTime = currentTime;
        if(timeD <= MIN_CLICK_DELAY_TIME){
            return;
        }
        onNoDoubleClick(v);
    }

    public abstract void onNoDoubleClick(View v);
}
