package com.moemoe.lalala.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.tag.BaseTag;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public class BaseUrlSpan extends ClickableSpan {

    protected Context mContext;
    /**
     * 跳转url
     */
    protected String mUrl;
    /**
     *对应的标签
     */
    protected BaseTag mTag;

    public BaseUrlSpan(Context context,BaseTag mTag){
        mContext = context;
        this.mTag = mTag;
    }

    public BaseUrlSpan(Context context, String url,BaseTag mTag){
        mContext = context;
        mUrl = url;
        this.mTag = mTag;
    }

    @Override
    public void onClick(View widget) {

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

    public BaseTag getmTag() {
        return mTag;
    }

    public void setmTag(BaseTag mTag) {
        this.mTag = mTag;
    }
}