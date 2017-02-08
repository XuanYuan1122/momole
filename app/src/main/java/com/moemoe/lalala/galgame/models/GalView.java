package com.moemoe.lalala.galgame.models;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by Haru on 2016/8/30.
 */
public class GalView extends GLSurfaceView {

    private static GalView galView;
    private GalModel model;
    private GalRenderer renderer = new GalRenderer();

    public GalView(Context context) {
        this(context,null);
        galView = this;
    }

    public GalView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
