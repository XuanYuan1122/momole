package com.moemoe.lalala.galgame;

/**
 * Created by Haru on 2016/7/18 0018.
 */
public class Live2DDefine {

    public static boolean DEBUG_LOG = false;
    public static boolean DEBUG_TOUCH_LOG=false;
    public static boolean DEBUG_DRAW_HIT_AREA=false;

    public static final float VIEW_MAX_SCALE = 2f;
    public static final float VIEW_MIN_SCALE = 0.8f;

    public static final float VIEW_LOGICAL_LEFT = -1;
    public static final float VIEW_LOGICAL_RIGHT = 1;

    public static final String MODEL_LEN	= "live2d/len/model.json";
    public static final String MODEL_LEN_SWIN = "live2d/len/model_swim.json";

    public static final String BACK_IMAGE_NAME = "image/neta_school.png" ;

    static final String MOTION_GROUP_IDLE ="idle";
    static final String MOTION_GROUP_START_FIRST ="start_first";
    static final String MOTION_GROUP_TAP_BELLY	="tap_belly";
    static final String MOTION_GROUP_TAP_CHEST	="tap_chest";
    static final String MOTION_GROUP_TAP_FACE_0_4	="tap_face_0-4";
    static final String MOTION_GROUP_TAP_FACE_4_7	="tap_face_4-7";
    static final String MOTION_GROUP_TAP_FACE_7_10	="tap_face_7-10";
    static final String MOTION_GROUP_TAP_FACE_10_12	="tap_face_10-12";
    static final String MOTION_GROUP_TAP_FACE_12_14	="tap_face_12-14";
    static final String MOTION_GROUP_TAP_FACE_14_17	="tap_face_14-17";
    static final String MOTION_GROUP_TAP_FACE_17_19	="tap_face_17-19";
    static final String MOTION_GROUP_TAP_FACE_19_22	="tap_face_19-22";
    static final String MOTION_GROUP_TAP_FACE_22_24	="tap_face_22-24";
    static final String MOTION_GROUP_TAP_HAND_R	="tap_hand_r";
    static final String MOTION_GROUP_TAP_HAND_L	="tap_hand_l";
    static final String MOTION_GROUP_TAP_HEAD	="tap_head";
    static final String MOTION_GROUP_TAP_SKIRT	="tap_skirt";
    static final String MOTION_GROUP_FLICK_HEAD	="flick_head";
    static final String MOTION_GROUP_PINCH_IN	="pinch_in";
    static final String MOTION_GROUP_PINCH_OUT	="pinch_out";
    static final String MOTION_GROUP_SHAKE		="shake";

    static final String HIT_AREA_HEAD		="head";
    static final String HIT_AREA_CHEST		="chest";
    static final String HIT_AREA_BELLY		="belly";
    static final String HIT_AREA_SKIRT		="skirt";
    static final String HIT_AREA_HAND_R		="hand_r";
    static final String HIT_AREA_HAND_L 	="hand_l";
    static final String HIT_AREA_FACE 	    ="face";

    public static final int PRIORITY_IDLE = 1;
    public static final int PRIORITY_NORMAL = 2;
    public static final int PRIORITY_FORCE = 3;

    public static final float VIEW_LOGICAL_MAX_LEFT = -2;
    public static final float VIEW_LOGICAL_MAX_RIGHT = 2;
    public static final float VIEW_LOGICAL_MAX_BOTTOM = -2;
    public static final float VIEW_LOGICAL_MAX_TOP = 2;
}
