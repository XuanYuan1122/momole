package com.moemoe.lalala.presenter;

import android.text.TextUtils;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.ApiResult;
import com.moemoe.lalala.model.entity.CommentDetailEntity;
import com.moemoe.lalala.model.entity.CommentDetailRqEntity;
import com.moemoe.lalala.model.entity.CommentSendEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewCommentEntity;
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

public class CommentDetailPresenter implements CommentDetailContract.Presenter {

    private CommentDetailContract.View view;
    private ApiService apiService;

    @Inject
    public CommentDetailPresenter(CommentDetailContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void requestCommentDetail(final CommentDetailRqEntity data) {
        apiService.getCommentDetail( data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<CommentDetailEntity>() {
                    @Override
                    public void onSuccess(CommentDetailEntity commentDetailEntity) {
                        view.onGetDetailSuccess(commentDetailEntity);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void deleteComment(final NewCommentEntity entity) {
        apiService.deleteNewComment(entity.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        view.onDeleteComment();
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        view.onFailure(code,msg);
                    }
                });
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
                            view.onSendComment();
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
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
                                            view.onSendComment();
                                        }

                                        @Override
                                        public void onFail(int code,String msg) {
                                            view.onFailure(code,msg);
                                        }
                                    });
                        }

                        @Override
                        public void onError(Throwable e) {
                            view.onFailure(-1,"");
                        }

                        @Override
                        public void onNext(Image image) {
                            images.add(image);
                        }
                    });
        }
    }
}
