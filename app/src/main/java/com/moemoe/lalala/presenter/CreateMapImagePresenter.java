package com.moemoe.lalala.presenter;

import android.text.TextUtils;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.ForwardSendEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.MapAddressEntity;
import com.moemoe.lalala.model.entity.ShareArticleSendEntity;
import com.moemoe.lalala.model.entity.ShareFolderSendEntity;
import com.moemoe.lalala.model.entity.UploadResultEntity;
import com.moemoe.lalala.model.entity.UserMapSendEntity;
import com.moemoe.lalala.utils.Utils;
import com.moemoe.lalala.view.activity.CreateForwardActivity;

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

public class CreateMapImagePresenter implements CreateMapImageContract.Presenter {

    private CreateMapImageContract.View view;
    private ApiService apiService;

    @Inject
    public CreateMapImagePresenter(CreateMapImageContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadAddressList() {
        apiService.loadMapAddressList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<MapAddressEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<MapAddressEntity> entities) {
                        if(view != null) view.onLoadAddressListSuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void saveUserMapImage(String cover,String orCover, String id, final boolean needCheck) {
        final UserMapSendEntity entity = new UserMapSendEntity();
        entity.placeId = id;
        ArrayList<Object> items = new ArrayList<>();
        items.add(orCover);
        Utils.uploadFiles(apiService,items,cover,0,"","",new Observer<UploadResultEntity>() {
            @Override
            public void onError(Throwable e) {
                if(view != null) view.onFailure(-1,"");
            }

            @Override
            public void onComplete() {
                if(needCheck){
                    apiService.checkUserMapImage(entity)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new NetSimpleResultSubscriber() {
                                @Override
                                public void onSuccess() {
                                    if(view != null) view.onSaveSuccess();
                                }

                                @Override
                                public void onFail(int code, String msg) {
                                    if(view != null) view.onFailure(code, msg);
                                }
                            });
                }else {
                    apiService.saveUserMapImage(entity)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new NetSimpleResultSubscriber() {
                                @Override
                                public void onSuccess() {
                                    if(view != null) view.onSaveSuccess();
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
                if(uploadResultEntity.getType().equals("cover")){
                    entity.md5 = uploadResultEntity.getMd5();
                    entity.picUrl = uploadResultEntity.getPath();
                    entity.height = 110;
                    entity.width = 90;
                }else {
                    entity.artwork = uploadResultEntity.getPath();
                }
            }
        });
    }
}
