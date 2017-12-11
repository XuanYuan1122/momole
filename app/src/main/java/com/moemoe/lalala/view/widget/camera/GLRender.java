package com.moemoe.lalala.view.widget.camera;

import android.opengl.GLSurfaceView;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yi on 2017/11/16.
 */

public class GLRender implements GLSurfaceView.Renderer, IFaceDetector.FaceDetectorListener{

    public static final boolean USE_OES_TEXTURE=false;

    @IntDef(value = {
            CMD_PROCESS_FRAME,
            CMD_SETUP_SURFACE_TEXTURE,
            CMD_SET_FILTER,
            CMD_RERUN_ONDRAW_RUNNABLE,
            CMD_RERUN_DRAWEND_RUNNABLE,
    }
    )
    @Retention(RetentionPolicy.SOURCE)
    public @interface RenderCmd {
    }

    public final static int CMD_PROCESS_FRAME = 0;
    public final static int CMD_SETUP_SURFACE_TEXTURE = 1;
    public final static int CMD_SET_FILTER = 2;
    public final static int CMD_RERUN_ONDRAW_RUNNABLE = 5;
    public final static int CMD_RERUN_DRAWEND_RUNNABLE = 6;

    /**
     * 命令的一项
     */
    static class CmdItem {
        @GPUImageRenderer.RenderCmd
        int cmdId;
        Object param1;
        Object param2;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }

    @Override
    public void onDetectFinish() {

    }
}
