package com.app;

import com.app.common.Callback;
import com.app.http.HttpMethod;
import com.app.http.RequestParams;

/**
 * Created by Haru on 2016/4/12 0012.
 * http请求接口
 */
public interface HttpManager {

    void setBaseUrl(String url);
    /**
     * 异步GET请求
     *
     * @param entity
     * @param callback
     * @param <T>
     * @return
     */
    <T> Callback.Cancelable get(RequestParams entity, Callback.CommonCallback<T> callback);

    /**
     * 异步POST请求
     *
     * @param entity
     * @param callback
     * @param <T>
     * @return
     */
    <T> Callback.Cancelable post(RequestParams entity, Callback.CommonCallback<T> callback);

    /**
     * 异步请求
     *
     * @param method
     * @param entity
     * @param callback
     * @param <T>
     * @return
     */
    <T> Callback.Cancelable request(HttpMethod method, RequestParams entity, Callback.CommonCallback<T> callback);


    /**
     * 同步GET请求
     *
     * @param entity
     * @param resultType
     * @param <T>
     * @return
     * @throws Throwable
     */
    <T> T getSync(RequestParams entity, Class<T> resultType) throws Throwable;

    /**
     * 同步POST请求
     *
     * @param entity
     * @param resultType
     * @param <T>
     * @return
     * @throws Throwable
     */
    <T> T postSync(RequestParams entity, Class<T> resultType) throws Throwable;

    /**
     * 同步请求
     *
     * @param method
     * @param entity
     * @param resultType
     * @param <T>
     * @return
     * @throws Throwable
     */
    <T> T requestSync(HttpMethod method, RequestParams entity, Class<T> resultType) throws Throwable;

    <T> T create(final Class<T> service);
}
