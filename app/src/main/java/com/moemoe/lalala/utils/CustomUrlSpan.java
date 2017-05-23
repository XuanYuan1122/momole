package com.moemoe.lalala.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.view.activity.NewPersonalActivity;
import com.moemoe.lalala.view.activity.WebViewActivity;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public class CustomUrlSpan extends ClickableSpan {

    private Context mContext;
    /**
     * 跳转url
     */
    private String mUrl;
    /**
     * 跳转标题
     */
    private String mTitle;
    /**
     *
     * @param title 跳转标题
     * @param url 跳转url
     */
    public CustomUrlSpan(Context context, String title, String url){
        mContext = context;
        mTitle = title;
        mUrl = url;
    }
    @Override
    public void onClick(View widget) {
        if(mUrl.startsWith("http")){
            WebViewActivity.startActivity(mContext, mUrl, mTitle, false, true);
        }else {
            Intent i = new Intent(mContext, NewPersonalActivity.class);
            i.putExtra("uuid",mUrl);
            mContext.startActivity(i);
        }

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(ContextCompat.getColor(mContext,R.color.main_cyan));
        ds.setUnderlineText(false);
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}