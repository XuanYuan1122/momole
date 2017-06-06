package com.moemoe.lalala.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by yi on 2016/11/30.
 */

public class GlideRoundTransform extends BitmapTransformation {
    //private static float radius = 0f;
    private static float[] mRadii = new float[]{0,0,0,0,0,0,0,0};

    public GlideRoundTransform(Context context) {
        this(context, 4);
    }

    public GlideRoundTransform(Context context, int dp) {
        super(context);
        float radius = Resources.getSystem().getDisplayMetrics().density * dp;
        mRadii = new float[]{
                radius,radius,
                radius,radius,
                radius,radius,
                radius,radius};
    }

    public GlideRoundTransform(Context context,int tl,int tr,int br,int bl){
        super(context);
        float t1 = Resources.getSystem().getDisplayMetrics().density * tl;
        float t2 = Resources.getSystem().getDisplayMetrics().density * tr;
        float b1 = Resources.getSystem().getDisplayMetrics().density * br;
        float b2 = Resources.getSystem().getDisplayMetrics().density * bl;
        mRadii = new float[]{
                t1,t1,
                t2,t2,
                b1,b1,
                b2,b2};
    }

    @Override protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return roundCrop(pool, toTransform);
    }

    private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        Path path = new Path();
        path.addRoundRect(rectF,mRadii, Path.Direction.CW);
        canvas.drawPath(path,paint);
       // canvas.drawRoundRect(rectF, radius, radius, paint);
        return result;
    }

    @Override public String getId() {
        return getClass().getName();
    }
}
