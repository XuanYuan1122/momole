package com.moemoe.lalala.view.widget.view;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 *
 * Created by yi on 2018/1/23.
 */

public class TagAddTextView extends android.support.v7.widget.AppCompatTextView {

    private PointF pointF;

    public TagAddTextView(Context context) {
        super(context);
    }

    public TagAddTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagAddTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PointF getPointF() {
        return pointF;
    }

    public void setPointF(PointF pointF) {
        setX(pointF.x);
        setY(pointF.y);
    }
}
