package com.moemoe.lalala.network;

import com.moemoe.lalala.data.ApiResult;
import com.moemoe.lalala.data.AuthorInfo;
import com.moemoe.lalala.data.LoginBean;
import com.moemoe.lalala.data.LoginResultBean;
import com.moemoe.lalala.data.RegisterBean;
import com.moemoe.lalala.data.ReplyBean;
import com.moemoe.lalala.data.SignBean;
import com.moemoe.lalala.data.ThirdLoginBean;
import com.moemoe.lalala.data.UploadBean;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Haru on 2016/9/12.
 */
interface IOtakuAccountV2 {

    @POST("api/user/loginOpenId")
    Call<ApiResult<LoginResultBean>> loginThird(@Body ThirdLoginBean data);

    @POST("api/user/loginMobile")
    Call<ApiResult<LoginResultBean>> login(@Body LoginBean bean);

    @PUT("api/user/logout")
    Call<ApiResult> logout();

    @POST("api/code/sendRegister")
    Call<ApiResult> requestRegisterCode(@Query("mobile")String data);

    @POST("api/user/register")
    Call<ApiResult> phoneRegister(@Body RegisterBean bean);

    @POST("api/code/check")
    Call<ApiResult> checkVCode(@Body RegisterBean bean);

    @POST("api/code/sendForget")
    Call<ApiResult> requestCode4ResetPwd(@Query("mobile") String data);

    @POST("api/user/chagePwd")
    Call<ApiResult> changePassword(@Body RegisterBean bean);

    @POST("api/user/chageForgetPwd")
    Call<ApiResult> resetPwdByCode(@Body RegisterBean bean);

    @POST("api/upload/{suffix}")
    Call<ApiResult<UploadBean>> requestQnFileKey(@Path("suffix") String suffix);

    @GET("api/user/{userId}/info")
    Call<ApiResult<AuthorInfo>> requestUserInfo( @Path("userId") String userId);

    @GET("api/user/getReply")
    Call<ApiResult<ArrayList<ReplyBean>>> requestCommentFromOther( @Query("index")int index
            , @Query("size")int len);

    @GET("api/user/sign")
    Call<ApiResult<SignBean>> checkSignToday();

    @PUT("api/user/sign")
    Call<ApiResult<SignBean>> signToday();
}
