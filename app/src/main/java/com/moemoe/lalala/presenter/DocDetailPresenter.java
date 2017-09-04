package com.moemoe.lalala.presenter;

import android.text.TextUtils;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.ApiResult;
import com.moemoe.lalala.model.entity.CommentSendEntity;
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

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
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
    public void requestCommentFloor(String id, final long floor, int len, boolean target, final boolean isJump, final boolean clear, final boolean addBefore) {
        apiService.requestCommentsFromFloor(id,floor,len,target)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<NewCommentEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<NewCommentEntity> newCommentEntities) {
                        if(view != null)  view.onCommentsLoaded(newCommentEntities,floor == 1,isJump,clear,addBefore);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
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
    public void checkEgg(String docId) {
        apiService.checkEgg(docId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if(view!=null)view.checkEggSuccess(aBoolean);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void postOrCancelEgg(String docId, boolean isPost) {
        if(isPost){
            apiService.removeEgg(docId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view!=null)view.postOrCancelEggSuccess(false);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view!=null)view.onFailure(code, msg);
                        }
                    });
        }else {
            apiService.postEgg(docId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view!=null)view.postOrCancelEggSuccess(true);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view!=null)view.onFailure(code, msg);
                        }
                    });
        }
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
            Observable.from(paths)
                    .observeOn(Schedulers.io())
                    .concatMap(new Func1<String, Observable<UploadEntity>>() {
                        @Override
                        public Observable<UploadEntity> call(String s) {
                            String temp = FileUtil.getExtensionName(s);
                            if(TextUtils.isEmpty(temp)){
                                temp = "jpg";
                            }
                            return Observable.zip(
                                    apiService.requestQnFileKey(temp),
                                    Observable.just(s),
                                    new Func2<ApiResult<UploadEntity>, String, UploadEntity>() {
                                        @Override
                                        public UploadEntity call(ApiResult<UploadEntity> uploadEntityApiResult, String s) {
                                            uploadEntityApiResult.getData().setLocalPath(s);
                                            return uploadEntityApiResult.getData();
                                        }
                                    }
                            );
                        }
                    })
                    .observeOn(Schedulers.io())
                    .concatMap(new Func1<UploadEntity, Observable<Image>>() {
                        @Override
                        public Observable<Image> call(final UploadEntity uploadEntity) {
                            final File file = new File(uploadEntity.getLocalPath());
                            final UploadManager uploadManager = new UploadManager();
                            return Observable.create(new Observable.OnSubscribe<Image>() {
                                @Override
                                public void call(final Subscriber<? super Image> subscriber) {
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
                                                    subscriber.onNext(image);
                                                    subscriber.onCompleted();
                                                } else {
                                                    subscriber.onError(null);
                                                }
                                            }
                                        }, null);
                                    }catch (Exception e){
                                        subscriber.onError(e);
                                    }
                                }
                            });
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Image>() {
                        @Override
                        public void onCompleted() {
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
                        public void onError(Throwable e) {
                            if(view != null) view.onFailure(-1,"");
                        }

                        @Override
                        public void onNext(Image image) {
                            images.add(image);
                        }
                    });
        }
    }

    @Override
    public void likeTag(boolean isLike, final int position, TagLikeEntity entity) {
        if(isLike){
            apiService.dislikeNewTag(entity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onPlusLabel(position,false);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }else {
            apiService.likeNewTag(entity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onPlusLabel(position,true);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }
    }

    @Override
    public void deleteComment(final NewCommentEntity entity, final int position) {
        apiService.deleteNewComment(entity.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onDeleteComment(entity,position);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void createLabel(final TagSendEntity entity) {
        apiService.createNewTag(entity)
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
