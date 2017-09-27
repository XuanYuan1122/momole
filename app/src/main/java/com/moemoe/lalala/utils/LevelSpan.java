package com.moemoe.lalala.utils;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.tag.BaseTag;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public class LevelSpan extends ForegroundColorSpan {

    private float size;

    public LevelSpan(@ColorInt int color,float size) {
        super(color);
        this.size = size;
    }

    public LevelSpan(Parcel src) {
        super(src);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(getForegroundColor());
        ds.setUnderlineText(false);
        ds.setTextSize(size);
    }
}