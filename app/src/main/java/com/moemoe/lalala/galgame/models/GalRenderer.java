//package com.moemoe.lalala.galgame.models;
//
//import android.opengl.GLSurfaceView;
//
//import com.moemoe.lalala.galgame.SimpleImage;
//import com.moemoe.lalala.galgame.framework.L2DMatrix44;
//import com.moemoe.lalala.galgame.framework.L2DViewMatrix;
//
//import java.io.File;
//import java.io.InputStream;
//
//import javax.microedition.khronos.egl.EGLConfig;
//import javax.microedition.khronos.opengles.GL10;
//
///**
// * Created by Haru on 2016/8/31.
// */
//public class GalRenderer implements GLSurfaceView.Renderer {
//
//    private static File backgroundFile;
//    private float accelX = 0;
//    private float accelY = 0;
//    private SimpleImage bg;
//    private InputStream bgStream;
//    private L2DMatrix44 deviceToScreen;
//    private float wifth;
//    private float height;
//    private boolean isLoad;
//    private GalModel model;
//    private L2DViewMatrix viewMatrix;
//
//    public GalRenderer(){
//        deviceToScreen = new L2DMatrix44();
//        viewMatrix = new L2DViewMatrix();
//        viewMatrix.setMaxScale(2.0f);
//        viewMatrix.setMinScale(0.1f);
//        viewMatrix.setMaxScreenRect(-2.0f,2.0f,-2.0f,2.0f);
//    }
//
//    public final void release(){
//        if(model != null){
//            model.release();
//        }
//    }
//
//    public void setViewMatrix(L2DViewMatrix l2DViewMatrix){
//        viewMatrix = l2DViewMatrix;
//    }
//
//    public void setModel(GalModel model){
//        if(this.model != null) this.model.release();
//        this.model = model;
//        this.model.setOnLoadModelListener(new GalModel.OnLoadModelListener() {
//            @Override
//            public void onLoadModel() {
//                reset();
//            }
//        });
//    }
//
//    public void reset() {
//        bg = null;
//        isLoad = false;
//    }
//
//    @Override
//    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//
//    }
//
//    @Override
//    public void onSurfaceChanged(GL10 gl, int width, int height) {
//
//    }
//
//    @Override
//    public void onDrawFrame(GL10 gl) {
//
//    }
//}
