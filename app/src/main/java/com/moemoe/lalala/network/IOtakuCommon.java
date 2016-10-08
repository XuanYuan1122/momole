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
 * Created by Haru on 2016/4/28 0028.
 */
public interface IOtakuCommon {

    @POST("otakuhouse/query")
    @FormUrlEncoded
    void requestSlotMachineUrl(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("Q") String q,@Field("DATA")String data ,Callback.CommonCallback<String> callback);

    @POST("otakuhouse/query")
    @FormUrlEncoded
    void requestClubList(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("Q") String q,@Field("DATA")String data ,Callback.CommonCallback<String> callback);

    @POST("otakuhouse/query")
    @FormUrlEncoded
    void requestGalTsukkomi(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("Q") String q,@Field("DATA")String data ,Callback.CommonCallback<String> callback);

    @POST("otakuhouse/query")
    @FormUrlEncoded
    void requestPerson(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("Q") String q,@Field("DATA")String data ,Callback.CommonCallback<String> callback);

    @POST("otakuhouse/query")
    @FormUrlEncoded
    void requestFriendClubList(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("Q") String q,@Field("DATA")String data ,Callback.CommonCallback<String> callback);

    @POST("otakuhouse/command")
    @FormUrlEncoded
    void followClub(@HEAD(Otaku.X_ACCESS_TOKEN) String head, @Field("D") String d, @Field("X") String x, @Field("DATA") String data, Callback.CommonCallback<String> callback);

    @POST("otakuhouse/command")
    @FormUrlEncoded
    void unfollowClub(@HEAD(Otaku.X_ACCESS_TOKEN) String head, @Field("D") String d, @Field("X") String x, @Field("DATA") String data, Callback.CommonCallback<String> callback);

    @POST("otakuhouse/command")
    @FormUrlEncoded
    void modifyMyIcon(@HEAD(Otaku.X_ACCESS_TOKEN) String head, @Field("D") String d, @Field("X") String x, @Field("DATA") String data, Callback.CommonCallback<String> callback);

    @POST("otakuhouse/command")
    @FormUrlEncoded
    void modifyAll(@HEAD(Otaku.X_ACCESS_TOKEN) String head, @Field("D") String d, @Field("X") String x, @Field("DATA") String data, Callback.CommonCallback<String> callback);

    @POST("otakuhouse/command")
    @FormUrlEncoded
    void modifyRecommandDoc(@HEAD(Otaku.X_ACCESS_TOKEN) String head, @Field("D") String d, @Field("X") String x, @Field("DATA") String data, Callback.CommonCallback<String> callback);

    @GET("app/version/check/{platform}/{version}")
    void checkVersion(@Path("platform")String platform,@Path("version")int version,Callback.CommonCallback<String> callback);

    @GET("neta/api/wall/blocks")
    @FormUrlEncoded
    void getWallBlocks(@Field("index")int index,@Field("length")int length,Callback.CommonCallback<String> callback);

    @PUT("neta/api/report")
    @FormUrlEncoded
    void report(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("target")String target,@Field("id")String id,@Field("rType")String type,@Field("reason")String reason,Callback.CommonCallback<String> callback);

    @GET("neta/api/dustbin/status")
    void dustState(@HEAD(Otaku.X_ACCESS_TOKEN) String head,Callback.CommonCallback<String> callback);

    @PUT("neta/api/dustbin/randOne")
    void getDust(@HEAD(Otaku.X_ACCESS_TOKEN) String head,Callback.CommonCallback<String> callback);

    @DELETE("neta/api/dustbin/rollback")
    @FormUrlEncoded
    void cancelDust(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("item")String id,Callback.CommonCallback<String> callback);

    @PUT("neta/api/dustbin/set")
    @FormUrlEncoded
    void sendDust(@HEAD(Otaku.X_ACCESS_TOKEN) String head,@Field("title")String title,@Field("content")String content,Callback.CommonCallback<String> callback);
}
