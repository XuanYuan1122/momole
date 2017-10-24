package com.moemoe.lalala.model.entity.tag;

import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.view.View;

import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.utils.BaseUrlSpan;
import com.moemoe.lalala.view.activity.DownLoadListActivity;
import com.moemoe.lalala.view.activity.ImageBigSelectActivity;
import com.moemoe.lalala.view.activity.NewPersonalActivity;

import java.util.ArrayList;
import java.util.HashMap;

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
        ArrayList<Image> temp = new ArrayList<>();
        Image image = new Image();
        image.setPath(mUrl);
        HashMap<String,String> map = mTag.getAttrs();
        image.setH(Integer.valueOf(map.get("h")));
        image.setW(Integer.valueOf(map.get("w")));
        temp.add(image);
        Intent intent = new Intent(mContext, ImageBigSelectActivity.class);
        intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, temp);
        intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
                0);
        mContext.startActivity(intent);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
    }
}
