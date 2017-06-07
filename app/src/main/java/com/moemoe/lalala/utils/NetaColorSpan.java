package com.moemoe.lalala.utils;

import android.text.style.ForegroundColorSpan;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public class NetaColorSpan extends ForegroundColorSpan {
    /**
     * 跳转url
     */
    private String mUrl;
    /**
     * 跳转标题
     */
    private String mTitle;

    public NetaColorSpan(int color) {
        super(color);
    }

    public NetaColorSpan(int color,String url){
        super(color);
        mUrl = url;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}