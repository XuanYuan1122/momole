package com.moemoe.lalala.network;

import com.app.common.Callback;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

import retrofit2.http.FormUrlEncoded;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Haru on 2016/4/28 0028.
 */
public interface IOtakuCommonV2 {

    @POST("otakuhouse/query")
    @FormUrlEncoded
    Call<String> requestSlotMachineUrl(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("Q") String q
            , @Field("DATA") String data);

    @POST("otakuhouse/query")
    @FormUrlEncoded
    Call<String> requestClubList(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("Q") String q
            , @Field("DATA") String data);

    @POST("otakuhouse/query")
    @FormUrlEncoded
    Call<String> requestGalTsukkomi(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("Q") String q
            , @Field("DATA") String data);

    @POST("otakuhouse/query")
    @FormUrlEncoded
    Call<String> requestPerson(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("Q") String q
            , @Field("DATA") String data);

    @POST("otakuhouse/query")
    @FormUrlEncoded
    Call<String> requestFriendClubList(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("Q") String q
            , @Field("DATA") String data);

    @POST("otakuhouse/command")
    @FormUrlEncoded
    Call<String> followClub(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("D") String d
            , @Field("X") String x
            , @Field("DATA") String data);

    @POST("otakuhouse/command")
    @FormUrlEncoded
    Call<String> unfollowClub(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("D") String d
            , @Field("X") String x
            , @Field("DATA") String data);

    @POST("otakuhouse/command")
    @FormUrlEncoded
    Call<String> modifyMyIcon(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("D") String d
            , @Field("X") String x
            , @Field("DATA") String data);

    @POST("otakuhouse/command")
    @FormUrlEncoded
    Call<String> modifyAll(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("D") String d
            , @Field("X") String x
            , @Field("DATA") String data);

    @POST("otakuhouse/command")
    @FormUrlEncoded
    Call<String> modifyRecommandDoc(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("D") String d
            , @Field("X") String x
            , @Field("DATA") String data);

    @GET("app/version/check/{platform}/{version}")
    Call<String> checkVersion(@Path("platform") String platform
            , @Path("version") int version);

    @GET("neta/api/wall/blocks")
    Call<String> getWallBlocks(@Query("index") int index
            , @Query("length") int length);

    @PUT("neta/api/report")
    Call<String> report(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Query("target") String target
            , @Query("id") String id
            , @Query("rType") String type
            , @Query("reason") String reason);

    @GET("neta/api/dustbin/status")
    Call<String> dustState(@Header(Otaku.X_ACCESS_TOKEN) String head);

    @PUT("neta/api/dustbin/randOne")
    Call<String> getDust(@Header(Otaku.X_ACCESS_TOKEN) String head);

    @DELETE("neta/api/dustbin/rollback")
    Call<String> cancelDust(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Query("item") String id);

    @PUT("neta/api/dustbin/set")
    @FormUrlEncoded
    Call<String> sendDust(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("title") String title
            , @Field("content") String content);

    @GET("neta/api/dustbin/gots")
    Call<String> gotDustList(@Header(Otaku.X_ACCESS_TOKEN) String head
            ,@Query("index")int index
            ,@Query("length")int length);

    @GET("neta/api/dustbin/sots")
    Call<String> sotDustList(@Header(Otaku.X_ACCESS_TOKEN) String head
            ,@Query("index")int index
            ,@Query("length")int length);

    @PUT("neta/api/dustbin/randList")
    @FormUrlEncoded
    Call<String> getDustList(@Header(Otaku.X_ACCESS_TOKEN) String head
            ,@Field("size")int size);

    @PUT("neta/api/dustbin/fun")
    @FormUrlEncoded
    Call<String> funDust(@Header(Otaku.X_ACCESS_TOKEN) String head
            ,@Field("id")String id);

    @PUT("neta/api/dustbin/shit")
    @FormUrlEncoded
    Call<String> shitDust(@Header(Otaku.X_ACCESS_TOKEN) String head
            ,@Field("id")String id);

    @GET("neta/api/dustbin/top/yesterday")
    Call<String> top3DustList(@Header(Otaku.X_ACCESS_TOKEN) String head);


}
