package com.moemoe.lalala.presenter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.CommentSendV2Entity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.UploadResultEntity;
import com.moemoe.lalala.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class CreateCommentPresenter implements CreateCommentContract.Presenter {

    private CreateCommentContract.View view;
    private ApiService apiService;

    @Inject
    public CreateCommentPresenter(CreateCommentContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void createComment(final boolean isSec, final String id, final CommentSendV2Entity entity, ArrayList<String> path) {
        if(path.size() > 0){
            ArrayList<Object> item = new ArrayList<>();
            item.addAll(path);
            final ArrayList<Image> images = new ArrayList<>();
            Utils.uploadFiles(apiService, item, "", -1, "", "", new Observer<UploadResultEntity>() {

                @Override
                public void onError(Throwable e) {
                    if(view != null) view.onFailure(-1,"");
                }

                @Override
                public void onComplete() {
                    entity.image = images;
                    if(isSec){
                        apiService.sendCommentSec(id,entity)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new NetSimpleResultSubscriber() {
                                    @Override
                                    public void onSuccess() {
                                        if(view != null) view.onCreateCommentSuccess();
                                    }

                                    @Override
                                    public void onFail(int code, String msg) {
                                        if(view != null) view.onFailure(code, msg);
                                    }
                                });
                    }else {
                        apiService.sendComment(id,entity)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new NetSimpleResultSubscriber() {
                                    @Override
                                    public void onSuccess() {
                                        if(view != null) view.onCreateCommentSuccess();
                                    }

                                    @Override
                                    public void onFail(int code, String msg) {
                                        if(view != null) view.onFailure(code, msg);
                                    }
                                });
                    }
                }

                @Override
                public void onSubscribe(@NonNull Disposable d) {

                }

                @Override
                public void onNext(UploadResultEntity uploadResultEntity) {
                    Image image = new Image();
                    image.setPath(uploadResultEntity.getPath());
                    try {
                        JSONObject jsonObject = new JSONObject(uploadResultEntity.getAttr());
                        image.setH(jsonObject.getInt("h"));
                        image.setW(jsonObject.getInt("w"));
                        images.add(image);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else {
            entity.image = new ArrayList<>();
            if(isSec){
                apiService.sendCommentSec(id,entity)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetSimpleResultSubscriber() {
                            @Override
                            public void onSuccess() {
                                if(view != null) view.onCreateCommentSuccess();
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                if(view != null) view.onFailure(code, msg);
                            }
                        });
            }else {
                apiService.sendComment(id,entity)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetSimpleResultSubscriber() {
                            @Override
                            public void onSuccess() {
                                if(view != null) view.onCreateCommentSuccess();
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                if(view != null) view.onFailure(code, msg);
                            }
                        });
            }
        }
    }
}
