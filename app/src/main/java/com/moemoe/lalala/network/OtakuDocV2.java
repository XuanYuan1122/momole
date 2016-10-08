package com.moemoe.lalala.network;

import android.content.Context;
import com.moemoe.lalala.data.DocPut;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

/**
 * Created by Haru on 2016/5/1 0001.
 */
public enum OtakuDocV2 {
    INSTANCE;

    private IOtakuDocV2 service = Otaku.getInstance().retrofit.create(IOtakuDocV2.class);

    public Call<String> request5Club(String token
            ,String code){
        try {
            JSONObject json = new JSONObject();
            json.put("code", code);
            return service.request5Club(token,"CLUB_MYSTERY",json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Call<String> createNewTag(String token
            ,String docId
            ,String tag){
        return service.createNewTag(token, docId, tag);
    }

    public Call<String> deleteNewComment(String token,String id){
        return service.deleteNewComment(token, id);
    }

    public Call<String> dislikeNewTag(String token
            ,String tagId
            ,String docId){
        return service.dislikeNewTag(token, docId, tagId);
    }

    public Call<String> likeNewTag(String token
            ,String tagId
            ,String docId){
        return service.likeNewTag(token, docId, tagId);
    }

    public Call<String> sendNewComment(String token
            ,String docId
            ,String content
            ,String toUserId
            ,String images){
        return service.sendNewComment(token, docId, content, toUserId,images);
    }

    public Call<String> requestNewDoc(String token
            ,String id){
        return service.requestNewDoc(token, id);
    }

    public Call<String> requestDocHidePath(String token
            ,String id){
        return service.requestDocHidePath(token,id);
    }

    public Call<String> requestNewComment(String token
            ,String docId
            ,int index){
        return service.requestNewComment(token, docId, index, Otaku.LENGTH);
    }

    public Call<String> createRss(String token
            ,String rssId){
        return service.createRss(token, rssId);
    }

    public Call<String> cancelRss(String token
            ,String rssId){
        return  service.cancelRss(token, rssId);
    }

    public Call<String> requestNewDocList(String token
            ,String ui
            ,int index){
        return service.requestNewDocList(token, ui, index, Otaku.LENGTH);
    }

    public Call<String> createNormalDoc(String token
            ,DocPut doc){
        return service.createNormalDoc(token, doc.toJsonString());
    }

    public Call<String> requestNewBanner(String token
            ,String room){
        return service.requestNewBanner(token, room);
    }

    public Call<String> requestFeatured(String token
            ,String room){
        return service.requestFreatured(token, room);
    }

    public Call<String> requestClassList(String token
            ,int index
            ,int length
            ,String roomId){
        return service.requestClassList(token, index, length, roomId);
    }

    public Call<String> requestTagDocList(String token
            ,int index
            ,int len
            ,String tagName){
        return service.requestTagDocList(token, index, len, tagName);
    }

    public Call<String> requestTopTagDocList(String token
            ,String tagName){
        return service.requestTopTagDocList(token, tagName);
    }

    public Call<String> requestHotTagDocList(String token
            ,String tagName){
        return service.requestHotTagDocList(token, tagName);
    }

    public Call<String> requestTagTree(String token
            ,String tagName){
        return service.requestTagTree(token, tagName);
    }

    public Call<String> requestTagNode(String token
            ,String tagId){
        return service.requestTagNode(token, tagId);
    }

    public Call<String> requestMyTagDocList(String token
            ,int index
            ,int len
            ,String userId){
        return service.requestMyTagDocList(token, index, len, userId);
    }

    public Call<String> requestDepartmentDoc(String token
            ,int index
            ,int length
            ,String roomId
            ,String before){
        return service.requestDepartmentDocList(token,index,length,roomId,before);
    }
}
