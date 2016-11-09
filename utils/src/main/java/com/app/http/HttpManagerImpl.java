package com.app.http;

import android.text.TextUtils;

import com.app.HttpManager;
import com.app.Utils;
import com.app.common.Callback;
import com.app.common.util.CommonUtils;
import com.app.http.app.SimpleParamsBuilder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public final class HttpManagerImpl implements HttpManager{

    private static final Object lock = new Object();
    private static HttpManagerImpl instance;
    private static final HashMap<String, HttpTask<?>> DOWNLOAD_TASK = new HashMap<String, HttpTask<?>>(1);
    private final Map<Method,RequestParams> serviceMethodCache = new LinkedHashMap<>();
    private String baseUrl;

    private HttpManagerImpl() {
    }

    public static void registerInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new HttpManagerImpl();
                }
            }
        }
        Utils.Control.setHttpManager(instance);
    }

    @Override
    public void setBaseUrl(String url) {
        baseUrl = url;
    }

    @Override
    public <T> Callback.Cancelable get(RequestParams entity, Callback.CommonCallback<T> callback) {
        return request(HttpMethod.GET, entity, callback);
    }

    @Override
    public <T> Callback.Cancelable post(RequestParams entity, Callback.CommonCallback<T> callback) {
        return request(HttpMethod.POST, entity, callback);
    }

    @Override
    public <T> Callback.Cancelable request(HttpMethod method, RequestParams entity, Callback.CommonCallback<T> callback) {
        final String saveFilePath = entity.getSaveFilePath();
        if (!TextUtils.isEmpty(saveFilePath)) {
            HttpTask<?> task = DOWNLOAD_TASK.get(saveFilePath);
            if (task != null) {
                task.cancel();
                task = null;
            }
        }
        entity.setMethod(method);
        Callback.Cancelable cancelable = null;
        if (callback instanceof Callback.Cancelable) {
            cancelable = (Callback.Cancelable) callback;
        }
        HttpTask<T> task = null;
        if (!TextUtils.isEmpty(saveFilePath)) {
            task = new HttpTask<T>(entity, cancelable, callback) {
                @Override
                protected void onFinished() {
                    super.onFinished();
                    synchronized (DOWNLOAD_TASK) {
                        HttpTask<?> task = DOWNLOAD_TASK.get(saveFilePath);
                        if (task == this) {
                            DOWNLOAD_TASK.remove(saveFilePath);
                        }
                    }
                }
            };
            synchronized (DOWNLOAD_TASK) {
                DOWNLOAD_TASK.put(saveFilePath, task);
            }
        } else {
            task = new HttpTask<T>(entity, cancelable, callback);
        }
        return Utils.task().start(task);
    }

    @Override
    public <T> T getSync(RequestParams entity, Class<T> resultType) throws Throwable {
        return requestSync(HttpMethod.GET, entity, resultType);
    }

    @Override
    public <T> T postSync(RequestParams entity, Class<T> resultType) throws Throwable {
        return requestSync(HttpMethod.POST, entity, resultType);
    }

    @Override
    public <T> T requestSync(HttpMethod method, RequestParams entity, Class<T> resultType) throws Throwable {
        entity.setMethod(method);
        SyncCallback<T> callback = new SyncCallback<T>(resultType);
        HttpTask<T> task = new HttpTask<T>(entity, null, callback);
        return Utils.task().startSync(task);
    }

    @Override
    public <T> T create(Class<T> service) {
        CommonUtils.validateServiceInterface(service);
        eagerlyValidateMethods(service);

        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //RequestParams params = loadServiceMethod(method);
                        RequestParams params = new RequestParams(baseUrl);
                        SimpleParamsBuilder builder = new SimpleParamsBuilder(method);
                        builder.setArgs(args);
                        params.setCacheMaxAge(1);
                        builder.buildMethod();
                        params.setBuilder(builder);
                        Callback.CommonCallback<T> callback = (Callback.CommonCallback<T>) args[args.length - 1];
                        Utils.http().request(builder.getHttpMethod(),params,callback);
                        return null;
                    }
                });
    }

    private void eagerlyValidateMethods(Class<?> service){
        for(Method method : service.getDeclaredMethods()){
            loadServiceMethod(method);
        }
    }

    RequestParams loadServiceMethod(Method method){
        RequestParams result;
        synchronized (serviceMethodCache){
            result = serviceMethodCache.get(method);
            if(result == null){
                result = new RequestParams(baseUrl);
                result.setCacheMaxAge(1);
                serviceMethodCache.put(method,result);
            }
        }
        return result;
    }

    private class SyncCallback<T> implements Callback.TypedCallback<T> {

        private final Class<T> resultType;

        public SyncCallback(Class<T> resultType) {
            this.resultType = resultType;
        }

        @Override
        public Type getResultType() {
            return resultType;
        }

        @Override
        public void onSuccess(T result) {

        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {

        }

        @Override
        public void onCancelled(CancelledException cex) {

        }

        @Override
        public void onFinished() {

        }
    }


}
