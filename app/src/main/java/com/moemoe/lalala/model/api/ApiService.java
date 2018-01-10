package com.moemoe.lalala.model.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.moemoe.lalala.model.entity.*;

import java.util.ArrayList;
import java.util.Date;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 *
 * Created by yi on 2016/11/27.
 */

public interface ApiService {

    String URL_QINIU = "http://s.moemoe.la/";
    String REGISTER_PRICACE_URL = "http://s.moemoe.la/nonresponsibility.html";
    String LEVEL_DETAILS_URL = "http://s.moemoe.la/app/html/integral-v2.html";
    int LENGHT = 20;
    String SHARE_BASE = "http://2333.moemoe.la/share/doc/";
    String SHARE_BASE_DEBUG = "http:/183.131.152.216:8088/share/doc/";

    @GET("api/sys/getTime")
    Observable<ApiResult<Date>> getServerTime();

    @GET
    Observable<OldSimpleResult> getUrl(@Url String url);

    @PUT("api/user/sign")
    Observable<ApiResult<SignEntity>> signToday();

    @GET("api/app/check")
    Observable<ApiResult<AppUpdateEntity>> checkVersion(@Query("platform")String platform
            ,@Query("version")int version);

    @POST("api/user/chagePwd")
    Observable<ApiResult> changePassword(@Body RegisterEntity entity);

    @POST("api/user/chageForgetPwd")
    Observable<ApiResult> resetPwdByCode(@Body RegisterEntity entity);

    @GET("api/tag/load/{tagId}")
    Observable<ApiResult<TagNodeEntity>> requestTagNode(@Path("tagId") String tagId);

    @GET("api/doc/tagDocs")
    Observable<ApiResult<ArrayList<DocListEntity>>> requestTagDocList(@Query("index") int index
            , @Query("size") int len
            , @Query("tag") String tagName
            , @Query("subTags")boolean subTags);

    @GET("api/doc/tagDocSwimPool")
    Observable<ApiResult<ArrayList<DocListEntity>>> requestSwimDocList(@Query("index") int index
            , @Query("size") int len
            , @Query("subTags")boolean subTags);

    @GET("api/doc/tagDocTops")
    Observable<ApiResult<ArrayList<DocListEntity>>> requestTopTagDocList(@Query("tag") String tagName);

    @GET("api/doc/tagDocHots")
    Observable<ApiResult<ArrayList<DocListEntity>>> requestHotTagDocList(@Query("tag") String tagName);

    @GET("api/cal/uiDocs")
    Observable<ApiResult<ArrayList<CalendarDayItemEntity>>> requestUiDocList(@Query("uiId") String u
            , @Query("index") int index
            , @Query("size") int len);

    @POST("v2/kira/upload/{suffix}")
    Observable<ApiResult<UploadEntity>> requestQnFileKey(@Path("suffix") String suffix);

    @POST("api/doc/addV2")
    Observable<ApiResult> createNormalDoc(@Body DocPut doc);

    @POST("api/doc/updateDoc/{docId}")
    Observable<ApiResult> updateDoc(@Body DocPut doc,@Path("docId")String docId);

    @POST("api/doc/addAutumnV2")
    Observable<ApiResult> createQiuMingShanDoc(@Body DocPut doc);

    @POST("api/doc/addSwimPoolV2")
    Observable<ApiResult> createSwimPoolDoc(@Body DocPut doc);

    @POST("api/doc/addArticle")
    Observable<ApiResult<String>> createWenZhangDoc(@Body DocPut doc);

    @GET("api/classroom/banner")
    Observable<ApiResult<ArrayList<BannerEntity>>> requestNewBanner(@Query("room") String room);

    @GET("api/classroom/featured")
    Observable<ApiResult<ArrayList<FeaturedEntity>>> requestFreatured(@Query("room") String room);

    @GET("api/cal/docs")
    Observable<ApiResult<DepartmentEntity>> requestDepartmentDocList(@Query("index") int index
            , @Query("size") int len
            , @Query("roomId") String roomId
            , @Query("before") String before);

    @POST("api/coinbox/give/{num}")
    Observable<ApiResult> donationCoin(@Path("num")long coin);

    @GET("api/coinbox/get")
    Observable<ApiResult<DonationInfoEntity>> getDonationInfo();

    @GET("api/coinbox/getRankList")
    Observable<ApiResult<DonationInfoEntity>> getBookDonationInfo(@Query("index")int index
            , @Query("size")int size);

    @POST("api/user/updateInfoV2")
    Observable<ApiResult> modifyAll(@Body ModifyEntity bean);

    @POST("api/code/sendRegisterV2")
    Observable<ApiResult> requestRegisterCode(@Body CodeEntity data);

    @POST("api/code/sendForgetV2")
    Observable<ApiResult> requestCode4ResetPwd(@Body CodeEntity mobile);

    @GET("api/user/{userId}/infoV3")
    Observable<ApiResult<UserInfo>> requestUserInfoV2(@Path("userId") String userId);

    @POST("api/report/save")
    Observable<ApiResult> report(@Body ReportEntity bean);

    @POST("api/user/loginMobile")
    Observable<ApiResult<LoginResultEntity>> login(@Body LoginEntity bean);

    @POST("api/user/loginOpenId")
    Observable<ApiResult<LoginResultEntity>> loginThird(@Body ThirdLoginEntity data);

    @GET("api/user/getReply")
    Observable<ApiResult<ArrayList<ReplyEntity>>> requestCommentFromOther(@Query("index")int index
            , @Query("size")int len);

    @GET("api/doc/{userId}/favoritesV2")
    Observable<ApiResult<ArrayList<PersonDocEntity>>> requestFavoriteDocList(@Path("userId")String userId,@Query("index")int index
            , @Query("size")int length);

    @GET("api/user/getDocsV2")
    Observable<ApiResult<ArrayList<PersonDocEntity>>> requestUserTagDocListV2(@Query("index") int index
            , @Query("size") int len
            , @Query("userId") String tagName);

    @POST("api/user/register")
    Observable<ApiResult> phoneRegister(@Body RegisterEntity bean);

    @POST("api/code/check")
    Observable<ApiResult> checkVCode(@Body RegisterEntity bean);

    @GET("api/doc/tagDocAutumns")
    Observable<ApiResult<ArrayList<DocListEntity>>> requestQiuMingShanDocList(@Query("index")int index
            , @Query("size")int length
            , @Query("subTags")boolean subTags);

    @PUT("api/user/logout")
    Observable<ApiResult> logout();

    @GET("api/tag/wallV2")
    Observable<ApiResult<ArrayList<WallBlock>>> getWallBlocksV2(@Query("page") int page);

    @GET("api/doc/loadV3/{docId}")
    Observable<ApiResult<DocDetailEntity>> requestNewDocContent(@Path("docId") String id);

    @DELETE("api/doc/del/{uuid}")
    Observable<ApiResult> deleteDoc(@Path("uuid")String uuid);

    @POST("api/doc/{docId}/favorite")
    Observable<ApiResult> favoriteDoc(@Path("docId")String id);

    @DELETE("api/doc/{docId}/favorite")
    Observable<ApiResult> cancelFavoriteDoc(@Path("docId")String id);

    @POST("api/doc/comment")
    Observable<ApiResult> sendNewComment(@Body CommentSendEntity bean);

    @POST("api/tag/dislike")
    Observable<ApiResult> dislikeNewTag(@Body TagLikeEntity bean);

    @POST("api/tag/like")
    Observable<ApiResult> likeNewTag( @Body TagLikeEntity bean);

    @POST("api/tag/add")
    Observable<ApiResult<String>> createNewTag(@Body TagSendEntity bean);

    @DELETE("api/doc/comment/{uuid}")
    Observable<ApiResult> deleteNewComment(@Path("uuid") String id);

    @PUT("api/doc/pay/{docId}")
    Observable<ApiResult> requestDocHidePath(@Path("docId") String id);

    @PUT("api/doc/giveCoin")
    Observable<ApiResult> giveCoinToDoc(@Body GiveCoinEntity bean);

    @GET("api/dustbinV2/getTextV2")
    Observable<ApiResult<ArrayList<TrashEntity>>> getTextTrashList(@Query("size")int size,@Query("timestamp")int timestamp);

    @GET("api/dustbinV2/getImageV2")
    Observable<ApiResult<ArrayList<TrashEntity>>> getImgTrashList(@Query("size")int size,@Query("timestamp")int timestamp);

    @PUT("api/dustbinV2/operation")
    Observable<ApiResult> operationTrash(@Body TrashOperationEntity entity);

    @POST("api/dustbinV2/{type}/favorite/{id}")
    Observable<ApiResult> favoriteTrash(@Path("type")String type,@Path("id")String id);

    @POST("api/dustbinV2/remove/{type}/favorite/{id}")
    Observable<ApiResult> cancelFavoriteTrash(@Path("type")String type,@Path("id")String id);

    @GET("api/dustbinV2/sotText")
    Observable<ApiResult<ArrayList<TrashEntity>>> myTextTrashList(@Query("index")int index,@Query("size")int size);

    @GET("api/dustbinV2/sotImage")
    Observable<ApiResult<ArrayList<TrashEntity>>> myImgTrashList(@Query("index")int index,@Query("size")int size);

    @GET("api/dustbinV2/favoriteText")
    Observable<ApiResult<ArrayList<TrashEntity>>> myFavoriteTextTrashList(@Query("index")int index,@Query("size")int size);

    @GET("api/dustbinV2/favoriteImage")
    Observable<ApiResult<ArrayList<TrashEntity>>> myFavoriteImageTrashList(@Query("index")int index,@Query("size")int size);

    @POST("api/dustbinV2/addText")
    Observable<ApiResult> createTextTrash(@Body TrashPut put);

    @POST("api/dustbinV2/addImage")
    Observable<ApiResult> createImageTrash(@Body TrashPut put);

    @GET("api/dustbinV2/text/top3")
    Observable<ApiResult<ArrayList<TrashEntity>>> getTextTop3();

    @GET("api/dustbinV2/image/top3")
    Observable<ApiResult<ArrayList<TrashEntity>>> getImageTop3();

    @POST("api/dustbinV2/tag/like")
    Observable<ApiResult> likeTrashTag(@Body TagLikeEntity entity);

    @POST("api/dustbinV2/tag/dislike")
    Observable<ApiResult> dislikeTrashTag(@Body TagLikeEntity entity);

    @POST("api/dustbinV2/addTag")
    Observable<ApiResult<String>> createTrashTag(@Body TagSendEntity entity);

    @POST("api/user/follow/{userId}")
    Observable<ApiResult> followUser(@Path("userId")String userId);

    @POST("api/user/unfollow/{userId}")
    Observable<ApiResult> cancelfollowUser(@Path("userId")String userId);

    @GET("api/user/{userId}/main/info")
    Observable<ApiResult<PersonalMainEntity>> getPersonalMain(@Path("userId")String id);

    @GET("api/user/followers")
    Observable<ApiResult<ArrayList<PersonFollowEntity>>> getUserFollowList(@Query("userId")String id,@Query("index")int index,@Query("size")int size);

    @GET("api/user/fans")
    Observable<ApiResult<ArrayList<PersonFollowEntity>>> getUserFansList(@Query("userId")String id,@Query("index")int index,@Query("size")int size);

    @POST("api/user/{show}/favorite")
    Observable<ApiResult> showFavorite(@Path("show")boolean show);

    @POST("api/user/{show}/follow")
    Observable<ApiResult> showFollow(@Path("show")boolean show);

    @POST("api/user/{show}/fans")
    Observable<ApiResult> showFans(@Path("show")boolean show);

    @GET("v2/kira/user/dailyTask")
    Observable<ApiResult<DailyTaskEntity>> getDailyTask();

    @POST("api/doc/shareDoc")
    Observable<ApiResult> shareDoc();

    @GET("api/user/coin/details")
    Observable<ApiResult<ArrayList<CoinDetailEntity>>> getCoinDetails(@Query("index")int index,@Query("size")int size);

    @GET("api/user/{userId}/comments")
    Observable<ApiResult<ArrayList<NewCommentEntity>>> getCommentsList(@Path("userId")String userId,@Query("index")int index,@Query("size")int len);

    @POST("api/user/comment")
    Observable<ApiResult> sendComment(@Body CommentListSendEntity entity);

    @POST("api/doc/lz/comment")
    Observable<ApiResult> delCommentByOwner(@Body DelCommentEntity entity);

    @GET("api/user/list/my/badge")
    Observable<ApiResult<ArrayList<BadgeEntity>>> requestMyBadge(@Query("index")int index,@Query("size")int size);

    @GET("api/user/list/all/badge")
    Observable<ApiResult<ArrayList<BadgeEntity>>> requestAllBadge(@Query("index")int index,@Query("size")int size);

    @POST("api/user/save/badge")
    Observable<ApiResult> saveBadge(@Body ArrayList<String> ids);

    @POST("api/user/buy/badge/{id}")
    Observable<ApiResult> buyBadge(@Path("id")String id);

    @GET("api/dustbinV2/image/tags/{id}")
    Observable<ApiResult<ArrayList<DocTagEntity>>> getImgTrashTags(@Path("id")String id);

    @GET("api/dustbinV2/text/tags/{id}")
    Observable<ApiResult<ArrayList<DocTagEntity>>> getTextTrashTags(@Path("id")String id);

    @GET("api/app/check/build")
    Observable<ApiResult<BuildEntity>> checkBuild(@Query("buildVersion")int buildVersion, @Query("appVersion")int appVersion);

    @POST("v2/kira/bag/open")
    Observable<ApiResult> openBag(@Body BagModifyEntity entity);


    @POST("v2/kira/bag/update")
    Observable<ApiResult> updateBag(@Body BagModifyEntity entity);

    @GET("api/bag/{userId}/info")
    Observable<ApiResult<BagEntity>> getBagInfo(@Path("userId")String userId);

    @POST("v2/kira/upload/check/md5")
    Observable<ApiResult<ArrayList<UploadResultEntity>>> checkMd5(@Body ArrayList<NewUploadEntity> uploadentities);

    @POST("api/bag/folder/add")
    Observable<ApiResult> createFolder(@Body BagFolderInfo entity);

    @POST("api/bag/folder/{folderId}/upload")
    Observable<ApiResult> uploadFolder(@Path("folderId")String folderId,@Body ArrayList<UploadResultEntity> files);

    @GET("v2/kira/bag/check/size")
    Observable<ApiResult<Boolean>> checkSize(@Query("size")long size);

    @GET("api/bag/folder/{folderId}/list")
    Observable<ApiResult<ArrayList<FileEntity>>> getFolderItemList(@Path("folderId")String id, @Query("index")int index, @Query("size")int size);

    @GET("v2/kira/bag/buy/list")
    Observable<ApiResult<ArrayList<ShowFolderEntity>>> getBagFavoriteList(@Query("size")int size,@Query("index")int index);

    @POST("v2/kira/bag/buy/delete")
    Observable<ApiResult> deleteBagFavoriteList(@Body ArrayList<String> folderIds);

    @POST("v2/kira/bag/report")
    Observable<ApiResult> reportBag(@Body ReportEntity bean);

    @POST("api/bag/folder/{folderId}/buy")
    Observable<ApiResult> buyFolder(@Path("folderId")String folderId);

    @POST("api/bag/folder/{folderId}/follow")
    Observable<ApiResult> followFolder(@Path("folderId")String folderId);

    @POST("api/bag/follow/delete")
    Observable<ApiResult> deleteBagFollowList(@Body ArrayList<String> ids);

    @POST("api/bag/folder/delete")
    Observable<ApiResult> deleteFolders(@Body ArrayList<String> folderIds);

    @POST("api/bag/folder/{folderId}/update")
    Observable<ApiResult> modifyFolder(@Path("folderId")String folderId,@Body BagFolderInfo.FolderInfo userBagFolder);

    @POST("api/bag/folder/{folderId}/file/delete")
    Observable<ApiResult> deleteFiles(@Path("folderId")String folderId,@Body ArrayList<String> fileIds);

    @POST("api/bag/folder/{folderId}/move")
    Observable<ApiResult> moveFiles(@Path("folderId")String folderId, @Body MoveFileEntity entity);

    @POST("v2/kira/bag/update/file/name/{type}")
    Observable<ApiResult> modifyFile(@Path("type")String type,@Body ModifyFileEntity entity);

    @POST("v2/kira/bag/file/copy")
    Observable<ApiResult> copyFile(@Body CopyFileEntity entity);

    @POST("api/tag/delete")
    Observable<ApiResult> delTags(@Body DelTagEntity entity);

    @POST("v2/kira/tag/remove")
    Observable<ApiResult> delTagsV2(@Body DelTagEntity entity);

    @POST("api/doc/reply/info")
    Observable<ApiResult<CommentDetailEntity>> getCommentDetail(@Body CommentDetailRqEntity entity);

    @GET("api/user/getSystemMsg")
    Observable<ApiResult<ArrayList<NetaMsgEntity>>> getSystemMsg(@Query("index")int index,@Query("size")int size);

    @GET("api/user/getNetaMsg")
    Observable<ApiResult<ArrayList<NetaMsgEntity>>> getNetaMsg(@Query("index")int index,@Query("size")int size);

    @GET("api/user/getAtMsg")
    Observable<ApiResult<ArrayList<NetaMsgEntity>>> getAtMsg(@Query("index")int index,@Query("size")int size);

    @POST("api/search/searchDoc")
    Observable<ApiResult<ArrayList<PersonDocEntity>>> getSearchDoc(@Body SearchEntity entity);

    @POST("v2/kira/search/searchBag")
    Observable<ApiResult<ArrayList<ShowFolderEntity>>> getSearchBag(@Body SearchEntity entity);

    @POST("api/search/searchUser")
    Observable<ApiResult<ArrayList<PersonFollowEntity>>> getSearchUser(@Body SearchEntity entity);

    @POST("api/search/searchKira")
    Observable<ApiResult<ArrayList<PersonFollowEntity>>> getSearchKira(@Body SearchEntity entity);

    @GET("api/talk/{open}/ignore/{talkId}")
    Observable<ApiResult> ignoreUser(@Path("talkId")String talkId,@Path("open")boolean open);

    @GET("api/user/list/activity")
    Observable<ApiResult<ArrayList<NetaEvent>>> getEventList();

    @POST("api/user/save/activity")
    Observable<ApiResult> saveEvent(@Body NetaEvent event);

    @GET("api/bag/{userId}/folder/info/{folderId}")
    Observable<ApiResult<BagDirEntity>> getFolder(@Path("userId")String userId,@Path("folderId")String folderId);

    @GET("api/user/getBrowseDoc")
    Observable<ApiResult<ArrayList<PersonDocEntity>>> getDocHistory(@Query("index")int index,@Query("size")int size);

    @POST("api/user/saveLive2d/{type}")
    Observable<ApiResult> saveLive2d(@Path("type")String type);

    @POST("api/user/save/black/{userId}")
    Observable<ApiResult> saveBlackUser(@Path("userId") String userId);

    @POST("api/user/remove/black/{userId}")
    Observable<ApiResult> removeBlackUser(@Path("userId") String userId);

    @GET("api/user/list/black")
    Observable<ApiResult<ArrayList<RejectEntity>>> getBlackList(@Query("index") int index, @Query("size") int size);

    @POST("api/doc/check/{docId}/egg")
    Observable<ApiResult<Boolean>> checkEgg(@Path("docId") String docId);

    @POST("api/doc/post/{docId}/egg")
    Observable<ApiResult> postEgg(@Path("docId") String docId);

    @POST("api/doc/remove/{docId}/egg")
    Observable<ApiResult> removeEgg(@Path("docId") String docId);

    @GET("api/shop/list")
    Observable<ApiResult<ArrayList<CoinShopEntity>>> loadShopList(@Query("index")int index, @Query("size")int size);

    @POST("api/shop/order/{id}")
    Observable<ApiResult<CreateOrderEntity>> createOrder(@Path("id")String id);

    @POST("api/shop/order/{num}/{id}")
    Observable<ApiResult<CreateOrderEntity>> createOrderNum(@Path("num")int num,@Path("id")String id);

    @POST("api/shop/orderV2/{num}/{id}")
    Observable<ApiResult<CreateOrderEntity>> createOrderNum(@Path("num")int num,@Path("id")String id,@Query("from")String from);

    @POST("api/user/save/address")
    Observable<ApiResult> saveUserAddress(@Body AddressEntity entity);

    @GET("api/user/find/address")
    Observable<ApiResult<AddressEntity>> loadUserAddress();

    @GET("api/shop/my/order")
    Observable<ApiResult<ArrayList<OrderEntity>>> loadOrderList(@Query("index")int index, @Query("size")int size);

    @POST("api/shop/cancel/{orderId}")
    Observable<ApiResult> cancelOrder(@Path("orderId")String orderId);

    @POST("api/shop/pay")
    Observable<ApiResult<PayResEntity>> payOrder(@Body PayReqEntity entity);

    @POST("api/shop/batch/order")
    Observable<ApiResult<ArrayList<JsonObject>>> createPayList(@Body OrderTmp orderTmp);

    @GET("api/app/check/txbb")
    Observable<ApiResult<Boolean>> checkTxbb();

    @POST("api/user/{follow}/follow/club/{clubId}")
    Observable<ApiResult> followClub(@Path("follow")boolean follow,@Path("clubId")String clubId);

    @POST("api/user/{follow}/follow/department/{departmentId}")
    Observable<ApiResult> followDepartment(@Path("follow")boolean follow,@Path("departmentId")String clubId);

    @GET("api/cal/{departmentId}/is/follow")
    Observable<ApiResult<Boolean>> isFollowDepartment(@Path("departmentId")String clubId);

    @GET("api/index/topUser")
    Observable<ApiResult<ArrayList<XianChongEntity>>> loadXianChongList();

    @GET("v2/kira/bag/{userId}/info")
    Observable<ApiResult<NewBagEntity>> loadBagData(@Path("userId")String userId);

    @GET("v2/kira/bag/{userId}/items")
    Observable<ApiResult<BagMyEntity>> loadBagMy(@Path("userId")String userId);

    @GET("v2/kira/bag/my/follow")
    Observable<ApiResult<BagMyEntity>> loadBagMyFollow();

    @POST("v2/kira/bag/update/{id}/folder")
    Observable<ApiResult> updateFolder(@Path("id")String folderId, @Body FolderRepEntity entity);

    @POST("v2/kira/bag/add/folder")
    Observable<ApiResult> createFolder(@Body FolderRepEntity entity);

    @GET("v2/kira/bag/{userId}/{type}/folder/list")
    Observable<ApiResult<ArrayList<ShowFolderEntity>>> loadFolderList(@Path("userId")String userId,@Path("type")String type,@Query("size")int size,@Query("index")int index);

    @POST("v2/kira/bag/delete/{type}/folder")
    Observable<ApiResult> deleteFolders(@Body ArrayList<String> folderIds,@Path("type")String type);

    @POST("v2/kira/bag/top/{id}/folder")
    Observable<ApiResult> topFolder(@Path("id")String folderId);

    @GET("v2/kira/bag/{userId}/{type}/folder/{folderId}/info")
    Observable<ApiResult<NewFolderEntity>> loadFolderInfo(@Path("userId")String userId,@Path("type")String type,@Path("folderId")String folderId);

    @GET("v2/kira/bag/{userId}/{type}/folder/{folderId}/file/list")
    Observable<ApiResult<JsonArray>> loadFileList(@Path("userId")String userId, @Path("type")String type, @Path("folderId")String folderId, @Query("size")int size, @Query("index")int index);

    @GET("v2/kira/bag/{userId}/cartoon/{parentFolderId}/folder/list")
    Observable<ApiResult<ArrayList<ManHua2Entity>>> loadFiManHua2List(@Path("userId")String userId, @Path("parentFolderId")String parentFolderId, @Query("size")int size, @Query("index")int index);

    @POST("v2/kira/bag/delete/{type}/{folderId}/file")
    Observable<ApiResult> deleteFiles(@Body ArrayList<String> fileIds,@Path("type")String type,@Path("folderId")String folderId);

    @POST("v2/kira/bag/delete/{parentFolderId}/cartoon/folder")
    Observable<ApiResult> deleteManHua2(@Body ArrayList<String> fileIds,@Path("parentFolderId")String parentFolderId);

    @POST("v2/kira/bag/top/{parentFolderId}/cartoon/{folderId}/folder")
    Observable<ApiResult> topManHua2(@Path("parentFolderId")String parentFolderId,@Path("folderId")String folderId);

    @POST("v2/kira/bag/top/{folderId}/folder/{type}/file/{fileId}")
    Observable<ApiResult> topFile(@Path("folderId")String folderId,@Path("type")String type,@Path("fileId")String fileId);

    @POST("v2/kira/bag/remove/{userId}/folder/{type}/{folderId}/follow")
    Observable<ApiResult> removeFollowFolder(@Path("userId")String userId,@Path("type")String type,@Path("folderId")String folderId);

    @POST("v2/kira/bag/{userId}/folder/{type}/{folderId}/follow")
    Observable<ApiResult> followFolder(@Path("userId")String userId,@Path("type")String type,@Path("folderId")String folderId);

    @POST("v2/kira/bag/{userId}/folder/{type}/{folderId}/buy")
    Observable<ApiResult> buyFolder(@Path("userId")String userId,@Path("type")String type,@Path("folderId")String folderId);

    @POST("v2/kira/bag/folder/fiction/{folderId}/upload")
    Observable<ApiResult> uploadXiaoshuo(@Path("folderId")String folderId,@Body ArrayList<UploadResultEntity> entities);

    @POST("v2/kira/bag/folder/image/{folderId}/upload")
    Observable<ApiResult> uploadTuji(@Path("folderId")String folderId,@Body ArrayList<UploadResultEntity> entities);

    @POST("v2/kira/bag/folder/synthesize/{folderId}/upload")
    Observable<ApiResult> uploadZonghe(@Path("folderId")String folderId,@Body ArrayList<UploadResultEntity> entities);

    @POST("v2/kira/bag/update/{parentFolderId}/cartoon/{id}/folder")
    Observable<ApiResult> uploadManhua2(@Path("parentFolderId")String parentFolderId,@Path("id")String folderId,@Body ManHuaUploadEntity entities);

    @POST("v2/kira/bag/add/{parentFolderId}/cartoon/folder")
    Observable<ApiResult> uploadManhua(@Path("parentFolderId")String parentFolderId,@Body ManHuaUploadEntity entities);

    @GET("v2/kira/bag/my/dynamic")
    Observable<ApiResult<ArrayList<DynamicTopEntity>>> loadDynamicTop();

    @GET("v2/kira/bag/{userId}/article/list")
    Observable<ApiResult<ArrayList<WenZhangFolderEntity>>> loadWenZhangList(@Path("userId")String userId,@Query("size")int size,@Query("index")int index);

    @GET("v2/kira/bag/{userId}/favorite/article/list")
    Observable<ApiResult<ArrayList<WenZhangFolderEntity>>> loadWenZhangFollowList(@Path("userId")String userId,@Query("size")int size,@Query("index")int index);

    @POST("v2/kira/bag/delete/cartoon/{parentFolderId}/{folderId}/file")
    Observable<ApiResult> deleteManHuaFile(@Path("parentFolderId")String parentId,@Path("folderId")String folderId,@Body ArrayList<String> fileIds);

    @GET("v2/kira/bag/follow/{type}/folder/list")
    Observable<ApiResult<ArrayList<ShowFolderEntity>>> loadFollowFolderList(@Path("type")String type,@Query("size")int size,@Query("index")int index);

    @GET("v2/kira/bag/my/follow/feed/{lastTime}")
    Observable<ApiResult<ArrayList<DynamicEntity>>> loadDynamicList(@Path("lastTime")long lastTime);

    @GET("v2/kira/bag/folder/recommend/reload/{excludeFolderId}")
    Observable<ApiResult<ArrayList<ShowFolderEntity>>> loadRefreshList(@Path("excludeFolderId")String excludeFolderId,@Query("folderName")String folderName,@Query("page")int page);

    @GET("v2/kira/user/getSampleUserInfo/{userId}")
    Observable<ApiResult<JsonObject>> loadSampleUserInfo(@Path("userId")String userId);

    @GET("v2/kira/user/eachOthers")
    Observable<ApiResult<ArrayList<PhoneMenuEntity>>> loadFollowListBoth(@Query("index")int index,@Query("size")int size);

    @GET("v2/kira/user/followers")
    Observable<ApiResult<ArrayList<PhoneMenuEntity>>> loadFollowListFollow(@Query("index")int index,@Query("size")int size);

    @GET("v2/kira/user/fans")
    Observable<ApiResult<ArrayList<PhoneMenuEntity>>> loadFollowListFans(@Query("index")int index,@Query("size")int size);

    @GET("v2/kira/user/getRcToken")
    Observable<ApiResult<String>> loadRcToken();

    @GET("v2/kira/mobile/get/role/likes")
    Observable<ApiResult<ArrayList<PhoneMateEntity>>> loadMateList();

    @GET("v2/kira/mobile/get/{role}/clothes")
    Observable<ApiResult<ArrayList<PhoneFukuEntity>>> loadFukuList(@Path("role")String role);

    @POST("v2/kira/mobile/set/{role}/clothes/{clothesId}")
    Observable<ApiResult> setFuku(@Path("role")String role,@Path("clothesId")String clothesId);

    @POST("v2/kira/mobile/set/{role}/to/deskmate")
    Observable<ApiResult> setMate(@Path("role")String role);

    @GET("v2/kira/tag/{targetId}/all")
    Observable<ApiResult<ArrayList<DocTagEntity>>> loadTags(@Path("targetId")String id);

    @POST("v2/kira/tag/add")
    Observable<ApiResult<String>> sendTag(@Body TagSendEntity entity);

    @POST("v2/kira/tag/{targetId}/{like}/like/{tagId}")
    Observable<ApiResult> plusTag(@Path("like")boolean like,@Path("targetId")String targetId,@Path("tagId")String tagId);

    @GET("v2/kira/dynamic/rt")
    Observable<ApiResult<ArrayList<CommentV2Entity>>> loadRtComment(@Query("dynamicId") String dynamicId,@Query("size")int size,@Query("index")int index);

    @GET("v2/kira/comment/get/{orderBy}/{targetId}/comments")
    Observable<ApiResult<ArrayList<CommentV2Entity>>> loadComment(@Path("targetId")String targetId,@Path("orderBy")String orderBy,@Query("size")int size,@Query("start")int index);

    @GET("v2/kira/comment/get/sec/{orderBy}/{targetId}/comments")
    Observable<ApiResult<ArrayList<CommentV2SecEntity>>> loadCommentSec(@Path("targetId")String targetId, @Path("orderBy")String orderBy, @Query("size")int size, @Query("start")int index);

    @POST("v2/kira/comment/remove/{type}/{targetId}/comment/{commentId}")
    Observable<ApiResult> deleteComment(@Path("targetId")String targetId,@Path("type")String type,@Path("commentId")String commentId);

    @POST("v2/kira/comment/{parentId}/sec/{targetId}/remove/{commentId}/comment")
    Observable<ApiResult> deleteCommentSec(@Path("targetId")String targetId,@Path("parentId")String parentId,@Path("commentId")String commentId);

    @POST("v2/kira/comment/like/{flag}/{targetId}/comment/{commentId}")
    Observable<ApiResult> favoriteComment(@Path("targetId")String targetId,@Path("flag")boolean flag,@Path("commentId")String commentId);

    @POST("v2/kira/comment/like/sec/{flag}/{targetId}/comment/{commentId}")
    Observable<ApiResult> favoriteCommentSec(@Path("targetId")String targetId,@Path("flag")boolean flag,@Path("commentId")String commentId);

    @POST("v2/kira/comment/send/{targetId}/comment")
    Observable<ApiResult> sendComment(@Path("targetId")String targetId, @Body CommentSendV2Entity entity);

    @POST("v2/kira/comment/send/doc/{docId}/comment")
    Observable<ApiResult> sendCommentWenZhang(@Path("docId")String targetId, @Body CommentSendV2Entity entity);

    @POST("v2/kira/comment/send/sec/{commentId}/comment")
    Observable<ApiResult> sendCommentSec(@Path("commentId")String commentId, @Body CommentSendV2Entity entity);

    @POST("v2/kira/dynamic/shareArticle")
    Observable<ApiResult> shareArticle(@Body ShareArticleSendEntity entity);

    @POST("v2/kira/dynamic/shareFolder")
    Observable<ApiResult> shareFolder(@Body ShareFolderSendEntity entity);

    @POST("v2/kira/dynamic/retweet")
    Observable<ApiResult<Float>> rtDynamic(@Body ForwardSendEntity entity);

    @POST("v2/kira/dynamic/send")
    Observable<ApiResult> createDynamic(@Body DynamicSendEntity entity);

    @GET("v2/kira/comment/get/{targetId}/top")
    Observable<ApiResult<ArrayList<CommentV2Entity>>> loadTopComment(@Path("targetId")String id);

    @POST("v2/kira/dynamic/deleteDynamic/{dynamicId}/{type}")
    Observable<ApiResult> deleteDynamic(@Path("dynamicId")String dynamicId,@Path("type")String type);

    @POST("v2/kira/dynamic/reward/{dynamicId}/num/{num}")
    Observable<ApiResult> giveCoinToDynamic(@Path("dynamicId")String id,@Path("num")int num);

    @GET("v2/kira/dynamic/getDynamicList/follow")
    Observable<ApiResult<ArrayList<NewDynamicEntity>>> loadFeedFollowList(@Query("time")long timestamp);

    @GET("v2/kira/dynamic/getMyFavDynamicList")
    Observable<ApiResult<ArrayList<NewDynamicEntity>>> loadFeedFavoriteList(@Query("index")int index,@Query("size")int size);

    @GET("v2/kira/dynamic/getDynamicList/random")
    Observable<ApiResult<ArrayList<NewDynamicEntity>>> loadFeedRandomList(@Query("index")int index,@Query("size")int size);

    @GET("v2/kira/dynamic/getDynamicList/playground")
    Observable<ApiResult<ArrayList<NewDynamicEntity>>> loadFeedGroundList(@Query("time")long timestamp);

//    @GET("v2/kira/dynamic/getMyDynamicList")
//    Observable<ApiResult<ArrayList<NewDynamicEntity>>> loadFeedMyList(@Query("time")long timestamp);

    @GET("v2/kira/dynamic/get/{userId}/dynamic/list")
    Observable<ApiResult<ArrayList<NewDynamicEntity>>> loadFeedMyList(@Path("userId")String userId,@Query("time")long timestamp);

    @GET("v2/kira/user/get/tickets")
    Observable<ApiResult<Integer>> loadTicketsNum();

    @POST("v2/kira/sound/unlock/{soundId}")
    Observable<ApiResult> unlockLuYin(@Path("soundId")String id);

    @GET("v2/kira/sound/sounds")
    Observable<ApiResult<ArrayList<LuYinEntity>>> loadLuYinList(@Query("type")String type,@Query("roleName")String roleName,@Query("index")int index,@Query("size")int size);

    @POST("v2/kira/dynamic/saveMyFavDynamic/{dynamicId}")
    Observable<ApiResult> collectDynamic(@Path("dynamicId")String dynamicId);

    @POST("v2/kira/dynamic/deleteFavDynamic/{id}")
    Observable<ApiResult> cancelCollectDynamic(@Path("id")String id);

    @POST("v2/kira/dynamic/saveDynamicReport")
    Observable<ApiResult> reportDynamic(@Body ReportEntity entity);

    @GET("v2/kira/user/getUserInfoByUserNo")
    Observable<ApiResult<ArrayList<InviteUserEntity>>> loadInviteList();

    @GET("v2/kira/user/getUserNameByUserNo/{userno}")
    Observable<ApiResult<String>> getUserNameByNo(@Path("userno")String no);

    @POST("v2/kira/user/inviteUserByUserNO/{userno}")
    Observable<ApiResult> useInviteNo(@Path("userno")String userno);

    @GET("v2/kira/story/find/all")
    Observable<ApiResult<ArrayList<JuQIngStoryEntity>>> getAllStory();

    @GET("v2/kira/story/find/story/version")
    Observable<ApiResult<Integer>> checkStoryVersion();

    @GET("v2/kira/story/find/trigger/all")
    Observable<ApiResult<ArrayList<JuQingTriggerEntity>>> getAllTrigger();

    @POST("v2/kira/story/save/record/{storyId}")
    Observable<ApiResult<Long>> doneStory(@Path("storyId")String id);

    @GET("v2/kira/story/find/{level}/group/{type}")
    Observable<ApiResult<ArrayList<JuQingEntity>>> loadStoryList(@Path("level")int level, @Path("type")int type,@Query("index")int index,@Query("size")int size);

    @GET("v2/kira/story/find/my/story")
    Observable<ApiResult<ArrayList<JuQingDoneEntity>>> getDoneJuQing();

    @GET("v2/kira/doc/get/department")
    Observable<ApiResult<ArrayList<SubmissionDepartmentEntity>>> loadDepartment();

    @POST("v2/kira/doc/submit/contribute")
    Observable<ApiResult> submission(@Body SendSubmissionEntity entity);

    @GET("v2/kira/doc/get/contribute")
    Observable<ApiResult<ArrayList<SubmissionItemEntity>>> loadSubmissionList(@Query("index")int index,@Query("size")int size);

    @GET("v2/kira/map/pics")
    Observable<ApiResult<ArrayList<MapEntity>>> loadMapPics();

    @GET("v2/kira/comment/get/hot")
    Observable<ApiResult<ArrayList<Comment24Entity>>> load24Comments(@Query("page")int page);

    @GET("v2/kira/bag/hot/folder")
    Observable<ApiResult<ArrayList<ShowFolderEntity>>> load24Folder();

    @GET("v2/kira/bag/hot/folderV2/{idx}")
    Observable<ApiResult<ArrayList<ShowFolderEntity>>> loadHotFolderV2(@Path("idx")int index);

    @GET("v2/kira/dynamic/get/{dynamicId}/dynamic")
    Observable<ApiResult<NewDynamicEntity>> getDynamic(@Path("dynamicId")String id);

    @GET("v2/kira/doc/get/doc/{departmentType}/list")
    Observable<ApiResult<ArrayList<DocResponse>>> loadOldDocList(@Path("departmentType")String type,@Query("timestamp")long timestamp);

    @GET("v2/kira/group/notify/list")
    Observable<ApiResult<ArrayList<GroupNoticeEntity>>> loadMsgList(@Query("index")int index,@Query("size")int size);

    @POST("v2/kira/group/push/{result}/for/{notifyId}")
    Observable<ApiResult> responseNotice(@Path("result")boolean result,@Path("notifyId")String id);

    @GET("v2/kira/group/list")
    Observable<ApiResult<ArrayList<GroupEntity>>> loadGroupList(@Query("index")int index,@Query("size")int size);

    @GET("v2/kira/group/my/list")
    Observable<ApiResult<ArrayList<GroupEntity>>> loadMyGroupList(@Query("index")int index,@Query("size")int size);

    @GET("v2/kira/group/{groupId}/info")
    Observable<ApiResult<GroupEntity>> loadGroup(@Path("groupId")String groupId);

    @POST("v2/kira/group/apply/join/{groupId}")
    Observable<ApiResult> applyJoinGroup(@Path("groupId")String groupId);

    @POST("v2/kira/group/quit/{groupId}")
    Observable<ApiResult> quitGroup(@Path("groupId")String groupId);

    @POST("v2/kira/group/dismiss/{groupId}")
    Observable<ApiResult> dismissGroup(@Path("groupId")String groupId);

    @POST("v2/kira/group/join/{groupId}")
    Observable<ApiResult> JoinAuthorGroup(@Path("groupId")String groupId);

    @POST("v2/kira/group/apply")
    Observable<ApiResult> createGroup(@Body GroupEditEntity editEntity);

    @POST("v2/kira/group/update/{groupId}/info")
    Observable<ApiResult> updateGroup(@Path("groupId")String groupId,@Body GroupEditEntity editEntity);

    @POST("v2/kira/group/invite/{userId}/join/{groupId}")
    Observable<ApiResult> inviteUserJoinGroup(@Path("userId")String userId,@Path("groupId")String groupId);

    @GET("v2/kira/group/{groupId}/user/list")
    Observable<ApiResult<ArrayList<UserTopEntity>>> loadGroupMemberList(@Path("groupId")String groupId,@Query("index")int index,@Query("size")int size);

    @POST("v2/kira/group/remove/member")
    Observable<ApiResult> delGroupMember(@Body GroupMemberDelEntity entity);

    @GET("v2/kira/map/list/place")
    Observable<ApiResult<ArrayList<MapAddressEntity>>> loadMapAddressList();

    @POST("v2/kira/user/saveUserGps")
    Observable<ApiResult> saveUserLocation(@Body UserLocationEntity entity);

    @POST("v2/kira/user/saveUserPic")
    Observable<ApiResult> saveUserMapImage(@Body UserMapSendEntity entity);

    @POST("v2/kira/user/saveUserPicCheck")
    Observable<ApiResult> checkUserMapImage(@Body UserMapSendEntity entity);

    @GET("v2/kira/map/list/all/user")
    Observable<ApiResult<ArrayList<MapEntity>>> loadMapAllUser();

    @GET("v2/kira/map/list/birthday/user")
    Observable<ApiResult<ArrayList<MapEntity>>> loadMapBirthdayUser();

    @GET("v2/kira/map/list/eachFollow/user")
    Observable<ApiResult<ArrayList<MapEntity>>> loadMapEachFollowUser();

    @GET("v2/kira/map/list/top/user")
    Observable<ApiResult<NearUserEntity>> loadMapTopUser();

    @GET("v2/kira/map/list/near/userV2")
    Observable<ApiResult<NearUserEntity>> loadMapNearUser(@Query("lat") double lat,@Query("lon")double lon);

    @GET("v2/kira/mobile/get/audio")
    Observable<ApiResult<ArrayList<Live2dMusicEntity>>> loadLive2dMusicList();

    @GET("v2/kira/map/list/all/sysUserPic")
    Observable<ApiResult<ArrayList<MapUserImageEntity>>> loadMapSelectList();

    @GET("v2/kira/user/list/{userId}/artwork")
    Observable<ApiResult<ArrayList<MapHistoryEntity>>> loadMapHistoryList(@Path("userId")String userId,@Query("index")int index,@Query("size")int size);

    @POST("v2/kira/dynamic/{flag}/{dynamicId}/like")
    Observable<ApiResult> likeDynamic(@Path("dynamicId")String dynamicId,@Path("flag")boolean flag);

    @POST("v2/kira/live2d/buy/{id}")
    Observable<ApiResult> buyLive2d(@Path("id")String id);

    @GET("v2/kira/live2d/list")
    Observable<ApiResult<ArrayList<Live2dShopEntity>>> loadLive2dList(@Query("index")int index,@Query("size")int size);

    @POST("v2/kira/live2d/score/{num}/for/{id}")
    Observable<ApiResult> pingfenLive2d(@Path("num")int num,@Path("id")String id);

    @GET("v2/kira/dynamic/getDynamicList/randomV2")
    Observable<ApiResult<ArrayList<DiscoverEntity>>> loadDiscoverList(@Query("minIdx")long minIdx,@Query("maxIdx")long maxIdx);

    @GET("v2/kira/dynamic/getDynamicList/followV2")
    Observable<ApiResult<ArrayList<DiscoverEntity>>> loadFollowList(@Query("time")long time);

    @POST("v2/kira/user/pic/{flag}/likeV2/for/{artworkId}")
    Observable<ApiResult> likeUserMapRole(@Path("flag")boolean flag,@Path("artworkId")String artworkId);

    @POST("v2/kira/user/remove/artwork")
    Observable<ApiResult> deleteHistoryMapRole(@Body DeleteRoleSend object);

    @GET("v2/kira/app/get/all")
    Observable<ApiResult<ArrayList<SplashEntity>>> loadSplashList();

    @GET("v2/kira/fx/roles")
    Observable<ApiResult<ArrayList<ShareLive2dEntity>>> loadShareLive2dList();

    @POST("v2/kira/kpi/department/{id}/click")
    Observable<ApiResult> clickDepartment(@Path("id")String id);

    @POST("v2/kira/kpi/department/{id}/{time}/stay")
    Observable<ApiResult> stayDepartment(@Path("id")String id,@Path("time")int time);

    @POST("v2/kira/kpi/fx/{type}")
    Observable<ApiResult> shareKpi(@Path("type")String type);

    @GET("v2/kira/dynamic/notify/list/{timestamp}")
    Observable<ApiResult<ArrayList<FeedNoticeEntity>>> loadFeedNoticeList(@Path("timestamp")long timestamp);

    @GET("v2/kira/dynamic/getDynamicList/{type}/followV3")
    Observable<ApiResult<ArrayList<FeedNoticeEntity>>> loadFeedNoticeListV3(@Path("type")String type,@Query("followTime") long followTime,@Query("notifyTime")long notifyTime);

    @GET("v2/kira/doc/get/department/part")
    Observable<ApiResult<ArrayList<LuntanTabEntity>>> loadLuntanTabList();

    @POST("api/doc/addV3")
    Observable<ApiResult> createDocV3(@Body DocPut doc);

    @GET("v2/kira/doc/get/department/{departmentId}/list")
    Observable<ApiResult<ArrayList<DocResponse>>> loadDepartmentDocList(@Path("departmentId")String departmentId,@Query("timestamp")long timestamp);

    @GET("v2/kira/group/department/{departmentId}")
    Observable<ApiResult<ArrayList<DepartmentGroupEntity>>> loadDepartmentGroup(@Path("departmentId")String departmentId);

    @GET("v2/kira/mobile/get/ar/stick")
    Observable<ApiResult<ArrayList<StickEntity>>> loadStickList();

    @POST("v2/kira/mobile/buy/ar/stick")
    Observable<ApiResult> buyStick(@Body StickSend object);

    @GET("v2/kira/game/{userId}/resurgence/num")
    Observable<ApiResult<Integer>> getFuHuoNum(@Path("userId")String id);

    @POST("v2/kira/game/{userId}/exchange/{num}")
    Observable<ApiResult> useCiYuanBiGetFuHuo(@Path("userId")String id,@Path("num")int num);

    @GET("v2/kira/bag/newest/folder")
    Observable<ApiResult<ArrayList<ShowFolderEntity>>> loadNewFolder(@Query("index")int index,@Query("size")int size);

    @GET("v2/kira/dynamic/getDynamicList/randomV3")
    Observable<ApiResult<ArrayList<DiscoverEntity>>> loadHotDynamicList(@Query("minIdx")long minIdx,@Query("maxIdx")long maxIdx);

    @GET("v2/kira/user/list/recommend")
    Observable<ApiResult<ArrayList<FeedRecommendUserEntity>>> loadFeedRecommentUserList();

    @GET("v2/kira/tag/match/all/type/{type}")
    Observable<ApiResult<ArrayList<RecommendTagEntity>>> loadRecommendTag(@Path("type")String type);

    @GET("v2/kira/tag/match/all/{word}")
    Observable<ApiResult<ArrayList<RecommendTagEntity>>> loadKeywordTag(@Path("word")String word);

    @GET("api/shop/product/{id}")
    Observable<ApiResult<CoinShopEntity>> loadShopDetail(@Path("id")String id);

    @GET("v2/kira/game/{userId}/has/{gameId}/{roleId}/status")
    Observable<ApiResult<Boolean>> hasRole(@Path("userId")String userId,@Path("gameId")String gameId,@Path("roleId")String roleId);

    @POST("v2/kira/user/saveUserText")
    Observable<ApiResult> saveUserText(@Body SimpleRequestEntity entity);

    @GET("v2/kira/game/price/info")
    Observable<ApiResult<GamePriceInfoEntity>> getGamePriceInfo();

    @GET("v2/kira/dynamic/red/{dynamicId}")
    Observable<ApiResult<ArrayList<HongBaoEntity>>> loadHongBaoList(@Path("dynamicId")String id);
}
