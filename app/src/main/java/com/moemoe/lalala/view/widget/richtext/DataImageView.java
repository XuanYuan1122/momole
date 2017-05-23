package com.moemoe.lalala.view.widget.richtext;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 可以存放path和bitmap的简单ImageView
 * Created by yi on 2017/5/15.
 */

public class DataImageView extends ImageView {
    private String path;
    private Bitmap bitmap;

    public DataImageView(Context context) {
        this(context,null);
    }

    public DataImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DataImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
