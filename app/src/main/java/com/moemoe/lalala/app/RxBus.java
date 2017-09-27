package com.moemoe.lalala.app;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by yi on 2017/3/2.
 */

public class RxBus {
    private static volatile RxBus mInstance;
    private Subject<Object> mSubject;
    private HashMap<String,CompositeDisposable> mSubscriptionMap;

    private RxBus(){
        mSubject = PublishSubject.create().toSerialized();
    }

    public static RxBus getInstance(){
        if(mInstance == null){
            synchronized (RxBus.class){
                if(mInstance == null){
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    /**
     * 发送事件
     * @param o
     */
    public void post(Object o){
        mSubject.onNext(o);
    }

    /**
     * 返回指定类型的Observable实例
     * @param type
     * @param <T>
     * @return
     */
    public <T> Observable<T> toObservable(final Class<T> type){
        return mSubject.ofType(type);
    }

    /**
     * 是否已有观察者订阅
     * @return
     */
    public boolean hasObservers(){
        return mSubject.hasObservers();
    }

    /**
     * 默认订阅方法
     * @param type
     * @param next
     * @param error
     * @param <T>
     * @return
     */
    public <T>Disposable doSubscribe(Class<T> type, Consumer<T> next, Consumer<Throwable> error){
        return toObservable(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next,error);
    }

    /**
     * 保存订阅后的subscription
     * @param o
     * @param subscription
     */
    public void addSubscription(Object o,Disposable subscription){
        if(mSubscriptionMap == null){
            mSubscriptionMap = new HashMap<>();
        }
        String key = o.getClass().getName();
        if(mSubscriptionMap.get(key) != null){
            mSubscriptionMap.get(key).add(subscription);
        }else {
            CompositeDisposable compositeSubscription = new CompositeDisposable();
            compositeSubscription.add(subscription);
            mSubscriptionMap.put(key,compositeSubscription);
        }
    }

    /**
     * 取消订阅
     * @param o
     */
    public void unSubscribe(Object o){
        if(mSubscriptionMap == null) return;
        String key = o.getClass().getName();
        if(!mSubscriptionMap.containsKey(key)) return;
        if(mSubscriptionMap.get(key) != null) mSubscriptionMap.get(key).dispose();
        mSubscriptionMap.remove(key);
    }
}
