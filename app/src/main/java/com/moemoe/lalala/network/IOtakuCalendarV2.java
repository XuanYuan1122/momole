package com.moemoe.lalala.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public interface IOtakuCalendarV2 {

    @GET("neta/api/calendar/{day}")
    Call<String> requestCalendarOneDay(@Header(Otaku.X_ACCESS_TOKEN) String token
            , @Path("day") String day);

    @GET("neta/api/rss/mylist/{day}")
    Call<String> requestRss(@Header(Otaku.X_ACCESS_TOKEN) String token
            , @Path("day") String day
            , @Query("index") int index
            , @Query("length") int l);

    @GET("neta/api/calendar/{day}/ui/{id}")
    Call<String> refreshUi(@Header(Otaku.X_ACCESS_TOKEN) String token
            , @Path("day") String day
            , @Path("id") String id
            , @Query("index") int index
            , @Query("length") int l);

    @GET("neta/api/calendar/featured/{dayRange}")
    Call<String> requestFeatured(@Header(Otaku.X_ACCESS_TOKEN) String token
            , @Path("dayRange") String dayRange);
}
