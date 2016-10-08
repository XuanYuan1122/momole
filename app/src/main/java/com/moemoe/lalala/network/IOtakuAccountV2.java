package com.moemoe.lalala.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

import retrofit2.http.FormUrlEncoded;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by Haru on 2016/9/12.
 */
public interface IOtakuAccountV2 {

    @POST("otakuhouse/id/login")//v1版本，后续要更新掉
    @FormUrlEncoded
    Call<String> loginThird(@Field("DATA")String data);

    @POST("otakuhouse/id/login")//v1版本，后续要更新掉
    @FormUrlEncoded
    Call<String> login(@Field("DATA")String data);

    @POST("otakuhouse/id/logout")
    Call<String> logout();

    @POST("otakuhouse/id/register_validate_mobile")
    @FormUrlEncoded
    Call<String> register(@Field("DATA")String data);

    @POST("otakuhouse/id/register_mobile")
    @FormUrlEncoded
    Call<String> phoneRegister(@Field("DATA")String data);

    @POST("otakuhouse/id/check_vcode")
    @FormUrlEncoded
    Call<String> checkVCode(@Header(Otaku.X_ACCESS_TOKEN)String header
            ,@Field("DATA")String data);

    @POST("otakuhouse/id/password_forgot_send_mobile_code")
    @FormUrlEncoded
    Call<String> requestCode4ResetPwd(@Field("DATA")String data);

    @POST("otakuhouse/id/password_change")
    @FormUrlEncoded
    Call<String> changePassword(@Header(Otaku.X_ACCESS_TOKEN)String header
            ,@Field("DATA")String data);

    @POST("otakuhouse/id/password_forgot_change")
    @FormUrlEncoded
    Call<String> resetPwdByCode(@Header(Otaku.X_ACCESS_TOKEN)String header
            ,@Field("DATA")String data);

    @POST("neta/api/qn/upload")
    @FormUrlEncoded
    Call<String> requestQnFileKey(@Header(Otaku.X_ACCESS_TOKEN)String header
            ,@Field("fileSuffix")String suffix);

    @POST("otakuhouse/query")
    @FormUrlEncoded
    Call<String> requestSelfData(@Header(Otaku.X_ACCESS_TOKEN)String header
            ,@Field("Q")String q);

    @GET("neta/api/reply/list")
    Call<String> requestCommentFromOther(@Header(Otaku.X_ACCESS_TOKEN)String token
            ,@Query("index")int index
            ,@Query("length")int len);

    @GET("neta/api/user/day/sign")
    Call<String> checkSignToday(@Header(Otaku.X_ACCESS_TOKEN)String token);

    @PUT("neta/api/user/day/sign")
    Call<String> signToday(@Header(Otaku.X_ACCESS_TOKEN)String token);
}
