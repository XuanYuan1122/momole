package com.moemoe.lalala.network;

import android.content.Context;

import retrofit2.Call;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public enum OtakuCalendarV2 {
    INSTANCE;

    private IOtakuCalendarV2 service = Otaku.getInstance().retrofit.create(IOtakuCalendarV2.class);

    public Call<String> requestCalendarOneDay(String token
            ,String id){
        return service.requestCalendarOneDay(token,id);
    }

    public Call<String> requestRss(String token
            ,String day
            ,int index
            ,int total){
        return service.requestRss(token, day, index,total);
    }

    public Call<String> refreshUi(String token
            ,String day
            ,String id
            ,int index
            ,int len){
        return service.refreshUi(token, day, id, index,len);
    }

    public Call<String> requestFeatured(String token
            ,String dayRange){
        return service.requestFeatured(token,dayRange);
    }


}
