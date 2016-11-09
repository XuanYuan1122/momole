package com.moemoe.lalala.callback;

import android.content.Context;
import android.text.TextUtils;

import com.app.common.Callback;
import com.app.http.request.UriRequest;
import com.moemoe.lalala.BaseActivity;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.PreferenceManager;

import org.json.JSONObject;

/**
 * Created by Haru on 2016/5/16 0016.
 */
public class BaseCommonCallback implements Callback.InterceptCallback<String> {

    private InterceptCallback callback;
    private Context context;

    public BaseCommonCallback(InterceptCallback<String> callback,Context context){
        this.callback = callback;
        this.context = context;
    }

    @Override
    public void onSuccess(String result) {
        try {
            JSONObject json = new JSONObject(result);
            if(json.optInt("ok") == Otaku.SERVER_OK){
                callback.onSuccess(result);
            }else{
                String err = json.optString("error_code");
                if(TextUtils.isEmpty(err)){
                    err = json.optString("data");
                }
                if(!TextUtils.isEmpty(err) && err.contains("TOKEN")){
                    String uuid = PreferenceManager.getInstance(context).getUUid();
                    if(!TextUtils.isEmpty(uuid)){
                        ((BaseActivity) context).tryLoginFirst(null);
                    }
                }
                callback.onError(null,true);
            }
        }catch (Exception e){

        }
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        try {
            String error = ex.toString();
            String res = error.split("result:")[1];
            JSONObject json = new JSONObject(res);
            String err = json.optString("data");
            if(!TextUtils.isEmpty(err) && err.contains("TOKEN")){
                String uuid = PreferenceManager.getInstance(context).getUUid();
                if(!TextUtils.isEmpty(uuid)){
                    ((BaseActivity) context).tryLoginFirst(null);
                }
            }
            callback.onError(ex,true);
        }catch (Exception e){
            callback.onError(ex,isOnCallback);
        }finally {

        }
    }

    @Override
    public void onCancelled(CancelledException cex) {
        callback.onCancelled(cex);
    }

    @Override
    public void onFinished() {
        callback.onFinished();
    }

    @Override
    public void beforeRequest(UriRequest request) throws Throwable {
        callback.beforeRequest(request);
    }

    @Override
    public void afterRequest(UriRequest request) throws Throwable {
        callback.afterRequest(request);
    }
}
