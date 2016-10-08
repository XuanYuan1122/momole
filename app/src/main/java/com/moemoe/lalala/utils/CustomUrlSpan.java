package com.moemoe.lalala.utils;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.WebViewActivity;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public class CustomUrlSpan extends ClickableSpan {

    private Context mContext;
    /**
     * 跳转url
     */
    private  String mUrl;
    /**
     * 跳转标题
     */
    private  String mTitle;
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
        WebViewActivity.startActivity(mContext, mUrl, mTitle, false, true);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(mContext.getResources().getColor(R.color.main_title_cyan));
        ds.setUnderlineText(false);
    }
}