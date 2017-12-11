package com.moemoe.lalala.presenter;

import android.text.TextUtils;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.GroupEditEntity;
import com.moemoe.lalala.model.entity.GroupEntity;
import com.moemoe.lalala.model.entity.UploadResultEntity;
import com.moemoe.lalala.utils.Utils;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by yi on 2016/11/29.
 */

public class PhoneGroupEditPresenter implements PhoneGroupEditContract.Presenter {

    private PhoneGroupEditContract.View view;
    private ApiService apiService;

    @Inject
    public PhoneGroupEditPresenter(PhoneGroupEditContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }


    @Override
    public void createGroup(final GroupEditEntity editEntity) {
        if(TextUtils.isEmpty(editEntity.cover)){
            apiService.createGroup(editEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onEditSuccess();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }else {
            Utils.uploadFile(apiService, editEntity.cover, new Observer<UploadResultEntity>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(UploadResultEntity uploadResultEntity) {
                    editEntity.cover = uploadResultEntity.getPath();
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {
                    apiService.createGroup(editEntity)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new NetSimpleResultSubscriber() {
                                @Override
                                public void onSuccess() {
                                    if(view != null) view.onEditSuccess();
                                }

                                @Override
                                public void onFail(int code, String msg) {
                                    if(view != null) view.onFailure(code, msg);
                                }
                            });
                }
            });
        }
    }

    @Override
    public void updateGroup(final String id, final GroupEditEntity editEntity) {
        if(TextUtils.isEmpty(editEntity.cover)){
            apiService.updateGroup(id,editEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onEditSuccess();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }else {
            Utils.uploadFile(apiService, editEntity.cover, new Observer<UploadResultEntity>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(UploadResultEntity uploadResultEntity) {
                    editEntity.cover = uploadResultEntity.getPath();
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {
                    apiService.updateGroup(id,editEntity)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new NetSimpleResultSubscriber() {
                                @Override
                                public void onSuccess() {
                                    if(view != null) view.onEditSuccess();
                                }

                                @Override
                                public void onFail(int code, String msg) {
                                    if(view != null) view.onFailure(code, msg);
                                }
                            });
                }
            });
        }
    }
}
