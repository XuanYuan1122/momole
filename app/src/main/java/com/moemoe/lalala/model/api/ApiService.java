package com.moemoe.lalala.model.api;

import com.moemoe.lalala.model.entity.ApiResult;
import com.moemoe.lalala.model.entity.AppUpdateEntity;
import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.BagDirEntity;
import com.moemoe.lalala.model.entity.BagEntity;
import com.moemoe.lalala.model.entity.BagFolderInfo;
import com.moemoe.lalala.model.entity.BagModifyEntity;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.BuildEntity;
import com.moemoe.lalala.model.entity.CalendarDayEntity;
import com.moemoe.lalala.model.entity.CalendarDayItemEntity;
import com.moemoe.lalala.model.entity.ChatContentEntity;
import com.moemoe.lalala.model.entity.CodeEntity;
import com.moemoe.lalala.model.entity.CoinDetailEntity;
import com.moemoe.lalala.model.entity.CommentDetailEntity;
import com.moemoe.lalala.model.entity.CommentDetailRqEntity;
import com.moemoe.lalala.model.entity.CommentListSendEntity;
import com.moemoe.lalala.model.entity.CommentSendEntity;
import com.moemoe.lalala.model.entity.CreatePrivateMsgEntity;
import com.moemoe.lalala.model.entity.DailyTaskEntity;
import com.moemoe.lalala.model.entity.DelCommentEntity;
import com.moemoe.lalala.model.entity.DelTagEntity;
import com.moemoe.lalala.model.entity.DepartmentEntity;
import com.moemoe.lalala.model.entity.DocDetailEntity;
import com.moemoe.lalala.model.entity.DocListEntity;
import com.moemoe.lalala.model.entity.DocPut;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.DonationInfoEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.model.entity.FileEntity;
import com.moemoe.lalala.model.entity.GiveCoinEntity;
import com.moemoe.lalala.model.entity.Live2dModelEntity;
import com.moemoe.lalala.model.entity.LoginEntity;
import com.moemoe.lalala.model.entity.LoginResultEntity;
import com.moemoe.lalala.model.entity.ModifyEntity;
import com.moemoe.lalala.model.entity.MoveFileEntity;
import com.moemoe.lalala.model.entity.NetaEvent;
import com.moemoe.lalala.model.entity.NetaMsgEntity;
import com.moemoe.lalala.model.entity.NewCommentEntity;
import com.moemoe.lalala.model.entity.NewUploadEntity;
import com.moemoe.lalala.model.entity.OldSimpleResult;
import com.moemoe.lalala.model.entity.PersonDocEntity;
import com.moemoe.lalala.model.entity.PersonFollowEntity;
import com.moemoe.lalala.model.entity.PersonalMainEntity;
import com.moemoe.lalala.model.entity.RegisterEntity;
import com.moemoe.lalala.model.entity.ReplyEntity;
import com.moemoe.lalala.model.entity.ReportEntity;
import com.moemoe.lalala.model.entity.SearchEntity;
import com.moemoe.lalala.model.entity.SendPrivateMsgEntity;
import com.moemoe.lalala.model.entity.SignEntity;
import com.moemoe.lalala.model.entity.SnowEntity;
import com.moemoe.lalala.model.entity.SnowInfo;
import com.moemoe.lalala.model.entity.TagLikeEntity;
import com.moemoe.lalala.model.entity.TagNodeEntity;
import com.moemoe.lalala.model.entity.TagSendEntity;
import com.moemoe.lalala.model.entity.ThirdLoginEntity;
import com.moemoe.lalala.model.entity.TrashEntity;
import com.moemoe.lalala.model.entity.TrashOperationEntity;
import com.moemoe.lalala.model.entity.TrashPut;
import com.moemoe.lalala.model.entity.UploadEntity;
import com.moemoe.lalala.model.entity.UploadResultEntity;
import com.moemoe.lalala.model.entity.UserInfo;
import com.moemoe.lalala.model.entity.WallBlock;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
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

    @GET("api/user/sign")
    Observable<ApiResult<SignEntity>> checkSignToday();

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

    @POST("api/upload/{suffix}")
    Observable<ApiResult<UploadEntity>> requestQnFileKey(@Path("suffix") String suffix);

    @POST("api/doc/addV2")
    Observable<ApiResult> createNormalDoc(@Body DocPut doc);

    @POST("api/doc/updateDoc/{docId}")
    Observable<ApiResult> updateDoc(@Body DocPut doc,@Path("docId")String docId);

    @POST("api/doc/addAutumnV2")
    Observable<ApiResult> createQiuMingShanDoc(@Body DocPut doc);

    @POST("api/doc/addSwimPoolV2")
    Observable<ApiResult> createSwimPoolDoc(@Body DocPut doc);

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

    @GET("api/user/{userId}/infoV2")
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

    @GET("api/cal/blackboard/{day}")
    Observable<ApiResult<CalendarDayEntity>> requestCalDayList(@Path("day")String day);

    @GET("api/doc/loadV2/{docId}")
    Observable<ApiResult<DocDetailEntity>> requestNewDocContent(@Path("docId") String id);

    @GET("api/doc/{docId}/comments")
    Observable<ApiResult<ArrayList<NewCommentEntity>>> requestCommentsFromFloor(@Path("docId")String id
            , @Query("index")long floor
            , @Query("size")int length
            , @Query("seelz")boolean target);

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

    @GET("api/dustbinV2/text/info/{id}")
    Observable<ApiResult<TrashEntity>> getTextTrashDetail(@Path("id")String id);

    @GET("api/dustbinV2/image/info/{id}")
    Observable<ApiResult<TrashEntity>> getImageTrashDetail(@Path("id")String id);

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

    @GET("api/snowman/info")
    Observable<ApiResult<SnowEntity>> requestSnowInfo();

    @GET("api/snowman/rank")
    Observable<ApiResult<SnowInfo>> requestSnowRank(@Query("index")int index,@Query("size")int size);

    @POST("api/snowman/click")
    Observable<ApiResult> clickSnowman();

    @GET("api/user/dailyTask")
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

    @POST("api/bag/open")
    Observable<ApiResult> openBag(@Body BagModifyEntity entity);

    @POST("api/bag/update")
    Observable<ApiResult> updateBag(@Body BagModifyEntity entity);

    @GET("api/bag/{userId}/info")
    Observable<ApiResult<BagEntity>> getBagInfo(@Path("userId")String userId);

    @GET("api/bag/{userId}/folder/list")
    Observable<ApiResult<ArrayList<BagDirEntity>>> getFolderList(@Path("userId")String userId,@Query("size") int size,@Query("index") int index);

    @POST("api/upload/check/md5")
    Observable<ApiResult<ArrayList<UploadResultEntity>>> checkMd5(@Body ArrayList<NewUploadEntity> uploadentities);

    @POST("api/bag/folder/add")
    Observable<ApiResult> createFolder(@Body BagFolderInfo entity);

    @POST("api/bag/folder/{folderId}/upload")
    Observable<ApiResult> uploadFolder(@Path("folderId")String folderId,@Body ArrayList<UploadResultEntity> files);

    @GET("api/bag/check/size")
    Observable<ApiResult<Boolean>> checkSize(@Query("size")long size);

    @GET("api/bag/folder/{folderId}/list")
    Observable<ApiResult<ArrayList<FileEntity>>> getFolderItemList(@Path("folderId")String id, @Query("index")int index, @Query("size")int size);

    @POST("api/bag/favorites")
    Observable<ApiResult<ArrayList<BagDirEntity>>> getBagFavoriteList(@Query("size")int size,@Query("index")int index);

    @POST("api/bag/follows")
    Observable<ApiResult<ArrayList<BagDirEntity>>> getBagFollowList(@Query("size")int size,@Query("index")int index);

    @POST("api/bag/favorite/delete")
    Observable<ApiResult> deleteBagFavoriteList(@Body ArrayList<String> ids);

    @POST("api/bag/report")
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

    @POST("api/bag/folder/file/{fileId}/update")
    Observable<ApiResult> modifyFile(@Path("fileId")String fileId,@Body String fileName);

    @POST("api/bag/folder/{folderId}/file/{fileId}/copy")
    Observable<ApiResult> copyFile(@Path("folderId")String folderId,@Path("fileId")String fileId);

    @POST("api/tag/delete")
    Observable<ApiResult> delTags(@Body DelTagEntity entity);

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

    @POST("api/search/searchBag")
    Observable<ApiResult<ArrayList<BagDirEntity>>> getSearchBag(@Body SearchEntity entity);

    @POST("api/search/searchUser")
    Observable<ApiResult<ArrayList<PersonFollowEntity>>> getSearchUser(@Body SearchEntity entity);

    @POST("api/talk/create/{toUserId}")
    Observable<ApiResult<CreatePrivateMsgEntity>> createPrivateMsg(@Path("toUserId")String userId);

    @GET("api/talk/find/{talkId}/history")
    Observable<ApiResult<ArrayList<ChatContentEntity>>> loadTalkHistory(@Path("talkId")String talkId);

    @GET("api/talk/find/{talkId}/message")
    Observable<ApiResult<ArrayList<ChatContentEntity>>> findTalk(@Path("talkId")String talkId,@Query("startTime")String startTime,@Query("endTime")String endTime);

    @POST("api/talk/send")
    Observable<ApiResult<String>> sendPrivateMsg(@Body SendPrivateMsgEntity entity);

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

    @GET("api/user/list/live2d")
    Observable<ApiResult<ArrayList<Live2dModelEntity>>> getFukuList();

    @POST("api/user/saveLive2d/{type}")
    Observable<ApiResult> saveLive2d(@Path("type")String type);
}
