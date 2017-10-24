package com.moemoe.lalala.model.entity.tag;

import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.view.View;

import com.moemoe.lalala.utils.BaseUrlSpan;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.activity.NewPersonalActivity;

/**
 * Created by yi on 2017/9/19.
 */

public class UserUrlSpan extends BaseUrlSpan {

    public UserUrlSpan(Context context,BaseTag tag){
        super(context,tag);
    }

    public UserUrlSpan(Context context, String url,BaseTag tag) {
        super(context, url,tag);
    }

    @Override
    public void onClick(View widget) {
        if(!mUrl.equals(PreferenceUtils.getUUid())){
            Intent i = new Intent(mContext, NewPersonalActivity.class);
            i.putExtra("uuid",mUrl);
            mContext.startActivity(i);
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
    }
}
