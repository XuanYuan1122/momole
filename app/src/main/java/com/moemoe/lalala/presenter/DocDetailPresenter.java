package com.moemoe.lalala.presenter;

import android.text.TextUtils;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.ApiResult;
import com.moemoe.lalala.model.entity.CommentSendEntity;
import com.moemoe.lalala.model.entity.CommentV2Entity;
import com.moemoe.lalala.model.entity.DocDetailEntity;
import com.moemoe.lalala.model.entity.GiveCoinEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewCommentEntity;
import com.moemoe.lalala.model.entity.TagLikeEntity;
import com.moemoe.lalala.model.entity.TagSendEntity;
import com.moemoe.lalala.model.entity.UploadEntity;
import com.moemoe.lalala.utils.FileUtil;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by yi on 2016/11/29.
 */

public class DocDetailPresenter implements DocDetailContract.Presenter {

    private DocDetailContract.View view;
    private ApiService apiService;
    private boolean favoriteFlag;

    @Inject
    public DocDetailPresenter(DocDetailContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void requestDoc(String id) {
        apiService.requestNewDocContent(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<DocDetailEntity>() {
                    @Override
                    public void onSuccess(DocDetailEntity docDetailEntity) {
                        favoriteFlag = docDetailEntity.isFavoriteFlag();
                        if(view != null) view.onDocLoaded(docDetailEntity);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void shareDoc() {
        apiService.shareDoc()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
    }

    @Override
    public void deleteDoc(String docId) {
        apiService.deleteDoc(docId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onDeleteDoc();
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void requestTopComment(String id,int type, boolean sortTime, final int index) {
        if(type == 0) {//0 转发 1 评论
            apiService.loadRtComment(id,ApiService.LENGHT,index)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<CommentV2Entity>>() {
                        @Override
                        public void onSuccess(ArrayList<CommentV2Entity> commentV2Entities) {
                            if(view != null) view.onLoadTopCommentSuccess(commentV2Entities,index == 0);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }else {
            apiService.loadComment(id,sortTime?"time":"like",ApiService.LENGHT,index)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<CommentV2Entity>>() {
                        @Override
                        public void onSuccess(ArrayList<CommentV2Entity> commentV2Entities) {
                            if(view != null) view.onLoadTopCommentSuccess(commentV2Entities,index == 0);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }
    }

    @Override
    public void followUser(String id,boolean isFollow) {
        if (!isFollow){
            apiService.followUser(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onFollowSuccess(true);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }else {
            apiService.cancelfollowUser(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onFollowSuccess(false);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }
    }

    @Override
    public void favoriteComment(String id, String commentId, final boolean isFavorite, final int position) {
        apiService.favoriteComment(id,!isFavorite,commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.favoriteCommentSuccess(!isFavorite,position);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void favoriteDoc(String id) {
        if(favoriteFlag){
            apiService.cancelFavoriteDoc(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            favoriteFlag = false;
                            if(view != null)  view.onFavoriteDoc(favoriteFlag);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }else {
            apiService.favoriteDoc(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            favoriteFlag = true;
                            if(view != null) view.onFavoriteDoc(favoriteFlag);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }
    }

    @Override
    public void sendComment(ArrayList<String> paths, final CommentSendEntity entity) {
        if(paths.size() == 0){
            apiService.sendNewComment(entity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onSendComment();
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }else {
            final ArrayList<Image> images = new ArrayList<>();
            Observable.fromIterable(paths)
                    .observeOn(Schedulers.io())
                    .concatMap(new Function<String, ObservableSource<UploadEntity>>() {
                        @Override
                        public ObservableSource<UploadEntity> apply(@NonNull String s) throws Exception {
                            String temp = FileUtil.getExtensionName(s);
                            if(TextUtils.isEmpty(temp)){
                                temp = "jpg";
                            }
                            return Observable.zip(
                                    apiService.requestQnFileKey(temp),
                                    Observable.just(s),
                                    new BiFunction<ApiResult<UploadEntity>, String, UploadEntity>() {
                                        @Override
                                        public UploadEntity apply(@NonNull ApiResult<UploadEntity> uploadEntityApiResult, @NonNull String s) throws Exception {
                                            uploadEntityApiResult.getData().setLocalPath(s);
                                            return uploadEntityApiResult.getData();
                                        }
                                    }
                            );
                        }
                    })
                    .observeOn(Schedulers.io())
                    .concatMap(new Function<UploadEntity, ObservableSource<Image>>() {
                        @Override
                        public ObservableSource<Image> apply(@NonNull final UploadEntity uploadEntity) throws Exception {
                            final File file = new File(uploadEntity.getLocalPath());
                            final UploadManager uploadManager = new UploadManager();
                            return Observable.create(new ObservableOnSubscribe<Image>() {
                                @Override
                                public void subscribe(@NonNull final ObservableEmitter<Image> res) throws Exception {
                                    try {
                                        uploadManager.put(file,uploadEntity.getFilePath(), uploadEntity.getUploadToken(), new UpCompletionHandler() {
                                            @Override
                                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                                if (info.isOK()) {
                                                    Image image = new Image();
                                                    image.setPath(key);
                                                    try {
                                                        image.setH(response.getInt("h"));
                                                        image.setW(response.getInt("w"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    res.onNext(image);
                                                    res.onComplete();
                                                } else {
                                                    res.onError(null);
                                                }
                                            }
                                        }, null);
                                    }catch (Exception e){
                                        res.onError(e);
                                    }
                                }
                            });
                        }

                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Image>() {

                        @Override
                        public void onError(Throwable e) {
                            if(view != null) view.onFailure(-1,"");
                        }

                        @Override
                        public void onComplete() {
                            entity.images = images;
                            apiService.sendNewComment(entity)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new NetSimpleResultSubscriber() {
                                        @Override
                                        public void onSuccess() {
                                            if(view != null) view.onSendComment();
                                        }

                                        @Override
                                        public void onFail(int code,String msg) {
                                            if(view != null) view.onFailure(code,msg);
                                        }
                                    });
                        }

                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(Image image) {
                            images.add(image);
                        }
                    });
        }
    }

    @Override
    public void likeTag(final boolean isLike, final int position, TagLikeEntity entity) {
        apiService.plusTag(!isLike,entity.getDocId(),entity.getTagId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onPlusLabel(position, !isLike);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
//
//        if(isLike){
//            apiService.dislikeNewTag(entity)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new NetSimpleResultSubscriber() {
//                        @Override
//                        public void onSuccess() {
//                            if(view != null) view.onPlusLabel(position,false);
//                        }
//
//                        @Override
//                        public void onFail(int code,String msg) {
//                            if(view != null) view.onFailure(code,msg);
//                        }
//                    });
//        }else {
//            apiService.likeNewTag(entity)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new NetSimpleResultSubscriber() {
//                        @Override
//                        public void onSuccess() {
//                            if(view != null) view.onPlusLabel(position,true);
//                        }
//
//                        @Override
//                        public void onFail(int code,String msg) {
//                            if(view != null) view.onFailure(code,msg);
//                        }
//                    });
//        }
    }

    @Override
    public void deleteComment(String id, String commentId, final int position) {
        apiService.deleteComment(id,"doc",commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onDeleteCommentSuccess(position);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void createLabel(final TagSendEntity entity) {
        apiService.sendTag(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if(view != null) view.onCreateLabel(s,entity.getTag());
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void getCoinContent(String id) {
        apiService.requestDocHidePath(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onGetCoinContent();
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void giveCoin(final GiveCoinEntity entity) {
        apiService.giveCoinToDoc(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onGiveCoin(entity.coins);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void release() {
        view = null;
    }
}
