package com.moemoe.lalala.network;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

import retrofit2.http.FormUrlEncoded;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Haru on 2016/5/1 0001.
 */
public interface IOtakuDocV2 {

    @POST("otakuhouse/query")
    @FormUrlEncoded
    Call<String> request5Club(@Header(Otaku.X_ACCESS_TOKEN) String token
            , @Field("Q") String q
            , @Field("DATA") String data);

    @POST("neta/api/doc/tag")
    @FormUrlEncoded
    Call<String> createNewTag(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("docId") String d
            , @Field("tag") String x);

    @POST("neta/api/doc/tag/dislike")
    @FormUrlEncoded
    Call<String> dislikeNewTag(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("docId") String d
            , @Field("tagId") String x);

    @POST("neta/api/doc/tag/like")
    @FormUrlEncoded
    Call<String> likeNewTag(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("docId") String d
            , @Field("tagId") String x);

    @POST("neta/api/doc/comment")
    @FormUrlEncoded
    Call<String> sendNewComment(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("docId") String docId
            , @Field("content") String x
            , @Field("toUserId") String t
            , @Field("images") String images);

    @DELETE("neta/api/doc/comment/{id}")
    Call<String> deleteNewComment(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Path("id") String id);

    @GET("neta/api/doc/{id}")
    Call<String> requestNewDoc(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Path("id") String id);

    @GET("neta/api/doc/{id}/content")
    Call<String> requestNewDocContent(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Path("id") String id);

    @GET("neta/api/doc/{id}/tags")
    Call<String> requestNewDocTag(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Path("id") String id);

    @PUT("neta/api/doc/{id}/pay")
    Call<String> requestDocHidePath(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Path("id") String id);

    @GET("neta/api/doc/{docId}/comments")
    Call<String> requestNewComment(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Path("docId") String id
            , @Query("index") int index
            , @Query("length") int l);

    @POST("neta/api/rss/subscribe")
    @FormUrlEncoded
    Call<String> createRss(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("rssId") String rssId);

    @POST("neta/api/rss/cancel")
    @FormUrlEncoded
    Call<String> cancelRss(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Field("rssId") String rssId);

    @GET("neta/api/calendar/ui/{ui}")
    Call<String> requestNewDocList(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Path("ui") String u
            , @Query("index") int index
            , @Query("length") int len);

    @PUT("neta/api/tagDoc")
    Call<String> createNormalDoc(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Query("data") String doc);

    @GET("neta/api/classroom/banner")
    Call<String> requestNewBanner(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Query("room") String room);

    @GET("neta/api/classroom/featured")
    Call<String> requestFreatured(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Query("room") String room);

    @GET("neta/api/classroom/docs")
    Call<String> requestClassList(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Query("index") int index
            , @Query("length") int len
            , @Query("roomId") String roomId);

    @GET("neta/api/tag/docs")
    Call<String> requestTagDocList(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Query("index") int index
            , @Query("length") int len
            , @Query("tagName") String tagName);

    @GET("neta/api/tag/docTops")
    Call<String> requestTopTagDocList(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Query("tagName") String tagName);

    @GET("neta/api/tag/docHots")
    Call<String> requestHotTagDocList(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Query("tagName") String tagName);

    @GET("neta/api/tag/tree")
    Call<String> requestTagTree(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Query("tagName") String tagName);

    @GET("neta/api/tag/load")
    Call<String> requestTagNode(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Query("tagId") String tagId);

    @GET("neta/api/user/myTagDocList")
    Call<String> requestMyTagDocList(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Query("index") int index
            , @Query("length") int len
            , @Query("userId") String tagName);

    @GET("neta/api/department/docs")
    Call<String> requestDepartmentDocList(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Query("index") int index
            , @Query("length") int len
            , @Query("roomId") String roomId
            , @Query("before") String before);

    @PUT("neta/api/doc/{id}/giveCoin")
    @FormUrlEncoded
    Call<String> geiveCoinToDoc(@Header(Otaku.X_ACCESS_TOKEN) String head
            ,@Path("id") String id
            ,@Field("coins")int coins);

    @GET("neta/api/doc/{docId}/comments/floor")
    Call<String> requestCommentsFromFloor(@Header(Otaku.X_ACCESS_TOKEN)String head
            ,@Path("docId")String id
            ,@Query("floor")int floor
            ,@Query("length")int length
            ,@Query("target")String target);

    @PUT("neta/api/doc/{id}/favorite")
    Call<String> favoriteDoc(@Header(Otaku.X_ACCESS_TOKEN)String head
            ,@Path("id")String id);

    @DELETE("neta/api/doc/{id}/favorite")
    Call<String> cancelFavoriteDoc(@Header(Otaku.X_ACCESS_TOKEN)String head
            ,@Path("id")String id);

    @GET("neta/api/tagDoc/favorites")
    Call<String> requestFavoriteDocListUgc(@Header(Otaku.X_ACCESS_TOKEN)String head
            ,@Query("userId")String id
            ,@Query("index")int index
            ,@Query("length")int length);

    @GET("neta/api/calDoc/favorites")
    Call<String> requestFavoriteDocListPgc(@Header(Otaku.X_ACCESS_TOKEN)String head
            ,@Query("userId")String id
            ,@Query("index")int index
            ,@Query("length")int length);

    @GET("neta/api/tag/docAutumns")
    Call<String> requestQiuMingShanDocList(@Header(Otaku.X_ACCESS_TOKEN)String head
            ,@Query("index")int index
            ,@Query("length")int length);

    @PUT("neta/api/tagDocAutumn")
    Call<String> createQiuMingShanDoc(@Header(Otaku.X_ACCESS_TOKEN) String head
            , @Query("data") String doc);
}
