package com.moemoe.lalala.network;

import android.text.TextUtils;

import org.json.JSONObject;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Haru on 2016/9/12.
 */
public class CallbackFactory<T> {
    private static CallbackFactory instance = new CallbackFactory();

    public static CallbackFactory getInstance(){return instance;}

    private CallbackFactory(){}

    public Callback<String> callback(final OnNetWorkCallback<String,String> callback){
        return new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                int code = response.code();
                String body = response.body();
                Headers headers = response.headers();
                String token = headers.get(Otaku.X_ACCESS_TOKEN);
                if(TextUtils.isEmpty(token)){
                    token = headers.get(Otaku.X_ACCESS_TOKEN.toLowerCase());
                }
                if(code == 200){
                    if(callback != null){
                        try {
                            JSONObject json = new JSONObject(body);
                            if (json.optInt("ok") == Otaku.SERVER_OK) {
                                String data = json.optString("data");
                                if(!TextUtils.isEmpty(data)){
                                    callback.success(token,data);
                                }else {
                                    callback.success(token,body);
                                }
                            }else{
                                callback.failure(body);
                            }
                        }catch (Exception e){
                            //callback.failure(body);
                        }
                    }
                }else {
                    if(callback != null)callback.failure(response.message());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (callback != null)callback.failure(t != null?t.toString():null);
            }
        };
    }
}
