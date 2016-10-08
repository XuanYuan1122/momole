//package com.moemoe.lalala.network;
//
//import android.content.Context;
//import android.text.TextUtils;
//
//import com.app.Utils;
//import com.app.common.Callback;
//import com.moemoe.lalala.callback.BaseCommonCallback;
//import com.moemoe.lalala.data.DocPut;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//
///**
// * Created by Haru on 2016/5/1 0001.
// */
//public enum  OtakuDoc {
//    INSTANCE;
//
//    private IOtakuDoc service = Utils.http().create(IOtakuDoc.class);
//
//    public void request5Club(String token,String code,Callback.InterceptCallback<String> callback,Context context){
//        try {
//            JSONObject json = new JSONObject();
//            json.put("code", code);
//            service.request5Club(token,"CLUB_MYSTERY",json.toString(),new BaseCommonCallback(callback,context));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void createNewTag(String token,String docId,String tag,Callback.InterceptCallback<String> callback,Context context){
//        service.createNewTag(token, docId, tag,new BaseCommonCallback(callback,context));
//    }
//
//    public void deleteNewComment(String token,String id,Callback.InterceptCallback<String> callback,Context context){
//        service.deleteNewComment(token, id, new BaseCommonCallback(callback, context));
//    }
//
//    public void dislikeNewTag(String token,String tagId,String docId,Callback.InterceptCallback<String> callback,Context context){
//        service.dislikeNewTag(token, docId, tagId, new BaseCommonCallback(callback, context));
//    }
//
//    public void likeNewTag(String token,String tagId,String docId,Callback.InterceptCallback<String> callback,Context context){
//        service.likeNewTag(token, docId, tagId, new BaseCommonCallback(callback, context));
//    }
//
//    public void sendNewComment(String token,String docId,String content,String toUserId,String images,Callback.InterceptCallback<String> callback,Context context){
//        service.sendNewComment(token, docId, content, toUserId,images,new BaseCommonCallback(callback, context));
//    }
//
//    public void requestNewDoc(String token,String id,Callback.InterceptCallback<String> callback,Context context){
//        service.requestNewDoc(token, id, new BaseCommonCallback(callback, context));
//    }
//
//    public void requestNewComment(String token,String docId,int index,Callback.InterceptCallback<String> callback,Context context){
//        service.requestNewComment(token, docId, index, Otaku.LENGTH, new BaseCommonCallback(callback, context));
//    }
//
//    public void createRss(String token,String rssId,Callback.InterceptCallback<String> callback,Context context){
//        service.createRss(token, rssId, new BaseCommonCallback(callback, context));
//    }
//
//    public void cancelRss(String token,String rssId,Callback.InterceptCallback<String> callback,Context context){
//        service.cancelRss(token, rssId, new BaseCommonCallback(callback, context));
//    }
//
//    public void requestNewDocList(String token,String ui,int index,Callback.InterceptCallback<String> callback,Context context){
//        service.requestNewDocList(token, ui, index, Otaku.LENGTH, new BaseCommonCallback(callback, context));
//    }
//
//    public void createNormalDoc(String token,DocPut doc,Callback.InterceptCallback<String> callback,Context context){
//        service.createNormalDoc(token, doc.toJsonString(), new BaseCommonCallback(callback, context));
//    }
//
//    public void requestNewBanner(String token,String room,Callback.InterceptCallback<String> callback,Context context){
//        service.requestNewBanner(token, room, new BaseCommonCallback(callback, context));
//    }
//
//    public void requestFeatured(String token,String room,Callback.InterceptCallback<String> callback,Context context){
//        service.requestFreatured(token, room, new BaseCommonCallback(callback, context));
//    }
//
//    public void requestClassList(String token,int index,int length,String roomId,Callback.InterceptCallback<String> callback,Context context){
//        service.requestClassList(token, index, length, roomId, new BaseCommonCallback(callback, context));
//    }
//
//    public void requestTagDocList(String token,int index,int len,String tagName,Callback.InterceptCallback<String> callback,Context context){
//        service.requestTagDocList(token, index, len, tagName, new BaseCommonCallback(callback, context));
//    }
//
//    public void requestTopTagDocList(String token,String tagName,Callback.InterceptCallback<String> callback,Context context){
//        service.requestTopTagDocList(token, tagName, new BaseCommonCallback(callback, context));
//    }
//
//    public void requestHotTagDocList(String token,String tagName,Callback.InterceptCallback<String> callback,Context context){
//        service.requestHotTagDocList(token, tagName, new BaseCommonCallback(callback, context));
//    }
//
//    public void requestTagTree(String token,String tagName,Callback.InterceptCallback<String> callback,Context context){
//        service.requestTagTree(token, tagName, new BaseCommonCallback(callback, context));
//    }
//
//    public void requestTagNode(String token,String tagId,Callback.InterceptCallback<String> callback,Context context){
//        service.requestTagNode(token, tagId, new BaseCommonCallback(callback, context));
//    }
//
//    public void requestMyTagDocList(String token,int index,int len,String userId,Callback.InterceptCallback<String> callback,Context context){
//        service.requestMyTagDocList(token, index, len, userId, new BaseCommonCallback(callback, context));
//    }
//
//    public void requestDepartmentDoc(String token,int index,int length,String roomId,String before,Callback.InterceptCallback<String> callback,Context context){
//        service.requestDepartmentDocList(token,index,length,roomId,before,new BaseCommonCallback(callback,context));
//    }
//}
