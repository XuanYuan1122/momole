package com.moemoe.lalala.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.moemoe.lalala.BaseActivity;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.AuthorInfo;
import com.moemoe.lalala.galgame.Live2DDefine;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public class PreferenceManager {

    private static PreferenceManager mPreferenceMng = null;
    private static Context mContext = null;
    private static PreferenceInfo mPreferInfo = null;
    private static AuthorInfo mAuthorInfo = null;
    private static long sLastAccessTokenTime = 0;
    private static long sLastLauncherTime = 0;

    private PreferenceManager() {}

    public static PreferenceManager getInstance(Context context) {
        if (mPreferenceMng == null) {
            mContext = context;
            mPreferenceMng = new PreferenceManager();
        }
        if (mPreferInfo == null) {
            mPreferInfo = initPreferenceInfo();
        }
        return mPreferenceMng;
    }

    public static boolean isLogin(Context context){
        if(mAuthorInfo == null){
            return false;
        }
        return !TextUtils.isEmpty(mAuthorInfo.getmUUid()) && !TextUtils.isEmpty(mAuthorInfo.getmToken());
        // 登录失败
//		return !TextUtils.isEmpty(sAccessToken);
    }

    public String getToken(){
        if(sLastAccessTokenTime > 0 && (System.currentTimeMillis() - sLastAccessTokenTime) > 1000 * 3600 * 1 && mAuthorInfo != null && !TextUtils.isEmpty(mAuthorInfo.getmToken())){
            ((BaseActivity)mContext).tryLoginFirst(null);
        }
        return mAuthorInfo == null? "": TextUtils.isEmpty(mAuthorInfo.getmToken())? "" : mAuthorInfo.getmToken();
    }

    public String getUUid(){ return  mAuthorInfo == null? "": TextUtils.isEmpty(mAuthorInfo.getmUUid())?"":mAuthorInfo.getmUUid(); }

    public static PreferenceInfo initPreferenceInfo() {
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        boolean bFirstLaunch = sp.getBoolean(
                PreferencesDef.IS_APP_FIRST_LAUNCH, true);
        boolean bVersion2 = sp.getBoolean(PreferencesDef.B_VERSION2, true);
        String strUserKry = sp.getString(PreferencesDef.USER_KEY, "");
        String versionCode = sp.getString(PreferencesDef.VERSION_CODE,"");
        if (mPreferInfo == null) {
            mPreferInfo = new PreferenceInfo();
        }
        mPreferInfo.setAppFirstLaunch(bFirstLaunch);
        mPreferInfo.setUserKey(strUserKry);
        mPreferInfo.setVersion2FirstLaunch(bVersion2);
        mPreferInfo.setVersionCode(versionCode);
        return mPreferInfo;
    }

    public PreferenceInfo getPreferInfo() {
        return mPreferInfo;
    }

    public void saveFirstLaunch() {
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(PreferencesDef.IS_APP_FIRST_LAUNCH,
                mPreferInfo.isAppFirstLaunch());
        editor.putString(PreferencesDef.VERSION_CODE,
                mPreferInfo.getVersionCode());
        editor.commit();
    }

    public void saveGetTrash(String jsonStr){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("get_trash",jsonStr);
        editor.commit();
    }

    public String getTrash(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getString("get_trash", "");
    }

    public void saveGetImgTrash(String jsonStr){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("get_img_trash",jsonStr);
        editor.commit();
    }

    public String getImgTrash(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getString("get_img_trash", "");
    }

    public void savePassEvent(int jsonStr){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("pass_event",jsonStr);
        editor.commit();
    }

    public int getPassEvent(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getInt("pass_event", 0);
    }

    public void saveHaveGameFuku(boolean have){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("game_fuku",have);
        editor.commit();
    }

    public boolean getHaveGameFuku(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getBoolean("game_fuku", false);
    }

    public void saveDocCurFloor(String docId,int floor){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(docId,floor);
        editor.commit();
    }

    public void removeData(String data){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(data);
        editor.commit();
    }

    public int getDocCurFloor(String docId){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getInt(docId, 0);
    }

    public void saveNewVersionFirstLaunch(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(PreferencesDef.B_VERSION2,
                mPreferInfo.isVersion2FirstLaunch());
        editor.commit();
    }

    public static void updateAccessTokenTimeToNow(){
        sLastAccessTokenTime = System.currentTimeMillis();
    }

    public void setLowIn3G(boolean isLow){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("key_low_in_3g", true);
        ed.commit();
    }

    public boolean getLowIn3G(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getBoolean("key_low_in_3g", true);
    }

    public void saveSelectFuku(String model){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("live2d_model", model);
        editor.commit();
    }

    public String getSelectFuku(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getString("live2d_model", Live2DDefine.MODEL_LEN);
    }

    public void saveCurrentActor(String name){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("key_current_actor", name);
        editor.commit();
    }

    public void setSelectActor(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("key_has_select_actor", true);
        ed.commit();
    }

    public void setTsukkomi(String tsukkomi){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("key_tsukkomi", tsukkomi);
        ed.commit();
    }

    public String getTsukkomi(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getString("key_tsukkomi", "");
    }

    public void setRedTsukkomi(String tsukkomi){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("read_tsukkomi", tsukkomi);
        ed.commit();
    }

    public String getRedTsukkomi(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getString("read_tsukkomi", "");
    }

    public void setRedSentence(String id,long day,int time){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("read_sentences_ids", id);
        ed.putLong("last_read_time",day);
        ed.putInt("continus_days",time);
        ed.commit();
    }

    public void setsLastLauncherTime(long time){
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong("last_launcher_time", time);
        ed.commit();
    }

    public long getsLastLauncherTime(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getLong("last_launcher_time",0);
    }

    public void setLashEventTime(long time){
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong("last_event_time", time);
        ed.commit();
    }

    public long getsLastEventTime(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getLong("last_event_time",0);
    }

    public String getRedId(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getString("read_sentences_ids", "");
    }

    public long getRedDay(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getLong("last_read_time", -1);
    }

    public int getContinusTime(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getInt("continus_days", -1);
    }

    public void saveCurrentRead(String name){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("key_current_read", name);
        editor.commit();
    }


    public String getCurrentRead(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getString("key_current_read", "");
    }

    public boolean hasSelectActor(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getBoolean("key_has_select_actor", false);
    }

    public String getCurrentActor(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getString("key_current_actor", "yz");
    }

    public void setHotTag(String data){
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("cache_hot_tag_dfefa",data);
        ed.commit();
    }

    public String getHotTag(){
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getString("cache_hot_tag_dfefa","");
    }

    public boolean isFirstStart(){
        SharedPreferences sp = mContext.getSharedPreferences(
                PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getBoolean("key_first_time_start", true);
    }

    public void setFirstStart(boolean need){
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("key_first_time_start", need);
        ed.commit();
    }

    public void saveThirdPartyLoginMsg(AuthorInfo authorInfo){
        mAuthorInfo = authorInfo;
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor= sp.edit();
        editor.putString("nickname",authorInfo.getmUserName());
        editor.putString("headurl",authorInfo.getmHeadPath());
        editor.putString("openid",authorInfo.getmUid());
        editor.putString("platform",authorInfo.getmPlatform());
        editor.putString("token",authorInfo.getmToken());
        editor.putString("password",authorInfo.getmPassword());
        editor.putString("phone",authorInfo.getmPhone());
        editor.putInt("user_id", authorInfo.getmUserId());
        editor.putString("uuid", authorInfo.getmUUid());
        editor.putString("dev_id", authorInfo.getmDevId());
        editor.putString("slogan", authorInfo.getSlogan());
        editor.putInt("nice_num", authorInfo.getNice_num());
        editor.putString("gender",authorInfo.getmGender());
        editor.putLong("register_time", authorInfo.getRegister_time());
        editor.putLong("birthday", authorInfo.getBirthday());
        editor.putString("level_name", authorInfo.getLevel_name());
        editor.putInt("level", authorInfo.getLevel());
        editor.putInt("level_color", authorInfo.getLevel_color());
        editor.putInt("level_start", authorInfo.getLevel_score_start());
        editor.putInt("level_end", authorInfo.getLevel_score_end());
        editor.putInt("score",authorInfo.getScore());
        editor.putInt("coin",authorInfo.getmCoin());
        editor.commit();
    }

    public void clearThirdPartyLoginMsg(){
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        mAuthorInfo = null;
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("nickname", "");
        editor.putString("headurl", "");
        editor.putString("openid", "");
        editor.putString("platform", "");
        editor.putString("token", "");
        editor.putString("password", "");
        editor.putString("phone","");
        editor.putInt("user_id", -1);
        editor.putString("uuid", "");
        editor.putString("dev_id", "");
        editor.putString("slogan", "");
        editor.putString("gender","");
        editor.putInt("nice_num", -1);
        editor.putLong("register_time", -1);
        editor.putLong("birthday", -1);
        editor.putString("level_name", "");
        editor.putInt("level", -1);
        editor.putInt("level_color", -1);
        editor.putInt("level_start", -1);
        editor.putInt("level_end",-1);
        editor.putInt("score",-1);
        editor.putInt("coin",-1);
        sLastAccessTokenTime = 0;
        editor.commit();
    }

    public AuthorInfo getThirdPartyLoginMsg() {
        SharedPreferences sp = mContext.getSharedPreferences(PreferencesDef.FILE_NAME, Activity.MODE_PRIVATE);
        mAuthorInfo = new AuthorInfo();
        mAuthorInfo.setmHeadPath(sp.getString("headurl", ""));
        mAuthorInfo.setmPlatform(sp.getString("platform", ""));
        mAuthorInfo.setmToken(sp.getString("token", ""));
        mAuthorInfo.setmUid(sp.getString("openid", ""));
        mAuthorInfo.setmUserName(sp.getString("nickname", ""));
        mAuthorInfo.setmPassword(sp.getString("password", ""));
        mAuthorInfo.setmPhone(sp.getString("phone", ""));
        mAuthorInfo.setmUserId(sp.getInt("user_id", -1));
        mAuthorInfo.setmUUid(sp.getString("uuid", ""));
        mAuthorInfo.setBirthday(sp.getLong("birthday", -1));
        mAuthorInfo.setNice_num(sp.getInt("nice_num", -1));
        mAuthorInfo.setRegister_time(sp.getLong("register_time", -1));
        mAuthorInfo.setSlogan(sp.getString("slogan", ""));
        mAuthorInfo.setmDevId(sp.getString("dev_id", ""));
        mAuthorInfo.setLevel_name(sp.getString("level_name", ""));
        mAuthorInfo.setLevel(sp.getInt("level", -1));
        mAuthorInfo.setLevel_color(sp.getInt("level_color", -1));
        mAuthorInfo.setLevel_score_start(sp.getInt("level_start", -1));
        mAuthorInfo.setLevel_score_end(sp.getInt("level_end", -1));
        mAuthorInfo.setScore(sp.getInt("score", -1));
        mAuthorInfo.setmGender(sp.getString("gender",""));
        mAuthorInfo.setmCoin(sp.getInt("coin",-1));
        return mAuthorInfo;
    }

    public interface PreferencesDef {
        public static final String FILE_NAME = "settings";
        public static final String IS_APP_FIRST_LAUNCH = "boolean_first_launch_version2";
        public static final String VERSION_CODE = "version_code";
        public static final String B_VERSION2 = "is_version2";
        public static final String USER_KEY = "str_user_key";
        public static final String USER_ID = "str_user_id";
    }

    public static class PreferenceInfo {
        private boolean isAppFirstLaunch = true;
        private String versionCode = "";
        private String userKey = "";
        private String userId = "";
        private boolean version2FirstLaunch = true;

        public String getUserKey() {
            return userKey;
        }

        public void setUserKey(String userKey) {
            this.userKey = userKey;
        }

        public boolean isAppFirstLaunch() {
            return isAppFirstLaunch && !versionCode.equals(mContext.getString(R.string.app_version_code));
        }

        public void setAppFirstLaunch(boolean isAppFirstLaunch) {
            this.isAppFirstLaunch = isAppFirstLaunch;
        }

        public void setVersionCode(String versionCode){this.versionCode = versionCode;}

        public String getVersionCode(){return versionCode;}

        public void setVersion2FirstLaunch(boolean b) {
            this.version2FirstLaunch = b;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public boolean isVersion2FirstLaunch() {
            return version2FirstLaunch;
        }
    }
}
