package com.moemoe.lalala.view.widget.camera;

import android.content.Context;
import android.graphics.PointF;

/**
 * Created by yi on 2017/11/16.
 */

public interface IFaceDetector {
    void init(Context context);

    void uninit();

    void reset();

    void switchMaxFaceCount(int count);

    void onFrameAvailable(int width, int height, Rotation rotate, boolean mirror,
                          byte[] yuvData,int direction);

    int getFaceDetectResult(PointF[][] detectResult, int imageScaleWidth, int imageScaleHeight,
                            int outputWidth, int outputHeight) ;

    public interface FaceDetectorListener {
        void onDetectFinish();
    }
}
