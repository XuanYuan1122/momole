package com.moemoe.lalala.model.api;

import com.moemoe.lalala.model.entity.ApiResult;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 *
 * Created by yi on 2016/11/29.
 */

public abstract class NetSimpleResultSubscriber implements Observer<ApiResult> {

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
        if(mDisposable != null && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
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
