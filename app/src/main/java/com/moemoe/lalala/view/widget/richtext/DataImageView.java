package com.moemoe.lalala.view.widget.richtext;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.moemoe.lalala.model.entity.Image;

/**
 * 可以存放path和bitmap的简单ImageView
 * Created by yi on 2017/5/15.
 */

public class DataImageView extends ImageView {
    private Image image;
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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
