//package com.moemoe.lalala.network;
//
//import android.content.Context;
//
//import com.app.Utils;
//import com.app.common.Callback;
//import com.moemoe.lalala.app.AppSetting;
//import com.moemoe.lalala.callback.BaseCommonCallback;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
///**
// * Created by Haru on 2016/4/28 0028.
// */
//public enum OtakuCommon {
//    INSTANCE;
//
//    private IOtakuCommon service = Utils.http().create(IOtakuCommon.class);
//
//    public void requestSlotMachineUrl(String token,Callback.CommonCallback<String> callback){
//        service.requestSlotMachineUrl(token, "SLOT_MACHINE_URL", null, callback);
//    }
//
//    public void modifyMyIcon(String token,String content,Callback.InterceptCallback<String> callback,Context context){
//        try {
//            JSONObject json = new JSONObject();
//            json.put("icon", content);
//            service.modifyMyIcon(token, "USER", "CHANGE_ICON", json.toString(), new BaseCommonCallback(callback,context));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void modifyAll(String token,String name,String sex,String birthday,Callback.InterceptCallback<String> callback,Context context){
//        try {
//            JSONObject json = new JSONObject();
//            json.put("nickname", name);
//            json.put("sex", sex);
//            json.put("birthday", birthday);
//            service.modifyAll(token, "USER", "CHANGE_ALL", json.toString(), new BaseCommonCallback(callback, context));
//        } catch (Exception e) {
//        }
//    }
//
//    public void modifyRecommandDoc(String token,String[] docIds,Callback.InterceptCallback<String> callback,Context context){
//        if (docIds != null && docIds.length > 0) {
//            try {
//                JSONArray array = new JSONArray();
//                for(int i = 0; i < docIds.length; i++) {
//                    array.put(docIds[i]);
//                }
//                JSONObject json = new JSONObject();
//                json.put("doc_ids", array);
//                service.modifyRecommandDoc(token, "DOC", "SAVE_RECOMMEND", json.toString(), new BaseCommonCallback(callback,context));
//            } catch (Exception e) {
//            }
//        }
//    }
//
//    public void requestPerson(String token,String user_id,Callback.InterceptCallback<String> callback,Context context){
//        try {
//            JSONObject json = new JSONObject();
//            json.put("user_id", user_id);
//            service.requestPerson(token, "USER_LOAD", json.toString(), new BaseCommonCallback(callback,context));
//        } catch (Exception e) {
//        }
//    }
//
//    public void unfollowClub(String token,String club_id,Callback.InterceptCallback<String> callback,Context context){
//        try {
//            JSONObject json = new JSONObject();
//            json.put("club_id", club_id);
//            service.unfollowClub(token, "CLUB", "MARK_CANCEL", json.toString(), new BaseCommonCallback(callback, context));
//        } catch (Exception e) {
//        }
//    }
//
//    public void followClub(String token,String club_id,Callback.InterceptCallback<String> callback,Context context){
//        try {
//            JSONObject json = new JSONObject();
//            json.put("club_id", club_id);
//            service.followClub(token, "CLUB", "MARK", json.toString(), new BaseCommonCallback(callback, context));
//        } catch (Exception e) {
//        }
//    }
//
//    public void requestClubList(String token,int index,int type,Callback.InterceptCallback<String> callback,Context context){
//        try {
//            String Q = "CLUB_HOT_LIST";
//            if (type == 0) {
//                Q = "CLUB_TOP_LIST";
//            } else if (type == 1) {
//                Q = "CLUB_HOT_LIST";
//            } else if (type == 2) {
//                Q = "USER_CLUB_LIST";
//            } else if (type == 3) {
//                Q = "USER_MARKED_CLUB_LIST";
//            }
//            JSONObject json = new JSONObject();
//            json.put("index", index);
//            json.put("length",Otaku.LENGTH);
//            service.requestClubList(token, Q, json.toString(), new BaseCommonCallback(callback, context));
//        } catch (JSONException e) {
//        }
//    }
//
//    public void requestFriendClubList(String token,String uuid,int index,int type, Callback.InterceptCallback<String> callback,Context context){
//        try {
//            JSONObject json = new JSONObject();
//            String Q = "USER_TARGET_CLUB_MARKED_LIST";
//            if(type == 0){
//                Q = "USER_TARGET_CLUB_MASTER_LIST";
//            }else if(type == 1){
//                Q = "USER_TARGET_CLUB_MARKED_LIST";
//                json.put("index", index);
//                json.put("length", Otaku.LENGTH);
//            }
//            json.put("user_id", uuid);
//            service.requestFriendClubList(token, Q, json.toString(), new BaseCommonCallback(callback, context));
//        } catch (JSONException e) {
//        }
//    }
//
//    public void requestGalTsukkomi(String token,String roleName, Callback.InterceptCallback<String> callback,Context context){
//        try {
//            JSONObject json = new JSONObject();
//			json.put("role", roleName);
//			json.put("length", Otaku.LENGTH);
//            service.requestGalTsukkomi(token, "MESSAGE_LOAD", json.toString(), new BaseCommonCallback(callback, context));
//        } catch (JSONException e) {
//        }
//    }
//
//    public void checkVersion(Callback.CommonCallback<String> callback){
//        service.checkVersion(AppSetting.CHANNEL, AppSetting.VERSION_CODE,callback);
//    }
//
//    public void getWallBlocks(int index,int len,Callback.CommonCallback<String> callback){
//        service.getWallBlocks(index, len, callback);
//    }
//
//    public void report(String token,String target,String id,String type,String reason,Callback.InterceptCallback<String> callback,Context context){
//        service.report(token, target, id, type, reason, new BaseCommonCallback(callback, context));
//    }
//
//    public void dustState(String token,Callback.InterceptCallback<String> callback,Context context){
//        service.dustState(token,new BaseCommonCallback(callback, context));
//    }
//
//    public void getDust(String token,Callback.InterceptCallback<String> callback,Context context){
//        service.getDust(token, new BaseCommonCallback(callback, context));
//    }
//
//    public void cancelDust(String token,String id,Callback.InterceptCallback<String> callback,Context context){
//        service.cancelDust(token, id, new BaseCommonCallback(callback, context));
//    }
//
//    public void sendDust(String token,String title,String content,Callback.InterceptCallback<String> callback,Context context){
//        service.sendDust(token,title,content,new BaseCommonCallback(callback, context));
//    }
//}
