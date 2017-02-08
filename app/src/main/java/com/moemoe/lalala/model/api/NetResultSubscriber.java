package com.moemoe.lalala.model.api;

import com.moemoe.lalala.model.entity.ApiResult;

import rx.Subscriber;

/**
 * Created by yi on 2016/11/28.
 */

public abstract class NetResultSubscriber<T> extends Subscriber<ApiResult<T>> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        onFail(-1,"");
    }

    @Override
    public void onNext(ApiResult<T> tApiResult) {
        if (tApiResult.getState() == 200) {
            onSuccess(tApiResult.getData());
        }else{
            onFail(tApiResult.getState(),tApiResult.getMessage());
        }
    }

    public abstract void onSuccess(T t);

    public abstract void onFail(int code,String msg);
}
