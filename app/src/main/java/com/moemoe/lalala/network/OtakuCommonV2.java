package com.moemoe.lalala.network;

import android.content.Context;
import com.moemoe.lalala.app.AppSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

/**
 * Created by Haru on 2016/4/28 0028.
 */
public enum OtakuCommonV2 {
    INSTANCE;

    private IOtakuCommonV2 service = Otaku.getInstance().retrofit.create(IOtakuCommonV2.class);

    public Call<String> requestSlotMachineUrl(String token){
        return service.requestSlotMachineUrl(token, "SLOT_MACHINE_URL", null);
    }

    public Call<String> modifyMyIcon(String token
            ,String content){
        try {
            JSONObject json = new JSONObject();
            json.put("icon", content);
            return service.modifyMyIcon(token, "USER", "CHANGE_ICON", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Call<String> modifyAll(String token
            ,String name
            ,String sex
            ,String birthday){
        try {
            JSONObject json = new JSONObject();
            json.put("nickname", name);
            json.put("sex", sex);
            json.put("birthday", birthday);
            return service.modifyAll(token, "USER", "CHANGE_ALL", json.toString());
        } catch (Exception e) {
        }
        return null;
    }

    public Call<String> modifyRecommandDoc(String token
            ,String[] docIds){
        if (docIds != null && docIds.length > 0) {
            try {
                JSONArray array = new JSONArray();
                for(int i = 0; i < docIds.length; i++) {
                    array.put(docIds[i]);
                }
                JSONObject json = new JSONObject();
                json.put("doc_ids", array);
                return service.modifyRecommandDoc(token, "DOC", "SAVE_RECOMMEND", json.toString());
            } catch (Exception e) {
            }
        }
        return null;
    }

    public Call<String> requestPerson(String token
            ,String user_id){
        try {
            JSONObject json = new JSONObject();
            json.put("user_id", user_id);
            return service.requestPerson(token, "USER_LOAD", json.toString());
        } catch (Exception e) {
        }
        return null;
    }

    public Call<String> unfollowClub(String token
            ,String club_id){
        try {
            JSONObject json = new JSONObject();
            json.put("club_id", club_id);
            return service.unfollowClub(token, "CLUB", "MARK_CANCEL", json.toString());
        } catch (Exception e) {
        }
        return null;
    }

    public Call<String> followClub(String token
            ,String club_id){
        try {
            JSONObject json = new JSONObject();
            json.put("club_id", club_id);
            return service.followClub(token, "CLUB", "MARK", json.toString());
        } catch (Exception e) {
        }
        return null;
    }

    public Call<String> requestClubList(String token
            ,int index
            ,int type){
        try {
            String Q = "CLUB_HOT_LIST";
            if (type == 0) {
                Q = "CLUB_TOP_LIST";
            } else if (type == 1) {
                Q = "CLUB_HOT_LIST";
            } else if (type == 2) {
                Q = "USER_CLUB_LIST";
            } else if (type == 3) {
                Q = "USER_MARKED_CLUB_LIST";
            }
            JSONObject json = new JSONObject();
            json.put("index", index);
            json.put("length",Otaku.LENGTH);
            return service.requestClubList(token, Q, json.toString());
        } catch (JSONException e) {
        }
        return null;
    }

    public Call<String> requestFriendClubList(String token
            ,String uuid
            ,int index
            ,int type){
        try {
            JSONObject json = new JSONObject();
            String Q = "USER_TARGET_CLUB_MARKED_LIST";
            if(type == 0){
                Q = "USER_TARGET_CLUB_MASTER_LIST";
            }else if(type == 1){
                Q = "USER_TARGET_CLUB_MARKED_LIST";
                json.put("index", index);
                json.put("length", Otaku.LENGTH);
            }
            json.put("user_id", uuid);
            return service.requestFriendClubList(token, Q, json.toString());
        } catch (JSONException e) {
        }
        return null;
    }

    public Call<String> requestGalTsukkomi(String token
            ,String roleName){
        try {
            JSONObject json = new JSONObject();
			json.put("role", roleName);
			json.put("length", Otaku.LENGTH);
            return service.requestGalTsukkomi(token, "MESSAGE_LOAD", json.toString());
        } catch (JSONException e) {
        }
        return null;
    }

    public Call<String> checkVersion(){
        return service.checkVersion(AppSetting.CHANNEL, AppSetting.VERSION_CODE);
    }

    public Call<String> getWallBlocks(int index
            ,int len){
        return service.getWallBlocks(index, len);
    }

    public Call<String> report(String token
            ,String target
            ,String id
            ,String type
            ,String reason){
        return service.report(token, target, id, type, reason);
    }

    public Call<String> dustState(String token){
        return service.dustState(token);
    }

    public Call<String> getDust(String token){
        return service.getDust(token);
    }

    public Call<String> cancelDust(String token
            ,String id){
        return service.cancelDust(token, id);
    }

    public Call<String> sendDust(String token
            ,String title
            ,String content){
        return service.sendDust(token,title,content);
    }

    public Call<String> gotDustList(String token
            ,int index
            ,int length){
        return service.gotDustList(token,index,length);
    }

    public Call<String> sotDustList(String token
            ,int index
            ,int length){
        return service.sotDustList(token,index,length);
    }

    public Call<String> getDustList(String token
            ,int size){
        return service.getDustList(token,size);
    }

    public Call<String> funDust(String token
            ,String id){
        return service.funDust(token,id);
    }

    public Call<String> shitDust(String token
            ,String id){
        return service.shitDust(token,id);
    }

    public Call<String> top3DustList(String token){
        return service.top3DustList(token);
    }
}
