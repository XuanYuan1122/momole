package com.moemoe.lalala.utils;

import android.os.Parcel;
import android.support.annotation.ColorInt;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;

/**
 *
 * Created by Haru on 2016/4/27 0027.
 */
public class BoldSpan extends ForegroundColorSpan {


    public BoldSpan(@ColorInt int color) {
        super(color);
    }

    public BoldSpan(Parcel src) {
        super(src);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(getForegroundColor());
        ds.setFakeBoldText(true);
        ds.setUnderlineText(false);
    }
}