package com.moemoe.lalala.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.greendao.gen.AuthorInfoDao;
import com.moemoe.lalala.model.entity.AuthorInfo;

/**
 * Created by yi on 2016/11/28.
 */

public class PreferenceUtils {

    private static final String FILE_NAME = "settings";
    private static AuthorInfo sAuthorInfo;

    public static AuthorInfo getAuthorInfo(){
        if(sAuthorInfo == null){
            sAuthorInfo = new AuthorInfo();
        }
        return sAuthorInfo;
    }

    public static void clearAuthorInfo(){
        if(sAuthorInfo != null){
            AuthorInfoDao dao = GreenDaoManager.getInstance().getSession().getAuthorInfoDao();
            dao.deleteByKey((long) 1);
            sAuthorInfo = null;
        }
    }

    public static void setAuthorInfo(AuthorInfo info){
        if(sAuthorInfo == null)  {
            sAuthorInfo = info;
        }else {
            if(!TextUtils.isEmpty(info.getHeadPath())){
                sAuthorInfo.setHeadPath(info.getHeadPath());
            }
            if(!TextUtils.isEmpty(info.getUserId())){
                sAuthorInfo.setUserId(info.getUserId());
            }
            if (!TextUtils.isEmpty(info.getOpenId())){
                sAuthorInfo.setOpenId(info.getOpenId());
            }
            if(!TextUtils.isEmpty(info.getPlatform())){
                sAuthorInfo.setPlatform(info.getPlatform());
            }
            if(!TextUtils.isEmpty(info.getToken())){
                sAuthorInfo.setToken(info.getToken());
            }
            if(!TextUtils.isEmpty(info.getPassword())){
                sAuthorInfo.setPassword(info.getPassword());
            }
            if(!TextUtils.isEmpty(info.getPhone())){
                sAuthorInfo.setPhone(info.getPhone());
            }
            if(info.getCoin() > 0){
                sAuthorInfo.setCoin(info.getCoin());
            }
            if(!TextUtils.isEmpty(info.getUserName())){
                sAuthorInfo.setUserName(info.getUserName());
            }
            if(info.getLevel() > 1){
                sAuthorInfo.setLevel(info.getLevel());
            }
            if(!TextUtils.isEmpty(info.getRcToken())){
                sAuthorInfo.setRcToken(info.getRcToken());
            }
            sAuthorInfo.setOpenBag(info.isOpenBag());
            sAuthorInfo.setInspector(info.isInspector());
        }
        sAuthorInfo.setId(1);
        AuthorInfoDao dao = GreenDaoManager.getInstance().getSession().getAuthorInfoDao();
        dao.insertOrReplace(sAuthorInfo);
    }

    public static String getToken(){
        return sAuthorInfo == null? "": sAuthorInfo.getToken();
    }

    public static String getUUid(){ return  sAuthorInfo == null ? "" : TextUtils.isEmpty(sAuthorInfo.getUserId()) ? "" : sAuthorInfo.getUserId(); }

    public static int hasMsg(Context context){
        int num = 0;
        if(PreferenceUtils.getMessageDot(context,"neta")
                || PreferenceUtils.getMessageDot(context,"system")
                || PreferenceUtils.getMessageDot(context,"at_user")
                || PreferenceUtils.getMessageDot(context,"normal")){
            num = PreferenceUtils.getNetaMsgDotNum(context)
                    + PreferenceUtils.getSysMsgDotNum(context)
                    + PreferenceUtils.getAtUserMsgDotNum(context)
                    + PreferenceUtils.getNormalMsgDotNum(context);
        }
        return num;
    }

    public static void clearMsg(Context context){
        PreferenceUtils.setNetaMsgDotNum(context,0);
        PreferenceUtils.setSysMsgDotNum(context,0);
        PreferenceUtils.setAtUserMsgDotNum(context,0);
        PreferenceUtils.setNormalMsgDotNum(context,0);
        PreferenceUtils.setMessageDot(context,"neta",false);
        PreferenceUtils.setMessageDot(context,"system",false);
        PreferenceUtils.setMessageDot(context,"at_user",false);
        PreferenceUtils.setMessageDot(context,"normal",false);
    }

    public static boolean isLogin(){
        if(sAuthorInfo == null){
            return false;
        }
        return !TextUtils.isEmpty(sAuthorInfo.getUserId()) && !TextUtils.isEmpty(sAuthorInfo.getToken());
    }

    public static void setFeedLastItem(Context context,String type,String id){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("feed_last_" + type, id);
        ed.commit();
    }

    public static String getFeedLastItem(Context context,String type){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getString("feed_last_" + type,"");
    }

    public static void setsLastLauncherTime(Context context,long time){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong("last_launcher_time", time);
        ed.commit();
    }

    public static long getsLastLauncherTime(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getLong("last_launcher_time",0);
    }

    public static void setJuQingVersion(Context context,int version){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("juqing_version_1", version);
        ed.commit();
    }

    public static int getJuQingVersion(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getInt("juqing_version_1",0);
    }

    public static void setMessageDot(Context context,String type,boolean isNew){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(type, isNew);
        ed.commit();
    }

    public static boolean getMessageDot(Context context,String type){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getBoolean(type,false);
    }

    public static void setNetaMsgDotNum(Context context, int num){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        if(num < 0) num = 0;
        ed.putInt("neta_msg_num", num);
        ed.commit();
    }

    public static int getNetaMsgDotNum(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getInt("neta_msg_num",0);
    }

    public static void setSysMsgDotNum(Context context, int num){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        if(num < 0) num = 0;
        ed.putInt("system_msg_num", num);
        ed.commit();
    }

    public static int getSysMsgDotNum(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getInt("system_msg_num",0);
    }

    public static void setAtUserMsgDotNum(Context context, int num){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        if(num < 0) num = 0;
        ed.putInt("at_user_msg_num", num);
        ed.commit();
    }

    public static int getAtUserMsgDotNum(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getInt("at_user_msg_num",0);
    }

    public static void setNormalMsgDotNum(Context context, int num){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        if(num < 0) num = 0;
        ed.putInt("normal_msg_num", num);
        ed.commit();
    }

    public static int getNormalMsgDotNum(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getInt("normal_msg_num",0);
    }

    public static void setDeskMate(Context context,String role){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("neta_role", role);
        ed.commit();
    }

    public static String getDeskMate(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getString("neta_role","");
    }

    public static void setGroupDotNum(Context context, int num){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        if(num < 0) num = 0;
        ed.putInt("phone_group_msg_dot_num", num);
        ed.commit();
    }

    public static int getGroupDotNum(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getInt("phone_group_msg_dot_num",0);
    }

    public static void setRCDotNum(Context context,int num){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("phone_rc_msg_dot_num", num);
        ed.commit();
    }

    public static int getRCDotNum(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getInt("phone_rc_msg_dot_num",0);
    }

    public static void setJuQingDotNum(Context context,int num){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("phone_ju_qing_msg_dot_num", num);
        ed.commit();
    }

    public static int getJuQIngDotNum(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getInt("phone_ju_qing_msg_dot_num",0);
    }

    public static void setDiscoverMinIdx(Context context,String type,long num){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong("discover_min_idx_" + type, num);
        ed.commit();
    }

    public static long getDiscoverMinIdx(Context context,String type){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getLong("discover_min_idx_" + type,0);
    }

    public static void setDiscoverMaxIdx(Context context,String type,long num){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong("discover_max_idx_" + type, num);
        ed.commit();
    }

    public static long getDiscoverMaxIdx(Context context,String type){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getLong("discover_max_idx_" + type,0);
    }

    public static void setIp(Context context,String ip){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("neta_ip", ip);
        ed.commit();
    }

    public static String getIp(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getString("neta_ip","");
    }

    public static void saveDoc(Context context,String doc){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("rich_doc", doc);
        ed.commit();
    }

    public static String getDoc(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getString("rich_doc","");
    }

    public static void setLastSnowTime(Context context,long time){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong("last_snow_time", time);
        ed.commit();
    }

    public static long getLastSnowTime(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getLong("last_snow_time",0);
    }

    public static void setLastTrashTime(Context context,int time,String type){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("last_trash_time_" + type, time);
        ed.commit();
    }

    public static int getBuildVersion(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getInt("build_version" + AppSetting.VERSION_CODE,0);
    }

    public static void setBuildVersion(Context context,int version){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("build_version" + AppSetting.VERSION_CODE, version);
        ed.commit();
    }

    public static int getLastTrashTime(Context context,String type){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getInt("last_trash_time_" + type,0);
    }

    public static int getLastFeedPosition(Context context,String type){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getInt("last_feed_position_" + type,-1);
    }

    public static void setLastFeedPosition(Context context,String type,int position){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("last_feed_position_" + type, position);
        ed.commit();
    }

    public static void setLastEventTime(Context context, long time){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong("last_event_time", time);
        ed.commit();
    }

    public static long getLastEventTime(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getLong("last_event_time",0);
    }

    public static void setAllBackSchool(Context context, boolean show){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("all_back_school", show);
        ed.commit();
    }

    public static boolean getAllBackSchool(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getBoolean("all_back_school",false);
    }

    public static void setBackSchoolDialog(Context context, boolean show){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("back_school_dialog", show);
        ed.commit();
    }

    public static boolean getBackSchoolDialog(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        return sp.getBoolean("back_school_dialog",false);
    }

    public static void setLowIn3G(Context context,boolean isLow){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("key_low_in_3g", isLow);
        ed.commit();
    }

    public static boolean getLowIn3G(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getBoolean("key_low_in_3g", true);
    }

    public static boolean isAppFirstLaunch(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        boolean bFirstLaunch = sp.getBoolean(
                "boolean_first_launch_version2", true);
        String versionCode = sp.getString("version_code","");
        return bFirstLaunch || !versionCode.equals(context.getString(R.string.app_version_code));
    }

    public static boolean isSetAlarm(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getBoolean(
                "boolean_set_alarm", false);
    }

    public static void setAlarm(Context context,boolean is){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("boolean_set_alarm", is);
        ed.commit();
    }

    public static void setAppFirstLaunch(Context context,boolean is){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("boolean_first_launch_version2", is);
        ed.commit();
    }

    public static void setVersionCode(Context context,String code){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("version_code", code);
        ed.commit();
    }

    public static boolean isVersion2FirstLaunch(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        boolean bVersion2 = sp.getBoolean("is_version2", true);
        return bVersion2;
    }

    public static void setVersion2FirstLaunch(Context context,boolean is){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("is_version2", is);
        ed.commit();
    }

    public static void saveHaveGameFuku(Context context,boolean have){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("game_fuku",have);
        editor.commit();
    }

    public static boolean getHaveGameFuku(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getBoolean("game_fuku", false);
    }

    public static void savePassEvent(Context context,int jsonStr){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("pass_event",jsonStr);
        editor.commit();
    }

    public static int getPassEvent(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getInt("pass_event", 0);
    }


    public static void saveDocCurFloor(Context context,String docId,int floor){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(docId,floor);
        editor.commit();
    }

    public static void removeData(Context context,String data){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(data);
        editor.commit();
    }

    public static int getDocCurFloor(Context context,String docId){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getInt(docId, 0);
    }

    public static int getReadFontSize(Context context) {
        return getReadFontSize(context,"");
    }

    public static int getReadFontSize(Context context,String bookId){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getInt("bookId",context.getResources().getDimensionPixelSize(R.dimen.x30));
    }

    public static void saveFontSize(Context context,String bookId,int fontSizePx){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(bookId,fontSizePx);
        editor.commit();
    }

    public static void saveFontSize(Context context,int fontSizePx){
        saveFontSize(context,"",fontSizePx);
    }

    public static void saveReadProgress(Context context,String bookId, int currentChapter, int curBeginPos, int curEndPos) {
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(bookId+"_chapter",currentChapter);
        editor.putInt(bookId+"_startPos",curBeginPos);
        editor.putInt(bookId+"_endPos",curEndPos);
        editor.commit();
    }

    public static int[] getReadProgress(Context context,String bookId){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        int chapter = sp.getInt(bookId + "_chapter", 1);
        int startPos = sp.getInt(bookId + "_startPos", 0);
        int endPos = sp.getInt(bookId + "_endPos", 0);
        return new int[]{chapter,startPos,endPos};
    }

    public static boolean isNight(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getBoolean("is_night",false);
    }

    public static void setNight(Context context,boolean isNight){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("is_night",isNight);
        editor.commit();
    }

    public static String getNearPosition(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getString("near_position", "");
    }

    public static void setNearPosition(Context context,String str) {
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("near_position",str);
        editor.commit();
    }

    public static String getTopUserPosition(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getString("top_position", "");
    }

    public static void setTopUserPosition(Context context,String str) {
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("top_position",str);
        editor.commit();
    }

    public static int getReadBrightness(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getInt("read_light", (int) ScreenUtils.getScreenBrightness(context));
    }

    public static void saveReadBrightness(Context context,int percent) {
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("read_light",percent);
        editor.commit();
    }

    public static int getBackSchoolLevel(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getInt("back_school_level", 0);
    }

    public static void setBackSchoolLevel(Context context,int level){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("back_school_level",level);
        editor.commit();
    }

    public static String getLenLastContent(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getString("len_last_content", "");
    }

    public static void setLenLastContent(Context context,String content){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("len_last_content",content);
        editor.commit();
    }

    public static String getMeiLastContent(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getString("mei_last_content", "");
    }

    public static void setMeiLastContent(Context context,String content){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("mei_last_content",content);
        editor.commit();
    }

    public static String getSariLastContent(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getString("sari_last_content", "");
    }

    public static void setSariLastContent(Context context,String content){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("sari_last_content",content);
        editor.commit();
    }

    public static String[] getLastGroupContentAndTime(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);

        return new String[]{
                sp.getString("group_last_content", ""),
                sp.getString("group_last_time", "")
        };
    }

    public static void setLastGroupContentAndTime(Context context,String content,String time){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("group_last_content",content);
        editor.putString("group_last_time",time);
        editor.commit();
    }
}
