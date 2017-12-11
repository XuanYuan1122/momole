package com.moemoe.lalala.galgame;

import android.app.Activity;
import android.util.Log;

import com.moemoe.lalala.galgame.framework.L2DViewMatrix;
import com.moemoe.lalala.galgame.framework.Live2DFramework;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import jp.live2d.Live2D;

/**
 * live2D 控制器
 * Created by Haru on 2016/7/18 0018.
 */
public class Live2DManager {
    private static final String TAG = "live2dManager";
    private Live2DView 				view;
    private ArrayList<LAppModel> models;
    private int 					modelCount		=-1;
    private boolean 				reloadFlg;
    private OnSoundLoadListener onSoundLoadListener;
    private String mModel;
    private boolean isLocal;

    public Live2DManager(String model,boolean isLocal) {
        Live2D.init();
        Live2DFramework.setPlatformManager(new PlatformManager());
        models = new ArrayList<>();
        reloadFlg = true;
        this.isLocal = isLocal;
        mModel = model;
    }

    public void setOnSoundLoadListener(OnSoundLoadListener l){
        onSoundLoadListener = l;
    }

    public void releaseModel() {
        for(int i=0;i<models.size();i++) {
            models.get(i).release();
        }
        models.clear();
    }

    public void update(GL10 gl) {//控制不同的人物model
        if(view != null) view.update();
        if(reloadFlg) {
            reloadFlg=false;
            try {
                    releaseModel();
                    LAppModel model = new LAppModel();
                    model.setOnSoundLoadListener(new LAppModel.OnSoundLoadListener() {
                        @Override
                        public void OnStart() {
                            if (onSoundLoadListener != null){
                                onSoundLoadListener.OnStart();
                            }
                        }

                        @Override
                        public void OnLoad(int count, int position) {
                            if (onSoundLoadListener != null){
                                onSoundLoadListener.OnLoad(count,position);
                            }
                        }

                        @Override
                        public void OnFinish() {
                            if (onSoundLoadListener != null){
                                onSoundLoadListener.OnFinish();
                            }
                        }
                    });
                    models.add(model);
                    models.get(0).load(gl, mModel,isLocal);
                    models.get(0).feedIn();
            } catch (Exception e) {
                Log.e(TAG, "Failed to load." + e.getStackTrace());
            }
        }
    }

    public LAppModel getModel(int no) {
        if(no>=models.size())return null;
        return models.get(no);
    }

    public int getModelNum()
    {
        return models.size();
    }

    public Live2DView  createView(Activity act) {
        view = new Live2DView( act ) ;
        view.setLive2DManager(this,isLocal);
        view.startAccel(act);
        view.setLocal(isLocal);
        return view ;
    }

    public void onResume() {
        if(Live2DDefine.DEBUG_LOG) Log.d(TAG, "onResume");
        view.onResume();
    }

    public void onPause(){
        if(Live2DDefine.DEBUG_LOG) Log.d(TAG, "onPause");
        view.onPause();
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if(Live2DDefine.DEBUG_LOG) Log.d(TAG, "onSurfaceChanged "+width+" "+height);
        view.setupView(width, height);

//        if(getModelNum()==0) {
//            changeModel();
//        }
    }

    public void changeModel(String model) {
        reloadFlg=true;
        modelCount++;
        mModel = model;
    }

    public boolean tapEvent(float x,float y) {
        if(Live2DDefine.DEBUG_LOG) Log.d(TAG, "tapEvent view x:"+ x + " y:" + y);
        for (int i=0; i < models.size(); i++) {
            if(models.get(i).hitTest(Live2DDefine.HIT_AREA_HEAD,x, y )) {
                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_HEAD, Live2DDefine.PRIORITY_NORMAL );
            }else if(models.get(i).hitTest(Live2DDefine.HIT_AREA_OP,x, y )) {
                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_OP, Live2DDefine.PRIORITY_NORMAL );
            }else if(models.get(i).hitTest(Live2DDefine.HIT_AREA_FACE,x, y )) {
                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_FACE, Live2DDefine.PRIORITY_NORMAL );
            }else if(models.get(i).hitTest(Live2DDefine.HIT_AREA_HAND,x, y )) {
                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_HAND, Live2DDefine.PRIORITY_NORMAL );
            }else if(models.get(i).hitTest(Live2DDefine.HIT_AREA_BELLY,x, y )) {
                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_BELLY, Live2DDefine.PRIORITY_NORMAL );
            }else if(models.get(i).hitTest(Live2DDefine.HIT_AREA_SKIRT,x, y )) {
                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_SKIRT, Live2DDefine.PRIORITY_NORMAL );
            }else if(models.get(i).hitTest(Live2DDefine.HIT_AREA_BODY,x, y )) {
                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_BODY, Live2DDefine.PRIORITY_NORMAL );
            }else if(models.get(i).hitTest(Live2DDefine.HIT_AREA_HAND_L,x, y )) {
                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_HAND_L, Live2DDefine.PRIORITY_NORMAL );
            }else if(models.get(i).hitTest(Live2DDefine.HIT_AREA_HAND_R,x, y )) {
                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_HAND_R, Live2DDefine.PRIORITY_NORMAL );
            }else if(models.get(i).hitTest(Live2DDefine.HIT_AREA_XQ,x, y )) {
                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_XQ, Live2DDefine.PRIORITY_NORMAL );
            }else if(models.get(i).hitTest(Live2DDefine.HIT_AREA_XQOP,x, y )) {
                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_XQOP, Live2DDefine.PRIORITY_NORMAL );
            }else if(models.get(i).hitTest(Live2DDefine.HIT_AREA_TT,x, y )) {
                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_TT, Live2DDefine.PRIORITY_NORMAL );
            }else if(models.get(i).hitTest(Live2DDefine.HIT_AREA_XQS,x, y )) {
                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_XQS, Live2DDefine.PRIORITY_NORMAL );
            }else if(models.get(i).hitTest(Live2DDefine.HIT_AREA_CHEST,x, y )) {
                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_CHESET, Live2DDefine.PRIORITY_NORMAL );
            }else {
                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP, Live2DDefine.PRIORITY_NORMAL );
            }


//            if(models.get(i).hitTest(Live2DDefine.HIT_AREA_HEAD,x, y )) {
//                if(Live2DDefine.DEBUG_LOG) Log.d(TAG, "Tap head.");
//                //models.get(i).setRandomExpression();
//                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_HEAD, Live2DDefine.PRIORITY_NORMAL );
//            } else if(models.get(i).hitTest( Live2DDefine.HIT_AREA_CHEST,x, y)) {
//                if(Live2DDefine.DEBUG_LOG) Log.d(TAG, "Tap chest.");
//                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_CHEST, Live2DDefine.PRIORITY_NORMAL );
//            } else if(models.get(i).hitTest( Live2DDefine.HIT_AREA_BELLY,x, y)) {
//                if(Live2DDefine.DEBUG_LOG) Log.d(TAG, "Tap belly.");
//                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_BELLY, Live2DDefine.PRIORITY_NORMAL );
//            } else if(models.get(i).hitTest( Live2DDefine.HIT_AREA_SKIRT,x, y)) {
//                if(Live2DDefine.DEBUG_LOG) Log.d(TAG, "Tap skirt.");
//                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_SKIRT, Live2DDefine.PRIORITY_NORMAL );
//            } else if(models.get(i).hitTest( Live2DDefine.HIT_AREA_HAND_R,x, y)) {
//                if(Live2DDefine.DEBUG_LOG) Log.d(TAG, "Tap hand_r.");
//                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_HAND_R, Live2DDefine.PRIORITY_NORMAL );
//            } else if(models.get(i).hitTest( Live2DDefine.HIT_AREA_HAND_L,x, y)) {
//                if(Live2DDefine.DEBUG_LOG) Log.d(TAG, "Tap hand_l.");
//                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_HAND_L, Live2DDefine.PRIORITY_NORMAL );
//            } else if(models.get(i).hitTest( Live2DDefine.HIT_AREA_FACE,x, y)) {
//                if(Live2DDefine.DEBUG_LOG) Log.d(TAG, "Tap face.");
//                if(StringUtils.matchCurrentTime("00:00","04:00")){
//                    models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_FACE_0_4, Live2DDefine.PRIORITY_NORMAL );
//                }else if(StringUtils.matchCurrentTime("04:00","07:00")){
//                    models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_FACE_4_7, Live2DDefine.PRIORITY_NORMAL );
//                }else if(StringUtils.matchCurrentTime("07:00","10:00")){
//                    models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_FACE_7_10, Live2DDefine.PRIORITY_NORMAL );
//                }else if(StringUtils.matchCurrentTime("10:00","12:00")){
//                    models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_FACE_10_12, Live2DDefine.PRIORITY_NORMAL );
//                }else if(StringUtils.matchCurrentTime("12:00","14:00")){
//                    models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_FACE_12_14, Live2DDefine.PRIORITY_NORMAL );
//                }else if(StringUtils.matchCurrentTime("14:00","17:00")){
//                    models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_FACE_14_17, Live2DDefine.PRIORITY_NORMAL );
//                }else if(StringUtils.matchCurrentTime("17:00","19:00")){
//                    models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_FACE_17_19, Live2DDefine.PRIORITY_NORMAL );
//                }else if(StringUtils.matchCurrentTime("19:00","22:00")){
//                    models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_FACE_19_22, Live2DDefine.PRIORITY_NORMAL );
//                }else if(StringUtils.matchCurrentTime("22:00","23:59")){
//                    models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_TAP_FACE_22_24, Live2DDefine.PRIORITY_NORMAL );
//                }
//
//            }
        }
        return true;
    }

    public void flickEvent(float x,float y) {
        if(Live2DDefine.DEBUG_LOG) Log.d(TAG, "flick x:"+x+" y:"+y);

        for (int i=0; i<models.size(); i++)
        {
            if(models.get(i).hitTest( Live2DDefine.HIT_AREA_HEAD, x, y ))
            {
                if(Live2DDefine.DEBUG_LOG) Log.d(TAG, "Flick head.");
                models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_FLICK_HEAD, Live2DDefine.PRIORITY_NORMAL);
            }
        }
    }

    public void maxScaleEvent()
    {
        if(Live2DDefine.DEBUG_LOG) Log.d(TAG, "Max scale event.");

        for (int i=0; i<models.size(); i++)
        {
            models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_PINCH_IN,Live2DDefine.PRIORITY_NORMAL );
        }
    }



    public void minScaleEvent()
    {
        if(Live2DDefine.DEBUG_LOG) Log.d(TAG, "Min scale event.");

        for (int i=0; i<models.size(); i++)
        {
            models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_PINCH_OUT,Live2DDefine.PRIORITY_NORMAL );
        }
    }



    public void shakeEvent()
    {
        if(Live2DDefine.DEBUG_LOG) Log.d(TAG, "Shake event.");

        for (int i=0; i<models.size(); i++)
        {
            models.get(i).startRandomMotion(Live2DDefine.MOTION_GROUP_SHAKE,Live2DDefine.PRIORITY_FORCE );
        }
    }


    public void setAccel(float x,float y,float z)
    {
        for (int i=0; i<models.size(); i++)
        {
            models.get(i).setAccel(x, y, z);
        }
    }


    public void setDrag(float x,float y)
    {
        for (int i=0; i<models.size(); i++)
        {
            models.get(i).setDrag(x, y);
        }
    }


    public L2DViewMatrix getViewMatrix()
    {
        return view.getViewMatrix();
    }

    public interface OnSoundLoadListener{
        void OnStart();
        void OnLoad(int count, int position);
        void OnFinish();
    }
}
