package com.moemoe.lalala.presenter;

import android.text.TextUtils;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.ForwardSendEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.ShareArticleSendEntity;
import com.moemoe.lalala.model.entity.ShareFolderSendEntity;
import com.moemoe.lalala.model.entity.UploadResultEntity;
import com.moemoe.lalala.utils.Utils;
import com.moemoe.lalala.view.activity.CreateForwardActivity;

import org.json.JSONException;
import org.json.JSONObject;

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

public class CreateForwardPresenter implements CreateForwardContract.Presenter {

    private CreateForwardContract.View view;
    private ApiService apiService;

    @Inject
    public CreateForwardPresenter(CreateForwardContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void createForward(int type, final Object entity) {
        if(type == CreateForwardActivity.TYPE_DYNAMIC){
            if(TextUtils.isEmpty(((ForwardSendEntity) entity).img.getPath())){
                apiService.rtDynamic((ForwardSendEntity) entity)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetResultSubscriber<Float>() {
                            @Override
                            public void onSuccess(Float integer) {
                                if(view != null) view.onCreateForwardSuccess(integer);
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                if(view != null) view.onFailure(code, msg);
                            }
                        });
            }else {
                Utils.uploadFile(apiService, ((ForwardSendEntity) entity).img.getPath(), new Observer<UploadResultEntity>() {

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        apiService.rtDynamic((ForwardSendEntity) entity)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new NetResultSubscriber<Float>() {
                                    @Override
                                    public void onSuccess(Float integer) {
                                        if(view != null) view.onCreateForwardSuccess(integer);
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
                        Image image = new Image();
                        image.setPath(uploadResultEntity.getPath());
                        if(!TextUtils.isEmpty(uploadResultEntity.getAttr())){
                            try {
                                JSONObject jsonObject = new JSONObject(uploadResultEntity.getAttr());
                                image.setH(jsonObject.getInt("h"));
                                image.setW(jsonObject.getInt("w"));
                                ((ForwardSendEntity) entity).img = image;
                            } catch (JSONException e) {
                                ((ForwardSendEntity) entity).img = new Image();
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }else if(type == CreateForwardActivity.TYPE_ARTICLE){
            apiService.shareArticle((ShareArticleSendEntity) entity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onCreateForwardSuccess();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }else if(type == CreateForwardActivity.TYPE_FOLDER){
            apiService.shareFolder((ShareFolderSendEntity) entity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onCreateForwardSuccess();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }
    }
}
