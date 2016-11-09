package com.moemoe.lalala.network;

import com.moemoe.lalala.data.ApiResult;
import com.moemoe.lalala.data.AppUpdateInfo;
import com.moemoe.lalala.data.DonationInfoBean;
import com.moemoe.lalala.data.DustImageBean;
import com.moemoe.lalala.data.DustTextBean;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
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
    Call<String> requestPerson(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("Q") String q
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

    @GET("api/app/check")
    Call<ApiResult<AppUpdateInfo>> checkVersion(@Query("platform") String platform
            , @Query("version") int version);

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

    @POST("api/dustbin/addText")
    Call<ApiResult> sendDust(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Body DustTextBean bean);

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

    @PUT("api/dustbin/funText/{id}")
    Call<ApiResult> funDust(@Header(Otaku.X_ACCESS_TOKEN) String head
            ,@Path("id") String id);

    @PUT("neta/api/dustbin/shit")
    @FormUrlEncoded
    Call<String> shitDust(@Header(Otaku.X_ACCESS_TOKEN) String head
            ,@Field("id")String id);

    @GET("api/dustbin/imageTop3")
    Call<String> top3DustList(@Header(Otaku.X_ACCESS_TOKEN) String head);

    @POST("api/dustbin/addImage")
    Call<ApiResult> sendImgDust(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Body DustImageBean imageBean);

    @GET("neta/api/dustbin_image/gots")
    Call<String> gotImgDustList(@Header(Otaku.X_ACCESS_TOKEN) String head
            ,@Query("index")int index
            ,@Query("length")int length);

    @GET("neta/api/dustbin_image/sots")
    Call<String> sotImgDustList(@Header(Otaku.X_ACCESS_TOKEN) String head
            ,@Query("index")int index
            ,@Query("length")int length);

    @PUT("neta/api/dustbin_image/randList")
    @FormUrlEncoded
    Call<String> getImgDustList(@Header(Otaku.X_ACCESS_TOKEN) String head
            ,@Field("size")int size);

    @PUT("api/dustbin/funImage/{id}")
    Call<ApiResult> funImgDust(@Header(Otaku.X_ACCESS_TOKEN) String head
            ,@Path("id") String id);

    @PUT("neta/api/dustbin_image/shit")
    @FormUrlEncoded
    Call<String> shitImgDust(@Header(Otaku.X_ACCESS_TOKEN) String head
            ,@Field("id")String id);

    @GET("neta/api/dustbin_image/top/yesterday")
    Call<String> top3ImgDustList(@Header(Otaku.X_ACCESS_TOKEN) String head);

    @GET("api/coinbox/get")
    Call<ApiResult<DonationInfoBean>> getDonationInfo(@Header(Otaku.X_ACCESS_TOKEN)String head);

    @GET("api/coinbox/getRankList")
    Call<ApiResult<DonationInfoBean>> getBookDonationInfo(@Header(Otaku.X_ACCESS_TOKEN)String head);

    @POST("api/coinbox/give/{num}")
    Call<ApiResult> donationCoin(@Header(Otaku.X_ACCESS_TOKEN)String head, @Path("num")int coin);
}
