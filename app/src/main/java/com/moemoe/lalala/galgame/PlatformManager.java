package com.moemoe.lalala.galgame;

import android.util.Log;


import com.moemoe.lalala.galgame.framework.IPlatformManager;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import jp.live2d.ALive2DModel;
import jp.live2d.android.Live2DModelAndroid;

/**
 * Created by Haru on 2016/7/18 0018.
 */
public class PlatformManager implements IPlatformManager {
    static public final String 	TAG = "Live2D App";
    private GL10 gl;

    @Override
    public byte[] loadBytes(String path) {
        byte[] ret = null;
        try {
            InputStream in = FileManager.open(path);
            ret=new byte[in.available()];
            in.read(ret, 0, ret.length);
            in.close() ;
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    @Override
    public String loadString(String path) {
        String ret = null;
        try {
            InputStream in = FileManager.open(path);
            ret = in.toString();
            in.close() ;
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    @Override
    public ALive2DModel loadLive2DModel(String path) {
        ALive2DModel model = Live2DModelAndroid.loadModel(loadBytes(path));
        return model;
    }

    @Override
    public void loadTexture(ALive2DModel model, int no, String path) {
        try {
            InputStream in = FileManager.open(path) ;

            boolean mipmap = true;
            int glTexNo = LoadUtil.loadTexture(gl, in, mipmap);
            ((Live2DModelAndroid) model).setTexture(no, glTexNo);
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void log(String txt) {
        Log.i(TAG, txt);
    }

    public void setGL(GL10 gl) {
        this.gl = gl;
    }

}
