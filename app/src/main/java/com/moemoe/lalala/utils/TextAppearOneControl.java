package com.moemoe.lalala.utils;

import android.os.Handler;
import android.text.TextUtils;
import android.widget.TextView;

/**
 * Created by yi on 2017/10/15.
 */

public class TextAppearOneControl {

    private static final int SHOW_SPEED = 100;
    private static TextAppearOneControl instance;

    private TextView mTvText;
    private String showText;
    private boolean isFinish;
    private boolean isShowAll;
    private int mCurShowIndex;

    private Handler mHandler;
    private Runnable mShowTextRunnable;

    private TextAppearOneControl(){

    }

    public static TextAppearOneControl getInstance(){
        if(instance == null){
            synchronized (TextAppearOneControl.class){
                if(instance == null){
                    instance = new TextAppearOneControl();
                }
            }
        }
        return instance;
    }

    public void setTextView(TextView textView){
        this.mTvText = textView;
    }

    public void release(){
        if(mHandler != null){
            mHandler.removeCallbacks(mShowTextRunnable);
        }
        mHandler = null;
        mShowTextRunnable = null;
        mTvText = null;
        isFinish = true;
        isShowAll = true;
    }

    public void setTextAndStart(String str){
        if(mTvText == null) return;
        showText = str;
        if(!TextUtils.isEmpty(showText)){
            isFinish = false;
            isShowAll = false;
            //开始逐个显示
            mCurShowIndex = 1;
            mHandler = new Handler();
            mShowTextRunnable = new Runnable() {
                @Override
                public void run() {
                    if(isShowAll){
                        mTvText.setText(showText);
                        isFinish = true;
                        mCurShowIndex = 1;
                        mHandler.removeCallbacks(this);
                    }else {
                        if(mCurShowIndex > showText.length()){
                            isFinish = true;
                            mCurShowIndex = 1;
                            mHandler.removeCallbacks(this);
                        }else {
                            mTvText.setText(showText.substring(0,mCurShowIndex++));
                            mHandler.postDelayed(this,SHOW_SPEED);
                        }
                    }
                }
            };
            mHandler.post(mShowTextRunnable);
        }else {
            isFinish = true;
            isShowAll = true;
        }
    }

    public boolean isFinish(){
      return isFinish;
    }

    public void setShowAll(boolean showAll){
        isShowAll = showAll;
    }
}
