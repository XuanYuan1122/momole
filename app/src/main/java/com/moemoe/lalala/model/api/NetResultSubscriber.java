package com.moemoe.lalala.model.api;

import com.moemoe.lalala.model.entity.ApiResult;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * 网络请求回调
 * Created by yi on 2016/11/28.
 */

public abstract class NetResultSubscriber<T> implements Observer<ApiResult<T>> {

    private Disposable mDisposable;

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        mDisposable = d;
    }

    @Override
    public void onComplete() {
        if(mDisposable != null && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }

    @Override
    public void onError(Throwable e) {
        onFail(-1,"");
        e.printStackTrace();
        if(mDisposable != null && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
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
