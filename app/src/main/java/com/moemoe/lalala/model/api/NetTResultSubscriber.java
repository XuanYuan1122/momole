package com.moemoe.lalala.model.api;

import com.moemoe.lalala.model.entity.ApiResult;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by yi on 2016/11/29.
 */

public abstract class NetTResultSubscriber<T> implements Observer<T> {

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
        onSuccess();
    }

    @Override
    public void onError(Throwable e) {
        if(mDisposable != null && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
        onFail(e);
    }

    @Override
    public void onNext(T apiResult) {
        onLoading(apiResult);
    }

    public abstract void onSuccess();

    public abstract void onLoading(T res);

    public abstract void onFail(Throwable e);
}
