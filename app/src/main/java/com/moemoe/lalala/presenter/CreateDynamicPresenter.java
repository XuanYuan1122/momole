package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.DynamicSendEntity;
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

public class CreateDynamicPresenter implements CreateDynamicContract.Presenter {

    private CreateDynamicContract.View view;
    private ApiService apiService;

    @Inject
    public CreateDynamicPresenter(CreateDynamicContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void createDynamic(final DynamicSendEntity entity, ArrayList<String> paths) {
        if(paths.size() > 0){
            ArrayList<Object> items = new ArrayList<>();
            items.addAll(paths);
            final ArrayList<Image> res = new ArrayList<>();
            Utils.uploadFiles(apiService, items, "", -1, "", "", new Observer<UploadResultEntity>() {

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {
                    entity.images = res;
                    apiService.createDynamic(entity)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new NetSimpleResultSubscriber() {
                                @Override
                                public void onSuccess() {
                                    if(view != null) view.onCreateDynamicSuccess();
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
                    try {
                        JSONObject jsonObject = new JSONObject(uploadResultEntity.getAttr());
                        image.setH(jsonObject.getInt("h"));
                        image.setW(jsonObject.getInt("w"));
                        res.add(image);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else {
            apiService.createDynamic(entity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onCreateDynamicSuccess();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }
    }
}
