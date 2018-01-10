package com.moemoe.lalala.di.modules;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.moemoe.lalala.BuildConfig;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.retrofit.RetrofitUrlManager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * Created by yi on 2016/11/27.
 */
@Module
public class NetModule {

    private Context mContext;

    public NetModule(Context context){
        this.mContext = context;
    }

    @Provides
    public Context provideContext(){
        return mContext;
    }

    @Provides
    @Singleton
    public ApiService provideApiService(Retrofit retrofit){
        return retrofit.create(ApiService.class);
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttpClient){

        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BuildConfig.DEBUG ? TextUtils.isEmpty(PreferenceUtils.getIp(mContext))? BuildConfig.BASE_URL : PreferenceUtils.getIp(mContext): BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(Context context){
        File cache =  MoeMoeApplication.getInstance().getCacheDir();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("X-APP-PLATFORM", AppSetting.CHANNEL)
                                .addHeader("X-APP-VERSION",AppSetting.VERSION_CODE+"")
                                .addHeader("X-ACCESS-TOKEN", PreferenceUtils.getToken())
                                .addHeader("X-APP-TYPE", android.os.Build.MODEL)
                                .build();
                        return chain.proceed(request);
                    }
                });
        if(cache != null){
            Cache responseCache = new Cache(cache,1024 * 1024 * 10);
            builder.cache(responseCache);
        }
        return RetrofitUrlManager.getInstance().with(builder).build();
    }
}
