package com.moemoe.lalala.utils;

import android.widget.TextView;

/**
 * Created by yi on 2017/10/15.
 */

public class TextAppearOneControl {

    private static final int SHOW_SPEED = 500;
    private static TextAppearOneControl instance;

    private TextView mTvText;
    private boolean isFinish;

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
}
