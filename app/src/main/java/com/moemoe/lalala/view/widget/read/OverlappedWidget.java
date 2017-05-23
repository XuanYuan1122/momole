package com.moemoe.lalala.view.widget.read;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;

import com.moemoe.lalala.utils.PreferenceUtils;

/**
 * Created by yi on 2017/3/29.
 */

public class OverlappedWidget extends BaseReadView {

    private Path mPath0;

    GradientDrawable mBackShadowDrawableLR;
    GradientDrawable mBackShadowDrawableRL;

    public OverlappedWidget(Context context, String bookId, OnReadStateChangeListener listener) {
        super(context, bookId, listener);
        mTouch.x = 0.01f;
        mTouch.y = 0.01f;

        mPath0 = new Path();
        int[] mBackShadowColors = new int[]{0xaa666666, 0x666666};
        mBackShadowDrawableRL = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors);
        mBackShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mBackShadowDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors);
        mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
    }


    @Override
    protected void drawCurrentPageArea(Canvas canvas) {
        mPath0.reset();
        canvas.save();
        if (actionDownX > mScreenWidth >> 1) {
            mPath0.moveTo(mScreenWidth + touch_down, 0);
            mPath0.lineTo(mScreenWidth + touch_down, mScreenHeight);
            mPath0.lineTo(mScreenWidth, mScreenHeight);
            mPath0.lineTo(mScreenWidth, 0);
            mPath0.lineTo(mScreenWidth + touch_down, 0);
            mPath0.close();
            canvas.clipPath(mPath0, Region.Op.XOR);
            canvas.drawBitmap(mCurPageBitmap, touch_down, 0, null);
        } else {
            mPath0.moveTo(touch_down, 0);
            mPath0.lineTo(touch_down, mScreenHeight);
            mPath0.lineTo(mScreenWidth, mScreenHeight);
            mPath0.lineTo(mScreenWidth, 0);
            mPath0.lineTo(touch_down, 0);
            mPath0.close();
            canvas.clipPath(mPath0);
            canvas.drawBitmap(mCurPageBitmap, touch_down, 0, null);
        }
        try {
            canvas.restore();
        } catch (Exception e) {

        }
    }

    @Override
    protected void drawCurrentPageShadow(Canvas canvas) {
        canvas.save();
        GradientDrawable shadow;
        if (actionDownX > mScreenWidth >> 1) {
            shadow = mBackShadowDrawableLR;
            shadow.setBounds((int) (mScreenWidth + touch_down - 5), 0, (int) (mScreenWidth + touch_down + 5), mScreenHeight);
        } else {
            shadow = mBackShadowDrawableRL;
            shadow.setBounds((int) (touch_down - 5), 0, (int) (touch_down + 5), mScreenHeight);
        }
        shadow.draw(canvas);
        try {
            canvas.restore();
        } catch (Exception e) {

        }
    }

    @Override
    protected void drawCurrentBackArea(Canvas canvas) {

    }

    @Override
    protected void drawNextPageAreaAndShadow(Canvas canvas) {
        canvas.save();
        if (actionDownX > mScreenWidth >> 1) {
            canvas.clipPath(mPath0);
            canvas.drawBitmap(mNextPageBitmap, 0, 0, null);
        } else {
            canvas.clipPath(mPath0, Region.Op.XOR);
            canvas.drawBitmap(mNextPageBitmap, 0, 0, null);
        }
        try {
            canvas.restore();
        } catch (Exception e) {

        }
    }

    @Override
    protected void calcPoints() {

    }

    @Override
    protected void calcCornerXY(float x, float y) {

    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            float x = mScroller.getCurrX();
            float y = mScroller.getCurrY();
            if (actionDownX > mScreenWidth >> 1) {
                touch_down = -(mScreenWidth - x);
            } else {
                touch_down = x;
            }
            mTouch.y = y;
            //touch_down = mTouch.x - actiondownX;
            postInvalidate();
        }
    }

    @Override
    protected void startAnimation() {
        int dx;
        if (actionDownX > (mScreenWidth >> 1)) {
            dx = (int) -(mScreenWidth + touch_down);
            mScroller.startScroll((int) (mScreenWidth + touch_down), (int) mTouch.y, dx, 0, 700);
        } else {
            dx = (int) (mScreenWidth - touch_down);
            mScroller.startScroll((int) touch_down, (int) mTouch.y, dx, 0, 700);
        }
    }

    @Override
    protected void abortAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
    }

    @Override
    protected void restoreAnimation() {
        int dx;
        if (actionDownX > mScreenWidth / 2) {
            dx = (int) (mScreenWidth - mTouch.x);
        } else {
            dx = (int) (-mTouch.x);
        }
        mScroller.startScroll((int) mTouch.x, (int) mTouch.y, dx, 0, 300);
    }

    @Override
    protected void setBitmaps(Bitmap mCurPageBitmap, Bitmap mNextPageBitmap) {
        this.mCurPageBitmap = mCurPageBitmap;
        this.mNextPageBitmap = mNextPageBitmap;
    }

    @Override
    public void setTheme(boolean night) {
        resetTouchPoint();
        Bitmap bmp = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        if(night){
            bmp.eraseColor(Color.BLACK);
        }else {
            bmp.eraseColor(Color.WHITE);
        }
        if (bmp != null) {
            readPageFactory.setBgBitmap(bmp);
            if (isPrepared) {
                readPageFactory.onDraw(mCurPageCanvas);
                readPageFactory.onDraw(mNextPageCanvas);
                postInvalidate();
            }
        }
        PreferenceUtils.setNight(getContext(),night);
    }
}
