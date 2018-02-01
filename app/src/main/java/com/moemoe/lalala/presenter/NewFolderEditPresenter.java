package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.FolderRepEntity;
import com.moemoe.lalala.model.entity.UploadResultEntity;
import com.moemoe.lalala.utils.Utils;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 *
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
        Utils.uploadFile(apiService,entity.cover,new Observer<UploadResultEntity>() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
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
            public void onSubscribe(@NonNull Disposable d) {

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
            Utils.uploadFile(apiService,entity.cover,new Observer<UploadResultEntity>() {
                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {
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
                public void onSubscribe(@NonNull Disposable d) {

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
