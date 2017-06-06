package com.moemoe.lalala.view.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.moemoe.lalala.R;

/**
 * Created by Haru on 2016/3/11 0011.
 */
public class MyRoundedImageView extends ImageView {
    private int type;
    private static final int TYPE_CIRCLE = 0;
    private static final int TYPE_ROUND = 1;

    private static final int BODER_RADIUS_DEFAULT = 5;
    private Paint mBitmapPaint;
    private Paint mBorderPaint ;
    private int mRadius;
    private Matrix mMatrix;
    private BitmapShader mBitmapShader;
    private int mWidth;
    private RectF mRoundRect;
    private float mLeftTopRadius = .0f;
    private float mRightTopRadius = .0f;
    private float mLeftBottomRadius =.0f;
    private float mRightBottomRadius = .0f;
    private int mBorderOutSideColor = 0;
    private int mBorderInsideColor = 0;
    private int mBorderThickness = 3;
    private float[] mRadii = new float[]{0,0,0,0,0,0,0,0};
    private final RectF mBorderRect = new RectF();
    private Path mPath;
    private float mBorderRadius;

    public MyRoundedImageView(Context context) {
        super(context);
    }

    public MyRoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyRoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        mMatrix = new Matrix();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mPath = new Path();

        mBorderPaint = new Paint();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyRoundedImageView);
        mLeftTopRadius = a.getDimensionPixelSize(R.styleable.MyRoundedImageView_riv_left_top_radius, 0);
        mRightTopRadius = a.getDimensionPixelSize(R.styleable.MyRoundedImageView_riv_right_top_radius,0);
        mLeftBottomRadius = a.getDimensionPixelSize(R.styleable.MyRoundedImageView_riv_left_bottom_radius, 0);
        mRightBottomRadius = a.getDimensionPixelSize(R.styleable.MyRoundedImageView_riv_right_bottom_radius, 0);
        type = a.getInt(R.styleable.MyRoundedImageView_rect_type, TYPE_ROUND);
        mBorderThickness = a.getDimensionPixelSize(R.styleable.MyRoundedImageView_riv_border_thickness, 0);
        mBorderOutSideColor = a.getColor(R.styleable.MyRoundedImageView_riv_border_outside_color, getResources().getColor(R.color.gray_383d40));
        mBorderInsideColor = a.getColor(R.styleable.MyRoundedImageView_riv_border_inside_color, getResources().getColor(R.color.gray_383d40));
        mRadii = new float[]{
                mLeftTopRadius,mLeftTopRadius,
                mRightTopRadius,mRightTopRadius,
                mRightBottomRadius,mRightBottomRadius,
                mLeftBottomRadius,mLeftBottomRadius};
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderInsideColor);
        mBorderPaint.setStrokeWidth(mBorderThickness);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(type == TYPE_CIRCLE){
            mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());

            mRadius = mWidth / 2;

            setMeasuredDimension(mWidth, mWidth);
        }
    }

    private void setUpShader() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        Bitmap bmp = drawableToBitmap(drawable);
    // 将bmp作为着色器，就是在指定区域内绘制bmp
        mBitmapShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        if (type == TYPE_CIRCLE) {
    // 拿到bitmap宽或高的小值
            int bSize = Math.min(bmp.getWidth(), bmp.getHeight());
            scale = mWidth * 1.0f / bSize;
        } else if (type == TYPE_ROUND) {
    // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
            scale = Math.max(getWidth() * 1.0f / bmp.getWidth(), getHeight()
                    * 1.0f / bmp.getHeight());
        }
    // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix.setScale(scale, scale);
    // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);
    // 设置shader
        mBitmapPaint.setShader(mBitmapShader);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        setUpShader();
        if (type == TYPE_ROUND) {
            mPath.addRoundRect(mRoundRect,mRadii, Path.Direction.CW);
            canvas.drawPath(mPath, mBitmapPaint);
        } else {
            canvas.drawCircle(mRadius, mRadius, mRadius, mBitmapPaint);
            if(mBorderThickness != 0){
              canvas.drawCircle(mRadius, mRadius, mRadius - mBorderThickness, mBorderPaint);
            }
        }
    }

//    /**
//     * 边缘画圆
//     */
//    private void drawCircleBorder(Canvas canvas, int radius, int color) {
//        Paint paint = new Paint();
//        /* 去锯齿 */
//        paint.setAntiAlias(true);
//        paint.setFilterBitmap(true);
//        paint.setDither(true);
//        paint.setColor(color);
//        /* 设置paint的　style　为STROKE：空心 */
//        paint.setStyle(Paint.Style.STROKE);
//        /* 设置paint的外框宽度 */
//        paint.setStrokeWidth(mBorderThickness);
//        canvas.drawCircle(mRadius, mRadius, radius, paint);
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    // 圆角图片的范围
        if (type == TYPE_ROUND)
            mRoundRect = new RectF(0, 0, getWidth(), getHeight());
    }

    private static final String STATE_INSTANCE = "state_instance";
    private static final String STATE_TYPE = "state_type";
    private static final String STATE_LEFT_TOP_RADIUS = "state_left_top_radius";
    private static final String STATE_LEFT_BOTTOM_RADIUS = "state_left_bottom_radius";
    private static final String STATE_RIGHT_TOP_RADIUS = "state_right_top_radius";
    private static final String STATE_RIGHT_BOTTOM_RADIUS = "state_right_bottom_radius";
    private static final String STATE_BORDER_COLOR = "state_border_color";
    private static final String STATE_BORDER_THICKNESS = "state_border_thickness";
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE,super.onSaveInstanceState());
        bundle.putInt(STATE_TYPE, type);
        bundle.putFloat(STATE_LEFT_TOP_RADIUS, mLeftTopRadius);
        bundle.putFloat(STATE_LEFT_BOTTOM_RADIUS, mLeftBottomRadius);
        bundle.putFloat(STATE_RIGHT_TOP_RADIUS, mRightTopRadius);
        bundle.putFloat(STATE_RIGHT_BOTTOM_RADIUS,mRightBottomRadius);
        bundle.putInt(STATE_BORDER_COLOR, mBorderInsideColor);
        bundle.putInt(STATE_BORDER_THICKNESS,mBorderThickness);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(((Bundle)state).getParcelable(STATE_INSTANCE));
            this.type = bundle.getInt(STATE_TYPE);
            this.mLeftTopRadius = bundle.getFloat(STATE_LEFT_TOP_RADIUS);
            this.mLeftBottomRadius = bundle.getFloat(STATE_LEFT_BOTTOM_RADIUS);
            this.mRightTopRadius = bundle.getFloat(STATE_RIGHT_TOP_RADIUS);
            this.mRightBottomRadius = bundle.getFloat(STATE_RIGHT_BOTTOM_RADIUS);
            this.mBorderThickness = bundle.getInt(STATE_BORDER_THICKNESS);
            this.mBorderInsideColor = bundle.getInt(STATE_BORDER_COLOR);
        }else{
            super.onRestoreInstanceState(state);
        }
    }
}
