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
                        if(view != null)  view.onGetDetailSuccess(commentDetailEntity);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
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
                        if(view != null) view.onDeleteComment();
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
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
    public void release() {
        view = null;
    }
}
