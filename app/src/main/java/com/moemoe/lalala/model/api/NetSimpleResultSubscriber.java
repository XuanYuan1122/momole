package com.moemoe.lalala.model.api;

import com.moemoe.lalala.model.entity.ApiResult;

import rx.Subscriber;

/**
 * Created by yi on 2016/11/29.
 */

public abstract class NetSimpleResultSubscriber extends Subscriber<ApiResult> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        onFail(-1,"");
    }

    @Override
    public void onNext(ApiResult apiResult) {
        if (apiResult.getState() == 200) {
            onSuccess();
        }else{
            onFail(apiResult.getState(),apiResult.getMessage());
        }
    }

    public abstract void onSuccess();

    public abstract void onFail(int code,String msg);
}
