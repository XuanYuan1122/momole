package com.moemoe.lalala.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.galgame.Live2DDefine;
import com.moemoe.lalala.greendao.gen.AuthorInfoDao;
import com.moemoe.lalala.model.entity.AuthorInfo;
import com.moemoe.lalala.model.entity.SnowShowEntity;

import java.util.ArrayList;

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
            sAuthorInfo.setOpenBag(info.isOpenBag());
        }
        sAuthorInfo.setId(1);
        AuthorInfoDao dao = GreenDaoManager.getInstance().getSession().getAuthorInfoDao();
        dao.insertOrReplace(sAuthorInfo);
    }

    public static String getToken(){
        return sAuthorInfo == null? "": sAuthorInfo.getToken();
    }

    public static String getUUid(){ return  sAuthorInfo == null ? "" : TextUtils.isEmpty(sAuthorInfo.getUserId()) ? "" : sAuthorInfo.getUserId(); }

    public static boolean isLogin(Context context){
        if(sAuthorInfo == null){
            return false;
        }
        return !TextUtils.isEmpty(sAuthorInfo.getUserId()) && !TextUtils.isEmpty(sAuthorInfo.getToken());
    }
//
//    public static void setSignState(Context context,boolean state){
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
//        SharedPreferences.Editor ed = sp.edit();
//        ed.putString("sign_id", getUUid());
//        ed.putBoolean("sign_state",state);
//        ed.commit();
//    }
//
//    public static boolean getSignState(Context context){
//        SharedPreferences sp = context.getSharedPreferences(
//                FILE_NAME,Activity.MODE_PRIVATE);
//        String tempId = sp.getString("sign_id","");
//        if(!tempId.equals(getUUid())){
//            return false;
//        }
//        return sp.getBoolean("sign_state",false);
//    }

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

    public static void setSnowTemp(Context context,ArrayList<SnowShowEntity.PositionInfo> infos){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        for (int i = 0;i < infos.size();i++){
            SnowShowEntity.PositionInfo info = infos.get(i);
            ed.putInt("temp_snow_x_" + i,info.x);
            ed.putInt("temp_snow_y_" + i,info.y);
        }
        ed.putInt("temp_snow_size",infos.size());
        ed.commit();
    }

    public static ArrayList<SnowShowEntity.PositionInfo> getSnowTemp(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        ArrayList<SnowShowEntity.PositionInfo> infos = new ArrayList<>();
        int size = sp.getInt("temp_snow_size",0);
        for(int i = 0;i < size;i++ ){
            SnowShowEntity.PositionInfo info = new SnowShowEntity.PositionInfo(sp.getInt("temp_snow_x_" + i,0),sp.getInt("temp_snow_y_" + i,0));
            infos.add(info);
        }
        return infos;
    }

    public static void setSnowCache(Context context,ArrayList<SnowShowEntity.PositionInfo> infos){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        for (int i = 0;i < infos.size();i++){
            SnowShowEntity.PositionInfo info = infos.get(i);
            ed.putInt("cache_snow_x_" + i,info.x);
            ed.putInt("cache_snow_y_" + i,info.y);
        }
        ed.putInt("cache_snow_size",infos.size());
        ed.commit();
    }

    public static ArrayList<SnowShowEntity.PositionInfo> getSnowCache(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME,Activity.MODE_PRIVATE);
        ArrayList<SnowShowEntity.PositionInfo> infos = new ArrayList<>();
        int size = sp.getInt("cache_snow_size",0);
        for(int i = 0;i < size;i++ ){
            SnowShowEntity.PositionInfo info = new SnowShowEntity.PositionInfo(sp.getInt("cache_snow_x_" + i,0),sp.getInt("cache_snow_y_" + i,0));
            infos.add(info);
        }
        return infos;
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

    public static void setSimpleLabel(Context context,boolean isLow){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("simple_label", isLow);
        ed.commit();
    }

    public static boolean getSimpleLabel(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getBoolean("simple_label", false);
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
        return bFirstLaunch && !versionCode.equals(context.getString(R.string.app_version_code));
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

    public static void saveSelectFuku(Context context,String model){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("live2d_model", model);
        editor.commit();
    }

    public static String getSelectFuku(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getString("live2d_model", Live2DDefine.MODEL_LEN);
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
}