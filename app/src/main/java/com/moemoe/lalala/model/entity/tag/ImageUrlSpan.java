package com.moemoe.lalala.model.entity.tag;

import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.view.View;

import com.moemoe.lalala.utils.BaseUrlSpan;
import com.moemoe.lalala.view.activity.NewPersonalActivity;

/**
 * Created by yi on 2017/9/19.
 */

public class ImageUrlSpan extends BaseUrlSpan {

    public ImageUrlSpan(Context context, BaseTag tag){
        super(context,tag);
    }

    public ImageUrlSpan(Context context, String url, BaseTag tag) {
        super(context, url,tag);
    }

    @Override
    public void onClick(View widget) {
       //TODO 跳转大图查看
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
    }
}
