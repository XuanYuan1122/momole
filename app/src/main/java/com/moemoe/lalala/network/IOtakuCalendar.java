package com.moemoe.lalala.network;

import com.app.annotation.Field;
import com.app.annotation.FormUrlEncoded;
import com.app.annotation.GET;
import com.app.annotation.HEAD;
import com.app.annotation.Path;
import com.app.common.Callback;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public interface IOtakuCalendar {

    @GET("neta/api/calendar/{day}")
    @FormUrlEncoded
    void requestCalendarOneDay(@HEAD(Otaku.X_ACCESS_TOKEN)String token,@Path("day")String day,Callback.CommonCallback<String> callback);

    @GET("neta/api/rss/mylist/{day}")
    @FormUrlEncoded
    void requestRss(@HEAD(Otaku.X_ACCESS_TOKEN)String token,@Path("day")String day,@Field("index")int index,@Field("length")int l,Callback.CommonCallback<String> callback);

    @GET("neta/api/calendar/{day}/ui/{id}")
    @FormUrlEncoded
    void refreshUi(@HEAD(Otaku.X_ACCESS_TOKEN)String token,@Path("day")String day,@Path("id")String id,@Field("index")int index,@Field("length")int l,Callback.CommonCallback<String> callback);

    @GET("neta/api/calendar/featured/{dayRange}")
    @FormUrlEncoded
    void requestFeatured(@HEAD(Otaku.X_ACCESS_TOKEN)String token,@Path("dayRange")String dayRange,Callback.CommonCallback<String> callback);
}
