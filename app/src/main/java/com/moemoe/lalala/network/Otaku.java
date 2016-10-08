package com.moemoe.lalala.network;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public class Otaku {

    public static String URL_QINIU = "http://7xl0tq.com2.z0.glb.qiniucdn.com/";
    public static String URL_SHARE = "http://s.moemoe.la/";
    public static String REGISTER_PRICACE_URL = "http://7xkvyf.com2.z0.glb.qiniucdn.com/nonresponsibility.html";
    public static String LEVEL_DETAILS_URL = "http://7xl0tq.com2.z0.glb.qiniucdn.com/app/html/integral-v2.html";
    public static String SHARE_BASE = "http://neta.moemoe.la/neta/share/doc/";
    public static String SHARE_BASE_DEBUG = "http://neta1.moemoe.la/neta/share/doc/";
    public static final String X_ACCESS_TOKEN = "X-ACCESS-TOKEN";
    public static final String SERVER_Y = "Y";
    public static final String SERVER_N = "N";
    public static final int SERVER_OK = 1;
    public static final int LENGTH = 10;
    public static final int DOUBLE_LENGTH = 20;
    public static String sLotteryUrl = null;

    private static Otaku instance = null;
    Retrofit retrofit;

    public static Otaku getInstance(){return instance;}

    public static void setup(String baseUrl,File cacheDirectory){
        if(instance == null){
            instance = new Otaku(baseUrl,cacheDirectory);
        }
    }

    private Otaku(String baseUrl
            ,File cacheDirectory){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if(cacheDirectory != null){
            Cache responseCache = new Cache(cacheDirectory,1024 * 1024 * 16);
            builder.cache(responseCache);
        }
        OkHttpClient client = builder.build();
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();
    }

    public static OtakuCalendarV2 getCalendarV2(){return OtakuCalendarV2.INSTANCE;}
    public static OtakuAccountV2 getAccountV2(){return OtakuAccountV2.INSTANCE;}
    public static OtakuCommonV2 getCommonV2(){return OtakuCommonV2.INSTANCE;}
    public static OtakuDocV2 getDocV2(){return OtakuDocV2.INSTANCE;}
}
