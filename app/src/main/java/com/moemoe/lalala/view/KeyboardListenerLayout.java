package com.moemoe.lalala.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Haru on 2016/4/28 0028.
 */
public class KeyboardListenerLayout extends RelativeLayout {

    /**
     * 忽略200以内的高度变化
     */
    private static int DIMEN_IGNORE = 100;

    public interface onKeyboardChangeListener {
        public void onKeyBoardStateChange(int state);
    }

    public static final byte KEYBOARD_STATE_SHOW = -3;
    public static final byte KEYBOARD_STATE_HIDE = -2;
    public static final byte KEYBOARD_STATE_INIT = -1;

    private boolean mHasInit;
    private int mHideHeight;
    private int mLastBottom;
    private onKeyboardChangeListener mListener;

    public KeyboardListenerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public KeyboardListenerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KeyboardListenerLayout(Context context) {
        super(context);
        init();
    }

    private  void init(){
//		DIMEN_IGNORE = getContext().getResources().getDimensionPixelSize(R.dimen.keyboard_statusbar_height_ignore);
    }
    /**
     * set keyboard state listener
     */
    public void setOnkbdStateListener(onKeyboardChangeListener listener) {
        mListener = listener;
    }

    /**
     * if view.height changed, call this to reset state of keyboard listener
     */
    public void resetView(){
        mHasInit = false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed){
            if (!mHasInit) {
                mHasInit = true;
                mHideHeight = b;
                if (mListener != null) {
                    mListener.onKeyBoardStateChange(KEYBOARD_STATE_INIT);
                }
            } else {
                mHideHeight = Math.max(b, mHideHeight);
                int bottomChange = Math.abs(mLastBottom - b);
                if( bottomChange > DIMEN_IGNORE){
                    if (mHideHeight - b > DIMEN_IGNORE) {
                        if (mListener != null) {
                            mListener.onKeyBoardStateChange(KEYBOARD_STATE_SHOW);
                        }
                    }else if (mHideHeight - b <= DIMEN_IGNORE) {
                        if (mListener != null) {
                            mListener.onKeyBoardStateChange(KEYBOARD_STATE_HIDE);
                        }
                    }
                }
            }
            mLastBottom = b;
        }
    }

}
