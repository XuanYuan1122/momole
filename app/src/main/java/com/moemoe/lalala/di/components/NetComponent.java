package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.NetModule;
import com.moemoe.lalala.model.api.ApiService;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by yi on 2016/11/27.
 */
@Component(modules = NetModule.class)
@Singleton
public interface NetComponent {
    ApiService getApiService();
    OkHttpClient getOkhttp();
    Retrofit getRetrofit();
}
