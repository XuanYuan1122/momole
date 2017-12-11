package com.moemoe.lalala.galgame;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.text.TextUtils;

import com.moemoe.lalala.galgame.framework.L2DViewMatrix;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * live2D渲染器
 * Created by Haru on 2016/7/18 0018.
 */
public class Live2DRenderer implements GLSurfaceView.Renderer{
    private static final String TAG = "live2DRenderer";

    private Live2DManager mDelegate;
    private SimpleImage mBg;
    private float accelX=0;
    private float accelY=0;
    private boolean isRead = false;
    private Context context;
    private boolean isLocal;

    public Live2DRenderer(Live2DManager live2DMgr, Context context,boolean isLocal){
        this.mDelegate = live2DMgr ;
        this.context = context;
        this.isLocal = isLocal;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        Handler handler = new Handler(context.getMainLooper()){
//            @Override
//            public void handleMessage(Message msg) {
//                ((BaseActivity) context).createDialog(context.getString(R.string.msg_data_wait));
//            }
//        };
//        handler.sendEmptyMessage(0);
        //setupBackground(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mDelegate.onSurfaceChanged(gl, width, height);//Live2D Event
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        L2DViewMatrix viewMatrix = mDelegate.getViewMatrix();
        gl.glOrthof(
                viewMatrix.getScreenLeft(),
                viewMatrix.getScreenRight(),
                viewMatrix.getScreenBottom(),
                viewMatrix.getScreenTop(),
                0.5f, -0.5f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        OffscreenImage.createFrameBuffer(gl, width, height, 0);
        return ;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        mDelegate.update(gl);
        gl.glMatrixMode(GL10.GL_MODELVIEW) ;
        gl.glLoadIdentity() ;
        gl.glDisable(GL10.GL_DEPTH_TEST) ;
        gl.glDisable(GL10.GL_CULL_FACE) ;
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl.glEnable(GL10.GL_TEXTURE_2D) ;
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY) ;
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY) ;
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        gl.glColor4f(1, 1, 1, 1) ;
        gl.glPushMatrix() ;
        {
            L2DViewMatrix viewMatrix = mDelegate.getViewMatrix();
            gl.glMultMatrixf(viewMatrix.getArray(), 0) ;
            if(mBg!=null){
                gl.glPushMatrix() ;
                {
                    float SCALE_X = 0.25f ;
                    float SCALE_Y = 0.1f ;
                    gl.glTranslatef( -SCALE_X  * accelX , SCALE_Y * accelY , 0 ) ;

                    mBg.draw(gl);
                }
                gl.glPopMatrix() ;
            }
            for(int i=0;i<mDelegate.getModelNum();i++) {
                LAppModel model = mDelegate.getModel(i);
                if(model.isInitialized() && ! model.isUpdating()) {
                    model.update();
                    model.draw(gl);
                    model.getBackground();
                    if(mBg == null){
                        if (!TextUtils.isEmpty(model.getBackground())){
                            setupBackground(gl,model.getBackground(),isLocal);
                        }else {
                            setupBackground(gl, Live2DDefine.BACK_IMAGE_NAME,false);
                        }
                    }else {
                        if(!TextUtils.isEmpty(model.getBackground()) && !mBg.getBackground().equals(model.getBackground())){
                            setupBackground(gl,model.getBackground(),isLocal);
                        }
                    }
                }
            }
        }
        gl.glPopMatrix() ;
//        if(!isRead){
//            ((BaseActivity)context).finalizeDialog();
//            isRead = true;
//        }
    }

    public void setAccel(float x,float y,float z) {
        accelX=x;
        accelY=y;
    }

    private void setupBackground(GL10 context, String background,boolean isLocal) {
        try {
            InputStream in = FileManager.open(background,isLocal);
            mBg = new SimpleImage(context,in);
            mBg.setBackground(background);
            mBg.setDrawRect(
                    Live2DDefine.VIEW_LOGICAL_MAX_LEFT,
                    Live2DDefine.VIEW_LOGICAL_MAX_RIGHT,
                    Live2DDefine.VIEW_LOGICAL_MAX_BOTTOM,
                    Live2DDefine.VIEW_LOGICAL_MAX_TOP);


            mBg.setUVRect(0.0f,1.0f,0.0f,1.0f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
