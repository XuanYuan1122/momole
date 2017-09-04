package com.moemoe.lalala.presenter;

import android.text.TextUtils;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.ApiResult;
import com.moemoe.lalala.model.entity.FolderRepEntity;
import com.moemoe.lalala.model.entity.NewUploadEntity;
import com.moemoe.lalala.model.entity.UploadResultEntity;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.StringUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class NewFolderEditPresenter implements NewFolderEditContract.Presenter {

    private NewFolderEditContract.View view;
    private ApiService apiService;

    @Inject
    public NewFolderEditPresenter(NewFolderEditContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void addFolder(final FolderRepEntity entity) {
        final ArrayList<NewUploadEntity> entities = new ArrayList<>();
        entities.add(new NewUploadEntity(StringUtils.getFileMD5(new File(entity.cover)), FileUtil.getExtensionName(entity.cover)));
        apiService.checkMd5(entities)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Func1<ApiResult<ArrayList<UploadResultEntity>>, Observable<UploadResultEntity>>() {
                    @Override
                    public Observable<UploadResultEntity> call(final ApiResult<ArrayList<UploadResultEntity>> arrayListApiResult) {
                        final UploadResultEntity uploadResultEntity = arrayListApiResult.getData().get(0);
                        final File file = new File(entity.cover);
                        final UploadManager uploadManager = new UploadManager();
                        return Observable.create(new Observable.OnSubscribe<UploadResultEntity>() {
                            @Override
                            public void call(final Subscriber<? super UploadResultEntity> subscriber) {
                                final UploadResultEntity entity = new UploadResultEntity();
                                if(!uploadResultEntity.isSave()){
                                    try {
                                        uploadManager.put(file,uploadResultEntity.getPath(), uploadResultEntity.getUploadToken(), new UpCompletionHandler() {
                                            @Override
                                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                                if (info.isOK()) {
                                                    entity.setFileName(file.getName());
                                                    entity.setMd5(uploadResultEntity.getMd5());
                                                    entity.setPath(uploadResultEntity.getPath());
                                                    entity.setSave(uploadResultEntity.isSave());
                                                    entity.setSize(file.length());
                                                    entity.setType(uploadResultEntity.getType());
                                                    subscriber.onNext(entity);
                                                    subscriber.onCompleted();
                                                } else {
                                                    subscriber.onError(null);
                                                }
                                            }
                                        }, null);
                                    }catch (Exception e){
                                        subscriber.onError(e);
                                    }
                                }else {
                                    entity.setAttr(uploadResultEntity.getAttr());
                                    entity.setFileName(file.getName());
                                    entity.setMd5(uploadResultEntity.getMd5());
                                    entity.setPath(uploadResultEntity.getPath());
                                    entity.setSave(uploadResultEntity.isSave());
                                    entity.setSize(uploadResultEntity.getSize());
                                    entity.setType(uploadResultEntity.getType());
                                    subscriber.onNext(entity);
                                    subscriber.onCompleted();
                                }
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UploadResultEntity>() {
                    @Override
                    public void onCompleted() {
                        apiService.createFolder(entity)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new NetSimpleResultSubscriber() {
                                    @Override
                                    public void onSuccess() {
                                        if(view != null)view.onSuccess();
                                    }

                                    @Override
                                    public void onFail(int code, String msg) {
                                        if(view != null) view.onFailure(code, msg);
                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(UploadResultEntity uploadResultEntity) {
                        entity.cover = uploadResultEntity.getPath();
                        entity.coverSize = uploadResultEntity.getSize();
                    }
                });
    }

    @Override
    public void updateFolder(final String folderId, final FolderRepEntity entity) {
        if(entity.coverSize == -1){//封面没修改
            apiService.updateFolder(folderId,entity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null)view.onSuccess();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }else {
            final ArrayList<NewUploadEntity> entities = new ArrayList<>();
            entities.add(new NewUploadEntity(StringUtils.getFileMD5(new File(entity.cover)), FileUtil.getExtensionName(entity.cover)));
            apiService.checkMd5(entities)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap(new Func1<ApiResult<ArrayList<UploadResultEntity>>, Observable<UploadResultEntity>>() {
                        @Override
                        public Observable<UploadResultEntity> call(final ApiResult<ArrayList<UploadResultEntity>> arrayListApiResult) {
                            final UploadResultEntity uploadResultEntity = arrayListApiResult.getData().get(0);
                            final File file = new File(entity.cover);
                            final UploadManager uploadManager = new UploadManager();
                            return Observable.create(new Observable.OnSubscribe<UploadResultEntity>() {
                                @Override
                                public void call(final Subscriber<? super UploadResultEntity> subscriber) {
                                    final UploadResultEntity entity = new UploadResultEntity();
                                    if(!uploadResultEntity.isSave()){
                                        try {
                                            uploadManager.put(file,uploadResultEntity.getPath(), uploadResultEntity.getUploadToken(), new UpCompletionHandler() {
                                                @Override
                                                public void complete(String key, ResponseInfo info, JSONObject response) {
                                                    if (info.isOK()) {
                                                        entity.setFileName(file.getName());
                                                        entity.setMd5(uploadResultEntity.getMd5());
                                                        entity.setPath(uploadResultEntity.getPath());
                                                        entity.setSave(uploadResultEntity.isSave());
                                                        entity.setSize(file.length());
                                                        entity.setType(uploadResultEntity.getType());
                                                        subscriber.onNext(entity);
                                                        subscriber.onCompleted();
                                                    } else {
                                                        subscriber.onError(null);
                                                    }
                                                }
                                            }, null);
                                        }catch (Exception e){
                                            subscriber.onError(e);
                                        }
                                    }else {
                                        entity.setAttr(uploadResultEntity.getAttr());
                                        entity.setFileName(file.getName());
                                        entity.setMd5(uploadResultEntity.getMd5());
                                        entity.setPath(uploadResultEntity.getPath());
                                        entity.setSave(uploadResultEntity.isSave());
                                        entity.setSize(uploadResultEntity.getSize());
                                        entity.setType(uploadResultEntity.getType());
                                        subscriber.onNext(entity);
                                        subscriber.onCompleted();
                                    }
                                }
                            });
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<UploadResultEntity>() {
                        @Override
                        public void onCompleted() {
                            apiService.updateFolder(folderId,entity)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new NetSimpleResultSubscriber() {
                                        @Override
                                        public void onSuccess() {
                                            if(view != null)view.onSuccess();
                                        }

                                        @Override
                                        public void onFail(int code, String msg) {
                                            if(view != null) view.onFailure(code, msg);
                                        }
                                    });
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(UploadResultEntity uploadResultEntity) {
                            entity.cover = uploadResultEntity.getPath();
                        }
                    });
        }

    }

    @Override
    public void checkSize(long size) {
        apiService.checkSize(size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if(view != null) view.onCheckSize(aBoolean);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
