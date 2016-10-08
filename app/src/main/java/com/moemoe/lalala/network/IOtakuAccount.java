package com.moemoe.lalala.network;

import com.app.annotation.Field;
import com.app.annotation.FormUrlEncoded;
import com.app.annotation.GET;
import com.app.annotation.HEAD;
import com.app.annotation.HEADER;
import com.app.annotation.POST;
import com.app.common.Callback;
import com.app.http.app.InterceptRequestListener;
import com.moemoe.lalala.utils.StringUtils;

/**
 * Created by Haru on 2016/4/28 0028.
 */
public interface IOtakuAccount {

    @POST("otakuhouse/id/login")//v1版本，后续要更新掉
    @FormUrlEncoded
    void loginThird(@Field("DATA")String data,Callback.CommonCallback<String> callback);

    @POST("otakuhouse/id/login")//v1版本，后续要更新掉
    @FormUrlEncoded
    void login(@Field("DATA")String data,Callback.CommonCallback<String> callback);

    @POST("otakuhouse/id/logout")
    @FormUrlEncoded
    void logout(Callback.CommonCallback<String> callback);

    @POST("otakuhouse/id/register_validate_mobile")
    @FormUrlEncoded
    void register(@Field("DATA")String data,Callback.CommonCallback<String> callback);

    @POST("otakuhouse/id/register_mobile")
    @FormUrlEncoded
    void phoneRegister(@Field("DATA")String data,Callback.CommonCallback<String> callback);

    @POST("otakuhouse/id/check_vcode")
    @FormUrlEncoded
    void checkVCode(@HEAD(Otaku.X_ACCESS_TOKEN)String header,@Field("DATA")String data,Callback.CommonCallback<String> callback);

    @POST("otakuhouse/id/password_forgot_send_mobile_code")
    @FormUrlEncoded
    void requestCode4ResetPwd(@Field("DATA")String data,Callback.CommonCallback<String> callback);

    @POST("otakuhouse/id/password_change")
    @FormUrlEncoded
    void changePassword(@HEAD(Otaku.X_ACCESS_TOKEN)String header,@Field("DATA")String data,Callback.CommonCallback<String> callback);

    @POST("otakuhouse/id/password_forgot_change")
    @FormUrlEncoded
    void resetPwdByCode(@HEAD(Otaku.X_ACCESS_TOKEN)String header,@Field("DATA")String data,Callback.CommonCallback<String> callback);

    @POST("neta/api/qn/upload")
    @FormUrlEncoded
    void requestQnFileKey(@HEAD(Otaku.X_ACCESS_TOKEN)String header,@Field("fileSuffix")String suffix,Callback.CommonCallback<String> callback);

    @POST("otakuhouse/query")
    @FormUrlEncoded
    void requestSelfData(@HEAD(Otaku.X_ACCESS_TOKEN)String header,@Field("Q")String q,Callback.CommonCallback<String> callback);

    @GET("neta/api/reply/list")
    @FormUrlEncoded
    void requestCommentFromOther(@HEAD(Otaku.X_ACCESS_TOKEN)String token,@Field("index")int index,@Field("length")int len,Callback.CommonCallback<String> callback);
}
