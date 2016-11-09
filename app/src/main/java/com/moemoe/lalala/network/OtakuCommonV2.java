package com.moemoe.lalala.network;

import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.data.ApiResult;
import com.moemoe.lalala.data.AppUpdateInfo;
import com.moemoe.lalala.data.DonationInfoBean;
import com.moemoe.lalala.data.DustImageBean;
import com.moemoe.lalala.data.DustTextBean;

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

    public Call<ApiResult<AppUpdateInfo>> checkVersion(){
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

    public Call<ApiResult> sendDust(String token
            , DustTextBean bean){
        return service.sendDust(token,bean);
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

    public Call<ApiResult> funDust(String token
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

    public Call<ApiResult> sendImgDust(String token
            , DustImageBean bean){
        return service.sendImgDust(token,bean);
    }

    public Call<String> gotImgDustList(String token
            ,int index
            ,int length){
        return service.gotImgDustList(token,index,length);
    }

    public Call<String> sotImgDustList(String token
            ,int index
            ,int length){
        return service.sotImgDustList(token,index,length);
    }

    public Call<String> getImgDustList(String token
            ,int size){
        return service.getImgDustList(token,size);
    }

    public Call<ApiResult> funImgDust(String token
            ,String id){
        return service.funImgDust(token,id);
    }

    public Call<String> shitImgDust(String token
            ,String id){
        return service.shitImgDust(token,id);
    }

    public Call<String> top3ImgDustList(String token){
        return service.top3ImgDustList(token);
    }

    public Call<ApiResult<DonationInfoBean>> getDonationInfo(String head){
        return service.getDonationInfo(head);
    }

    public Call<ApiResult<DonationInfoBean>> getBookDonationInfo(String head){
        return service.getBookDonationInfo(head);
    }

    public Call<ApiResult> donationCoin(String head,int coin){
        return service.donationCoin(head,coin);
    }
}
