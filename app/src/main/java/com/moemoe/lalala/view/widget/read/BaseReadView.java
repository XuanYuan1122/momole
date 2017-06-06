package com.moemoe.lalala.view.widget.read;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ToastUtils;

/**
 * 小说阅读base页
 * Created by yi on 2017/3/28.
 */

public abstract class BaseReadView extends View {

    protected int mScreenWidth;
    protected int mScreenHeight;

    protected PointF mTouch = new PointF();//点击位置
    protected float actionDownX,actionDownY;
    protected float touch_down = 0;//当前触摸点与按下时的差值
    protected Bitmap mCurPageBitmap,mNextPageBitmap;//当前页，下一页
    protected Canvas mCurPageCanvas,mNextPageCanvas;
    protected ReadPageFactory readPageFactory = null;

    protected  OnReadStateChangeListener listener;
    protected String bookId;
    public boolean isPrepared = false;

    Scroller mScroller;

    public BaseReadView(Context context,String bookId//List<Chapter> chaptersList
        ,OnReadStateChangeListener listener) {
        super(context);
        this.listener = listener;
        this.bookId = bookId;
        mScreenWidth = DensityUtil.getScreenWidth(context);
        mScreenHeight = DensityUtil.getScreenHeight(context);

        mCurPageBitmap = Bitmap.createBitmap(mScreenWidth,mScreenHeight, Bitmap.Config.ARGB_8888);
        mNextPageBitmap  = Bitmap.createBitmap(mScreenWidth,mScreenHeight, Bitmap.Config.ARGB_8888);
        mCurPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);
        mScroller = new Scroller(getContext());

        readPageFactory = new ReadPageFactory(getContext(), bookId);
        readPageFactory.setOnReadStateChangeListener(listener);
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        readPageFactory.setBookId(bookId);
        this.bookId = bookId;
    }

    public synchronized void init(boolean night){
        if (!isPrepared){
            try {
                Bitmap bmp = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
                if(night){
                    bmp.eraseColor(Color.BLACK);
                }else {
                    bmp.eraseColor(Color.WHITE);
                }
                readPageFactory.setBgBitmap(bmp);
                // 自动跳转到上次阅读位置
                int pos[] = PreferenceUtils.getReadProgress(getContext(),bookId);
                int ret = readPageFactory.openBook(pos[0], new int[]{pos[1], pos[2]});
                if (ret == 0) {
                    listener.onLoadChapterFailure(pos[0]);
                    return;
                }
                readPageFactory.onDraw(mCurPageCanvas);
                postInvalidate();
            }catch (Exception e){

            }
            isPrepared = true;
        }
    }

    private int dx,dy;
    private long et = 0;
    private boolean cancel = false;
    private boolean center = false;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                et = System.currentTimeMillis();
                dx = (int) e.getX();
                dy = (int) e.getY();
                mTouch.x = dx;
                mTouch.y = dy;
                actionDownX = dx;
                actionDownY = dy;
                touch_down = 0;
                readPageFactory.onDraw(mCurPageCanvas);
                if(actionDownX >= mScreenWidth /3 && actionDownX <= mScreenWidth * 2 / 3
                        && actionDownY >= mScreenHeight / 3 && actionDownY <= mScreenHeight * 2 / 3){
                    center = true;
                }else {
                    center = false;
                    calcCornerXY(actionDownX, actionDownY);
                    if (actionDownX < mScreenWidth / 2) {// 从左翻
                        BookStatus status = readPageFactory.prePage();
                        if (status == BookStatus.NO_PRE_PAGE) {
                            ToastUtils.showShortToast(getContext(),"没有上一页啦");
                            return false;
                        } else if (status == BookStatus.LOAD_SUCCESS) {
                            abortAnimation();
                            readPageFactory.onDraw(mNextPageCanvas);
                        } else {
                            return false;
                        }
                    }else if (actionDownX >= mScreenWidth / 2) {// 从右翻
                        BookStatus status = readPageFactory.nextPage();
                        if (status == BookStatus.NO_NEXT_PAGE) {
                            ToastUtils.showShortToast(getContext(),"没有下一页啦");
                            listener.onBookFinish(bookId);
                            return false;
                        } else if (status == BookStatus.LOAD_SUCCESS) {
                            abortAnimation();
                            readPageFactory.onDraw(mNextPageCanvas);
                        } else {
                            return false;
                        }
                    }
                    listener.onFlip();
                    setBitmaps(mCurPageBitmap, mNextPageBitmap);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (center)
                    break;
                int mx = (int) e.getX();
                int my = (int) e.getY();
                cancel = (actionDownX < mScreenWidth / 2 && mx < mTouch.x) || (actionDownX > mScreenWidth / 2 && mx > mTouch.x);
                mTouch.x = mx;
                mTouch.y = my;
                touch_down = mTouch.x - actionDownX;
                this.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                long t = System.currentTimeMillis();
                int ux = (int) e.getX();
                int uy = (int) e.getY();
                if (center) { // ACTION_DOWN的位置在中间，则不响应滑动事件
                    resetTouchPoint();
                    if (Math.abs(ux - actionDownX) < 5 && Math.abs(uy - actionDownY) < 5) {
                        listener.onCenterClick();
                        return false;
                    }
                    break;
                }
                if ((Math.abs(ux - dx) < 10) && (Math.abs(uy - dy) < 10)){
                    if ((t - et < 1000)) { // 单击
                        startAnimation();
                    } else { // 长按
                        readPageFactory.cancelPage();
                        restoreAnimation();
                    }
                    postInvalidate();
                    return true;
                }
                if (cancel) {
                    readPageFactory.cancelPage();
                    restoreAnimation();
                    postInvalidate();
                } else {
                    startAnimation();
                    postInvalidate();
                }
                cancel = false;
                center = false;
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        calcPoints();
        drawCurrentPageArea(canvas);
        drawNextPageAreaAndShadow(canvas);
        drawCurrentPageShadow(canvas);
        drawCurrentBackArea(canvas);
    }

    protected abstract void calcPoints();

    protected abstract void drawCurrentPageArea(Canvas canvas);

    protected abstract void drawNextPageAreaAndShadow(Canvas canvas);

    protected abstract void drawCurrentPageShadow(Canvas canvas);

    protected abstract void drawCurrentBackArea(Canvas canvas);

    protected abstract void calcCornerXY(float x, float y);

    /**
     * 开启翻页
     */
    protected abstract void startAnimation();

    /**
     * 停止翻页动画（滑到一半调用停止的话  翻页效果会卡住 可调用#{restoreAnimation} 还原效果）
     */
    protected abstract void abortAnimation();

    /**
     * 还原翻页
     */
    protected abstract void restoreAnimation();

    protected abstract void setBitmaps(Bitmap mCurPageBitmap, Bitmap mNextPageBitmap);

    public abstract void setTheme(boolean night);

    /**
     * 复位触摸点位
     */
    protected void resetTouchPoint() {
        mTouch.x = 0.1f;
        mTouch.y = 0.1f;
        touch_down = 0;
        calcCornerXY(mTouch.x, mTouch.y);
    }

    public void jumpToChapter(int chapter) {
        resetTouchPoint();
        readPageFactory.openBook(chapter, new int[]{0, 0});
        readPageFactory.onDraw(mCurPageCanvas);
        readPageFactory.onDraw(mNextPageCanvas);
        postInvalidate();
    }

    public void nextPage() {
        BookStatus status = readPageFactory.nextPage();
        if (status == BookStatus.NO_NEXT_PAGE) {
            ToastUtils.showShortToast(getContext(),"没有下一页啦");
            listener.onBookFinish(bookId);
            return;
        } else if (status == BookStatus.LOAD_SUCCESS) {
            if (isPrepared) {
                readPageFactory.onDraw(mCurPageCanvas);
                readPageFactory.onDraw(mNextPageCanvas);
                postInvalidate();
            }
        } else {
            return;
        }
    }

    public void prePage() {
        BookStatus status = readPageFactory.prePage();
        if (status == BookStatus.NO_PRE_PAGE) {
            ToastUtils.showShortToast(getContext(),"没有上一页啦");
            return;
        } else if (status == BookStatus.LOAD_SUCCESS) {
            if (isPrepared) {
                readPageFactory.onDraw(mCurPageCanvas);
                readPageFactory.onDraw(mNextPageCanvas);
                postInvalidate();
            }
        } else {
            return;
        }
    }

    public synchronized void setFontSize(final int fontSizePx) {
        resetTouchPoint();
        readPageFactory.setTextFont(fontSizePx);
        if (isPrepared) {
            readPageFactory.onDraw(mCurPageCanvas);
            readPageFactory.onDraw(mNextPageCanvas);
            //SettingManager.getInstance().saveFontSize(bookId, fontSizePx);
            PreferenceUtils.saveFontSize(getContext(),fontSizePx);
            postInvalidate();
        }
    }

    public synchronized void setPercent(int percent){
        resetTouchPoint();
        readPageFactory.setPercent(percent);
        if (isPrepared) {
            readPageFactory.onDraw(mCurPageCanvas);
            readPageFactory.onDraw(mNextPageCanvas);
            postInvalidate();
        }
    }

    public synchronized void setTextColor(int textColor, int titleColor) {
        resetTouchPoint();
        readPageFactory.setTextColor(textColor, titleColor);
        if (isPrepared) {
            readPageFactory.onDraw(mCurPageCanvas);
            readPageFactory.onDraw(mNextPageCanvas);
            postInvalidate();
        }
    }

    public void setBattery(int battery) {
        readPageFactory.setBattery(battery);
        if (isPrepared) {
            readPageFactory.onDraw(mCurPageCanvas);
            postInvalidate();
        }
    }

//    public void setTime(String time) {
//        readPageFactory.setTime(time);
//    }

    public void setPosition(int[] pos) {
        int ret = readPageFactory.openBook(pos[0], new int[]{pos[1], pos[2]});
        if (ret == 0) {
            listener.onLoadChapterFailure(pos[0]);
            return;
        }
        readPageFactory.onDraw(mCurPageCanvas);
        postInvalidate();
    }

    public int[] getReadPos() {
        return readPageFactory.getPosition();
    }

    public String getHeadLine() {
        return readPageFactory.getHeadLineStr().replaceAll("@", "");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(readPageFactory != null) readPageFactory.recycle();
        if(mCurPageBitmap != null && !mCurPageBitmap.isRecycled()){
            mCurPageBitmap.recycle();
            mCurPageBitmap = null;
        }
        if(mNextPageBitmap != null && !mNextPageBitmap.isRecycled()){
            mNextPageBitmap.recycle();
            mNextPageBitmap = null;
        }
    }
}
