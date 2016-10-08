package com.moemoe.lalala.network;

import com.app.annotation.DELETE;
import com.app.annotation.Field;
import com.app.annotation.FormUrlEncoded;
import com.app.annotation.GET;
import com.app.annotation.HEAD;
import com.app.annotation.POST;
import com.app.annotation.PUT;
import com.app.annotation.Path;
import com.app.common.Callback;

/**
 * Created by Haru on 2016/5/1 0001.
 */
public interface IOtakuDoc {

    @POST("otakuhouse/query")
    @FormUrlEncoded
    void request5Club(@HEAD(Otaku.X_ACCESS_TOKEN)String token,@Field("Q")String q,@Field("DATA")String data,Callback.CommonCallback<String> callback);

    @POST("neta/api/doc/tag")
    @FormUrlEncoded
    void createNewTag(@HEAD(Otaku.X_ACCESS_TOKEN) String head, @Field("docId") String d, @Field("tag") String x,Callback.CommonCallback<String> callback);

    @POST("neta/api/doc/tag/dislike")
    @FormUrlEncoded
    void dislikeNewTag(@HEAD(Otaku.X_ACCESS_TOKEN) String head, @Field("docId") String d, @Field("tagId") String x,Callback.CommonCallback<String> callback);

    @POST("neta/api/doc/tag/like")
    @FormUrlEncoded
    void likeNewTag(@HEAD(Otaku.X_ACCESS_TOKEN) String head, @Field("docId") String d, @Field("tagId") String x,Callback.CommonCallback<String> callback);

    @POST("neta/api/doc/comment")
    @FormUrlEncoded
    void sendNewComment(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("docId")String docId, @Field("content") String x, @Field("toUserId") String t,@Field("images")String images,Callback.CommonCallback<String> callback);

    @DELETE("neta/api/doc/comment/{id}")
    @FormUrlEncoded
    void deleteNewComment(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Path("id")String id,Callback.CommonCallback<String> callback);

    @GET("neta/api/doc/{id}")
    @FormUrlEncoded
    void requestNewDoc(@HEAD(Otaku.X_ACCESS_TOKEN) String head, @Path("id")String id,Callback.CommonCallback<String> callback);

    @GET("neta/api/doc/{docId}/comments")
    @FormUrlEncoded
    void requestNewComment(@HEAD(Otaku.X_ACCESS_TOKEN) String head, @Path("docId")String id,@Field("index")int index,@Field("length")int l,Callback.CommonCallback<String> callback);

    @POST("neta/api/rss/subscribe")
    @FormUrlEncoded
    void createRss(@HEAD(Otaku.X_ACCESS_TOKEN) String head, @Field("rssId")String rssId,Callback.CommonCallback<String> callback);

    @POST("neta/api/rss/cancel")
    @FormUrlEncoded
    void cancelRss(@HEAD(Otaku.X_ACCESS_TOKEN) String head, @Field("rssId")String rssId,Callback.CommonCallback<String> callback);

    @GET("neta/api/calendar/ui/{ui}")
    @FormUrlEncoded
    void requestNewDocList(@HEAD(Otaku.X_ACCESS_TOKEN) String head, @Path("ui")String ui,@Field("index")int index,@Field("length")int len,Callback.CommonCallback<String> callback);

    @PUT("neta/api/tagDoc")
    @FormUrlEncoded
    void createNormalDoc(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("data")String doc,Callback.CommonCallback<String> callback);

    @GET("neta/api/classroom/banner")
    @FormUrlEncoded
    void requestNewBanner(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("room")String room,Callback.CommonCallback<String> callback);

    @GET("neta/api/classroom/featured")
    @FormUrlEncoded
    void requestFreatured(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("room")String room,Callback.CommonCallback<String> callback);

    @GET("neta/api/classroom/docs")
    @FormUrlEncoded
    void requestClassList(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("index")int index,@Field("length")int len,@Field("roomId")String roomId,Callback.CommonCallback<String> callback);

    @GET("neta/api/tag/docs")
    @FormUrlEncoded
    void requestTagDocList(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("index")int index,@Field("length")int len,@Field("tagName")String tagName,Callback.CommonCallback<String> callback);

    @GET("neta/api/tag/docTops")
    @FormUrlEncoded
    void requestTopTagDocList(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("tagName")String tagName,Callback.CommonCallback<String> callback);

    @GET("neta/api/tag/docHots")
    @FormUrlEncoded
    void requestHotTagDocList(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("tagName")String tagName,Callback.CommonCallback<String> callback);

    @GET("neta/api/tag/tree")
    @FormUrlEncoded
    void requestTagTree(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("tagName")String tagName,Callback.CommonCallback<String> callback);

    @GET("neta/api/tag/load")
    @FormUrlEncoded
    void requestTagNode(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("tagId")String tagId,Callback.CommonCallback<String> callback);

    @GET("neta/api/user/myTagDocList")
    @FormUrlEncoded
    void requestMyTagDocList(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("index")int index,@Field("length")int len,@Field("userId")String tagName,Callback.CommonCallback<String> callback);

    @GET("neta/api/department/docs")
    @FormUrlEncoded
    void requestDepartmentDocList(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("index")int index,@Field("length")int len,@Field("roomId")String roomId,@Field("before")String before,Callback.CommonCallback<String> callback);
}
