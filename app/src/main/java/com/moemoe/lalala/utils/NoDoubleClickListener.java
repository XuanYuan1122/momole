package com.moemoe.lalala.utils;

import android.view.View;

/**
 * Created by yi on 2016/11/28.
 */

public abstract class NoDoubleClickListener implements View.OnClickListener{

    private int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    public NoDoubleClickListener(int MIN_CLICK_DELAY_TIME) {
        this.MIN_CLICK_DELAY_TIME = MIN_CLICK_DELAY_TIME;
    }

    public NoDoubleClickListener() {
    }

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
